package org.javaboy.pagoda.web.controller.system;

import java.util.List;

import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.system.domain.SysNotice;
import org.javaboy.pagoda.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告 信息操作处理
 *
 * @author pagoda
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {
        @Autowired
        private ISysNoticeService noticeService;

        /**
         * 获取通知公告列表
         */
        @PreAuthorize("hasPermissions('system:notice:list')")
        @GetMapping("/list")
        public TableDataInfo list(SysNotice notice) {
                startPage();
                List<SysNotice> list = noticeService.selectNoticeList(notice);
                return getDataTable(list);
        }

        /**
         * 根据通知公告编号获取详细信息
         */
        @PreAuthorize("hasPermissions('system:notice:query')")
        @GetMapping(value = "/{noticeId}")
        public AjaxResult getInfo(@PathVariable Long noticeId) {
                return AjaxResult.success(noticeService.selectNoticeById(noticeId));
        }

        /**
         * 新增通知公告
         */
        @PreAuthorize("hasPermissions('system:notice:add')")
        @Log(title = "通知公告", businessType = BusinessType.INSERT)
        @PostMapping
        public AjaxResult add(@Validated @RequestBody SysNotice notice) {
                notice.setCreateBy(getUsername());
                return toAjax(noticeService.insertNotice(notice));
        }

        /**
         * 修改通知公告
         */
        @PreAuthorize("hasPermissions('system:notice:edit')")
        @Log(title = "通知公告", businessType = BusinessType.UPDATE)
        @PutMapping
        public AjaxResult edit(@Validated @RequestBody SysNotice notice) {
                notice.setUpdateBy(getUsername());
                return toAjax(noticeService.updateNotice(notice));
        }

        /**
         * 删除通知公告
         */
        @PreAuthorize("hasPermissions('system:notice:remove')")
        @Log(title = "通知公告", businessType = BusinessType.DELETE)
        @DeleteMapping("/{noticeIds}")
        public AjaxResult remove(@PathVariable Long[] noticeIds) {
                return toAjax(noticeService.deleteNoticeByIds(noticeIds));
        }
}
