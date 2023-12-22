package org.javaboy.pagoda.web.controller.system;

import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.domain.model.RegisterBody;
import org.javaboy.pagoda.common.utils.StringUtils;
import org.javaboy.pagoda.framework.web.service.SysRegisterService;
import org.javaboy.pagoda.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册验证
 *
 * @author pagoda
 */
@RestController
public class SysRegisterController extends BaseController {
        @Autowired
        private SysRegisterService registerService;

        @Autowired
        private ISysConfigService configService;

        @PostMapping("/register")
        public AjaxResult register(@RequestBody RegisterBody user) {
                if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
                        return error("当前系统没有开启注册功能！");
                }
                String msg = registerService.register(user);
                return StringUtils.isEmpty(msg) ? success() : error(msg);
        }
}
