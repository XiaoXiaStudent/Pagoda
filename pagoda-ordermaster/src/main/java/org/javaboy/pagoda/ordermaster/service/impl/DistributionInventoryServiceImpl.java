package org.javaboy.pagoda.ordermaster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.utils.SecurityUtils;
import org.javaboy.pagoda.ordermaster.entity.AllocationInventory;
import org.javaboy.pagoda.ordermaster.entity.DistributionInventory;
import org.javaboy.pagoda.ordermaster.mapper.DistributionInventoryMapper;
import org.javaboy.pagoda.ordermaster.service.IDistributionInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaboy.pagoda.ordermaster.vo.AssignmentVO;
import org.javaboy.pagoda.ordermaster.vo.MustFruitsVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
public class DistributionInventoryServiceImpl extends ServiceImpl<DistributionInventoryMapper, DistributionInventory> implements IDistributionInventoryService {

        @Resource
        DistributionInventoryMapper distributionInventoryMapper;

        @Resource
        ThreadPoolTaskExecutor threadPoolTaskExecutor;

        @Resource
        StringRedisTemplate stringRedisTemplate;

        @Override
        @Transactional
        public boolean insertTodayInventory(List<AllocationInventory> inventoryList, String username) {

                // 按 product_code 对 inventoryList 进行分组并汇总 quantity
                Map<String, Double> summaryMap = inventoryList.stream()
                        .collect(Collectors.groupingBy(AllocationInventory::getProductCode,
                                Collectors.summingDouble(AllocationInventory::getQuantity)));

                // 将汇总数据插入到 pagoda_distribution_inventory 表中
                summaryMap.forEach((productCode, totalQuantity) -> {
                        DistributionInventory existingRecord = lambdaQuery()
                                .eq(DistributionInventory::getProductCode, productCode)
                                .eq(DistributionInventory::getType, 2)
                                .one();

                        if (existingRecord != null) {
                                // 如果存在记录，则更新数量
                                existingRecord.setQuantity(totalQuantity);
                                existingRecord.setUpdatedTime(LocalDateTime.now());
                                existingRecord.setUpdatedBy(username);
                                existingRecord.setType(2);

                                updateById(existingRecord);
                        } else {
                                // 如果不存在记录，则插入新记录
                                DistributionInventory newRecord = new DistributionInventory();
                                newRecord.setProductCode(productCode);
                                newRecord.setQuantity(totalQuantity);
                                newRecord.setCreatedBy(username);
                                newRecord.setCreatedTime(LocalDateTime.now());
                                newRecord.setUpdatedBy(username);
                                newRecord.setUpdatedTime(LocalDateTime.now());
                                existingRecord.setType(2);
                                save(newRecord);
                        }
                });

                summaryMap.forEach((productCode, totalQuantity) -> {
                        stringRedisTemplate.opsForValue().set("allocation:" + productCode, totalQuantity.toString(), 8, TimeUnit.HOURS);
                });

                return true;
        }

        public double getAvailableStock(String productCode) {
                DistributionInventory existingRecord = lambdaQuery()
                        .eq(DistributionInventory::getProductCode, productCode)
                        .one();

                if (existingRecord != null) {
                        return existingRecord.getQuantity();
                } else {
                        return 0;
                }
        }

        @Override
        @Transactional
        public boolean decreaseStock(String goodsCode, Double quantity) {
                DistributionInventory existingRecord = lambdaQuery()
                        .eq(DistributionInventory::getProductCode, goodsCode)
                        .one();

                existingRecord.setQuantity(existingRecord.getQuantity() - quantity);
                existingRecord.setUpdatedTime(LocalDateTime.now());

                boolean update = update().setSql("quantity = quantity-" + quantity).eq("product_code", goodsCode)
                        .gt("quantity", 0).update();

                return update ? true : false;

        }

        @Override
        public AjaxResult getAssginmentlist() {

                List<AssignmentVO> list = distributionInventoryMapper.getAssginmentlist();

                return AjaxResult.success(list);
        }

        @Override
        public AjaxResult getMustFruits(String deptCode) {

                List<MustFruitsVO> mustFruits = distributionInventoryMapper.getMustFruits(deptCode);

                return AjaxResult.success(mustFruits);
        }

        @Override
        public AjaxResult importDistribution(List<AssignmentVO> distributionInventories, boolean updateSupport, String operName) {

                // 将 AssignmentVO 转换为 DistributionInventory
                List<DistributionInventory> distributionInventoryList = distributionInventories.stream()
                        .map(assignmentVO -> {
                                DistributionInventory distributionInventory = new DistributionInventory();
                                distributionInventory.setAllocationId(assignmentVO.getAllocationId());
                                distributionInventory.setProductCode(assignmentVO.getProductCode());
                                distributionInventory.setQuantity(assignmentVO.getQuantity());
                                distributionInventory.setType(assignmentVO.getType());
                                return distributionInventory;
                        })
                        .collect(Collectors.toList());

                // 更新 DistributionInventory 对象的字段
                distributionInventoryList = distributionInventoryList.stream()
                        .peek(d -> {
                                d.setCreatedTime(LocalDateTime.now()); // Set created_time to current time
                                d.setCreatedBy(operName); // Set created_by to the operName parameter
                                d.setUpdatedTime(LocalDateTime.now()); // Set updated_time to current time
                                d.setUpdatedBy(operName); // Set updated_by to the operName parameter
                                d.setDelFlag(0); // Set del_flag to 0
                        })
                        .collect(Collectors.toList());

                // 保存更新后的 DistributionInventory 对象
                saveBatch(distributionInventoryList);

                return AjaxResult.success("上传成功");

        }

}
