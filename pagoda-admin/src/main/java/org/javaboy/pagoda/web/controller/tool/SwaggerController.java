package org.javaboy.pagoda.web.controller.tool;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.javaboy.pagoda.common.core.controller.BaseController;

/**
 * swagger 接口
 *
 * @author pagoda
 */
@Controller
@RequestMapping("/tool/swagger")
public class SwaggerController extends BaseController {
        @PreAuthorize("hasPermissions('tool:swagger:view')")
        @GetMapping()
        public String index() {
                return redirect("/swagger-ui.html");
        }
}
