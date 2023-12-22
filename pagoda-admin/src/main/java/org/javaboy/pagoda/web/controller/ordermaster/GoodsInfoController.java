package org.javaboy.pagoda.web.controller.ordermaster;


import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.ordermaster.entity.GoodsInfo;
import org.javaboy.pagoda.ordermaster.service.IGoodsInfoService;
import org.javaboy.pagoda.ordermaster.vo.DistributeSendVO;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoR;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 订货系统商品表 前端控制器
 * </p>
 *
 * @author javaboy
 * @since 2023-04-23
 */
@RestController
@RequestMapping("/ordermaster/goods-info")
public class GoodsInfoController extends BaseController {

        @Resource
        IGoodsInfoService goodsInfoService;

        /**
         * 展示商品文员维护商品基础的信息
         *
         * @param goodInfoVO
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @GetMapping("/list")
        public TableDataInfo list( GoodInfoVO goodInfoVO) {
                startPage();
                List<GoodInfoR> list = goodsInfoService.getGoodInfoList(goodInfoVO);
                return getDataTable(list);
        }



        /**
         * 获取跟据id的商品信息
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @PostMapping("/list/{goodsCode}")
        public AjaxResult getGoodInfoByGoodCode(@PathVariable String goodsCode) {

                return AjaxResult.success(goodsInfoService.getGoodInfoByGoodCode(goodsCode));
        }



        /**
         * 跟据果品id  添加果品配送价格或最小订货量
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @PostMapping("/addadvance")
        public AjaxResult addPriceAndQuantityGoods(@RequestBody GoodInfoVO goodInfoVO) {

                return goodsInfoService.addPriceAndQuantityGoods(goodInfoVO);
        }

        /**
         * 修改果品基础信息
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @PutMapping
        public AjaxResult updateGoodsBaseInfo(@RequestBody GoodsInfo goodsInfo) {

                return  goodsInfoService.updateGoodsBaseInfo(goodsInfo);
        }

        /**
         * 跟据果品id  添加订货标签
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @PostMapping("/addrelate")
        public AjaxResult addRelatedGoods(@RequestBody GoodInfoVO goodInfoVO) {

                return goodsInfoService.addRelatedGoods(goodInfoVO);
        }

        /**
         * 配送获取所有门店的订货信息
         */
        /**
         * 配送发货单明细
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @PostMapping("/distribute/list")
        public AjaxResult distributeSendGoofInfos(DistributeSendVO distributeSendVO) {

                return AjaxResult.success(goodsInfoService.distributeSendGoofInfos(distributeSendVO));
        }
}



