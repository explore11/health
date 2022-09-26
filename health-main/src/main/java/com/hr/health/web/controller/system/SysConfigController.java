package com.hr.health.web.controller.system;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.page.TableDataInfo;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.system.domain.SysConfig;
import com.hr.health.system.service.ISysConfigService;
import com.hr.health.system.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author swq
 */
@RestController
@Api(tags = "参数配置")
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    @ApiOperation("获取参数配置列表")
    public Result<TableDataInfo> list(SysConfig config) {
        // 分页配置
        startPage();
        // 查询数据
        List<SysConfig> list = configService.selectConfigList(config);
        return Result.success(getDataTable(list));
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @PostMapping("/export")
    @ApiOperation("导出")
    public void export(HttpServletResponse response, SysConfig config) {
        // 查询数据
        List<SysConfig> list = configService.selectConfigList(config);
        // 导出
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    @ApiOperation("根据参数编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public Result<SysConfig> getInfo(@PathVariable Long configId) {
        return Result.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @ApiOperation("根据参数键名查询参数值")
    @GetMapping(value = "/configKey/{configKey}")
    public Result<String> getConfigKey(@PathVariable String configKey) {
        return Result.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @ApiOperation("新增参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        }
        config.setCreateBy(getUsername());
        return Result.judge(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @ApiOperation("修改参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        }
        config.setUpdateBy(getUsername());
        return Result.judge(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @ApiOperation("删除参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public Result remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return Result.success();
    }

}
