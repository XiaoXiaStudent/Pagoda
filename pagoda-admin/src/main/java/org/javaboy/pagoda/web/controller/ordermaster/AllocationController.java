package org.javaboy.pagoda.web.controller.ordermaster;


import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.poi.ExcelUtil;
import org.javaboy.pagoda.ordermaster.entity.AllocationInventory;
import org.javaboy.pagoda.ordermaster.service.IAllocationInventoryService;
import org.javaboy.pagoda.ordermaster.vo.AllocationVO;
import org.javaboy.pagoda.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  配送商品表 (门店自由订货管理)
 * </p>
 *
 * @author javaboy
 * @since 2023-04-20
 */
@RestController
@RequestMapping("/ordermaster/allocation")
public class AllocationController extends BaseController {
        @Autowired
        private ISysUserService userService;

        @Resource
        IAllocationInventoryService allocationInventoryService;

        /**
         * 门店当日订货果品清单
         * @param allocationInventory
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @PostMapping("/list")
        public TableDataInfo list( AllocationInventory allocationInventory) {
                startPage();
                List<AllocationVO> list = allocationInventoryService.getInventoryList(allocationInventory);
                return getDataTable(list);
        }



        @Log(title = "配送当日门店订货库存", businessType = BusinessType.IMPORT)
        @PreAuthorize("hasPermissions('pagoda:allocation:import')")
        @PostMapping("/importData")
        public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
                ExcelUtil<AllocationInventory> util = new ExcelUtil<>(AllocationInventory.class);
                List<AllocationInventory> allocationInventoryList = util.importExcel(file.getInputStream());
                String operName = getUsername();
                String message = allocationInventoryService.importAllocation(allocationInventoryList, updateSupport, operName);
                return AjaxResult.success(message);
        }

        @PostMapping("/importTemplate")
        public void importTemplate(HttpServletResponse response) {
                ExcelUtil<AllocationVO> util = new ExcelUtil<AllocationVO>(AllocationVO.class);
                // 创建一个空的数据列表，填充你要显示的数据
                List<AllocationVO> dataList = new ArrayList<>();
                AllocationVO allocation = new AllocationVO();
                allocation.setProductCode("10001");
                allocation.setProductName("商品1");
                allocation.setProductUnit("个");
                allocation.setIoTypeList("出入库类型 (1-在途入库；2-从外区调入；3-库存校准入库；4-其他入库；5-调出到外区；6-库存校准出库；7-其他出库)");
                allocation.setQuantityList("100");

                dataList.add(allocation);


                util.exportExcel(response, dataList,"订货上传模板" );
        }

        @PreAuthorize("hasPermissions('pagoda:allocation:remove')")
        @Log(title = "配送当日门店订货库存", businessType = BusinessType.DELETE)
        @DeleteMapping("/{productCodes}")
        public AjaxResult remove(@PathVariable Long[] productCodes) {
                return toAjax(allocationInventoryService.deleteAllocationInventoryByProductCodes(productCodes));
        }


}
