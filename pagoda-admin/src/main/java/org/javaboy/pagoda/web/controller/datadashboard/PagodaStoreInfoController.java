package org.javaboy.pagoda.web.controller.datadashboard;


import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.datadashboard.entity.PagodaStoreInfo;
import org.javaboy.pagoda.datadashboard.service.IPagodaStoreInfoService;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * <p>
 * 门店信息表 前端控制器
 * </p>
 *
 * @author javaboy
 * @since 2024-01-03
 */
@RestController
@RequestMapping("/datadashboard")
public class PagodaStoreInfoController {

        @Resource
        IPagodaStoreInfoService pagodaStoreInfoService;

        /**
         * 获取门店列表
         * @return
         */
        @GetMapping("/storeList")
        public AjaxResult storeList() {
                return AjaxResult.success(pagodaStoreInfoService.getStoreList());
        }

        @PreAuthorize("hasPermissions('pagoda:storedata:remove')")
        @Log(title = "删除门店信息", businessType = BusinessType.DELETE)
        @DeleteMapping("delStores/{storeCodes}")
        public AjaxResult remove(@PathVariable Long[] storeCodes ) {
                return pagodaStoreInfoService.deleteStoreByNumber(storeCodes);
        }

        @PreAuthorize("hasPermissions('pagoda:storedata:add')")
        @Log(title = "添加门店信息", businessType = BusinessType.INSERT)
        @PostMapping("/addStore")
        public AjaxResult addStore(@RequestBody PagodaStoreInfo  pagodaStoreInfo) {
                return pagodaStoreInfoService.addStore(pagodaStoreInfo);
        }


        @GetMapping("/store")
        public AjaxResult store(String storeCode) {
                return pagodaStoreInfoService.getStoreByCode(storeCode);
        }

        @PreAuthorize("hasPermissions('pagoda:storedata:edit')")
        @Log(title = "更新门店信息", businessType = BusinessType.UPDATE)
        @PutMapping("/updateStore")
        public AjaxResult updateStore(@RequestBody PagodaStoreInfo pagodaStoreInfo) {
                return pagodaStoreInfoService.updateStore(pagodaStoreInfo);
        }



        @PostMapping("/storeByQueryParams/{storeCodes}")
        public AjaxResult getStoreByQueryParams(@PathVariable Long[] storeCodes) {
                return pagodaStoreInfoService.getStoreByQueryParams(storeCodes);
        }

}
