package com.hr.health.web.controller.system;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.hr.health.common.annotation.Log;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysDept;
import com.hr.health.common.core.domain.entity.SysRole;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.core.page.TableDataInfo;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.framework.web.service.SysPermissionService;
import com.hr.health.framework.web.service.TokenService;
import com.hr.health.system.domain.SysUserRole;
import com.hr.health.system.service.ISysDeptService;
import com.hr.health.system.service.ISysRoleService;
import com.hr.health.system.service.ISysUserService;
import com.hr.health.system.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色信息
 *
 * @author swq
 */
@Api(tags = "角色信息")
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 角色列表
     *
     * @param role
     * @return
     */
    @ApiOperation("角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public Result<TableDataInfo> list(SysRole role) {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return Result.success(getDataTable(list));
    }

    /**
     * 导出
     *
     * @param response
     * @param role
     */
    @ApiOperation("导出")
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role) {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @ApiOperation("根据角色编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public Result<SysRole> getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return Result.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @ApiOperation("新增角色")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public Result add(@Validated @RequestBody SysRole role) {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return Result.failure(ResultCode.USER_ROLE_EXIST.code(), ResultCode.USER_ROLE_EXIST.message());
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return Result.failure(ResultCode.USER_ROLE_PERMISSIONS_EXIST.code(), ResultCode.USER_ROLE_PERMISSIONS_EXIST.message());
        }

        role.setCreateBy(getUsername());
        return Result.judge(roleService.insertRole(role));
    }

    /**
     * 修改保存角色
     */
    @ApiOperation("修改保存角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public Result edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return Result.failure(ResultCode.USER_ROLE_EXIST.code(), ResultCode.USER_ROLE_EXIST.message());
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return Result.failure(ResultCode.USER_ROLE_PERMISSIONS_EXIST.code(), ResultCode.USER_ROLE_PERMISSIONS_EXIST.message());
        }

        role.setUpdateBy(getUsername());
        return Result.judge(roleService.updateRole(role));
    }

    /**
     * 修改保存数据权限
     */
    @ApiOperation("修改保存数据权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public Result dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return Result.judge(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());
        return Result.judge(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @ApiOperation("删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public Result remove(@PathVariable Long[] roleIds) {
        return Result.judge(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @ApiOperation("获取角色选择框列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionSelect")
    public Result optionSelect() {
        return Result.success(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    @ApiOperation("查询已分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public Result<TableDataInfo> allocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return Result.success(getDataTable(list));
    }

    /**
     * 查询未分配用户角色列表
     */
    @ApiOperation("查询未分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public Result<TableDataInfo> unallocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return Result.success(getDataTable(list));
    }

    /**
     * 取消授权用户
     */
    @ApiOperation("取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public Result cancelAuthUser(@RequestBody SysUserRole userRole) {
        return Result.judge(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @ApiOperation("批量取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public Result cancelAuthUserAll(Long roleId, Long[] userIds) {
        return Result.judge(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @ApiOperation("批量选择用户授权")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public Result selectAuthUserAll(Long roleId, Long[] userIds) {
        roleService.checkRoleDataScope(roleId);
        return Result.judge(roleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * 获取对应角色部门树列表
     */
    @ApiOperation("获取对应角色部门树列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/deptTree/{roleId}")
    public Result deptTree(@PathVariable("roleId") Long roleId) {

        Map<String,Object> map =new HashMap<>();
        map.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        map.put("depts", deptService.selectDeptTreeList(new SysDept()));
        return Result.success(map);
    }
}
