package org.javaboy.pagoda.ordermaster.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.javaboy.pagoda.common.constant.PagodaConstants;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.domain.entity.SysDictData;
import org.javaboy.pagoda.common.utils.JsonUtil;
import org.javaboy.pagoda.common.utils.SecurityUtils;
import org.javaboy.pagoda.ordermaster.entity.AllocationInventory;
import org.javaboy.pagoda.ordermaster.entity.DistributionInventory;
import org.javaboy.pagoda.ordermaster.mapper.AllocationInventoryMapper;
import org.javaboy.pagoda.ordermaster.service.IAllocationInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaboy.pagoda.ordermaster.service.IDistributionInventoryService;
import org.javaboy.pagoda.ordermaster.vo.AllocationVO;
import org.javaboy.pagoda.ordermaster.vo.StoreOrderVO;
import org.javaboy.pagoda.system.mapper.SysDictDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-20
 */
@Service
@Slf4j
public class AllocationInventoryServiceImpl extends ServiceImpl<AllocationInventoryMapper, AllocationInventory> implements IAllocationInventoryService {

        @Resource
        AllocationInventoryMapper allocationInventoryMapper;

        @Resource
        SysDictDataMapper sysDictDataMapper;

        @Resource
        IDistributionInventoryService distributionInventoryService;

        @Resource
        StringRedisTemplate stringRedisTemplate;

        @Resource
        ObjectMapper objectMapper;

        @Resource
        ThreadPoolTaskExecutor threadPoolTaskExecutor;

