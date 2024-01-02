package org.javaboy.pagoda.web.controller.monitor;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.domain.entity.SysDept;
import org.javaboy.pagoda.common.core.domain.entity.SysUser;
import org.javaboy.pagoda.common.core.redis.RedisCache;
import org.javaboy.pagoda.system.domain.SysUserOnline;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.javaboy.pagoda.common.constant.Constants;
import org.javaboy.pagoda.common.core.domain.model.LoginUser;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.StringUtils;
import org.javaboy.pagoda.system.service.ISysUserOnlineService;

/**
 * 在线用户监控
 *
 * @author pagoda
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {
        @Autowired
        private ISysUserOnlineService userOnlineService;

        @Autowired
        private RedisCache redisCache;

        @PreAuthorize("hasPermissions('monitor:online:list')")
        @GetMapping("/list")
        public TableDataInfo list(String ipaddr, String userName) {
                Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
                List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
                for (String key : keys) {


                        LinkedHashMap userMap = (LinkedHashMap) redisCache.getCacheObject(key);
                        LoginUser user = mapToLoginUser(userMap);


                        if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
                                        userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                                }
                        } else if (StringUtils.isNotEmpty(ipaddr)) {
                                if (StringUtils.equals(ipaddr, user.getIpaddr())) {
                                        userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                                }
                        } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
                                if (StringUtils.equals(userName, user.getUsername())) {
                                        userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                                }
                        } else {
                                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
                        }
                }
                Collections.reverse(userOnlineList);
                userOnlineList.removeAll(Collections.singleton(null));
                return getDataTable(userOnlineList);
        }

        public LoginUser mapToLoginUser(LinkedHashMap<String, Object> userData) {
                LoginUser loginUser = new LoginUser();

                // 直接映射LoginUser的基本属性
                loginUser.setUserId(userData.get("userId") != null ? ((Integer) userData.get("userId")).longValue() : null);
                loginUser.setDeptId(userData.get("deptId") != null ? ((Integer) userData.get("deptId")).longValue() : null);
                loginUser.setToken((String) userData.get("token")); // Assuming token is always a String
                loginUser.setLoginTime(userData.get("loginTime") != null ? (Long) userData.get("loginTime") : null);
                loginUser.setExpireTime(userData.get("expireTime") != null ?(Long) userData.get("expireTime") : null);
                loginUser.setIpaddr((String) userData.get("ipaddr")); // Assuming ipaddr is always a String
                loginUser.setLoginLocation((String) userData.get("loginLocation")); // Assuming loginLocation is always a String
                loginUser.setBrowser((String) userData.get("browser")); // Assuming browser is always a String
                loginUser.setOs((String) userData.get("os")); // Assuming os is always a String

                // 特殊处理permissions，假设permissions是权限名称的集合
                Set<String> permissions = ((List<String>) userData.get("permissions"))
                        .stream()
                        .collect(Collectors.toSet());
                loginUser.setPermissions(permissions);

                // 特殊处理嵌套的SysUser对象
                LinkedHashMap<String, Object> sysUserData = (LinkedHashMap<String, Object>) userData.get("user");
                SysUser sysUser = mapToSysUser(sysUserData);
                loginUser.setUser(sysUser);

                // 设置其他的LoginUser特定方法...
                // loginUser.setSomeOtherProperty(...);

                return loginUser;
        }

        public SysUser mapToSysUser(LinkedHashMap<String, Object> sysUserData) {
                SysUser sysUser = new SysUser();

                // 映射SysUser的属性
                // 映射SysUser的属性
                sysUser.setUserId(sysUserData.get("userId") != null ? ((Integer) sysUserData.get("userId")).longValue() : null);
                sysUser.setDeptId(sysUserData.get("deptId") != null ? ((Integer) sysUserData.get("deptId")).longValue() : null);
                sysUser.setUserName(String.valueOf(sysUserData.get("userName"))); // Assuming userName is always a String
                sysUser.setNickName(String.valueOf(sysUserData.get("nickName"))); // Assuming nickName is always a String
                sysUser.setEmail(String.valueOf(sysUserData.get("email"))); // Assuming email is always a String
                sysUser.setPhonenumber(String.valueOf(sysUserData.get("phonenumber"))); // Assuming phonenumber is always a String
                sysUser.setSex(String.valueOf(sysUserData.get("sex"))); // Assuming sex is always a String
                sysUser.setAvatar(String.valueOf(sysUserData.get("avatar"))); // Assuming avatar is always a String
                sysUser.setStatus(String.valueOf(sysUserData.get("status"))); // Assuming status is always a String
                sysUser.setDelFlag(String.valueOf(sysUserData.get("delFlag"))); // Assuming delFlag is always a String
                sysUser.setLoginIp(String.valueOf(sysUserData.get("loginIp"))); // Assuming loginIp is always a String
                sysUser.setLoginDate(parseDate(sysUserData.get("loginDate"))); // loginDate needs to be parsed from timestamp

                LinkedHashMap<String, Object> deptData = (LinkedHashMap<String, Object>) sysUserData.get("dept");
                if (deptData != null) {
                        SysDept dept = mapToSysDept(deptData);
                        sysUser.setDept(dept);
                }

                return sysUser;
        }

        public SysDept mapToSysDept(LinkedHashMap<String, Object> deptData) {
                SysDept dept = new SysDept();

                dept.setDeptId(deptData.get("deptId") != null ? Long.valueOf(((Integer) deptData.get("deptId")).longValue()) : null);
                dept.setParentId(deptData.get("parentId") != null ? Long.valueOf(((Integer) deptData.get("parentId")).longValue()) : null);
                dept.setDeptName((String) deptData.get("deptName")); // Assuming deptName is always a String
                dept.setOrderNum(deptData.get("orderNum") != null ? Integer.valueOf(deptData.get("orderNum").toString()) : null);
                dept.setLeader((String) deptData.get("leader")); // Assuming leader is always a String

                // ...映射其他属性

                return dept;
        }

        private Date parseDate(Object epoch) {
                if (epoch != null) {
                        long time = Long.parseLong(epoch.toString());
                        return new Date(time);
                }
                return null;
        }

        /**
         * 强退用户
         */
        @PreAuthorize("hasPermissions('monitor:online:forceLogout')")
        @Log(title = "在线用户", businessType = BusinessType.FORCE)
        @DeleteMapping("/{tokenId}")
        public AjaxResult forceLogout(@PathVariable String tokenId) {
                redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + tokenId);
                return AjaxResult.success();
        }
}
