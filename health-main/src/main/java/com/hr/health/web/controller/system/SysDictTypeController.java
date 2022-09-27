package com.hr.health.web.controller.system;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysDictType;
import com.hr.health.common.core.page.TableDataInfo;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.system.service.ISysDictTypeService;
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
 * 数据字典类型
 *
 * @author swq
 */
@Api(tags = "数据字典类型")
@RestController
@RequestMapping("/system/dictType")
public class SysDictTypeController extends BaseController {
    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 获取数据字典类型列表
     *
     * @param dictType
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    @ApiOperation("获取数据字典类型列表")
    public Result<TableDataInfo> list(SysDictType dictType) {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return Result.success(getDataTable(list));
    }

    /**
     * 导出
     *
     * @param response
     * @param dictType
     */
    @ApiOperation("导出")
    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * 查询字典类型详细
     */
    @ApiOperation("查询字典类型详细")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public Result<SysDictType> getInfo(@PathVariable Long dictId) {
        return Result.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @ApiOperation("新增字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        }
        //新增数据操作
        return Result.judge(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @ApiOperation("修改字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        }
        return Result.judge(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @ApiOperation("删除字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public Result remove(@PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return Result.success();
    }

    /**
     * 获取字典选择框列表
     */
    @ApiOperation("获取字典选择框列表")
    @GetMapping("/optionSelect")
    public Result<List<SysDictType>> optionSelect() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return Result.success(dictTypes);
    }
}
