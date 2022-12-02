package com.hr.health.web.controller.system;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysDept;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.core.page.TableDataInfo;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.system.service.ISysDeptService;
import com.hr.health.system.service.ISysUserService;
import com.hr.health.system.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 *
 * @author swq
 */
@Api(tags = "用户信息")
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;


    /**
     * 根据路径生成压缩包
     */
    @ApiOperation("根据路径生成压缩包")
    @PreAuthorize("@ss.hasPermi('system:user:generateCompressedPackage')")
    @GetMapping("/generateCompressedPackage")
    public Result generateCompressedPackage(String path) {
        return userService.generateCompressedPackage(path);
    }

    /**
     * 据路径解析二维码
     */
    @ApiOperation("据路径解析二维码")
    @PreAuthorize("@ss.hasPermi('system:user:parseQrCode')")
    @GetMapping("/parseQrCode")
    public Result parseQrCode(String path) {
        return userService.parseQrCode(path);
    }

    /**
     * 生成二维码
     */
    @ApiOperation("生成二维码")
    @PreAuthorize("@ss.hasPermi('system:user:generateQrcode')")
    @GetMapping("/generateQrCode")
    public Result generateQrCode(SysUser user) {
        return userService.generateQrCode(user);
    }


    /**
     * 获取用户列表
     */
    @ApiOperation("获取用户列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public Result<TableDataInfo> list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return Result.success(getDataTable(list));
    }

    /**
     * 导出
     *
     * @param response
     * @param user
     */
    @ApiOperation("导出")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 导入
     *
     * @param file
     * @param updateSupport
     * @return
     * @throws Exception
     */
    @ApiOperation("导入")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public Result importData(MultipartFile file, boolean updateSupport) throws Exception {
        return userService.importData(file, updateSupport);
    }

    /**
     * 导入模板
     *
     * @param response
     */
    @ApiOperation("导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation("根据用户编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public Result<Map<String, Object>> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        Map<String, Object> map = userService.getInfo(userId);
        return Result.success(map);
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysUser user) {
        return userService.add(user);
    }

    /**
     * 修改用户
     */
    @ApiOperation("修改用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysUser user) {
        return userService.edit(user);
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public Result remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getUserId())) {
            return Result.failure(ResultCode.USER_NO_DELETE.code(), ResultCode.USER_NO_DELETE.message());
        }
        return Result.judge(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public Result resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return Result.judge(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysUser user) {
        return userService.changeStatus(user);
    }

    /**
     * 根据用户编号获取授权角色
     */
    @ApiOperation("根据用户编号获取授权角色")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public Result<Map<String, Object>> authRole(@PathVariable("userId") Long userId) {
        Map<String, Object> map = userService.authRole(userId);
        return Result.success(map);
    }

    /**
     * 用户授权角色
     */
    @ApiOperation("用户授权角色")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public Result insertAuthRole(Long userId, Long[] roleIds) {
        userService.insertAuthRole(userId, roleIds);
        return Result.success();
    }

    /**
     * 获取部门树列表
     */
    @ApiOperation("获取部门树列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public Result deptTree(SysDept dept) {
        return Result.success(deptService.selectDeptTreeList(dept));
    }


}
