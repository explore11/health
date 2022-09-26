package com.hr.health.web.controller.system;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysDept;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.system.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门信息
 *
 * @author swq
 */
@Api(tags = "部门信息")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    @ApiOperation("获取部门列表")
    public Result<List<SysDept>> list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.success(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @ApiOperation("查询部门列表（排除节点）")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public Result<List<SysDept>> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        //过滤
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return Result.success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @ApiOperation("根据部门编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public Result<SysDept> getInfo(@PathVariable Long deptId) {
        return Result.success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    @ApiOperation("新增部门")
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysDept dept) {
        //校验
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        }

        //新增操作
        dept.setCreateBy(getUsername());
        return Result.judge(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @ApiOperation("修改部门")
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysDept dept) {
        // 检查数据域
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);

        //校验
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        } else if (dept.getParentId().equals(deptId)) {
            return Result.failure(ResultCode.DATA_PARENT_DEPT_NO_SELF.code(), ResultCode.DATA_PARENT_DEPT_NO_SELF.message());
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0) {
            return Result.failure(ResultCode.DATA_DEPT_CONTAIN_NO_STOP_SON_DEPT.code(), ResultCode.DATA_DEPT_CONTAIN_NO_STOP_SON_DEPT.message());
        }

        // 修改操作
        dept.setUpdateBy(getUsername());
        return Result.judge(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @ApiOperation("删除部门")
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public Result remove(@PathVariable Long deptId) {
        // 校验
        if (deptService.hasChildByDeptId(deptId)) {
            return Result.failure(ResultCode.DATA_DEPT_CONTAIN_SON_DEPT_NO_DEL.code(), ResultCode.DATA_DEPT_CONTAIN_SON_DEPT_NO_DEL.message());
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return Result.failure(ResultCode.DATA_DEPT_EXISTED_DEPT_USER_NO_DEL.code(), ResultCode.DATA_DEPT_EXISTED_DEPT_USER_NO_DEL.message());
        }

        // 删除操作
        return Result.judge(deptService.deleteDeptById(deptId));
    }
}
