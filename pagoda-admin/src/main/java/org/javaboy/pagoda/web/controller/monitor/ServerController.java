package org.javaboy.pagoda.web.controller.monitor;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.framework.web.domain.Server;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author pagoda
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {
        @PreAuthorize("hasPermissions('monitor:server:list')")
        @GetMapping()
        public AjaxResult getInfo() throws Exception {
                Server server = new Server();
                server.copyTo();
                return AjaxResult.success(server);
        }
}
