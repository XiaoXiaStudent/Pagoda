package org.javaboy.pagoda.ordermaster.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.utils.SecurityUtils;
import org.javaboy.pagoda.common.utils.uuid.SnowflakeIdWorker;
import org.javaboy.pagoda.ordermaster.entity.OrderItems;
import org.javaboy.pagoda.ordermaster.entity.Orders;
import org.javaboy.pagoda.ordermaster.entity.ShoppingCart;
import org.javaboy.pagoda.ordermaster.mapper.OrdersMapper;
import org.javaboy.pagoda.ordermaster.service.IDistributionInventoryService;
import org.javaboy.pagoda.ordermaster.service.IOrderItemsService;
import org.javaboy.pagoda.ordermaster.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaboy.pagoda.ordermaster.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-05-02
 */
@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

        @Resource
        OrdersMapper ordersMapper;

        @Resource
        IShoppingCartService shoppingCartService;

        @Resource
        IOrderItemsService orderItemsService;

        @Resource
        IDistributionInventoryService distributionInventoryService;

        @Resource
        SnowflakeIdWorker snowflakeIdWorker;

        @Resource
        StringRedisTemplate stringRedisTemplate;

        @Autowired
        private DefaultRedisScript<Long> checkAndDecreaseStockScript;
       @Autowired
        private DefaultRedisScript<Long> addOrderToStreamScript;

        @Resource
        ObjectMapper objectMapper;

        @Resource
        ThreadPoolTaskExecutor threadPoolTaskExecutor;

        @PostConstruct
        public void init() {
                // 启动Redis Stream消费者

                threadPoolTaskExecutor.submit(() -> handleOrder());
        }

        //TODO   这从下单操作  我需要继续优化   主要有高并发等
        @Override
        @Transactional
        public AjaxResult order(String storeCode) {

                // 1. 从Redis中获取购物车数据
                String shoppingCartKey = "shopping_cart:" + storeCode;
                HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
                Map<String, String> shoppingCartMap = hashOps.entries(shoppingCartKey);

                long orderId = snowflakeIdWorker.nextId();

                // 2. 将从Redis中获取的购物车数据转换为 ShoppingCart 对象列表
                List<ShoppingCart> cartItems = shoppingCartMap.values().stream()
                        .map(json -> {
                                try {
                                        return objectMapper.readValue(json, ShoppingCart.class);
                                } catch (JsonProcessingException e) {
                                        throw new RuntimeException("Error parsing JSON", e);
                                }
                        })
                        .filter(item -> item.getDelFlag() != 1)
                        .collect(Collectors.toList());

                // 4. 检查购物车中的商品数量是否小于配送库存\
                //此处优化,  从redis中判断库存是否超量
                List<Object> scriptArgs = new ArrayList<>();
                List<Object> checkAndDecreaseStockScriptArgs = new ArrayList<>();
                scriptArgs.add(String.valueOf(orderId));
                scriptArgs.add(storeCode);
                scriptArgs.add(SecurityUtils.getUserId().toString());

                for (ShoppingCart cartItem : cartItems) {
                        checkAndDecreaseStockScriptArgs.add(cartItem.getOrderQuantity().toString());
                        scriptArgs.add(cartItem.getOrderQuantity().toString());


                        try {
                                scriptArgs.add(objectMapper.writeValueAsString(cartItems));
                        } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                        }

                        Long result = stringRedisTemplate.execute(
                                checkAndDecreaseStockScript,
                                Collections.singletonList("allocation:" + cartItem.getGoodsCode()),
                                checkAndDecreaseStockScriptArgs.toArray()
                        );

                        if (result == -1) {
                                return AjaxResult.error("条码 " + cartItem.getGoodsCode() + " 库存数据缺失，请重新获取库存数据");
                        } else if (result == -2) {
                                return AjaxResult.error("条码 " + cartItem.getGoodsCode() + " 库存不足，请重新更改数量");
                        }
                }

                // 7. 将订单数据添加到 Redis Stream 队列中
                String streamKey = "stream.orders";

                Long result = stringRedisTemplate.execute(
                        addOrderToStreamScript,
                        Collections.emptyList(),
                        scriptArgs.toArray()
                );


                // 8. 从购物车表中删除已处理的商品记录
                shoppingCartService.deleteItemsByStoreCode(storeCode);

                // 9. 下单成功，删除Redis中购物车对应的数据
                stringRedisTemplate.delete(shoppingCartKey);

                // 9. 返回成功的AjaxResult，包含订单信息
                return AjaxResult.success("订单创建成功");
        }

        private void handleOrder() {
                String streamKey = "stream.orders";
                String groupName = "order_group";
                String consumerName = "order_consumer";

                // 创建消费者组（如果不存在）
                try {
                        stringRedisTemplate.opsForStream().createGroup(streamKey, groupName);
                } catch (Exception e) {
                        // 消费者组已存在，忽略错误
                }

                log.info("handleOrder线程启动"); // 添加日志记录

                while (true) {
                        try {
                                // 读取指定队列中的消息，从最新的开始读取
                                List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream()
                                        .read(Consumer.from(groupName, consumerName),
                                                StreamOffset.create(streamKey,
                                                        ReadOffset.lastConsumed()));

                                log.info("读取消息，messages.size={}", messages.size()); // 添加日志记录
                                //判断消息是否存在
                                if (messages == null || messages.isEmpty()) {
                                        //如果没有数据 说明没有消息继续下一次循环
                                        continue;



                                }

                                // 处理消息
                                for (MapRecord<String, Object, Object> message : messages) {
                                        log.info("消息正在消费,{}", message.getId());
                                        // 确认消息已处理
                                        Map<Object, Object> value = message.getValue();

                                        createOrder(value);

                                        log.info("消息消费成功,{}", message.getId());

                                        stringRedisTemplate.opsForStream().acknowledge(streamKey, groupName, message.getId());
                                }

                                // 休眠一段时间，避免过多的CPU占用
                                Thread.sleep(1000);
                        } catch (Exception e) {
                                // 处理pending列表中的消息
                                HandPendingList();

                        }

                }

        }

        private void HandPendingList() {
                String streamKey = "stream.orders";
                String groupName = "order_group";
                String consumerName = "order_consumer";

                while (true) {
                        try {
                                // 读取指定队列中的消息，从最新的开始读取
                                List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream()
                                        .read(Consumer.from(groupName, consumerName),
                                                StreamOffset.create(streamKey,
                                                        ReadOffset.from("0")));

                                //判断消息是否存在
                                if (messages == null || messages.isEmpty()) {
                                        //如果没有数据 说明没有消息继续下一次循环
                                        break;

                                }

                                // 处理消息
                                for (MapRecord<String, Object, Object> message : messages) {

                                        // 确认消息已处理
                                        Map<Object, Object> value = message.getValue();

                                        createOrder(value);

                                        stringRedisTemplate.opsForStream().acknowledge(streamKey, groupName, message.getId());
                                }

                                // 休眠一段时间，避免过多的CPU占用
                                Thread.sleep(1000);
                        } catch (Exception e) {
                                // 处理pending列表中的消息
                                log.error("Error occurred while processing stream messages. Handling pending messages.", e);


                        }

                }
        }

        private void createOrder(Map<Object, Object> value) {



                // 从队列数据中解析订单信息
                Long orderId = Long.parseLong((String) value.get("orderId"));
                String storeCode = (String) value.get("storeCode");
                String userId = (String) value.get("userId");
                List<ShoppingCart> cartItems = null;
                try {
                        cartItems = objectMapper.readValue((String) value.get("cartItems"), new TypeReference<List<ShoppingCart>>() {
                        });
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return;
                }

                System.out.println("cartItems = " + cartItems);

                // 5. 创建新订单实体并填充相关信息
                Orders order = new Orders();
                order.setStoreCode(storeCode);
                order.setOrderId(orderId);

                // 设置其他订单信息，例如用户ID、支付方式等（根据需求）
                order.setDelFlag(0);
                order.setUserId(Integer.valueOf(userId));
                order.setUpdatedAt(LocalDateTime.now());
                order.setCreatedAt(LocalDateTime.now());
                order.setStatus("未支付");

                // 4. 为每个购物车条目创建订单项实体
                List<OrderItems> orderItems = new ArrayList<>();
                double totalAmount = 0;

                for (ShoppingCart cartItem : cartItems) {
                        OrderItems orderItem = new OrderItems();
                        orderItem.setOrderId(orderId);
                        orderItem.setGoodsCode(cartItem.getGoodsCode());
                        orderItem.setQuantity(cartItem.getOrderQuantity());
                        orderItem.setPrice(cartItem.getDistributionPrice());
                        orderItem.setSubtotal(cartItem.getOrderQuantity() * cartItem.getDistributionPrice());
                        // 设置其他订单项信息，例如商品名称、销售单位等（根据需求）
                        orderItems.add(orderItem);
                        totalAmount += orderItem.getSubtotal();

                }

                // 5. 计算订单总金额
                order.setTotalAmount(totalAmount);

                order.setStatus("支付成功");
                // 6. 将新订单插入到订单表中
                save(order);

                System.out.println("orderItems = " + orderItems);
                // 7. 将新订单项插入到订单项表中
                for (OrderItems orderItem : orderItems) {
                        orderItem.setOrderId(orderId);
                        orderItemsService.save(orderItem);

                        // 更新库存
                        distributionInventoryService.decreaseStock(orderItem.getGoodsCode(), orderItem.getQuantity());
                }
        }

}


