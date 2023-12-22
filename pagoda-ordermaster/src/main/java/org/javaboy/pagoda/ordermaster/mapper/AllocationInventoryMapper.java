package org.javaboy.pagoda.ordermaster.mapper;

import org.javaboy.pagoda.ordermaster.entity.AllocationInventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.javaboy.pagoda.ordermaster.vo.AllocationVO;
import org.javaboy.pagoda.ordermaster.vo.StoreOrderVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author javaboy
 * @since 2023-04-20
 */
public interface AllocationInventoryMapper extends BaseMapper<AllocationInventory> {

        List<AllocationVO> getInventoryList(AllocationInventory allocationInventory);

        List<StoreOrderVO> getStoreOrderList(String deptCode);

}
