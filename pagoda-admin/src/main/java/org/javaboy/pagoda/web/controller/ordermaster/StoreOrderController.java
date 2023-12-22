package org.javaboy.pagoda.web.controller.ordermaster;

import org.javaboy.pagoda.common.annotation.DataScope;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.ordermaster.entity.StoreInfo;
import org.javaboy.pagoda.ordermaster.service.*;
import org.javaboy.pagoda.ordermaster.service.impl.ShoppingCartServiceImpl;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ordermaster/storeorder")
public class StoreOrderController extends BaseController {

        @Resource
        IAllocationInventoryService allocationInventoryService;

        @Resource
        IStoreInfoService storeInfoService;

        @Resource
        IShoppingCartService shoppingCartService;

        @Resource
        IOrdersService ordersService;

        @Resource
        IDistributionInventoryService distributionInventoryService;

        /**
         * 门店订货果品清单,果品标签为3的也就是无的
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:storeorder:list')")
        @PostMapping("/list/{deptCode}")
        public AjaxResult list(@PathVariable String deptCode) {

                return allocationInventoryService.getStoreOrderList(deptCode);
        }

        /**
         * 门店必上果品清单 果品标签是1的和果品标签是2的
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:storeorder:list')")
        @PostMapping("/must/list/{deptCode}")
        public AjaxResult mustFruits(@PathVariable String deptCode) {

                return distributionInventoryService.getMustFruits(deptCode);
        }

        /**
         * 门店代码 +门店名称
         *
         * @param
         * @return
         */
        @GetMapping("/storeinfo")
        public AjaxResult storeinfo(StoreInfo storeInfo) {

                return AjaxResult.success(storeInfoService.getStores(storeInfo));
        }

        /**
         * 门店代码 +门店名称 分页展示
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:storeorder:list')")
        @GetMapping("/storeinfo/page")
        public TableDataInfo storeinfoByPage(StoreInfo storeInfo) {

                startPage();
                List<StoreInfo> list = storeInfoService.storeinfoByPage(storeInfo);
                return getDataTable(list);
        }

        /**
         * 添加商品到购物车中
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:storeorder:list')")
        @PostMapping("/addcart")
        public AjaxResult addShoppingCart(@RequestBody GoodInfoVO goodInfoVO) {

                return shoppingCartService.manageShoppingCart(goodInfoVO);
        }

        /**
         * 门店执行购物车保存操作,也就是执行下单操作
         */
        @PreAuthorize("hasPermissions('pagoda:storeorder:list')")
        @PostMapping("/order/{storeCode}")
        public AjaxResult order(@PathVariable String storeCode) {

                return ordersService.order(storeCode);
        }

}
