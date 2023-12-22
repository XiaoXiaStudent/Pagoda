package org.javaboy.pagoda.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.redis.RedisCache;
import org.javaboy.pagoda.system.domain.SysUserOnline;
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
                        LoginUser user = redisCache.getCacheObject(key);
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
