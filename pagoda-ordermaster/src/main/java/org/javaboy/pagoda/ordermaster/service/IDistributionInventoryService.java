package org.javaboy.pagoda.ordermaster.service;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.ordermaster.entity.AllocationInventory;
import org.javaboy.pagoda.ordermaster.entity.DistributionInventory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaboy.pagoda.ordermaster.vo.AssignmentVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-26
 */
public interface IDistributionInventoryService extends IService<DistributionInventory> {

        boolean insertTodayInventory(List<AllocationInventory> inventoryList,String username);

        double getAvailableStock(String goodsCode);

        boolean decreaseStock(String goodsCode, Double quantity);

        AjaxResult getAssginmentlist();

        AjaxResult getMustFruits(String deptCode);

        AjaxResult importDistribution(List<AssignmentVO> distributionInventories, boolean updateSupport, String operName);
}
