package org.javaboy.pagoda.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.constant.UserConstants;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.domain.entity.SysRole;
import org.javaboy.pagoda.common.core.domain.entity.SysUser;
import org.javaboy.pagoda.common.core.domain.model.LoginUser;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.StringUtils;
import org.javaboy.pagoda.common.utils.poi.ExcelUtil;

import org.javaboy.pagoda.framework.web.service.SysPermissionService;

import org.javaboy.pagoda.framework.web.service.TokenService;
import org.javaboy.pagoda.system.domain.SysUserRole;
import org.javaboy.pagoda.system.service.ISysRoleService;
import org.javaboy.pagoda.system.service.ISysUserService;
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
 * 角色信息
 *
 * @author pagoda
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {
        @Autowired
        private ISysRoleService roleService;

        @Autowired
        private TokenService tokenService;

        @Autowired
        private SysPermissionService permissionService;

        @Autowired
        private ISysUserService userService;

        @PreAuthorize("hasPermissions('system:role:list')")
        @GetMapping("/list")
        public TableDataInfo list(SysRole role) {
                startPage();
                List<SysRole> list = roleService.selectRoleList(role);
                return getDataTable(list);
        }

        @Log(title = "角色管理", businessType = BusinessType.EXPORT)
        @PreAuthorize("hasPermissions('system:role:export')")
        @PostMapping("/export")
        public void export(HttpServletResponse response, SysRole role) {
                List<SysRole> list = roleService.selectRoleList(role);
                ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
                util.exportExcel(response, list, "角色数据");
        }

        /**
         * 根据角色编号获取详细信息
         */
        @PreAuthorize("hasPermissions('system:role:query')")
        @GetMapping(value = "/{roleId}")
        public AjaxResult getInfo(@PathVariable Long roleId) {
                roleService.checkRoleDataScope(roleId);
                return AjaxResult.success(roleService.selectRoleById(roleId));
        }

        /**
         * 新增角色
         */
        @PreAuthorize("hasPermissions('system:role:add')")
        @Log(title = "角色管理", businessType = BusinessType.INSERT)
        @PostMapping
        public AjaxResult add(@Validated @RequestBody SysRole role) {
                if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
                        return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
                } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
                        return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
                }
                role.setCreateBy(getUsername());
                return toAjax(roleService.insertRole(role));

        }

        /**
         * 修改保存角色
         */
        @PreAuthorize("hasPermissions('system:role:edit')")
        @Log(title = "角色管理", businessType = BusinessType.UPDATE)
        @PutMapping
        public AjaxResult edit(@Validated @RequestBody SysRole role) {
                roleService.checkRoleAllowed(role);
                roleService.checkRoleDataScope(role.getRoleId());
                if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
                        return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
                } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
                        return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
                }
                role.setUpdateBy(getUsername());

                if (roleService.updateRole(role) > 0) {
                        // 更新缓存用户权限
                        LoginUser loginUser = getLoginUser();
                        if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin()) {
                                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                                tokenService.setLoginUser(loginUser);
                        }
                        return AjaxResult.success();
                }
                return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
        }

        /**
         * 修改保存数据权限
         */
        @PreAuthorize("hasPermissions('system:role:edit')")
        @Log(title = "角色管理", businessType = BusinessType.UPDATE)
        @PutMapping("/dataScope")
        public AjaxResult dataScope(@RequestBody SysRole role) {
                roleService.checkRoleAllowed(role);
                roleService.checkRoleDataScope(role.getRoleId());
                return toAjax(roleService.authDataScope(role));
        }

        /**
         * 状态修改
         */
        @PreAuthorize("hasPermissions('system:role:edit')")
        @Log(title = "角色管理", businessType = BusinessType.UPDATE)
        @PutMapping("/changeStatus")
        public AjaxResult changeStatus(@RequestBody SysRole role) {
                roleService.checkRoleAllowed(role);
                roleService.checkRoleDataScope(role.getRoleId());
                role.setUpdateBy(getUsername());
                return toAjax(roleService.updateRoleStatus(role));
        }

        /**
         * 删除角色
         */
        @PreAuthorize("hasPermissions('system:role:remove')")
        @Log(title = "角色管理", businessType = BusinessType.DELETE)
        @DeleteMapping("/{roleIds}")
        public AjaxResult remove(@PathVariable Long[] roleIds) {
                return toAjax(roleService.deleteRoleByIds(roleIds));
        }

        /**
         * 获取角色选择框列表
         */
        @PreAuthorize("hasPermissions('system:role:query')")
        @GetMapping("/optionselect")
        public AjaxResult optionselect() {
                return AjaxResult.success(roleService.selectRoleAll());
        }

        /**
         * 查询已分配用户角色列表
         */
        @PreAuthorize("hasPermissions('system:role:list')")
        @GetMapping("/authUser/allocatedList")
        public TableDataInfo allocatedList(SysUser user) {
                startPage();
                List<SysUser> list = userService.selectAllocatedList(user);
                return getDataTable(list);
        }

        /**
         * 查询未分配用户角色列表
         */
        @PreAuthorize("hasPermissions('system:role:list')")
        @GetMapping("/authUser/unallocatedList")
        public TableDataInfo unallocatedList(SysUser user) {
                startPage();
                List<SysUser> list = userService.selectUnallocatedList(user);
                return getDataTable(list);
        }

        /**
         * 取消授权用户
         */
        @PreAuthorize("hasPermissions('system:role:edit')")
        @Log(title = "角色管理", businessType = BusinessType.GRANT)
        @PutMapping("/authUser/cancel")
        public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole) {
                return toAjax(roleService.deleteAuthUser(userRole));
        }

        /**
         * 批量取消授权用户
         */
        @PreAuthorize("hasPermissions('system:role:edit')")
        @Log(title = "角色管理", businessType = BusinessType.GRANT)
        @PutMapping("/authUser/cancelAll")
        public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds) {
                return toAjax(roleService.deleteAuthUsers(roleId, userIds));
        }

        /**
         * 批量选择用户授权
         */
        @PreAuthorize("hasPermissions('system:role:edit')")
        @Log(title = "角色管理", businessType = BusinessType.GRANT)
        @PutMapping("/authUser/selectAll")
        public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds) {
                roleService.checkRoleDataScope(roleId);
                return toAjax(roleService.insertAuthUsers(roleId, userIds));
        }
}