        /**
         * allocationInventory 获取分货系统配送库存果品信息
         *
         * @param allocationInventory
         * @return
         */
        @Override
        public List<AllocationVO> getInventoryList(AllocationInventory allocationInventory) {
                List<AllocationVO> allocationVoList = allocationInventoryMapper.getInventoryList(allocationInventory);

                //获取配送出入库类型字典数据
                List<SysDictData> sysDictData = sysDictDataMapper.selectDictDataByType(PagodaConstants.PAGODA_INVENTORY_TYPE);

                Map<String, String> dictMap = sysDictData.stream()
                        .collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel));

                //将出入库类型和 商品数量进行拼接
                for (AllocationVO inventory : allocationVoList) {
                        String ioTypeList = inventory.getIoTypeList();
                        String quantityList = inventory.getQuantityList();

                        // 将逗号分隔的字符串转换为数组
                        String[] ioTypeArray = ioTypeList.split(",");
                        String[] quantityArray = quantityList.split(",");

                        StringBuilder combinedList = new StringBuilder();

                        for (int i = 0; i < ioTypeArray.length; i++) {
                                String ioType = dictMap.get(ioTypeArray[i]);

                                // 拼接合并后的字符串
                                combinedList.append(ioType)
                                        .append(" ")
                                        .append(quantityArray[i])
                                        .append(inventory.getProductUnit())
                                        .append(",");
                        }

                        // 去除最后一个逗号
                        if (combinedList.length() > 0) {
                                combinedList.deleteCharAt(combinedList.length() - 1);
                        }

                        // 将合并后的字符串设置到新的字段中
                        inventory.setIoTypeQuantity(combinedList.toString());
                }
                return allocationVoList;

        }

        @Override
        @Transactional
        public String importAllocation(List<AllocationInventory> allocationInventoryList, boolean updateSupport, String operName) {

                List<AllocationInventory> inventoryList = allocationInventoryList.stream().map(ai -> {
                        ai.setUpdatedBy(operName);
                        ai.setCreatedTime(LocalDateTime.now());
                        ai.setCreatedBy(SecurityUtils.getUsername());
                        ai.setUpdatedBy(null);
                        ai.setUpdatedTime(null);
                        ai.setStatus(1);

                        return ai;
                }).collect(Collectors.toList());

                saveBatch(inventoryList);

                String currentUsername = SecurityUtils.getUsername();
                threadPoolTaskExecutor.submit(() -> {
                        log.info("Task started");
                        try {
                                distributionInventoryService.insertTodayInventory(inventoryList, currentUsername);
                        } catch (Exception e) {
                                log.error("Task failed", e);
                        }
                        log.info("Task finished");
                });



                return "上传成功";
        }

        //删除门店订货商品
        @Override
        public int deleteAllocationInventoryByProductCodes(Long[] productCodes) {

                boolean update = lambdaUpdate().set(AllocationInventory::getDelFlag, 1).in(AllocationInventory::getProductCode, productCodes).update();

                Set<String> keysToDelete = Arrays.stream(productCodes)
                        .map(productCode -> "allocation:" + productCode)
                        .collect(Collectors.toSet());
                //同时删除对应缓存
                stringRedisTemplate.delete(keysToDelete);


                return update ? 1 : 0;
        }

        //门店订货商品展示
        //@Override
        //public AjaxResult getStoreOrderList(String  deptCode) {
        //
        //        try {
        //                String redisStoreOrderList = stringRedisTemplate.opsForValue().get("StoreOrderList:" + deptCode);
        //
        //                if (redisStoreOrderList == null) {
        //                        List<StoreOrderVO> storeOrderList = allocationInventoryMapper.getStoreOrderList(deptCode);
        //
        //                        stringRedisTemplate.opsForValue().set("StoreOrderList:" + deptCode, objectMapper.writeValueAsString(storeOrderList), 10l, TimeUnit.MINUTES);
        //
        //
        //                        return AjaxResult.success(storeOrderList);
        //                }
        //
        //                return AjaxResult.success(objectMapper.readValue(redisStoreOrderList, new TypeReference<List<StoreOrderVO>>() {}));
        //
        //
        //        } catch (JsonProcessingException e) {
        //                return AjaxResult.error(e.getMessage());
        //        }
        //
        //}

        //解决缓存穿透
        @Override
        public AjaxResult getStoreOrderList(String deptCode) {
                String redisStoreOrderList = stringRedisTemplate.opsForValue().get("StoreOrderList:" + deptCode);

                if (redisStoreOrderList == null) {
                        List<StoreOrderVO> storeOrderList = allocationInventoryMapper.getStoreOrderList(deptCode);

                        if (storeOrderList == null || storeOrderList.isEmpty()) {
                                stringRedisTemplate.opsForValue().set("StoreOrderList:" + deptCode, "", 2L, TimeUnit.MINUTES);
                                return AjaxResult.error("门店信息不存在");
                        }

                        stringRedisTemplate.opsForValue().set("StoreOrderList:" + deptCode, JsonUtil.serialize(storeOrderList), 10L, TimeUnit.MINUTES);

                        return AjaxResult.success(storeOrderList);
                }

                if (redisStoreOrderList.isEmpty()) {
                        return AjaxResult.error("门店信息不存在");
                }

                return AjaxResult.success(JsonUtil.deserialize(redisStoreOrderList, new TypeReference<List<StoreOrderVO>>() {
                }));
        }

        //@Override
        //public AjaxResult getStoreOrderList(String deptCode) {
        //        // 定义缓存键
        //        String cacheKey = "StoreOrderList:" + deptCode;
        //
        //        String setnxKey = "lock:" + deptCode;
        //        // 尝试从缓存中获取数据
        //        String redisStoreOrderList = stringRedisTemplate.opsForValue().get(cacheKey);
        //
        //        // 如果缓存中没有数据
        //        if (redisStoreOrderList == null) {
        //                // 定义锁键
        //                String lockKey = "StoreOrderListLock:" + deptCode;
        //                // 尝试获取互斥锁
        //                if (setnx(lockKey, , 10L)) {
        //                        try {
        //                                // 成功获取锁，查询数据库并更新缓存
        //                                List<StoreOrderVO> storeOrderList = allocationInventoryMapper.getStoreOrderList(deptCode);
        //
        //                                // 如果数据库中没有数据，设置缓存为空值，避免缓存穿透
        //                                if (storeOrderList == null || storeOrderList.isEmpty()) {
        //                                        stringRedisTemplate.opsForValue().set(cacheKey, "", 2L, TimeUnit.MINUTES);
        //                                        return AjaxResult.error("门店信息不存在");
        //                                }
        //
        //                                // 将查询结果存储到缓存中
        //                                stringRedisTemplate.opsForValue().set(cacheKey, JsonUtil.serialize(storeOrderList), 10L, TimeUnit.MINUTES);
        //                                return AjaxResult.success(storeOrderList);
        //                        } finally {
        //                                // 释放锁
        //                                delete(lockKey);
        //                        }
        //                } else {
        //                        // 获取锁失败，等待50毫秒后再次尝试获取缓存数据
        //                        try {
        //                                Thread.sleep(50);
        //                        } catch (InterruptedException e) {
        //                                e.printStackTrace();
        //                        }
        //                        return getStoreOrderList(deptCode);
        //                }
        //        }
        //
        //        // 如果缓存中的数据为空字符串，说明数据库中没有数据
        //        if (redisStoreOrderList.isEmpty()) {
        //                return AjaxResult.error("门店信息不存在");
        //        }
        //
        //        // 返回从缓存中获取的数据
        //        return AjaxResult.success(JsonUtil.deserialize(redisStoreOrderList, new TypeReference<List<StoreOrderVO>>() {
        //        }));
        //}

        // 获取门店必上果品的清单且 必上果品按照必上字段排序
        @Override
        public AjaxResult getMustFruits(String deptCode) {
                return null;
        }

}
