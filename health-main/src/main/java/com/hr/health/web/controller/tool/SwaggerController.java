package com.hr.health.web.controller.tool;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.hr.health.common.core.controller.BaseController;

/**
 * swagger 接口
 *
 * @author swq
 */
@Controller
@RequestMapping("/tool/swagger")
public class SwaggerController extends BaseController {
    @PreAuthorize("@ss.hasPermi('tool:swagger:view')")
    @GetMapping("/index")
    public String index() {
        return redirect("/doc.html");
    }
}
