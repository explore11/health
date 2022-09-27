package com.hr.health.web.controller.monitor;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.page.TableDataInfo;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.framework.web.service.SysPasswordService;
import com.hr.health.system.domain.SysLogininfor;
import com.hr.health.system.service.ISysLogininforService;
import com.hr.health.system.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统访问记录
 *
 * @author swq
 */
@Api(tags = "登录访问记录")
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {
    @Autowired
    private ISysLogininforService logininforService;

    /**
     * 查询登录日志列表
     *
     * @param logininfor
     * @return
     */
    @ApiOperation("查询登录日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public Result<TableDataInfo> list(SysLogininfor logininfor) {
        startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return Result.success(getDataTable(list));
    }

    /**
     * 导出
     *
     * @param response
     * @param logininfor
     */
    @ApiOperation("导出")
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininfor logininfor) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(response, list, "登录日志");
    }

    /**
     * 删除
     * @param infoIds
     * @return
     */
    @ApiOperation("删除")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public Result remove(@PathVariable Long[] infoIds) {
        return Result.judge(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清除
     * @return
     */
    @ApiOperation("清除")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public Result clean() {
        logininforService.cleanLogininfor();
        return Result.success();
    }
}
