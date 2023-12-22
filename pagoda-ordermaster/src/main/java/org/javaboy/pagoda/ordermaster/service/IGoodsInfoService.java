package org.javaboy.pagoda.ordermaster.service;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.ordermaster.entity.GoodsInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaboy.pagoda.ordermaster.vo.DistributeSendR;
import org.javaboy.pagoda.ordermaster.vo.DistributeSendVO;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoR;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;

import java.util.List;

/**
 * <p>
 * 订货系统商品表 服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-23
 */
public interface IGoodsInfoService extends IService<GoodsInfo> {

        List<GoodInfoR> getGoodInfoList(GoodInfoVO goodsInfo);

        GoodsInfo getGoodInfoByGoodCode(String goodsCode);


        AjaxResult addPriceAndQuantityGoods(GoodInfoVO goodInfoVO);

        AjaxResult updateGoodsBaseInfo(GoodsInfo goodsInfo);

        AjaxResult addRelatedGoods(GoodInfoVO goodInfoVO);

        List<DistributeSendR> distributeSendGoofInfos(DistributeSendVO distributeSendVO);
}
