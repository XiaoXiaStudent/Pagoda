package org.javaboy.pagoda.ordermaster.service;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.ordermaster.entity.AllocationInventory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaboy.pagoda.ordermaster.vo.AllocationVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-20
 */
public interface IAllocationInventoryService extends IService<AllocationInventory> {

        List<AllocationVO> getInventoryList(AllocationInventory allocationInventory);

        String importAllocation(List<AllocationInventory> allocationVoList, boolean updateSupport, String operName);

        int deleteAllocationInventoryByProductCodes(Long[] productCodes);

        AjaxResult getStoreOrderList(String deptCode);

        AjaxResult getMustFruits(String deptCode);
}
