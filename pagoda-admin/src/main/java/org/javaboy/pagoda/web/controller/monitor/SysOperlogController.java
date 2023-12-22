package org.javaboy.pagoda.web.controller.monitor;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.poi.ExcelUtil;
import org.javaboy.pagoda.system.domain.SysOperLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.system.service.ISysOperLogService;

/**
 * 操作日志记录
 *
 * @author pagoda
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {
        @Autowired
        private ISysOperLogService operLogService;

        @PreAuthorize("hasPermissions('monitor:operlog:list')")
        @GetMapping("/list")
        public TableDataInfo list(SysOperLog operLog) {
                startPage();
                List<SysOperLog> list = operLogService.selectOperLogList(operLog);
                return getDataTable(list);
        }

        @Log(title = "操作日志", businessType = BusinessType.EXPORT)
        @PreAuthorize("hasPermissions('monitor:operlog:export')")
        @PostMapping("/export")
        public void export(HttpServletResponse response, SysOperLog operLog) {
                List<SysOperLog> list = operLogService.selectOperLogList(operLog);
                ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
                util.exportExcel(response, list, "操作日志");
        }

        @Log(title = "操作日志", businessType = BusinessType.DELETE)
        @PreAuthorize("hasPermissions('monitor:operlog:remove')")
        @DeleteMapping("/{operIds}")
        public AjaxResult remove(@PathVariable Long[] operIds) {
                return toAjax(operLogService.deleteOperLogByIds(operIds));
        }

        @Log(title = "操作日志", businessType = BusinessType.CLEAN)
        @PreAuthorize("hasPermissions('monitor:operlog:remove')")
        @DeleteMapping("/clean")
        public AjaxResult clean() {
                operLogService.cleanOperLog();
                return AjaxResult.success();
        }
}
