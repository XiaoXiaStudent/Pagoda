package org.javaboy.pagoda.ordermaster.mapper;

import org.apache.ibatis.annotations.Param;
import org.javaboy.pagoda.ordermaster.entity.GoodsInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.javaboy.pagoda.ordermaster.vo.DistributeSendR;
import org.javaboy.pagoda.ordermaster.vo.DistributeSendVO;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoR;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;

import java.util.List;

/**
 * <p>
 * 订货系统商品表 Mapper 接口
 * </p>
 *
 * @author javaboy
 * @since 2023-04-23
 */
public interface GoodsInfoMapper extends BaseMapper<GoodsInfo> {

        List<GoodInfoR> getGoodBaseInfoList(GoodInfoVO goodsInfo);

        List<GoodInfoVO> getPriceAndQuantity(GoodInfoVO goodsInfo);

        List<GoodsInfo> findByGoodsCodes(@Param("goodsCodes") List<String> goodsCodes);

        List<DistributeSendR> distributeSendGoofInfos(DistributeSendVO distributeSendVO);
}
