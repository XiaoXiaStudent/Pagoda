package org.javaboy.pagoda.web.controller.ordermaster;

import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.poi.ExcelUtil;
import org.javaboy.pagoda.ordermaster.service.IDistributionInventoryService;
import org.javaboy.pagoda.ordermaster.vo.AssignmentVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 片区经理指定分货表
 */

@RestController
@RequestMapping("/ordermaster/assignment")
public class AssignmentController extends BaseController {

        @Resource
        IDistributionInventoryService distributionInventoryService;

        /**
         * 门店当日分货果品清单
         *
         * @param
         * @return
         */
        @PreAuthorize("hasPermissions('pagoda:allocation:list')")
        @PostMapping("/list")
        public AjaxResult list() {

                return distributionInventoryService.getAssginmentlist();
        }

        @Log(title = "配送分货库存", businessType = BusinessType.IMPORT)
        @PreAuthorize("hasPermissions('pagoda:purchase:import')")
        @PostMapping("/importData")
        public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
                ExcelUtil<AssignmentVO> util = new ExcelUtil<>(AssignmentVO.class);
                List<AssignmentVO> distributionInventories = util.importExcel(file.getInputStream());
                String operName = getUsername();
                return distributionInventoryService.importDistribution(distributionInventories, updateSupport, operName);

        }

        @PostMapping("/importTemplate")
        public void importTemplate(HttpServletResponse response) {
                ExcelUtil<AssignmentVO> util = new ExcelUtil<AssignmentVO>(AssignmentVO.class);
                // 创建一个空的数据列表，填充你要显示的数据
                List<AssignmentVO> dataList = new ArrayList<>();
                AssignmentVO assignmentVO = new AssignmentVO();
                assignmentVO.setProductCode("10001");
                assignmentVO.setType(1);
                assignmentVO.setQuantity(100.0);

                dataList.add(assignmentVO);

                util.exportExcel(response, dataList, "分货上传模板");
        }
        //
        //@PreAuthorize("hasPermissions('pagoda:allocation:remove')")
        //@Log(title = "配送分货库存", businessType = BusinessType.DELETE)
        //@DeleteMapping("/{productCodes}")
        //public AjaxResult remove(@PathVariable Long[] productCodes) {
        //        return toAjax(allocationInventoryService.deleteAllocationInventoryByProductCodes(productCodes));
        //}

}
