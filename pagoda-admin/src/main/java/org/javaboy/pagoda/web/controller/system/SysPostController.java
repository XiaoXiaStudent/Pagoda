package org.javaboy.pagoda.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.constant.UserConstants;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.poi.ExcelUtil;
import org.javaboy.pagoda.system.domain.SysPost;
import org.javaboy.pagoda.system.service.ISysPostService;
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
 * 岗位信息操作处理
 *
 * @author pagoda
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {
        @Autowired
        private ISysPostService postService;

        /**
         * 获取岗位列表
         */
        @PreAuthorize("hasPermissions('system:post:list')")
        @GetMapping("/list")
        public TableDataInfo list(SysPost post) {
                startPage();
                List<SysPost> list = postService.selectPostList(post);
                return getDataTable(list);
        }

        @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
        @PreAuthorize("hasPermissions('system:post:export')")
        @PostMapping("/export")
        public void export(HttpServletResponse response, SysPost post) {
                List<SysPost> list = postService.selectPostList(post);
                ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
                util.exportExcel(response, list, "岗位数据");
        }

        /**
         * 根据岗位编号获取详细信息
         */
        @PreAuthorize("hasPermissions('system:post:query')")
        @GetMapping(value = "/{postId}")
        public AjaxResult getInfo(@PathVariable Long postId) {
                return AjaxResult.success(postService.selectPostById(postId));
        }

        /**
         * 新增岗位
         */
        @PreAuthorize("hasPermissions('system:post:add')")
        @Log(title = "岗位管理", businessType = BusinessType.INSERT)
        @PostMapping
        public AjaxResult add(@Validated @RequestBody SysPost post) {
                if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
                        return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
                } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
                        return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
                }
                post.setCreateBy(getUsername());
                return toAjax(postService.insertPost(post));
        }

        /**
         * 修改岗位
         */
        @PreAuthorize("hasPermissions('system:post:edit')")
        @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
        @PutMapping
        public AjaxResult edit(@Validated @RequestBody SysPost post) {
                if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
                        return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
                } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
                        return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
                }
                post.setUpdateBy(getUsername());
                return toAjax(postService.updatePost(post));
        }

        /**
         * 删除岗位
         */
        @PreAuthorize("hasPermissions('system:post:remove')")
        @Log(title = "岗位管理", businessType = BusinessType.DELETE)
        @DeleteMapping("/{postIds}")
        public AjaxResult remove(@PathVariable Long[] postIds) {
                return toAjax(postService.deletePostByIds(postIds));
        }

        /**
         * 获取岗位选择框列表
         */
        @GetMapping("/optionselect")
        public AjaxResult optionselect() {
                List<SysPost> posts = postService.selectPostAll();
                return AjaxResult.success(posts);
        }
}
