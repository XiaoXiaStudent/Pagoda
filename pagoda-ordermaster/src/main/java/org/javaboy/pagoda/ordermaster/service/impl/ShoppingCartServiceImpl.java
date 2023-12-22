package org.javaboy.pagoda.ordermaster.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.utils.SecurityUtils;
import org.javaboy.pagoda.common.utils.bean.BeanUtils;
import org.javaboy.pagoda.ordermaster.entity.DistributionInventory;
import org.javaboy.pagoda.ordermaster.entity.GoodsInfo;
import org.javaboy.pagoda.ordermaster.entity.ShoppingCart;
import org.javaboy.pagoda.ordermaster.mapper.AllocationInventoryMapper;
import org.javaboy.pagoda.ordermaster.mapper.ShoppingCartMapper;
import org.javaboy.pagoda.ordermaster.service.IDistributionInventoryService;
import org.javaboy.pagoda.ordermaster.service.IShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;
import org.javaboy.pagoda.ordermaster.vo.ShoppingVO;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.LocalTaskExecutorThreadPool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-26
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

        @Resource
        AllocationInventoryMapper allocationInventoryMapper;

        @Resource
        IDistributionInventoryService distributionInventoryService;

        @Resource
        ShoppingCartMapper shoppingCartMapper;

        @Resource
        StringRedisTemplate stringRedisTemplate;

        @Resource
        private ObjectMapper objectMapper;

        @Resource
        ThreadPoolTaskExecutor threadPoolTaskExecutor;

        @Autowired
        private RedissonClient redissonClient;



        @Override
        public List<ShoppingVO> selectShoppingList(String storeCode) {
                List<ShoppingVO> shoppingVOS = shoppingCartMapper.selectShoppingList(storeCode);

                threadPoolTaskExecutor.submit(() -> {

                        // 添加购物车数据到Redis
                        String shoppingCartKey = "shopping_cart:" + storeCode;
                        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

                        for (ShoppingVO shoppingVO : shoppingVOS) {
                                String goodsCode = shoppingVO.getGoodsCode();
                                ShoppingCart shoppingCart = new ShoppingCart();
                                BeanUtils.copyProperties(shoppingVO, shoppingCart);

                                try {
                                        // 将购物车数据转换为JSON并保存到Redis
                                        hashOps.put(shoppingCartKey, goodsCode, objectMapper.writeValueAsString(shoppingCart));
                                } catch (JsonProcessingException e) {
                                        throw new RuntimeException("Error converting object to JSON", e);
                                }
                        }
                });
                return shoppingVOS ;
        }




        @Override
        public void deleteItemsByStoreCode(String storeCode) {
                UpdateWrapper<ShoppingCart> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("store_code", storeCode)
                        .set("del_flag", 1);
                this.update(updateWrapper);
        }

        @Override
        @Transactional
        public AjaxResult manageShoppingCart(GoodInfoVO goodInfoVO) {
                String storeCode = goodInfoVO.getStoreCode();
                String goodsCode = goodInfoVO.getGoodsCode();
                Double orderQuantity = goodInfoVO.getOrderQuantity();

                if (orderQuantity != null && orderQuantity <= 0) {
                        return AjaxResult.error("数量不合法，不能小于等于0");
                }

                String shoppingCartKey = "shopping_cart:" + storeCode;
                HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

                // 首先尝试从Redis中获取购物车数据
                ShoppingCart shoppingCart = null;
                String shoppingCartItemJson = hashOps.get(shoppingCartKey, goodsCode);
                if (shoppingCartItemJson != null) {
                        try {
                                shoppingCart = objectMapper.readValue(shoppingCartItemJson, ShoppingCart.class);
                        } catch (JsonProcessingException e) {
                                throw new RuntimeException("Error converting JSON to object", e);
                        }
                }

                shoppingCart = shoppingCart == null ? new ShoppingCart() : shoppingCart;
                BeanUtils.copyProperties(goodInfoVO, shoppingCart);
                shoppingCart.setUpdateBy(SecurityUtils.getUsername());
                shoppingCart.setUpdateTime(LocalDateTime.now());

                if (orderQuantity == null) {
                        if (shoppingCart.getCartId() == null) {
                                return AjaxResult.error("购物车记录不存在");
                        }
                        // 删除购物车中的商品记录，实际为逻辑删除
                        shoppingCart.setDelFlag(1);
                        baseMapper.updateById(shoppingCart);

                        // 在Redis中删除该购物车条目
                        hashOps.delete(shoppingCartKey, goodsCode);

                        return AjaxResult.success("删除成功");
                } else {
                        // 添加或更新购物车
                        if (shoppingCart.getCartId() == null) {
                                shoppingCart.setCreateBy(SecurityUtils.getUsername());
                                shoppingCart.setCreateTime(LocalDateTime.now());
                                baseMapper.insert(shoppingCart);
                        } else {
                                baseMapper.updateById(shoppingCart);
                        }

                        // 更新Redis中的购物车数据
                        try {
                                hashOps.put(shoppingCartKey, goodsCode, objectMapper.writeValueAsString(shoppingCart));
                        } catch (JsonProcessingException e) {
                                throw new RuntimeException("Error converting object to JSON", e);
                        }

                        return AjaxResult.success("添加成功");
                }
        }

}
