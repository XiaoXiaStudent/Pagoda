package org.javaboy.pagoda.ordermaster.mapper;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.ordermaster.entity.DistributionInventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.javaboy.pagoda.ordermaster.vo.AssignmentVO;
import org.javaboy.pagoda.ordermaster.vo.MustFruitsVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author javaboy
 * @since 2023-04-26
 */
public interface DistributionInventoryMapper extends BaseMapper<DistributionInventory> {



        List<MustFruitsVO> getMustFruits(String deptCode);

        List<AssignmentVO> getAssginmentlist();

}
