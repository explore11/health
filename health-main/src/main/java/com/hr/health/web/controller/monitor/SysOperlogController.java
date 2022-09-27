package com.hr.health.web.controller.monitor;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.page.TableDataInfo;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.system.domain.SysOperLog;
import com.hr.health.system.service.ISysOperLogService;
import com.hr.health.system.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 *
 * @author swq
 */
@Api(tags = "操作日志记录")
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {
    @Autowired
    private ISysOperLogService operLogService;

    /**
     * 查询操作日志列表
     *
     * @param operLog
     * @return
     */
    @ApiOperation("查询操作日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public Result<TableDataInfo> list(SysOperLog operLog) {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return Result.success(getDataTable(list));
    }

    /**
     * 导出
     *
     * @param response
     * @param operLog
     */
    @ApiOperation("导出")
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLog operLog) {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    /**
     * 删除
     *
     * @param operIds
     * @return
     */
    @ApiOperation("删除")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public Result remove(@PathVariable Long[] operIds) {
        return Result.judge(operLogService.deleteOperLogByIds(operIds));
    }

    /**
     * 清除
     *
     * @return
     */
    @ApiOperation("清除")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public Result clean() {
        operLogService.cleanOperLog();
        return Result.success();
    }
}
