package com.hr.health.web.controller.system;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.TreeSelect;
import com.hr.health.common.core.domain.entity.SysMenu;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.system.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单信息
 *
 * @author swq
 */
@Api(tags = "菜单信息")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @ApiOperation("获取菜单列表")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return Result.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @ApiOperation("根据菜单编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public Result<SysMenu> getInfo(@PathVariable Long menuId) {
        return Result.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @ApiOperation("获取菜单下拉树列表")
    @GetMapping("/treeselect")
    public Result<List<TreeSelect>> treeselect(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return Result.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @ApiOperation("加载对应角色菜单列表树")
    @GetMapping(value = "/roleMenuTreeSelect/{roleId}")
    public Result<Map<String, Object>> roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        // 查询数据
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        // 返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        map.put("menus", menuService.buildMenuTreeSelect(menus));
        return Result.success(map);
    }

    /**
     * 新增菜单
     */
    @ApiOperation("新增菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return Result.failure(ResultCode.PARAM_FORMAT_ERROR.code(), ResultCode.PARAM_FORMAT_ERROR.message());
        }

        menu.setCreateBy(getUsername());
        return Result.judge(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @ApiOperation("修改菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return Result.failure(ResultCode.DATA_ALREADY_EXISTED.code(), ResultCode.DATA_ALREADY_EXISTED.message());
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return Result.failure(ResultCode.PARAM_FORMAT_ERROR.code(), ResultCode.PARAM_FORMAT_ERROR.message());
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return Result.failure(ResultCode.DATA_PARENT_DEPT_NO_SELF.code(), ResultCode.DATA_PARENT_DEPT_NO_SELF.message());
        }

        menu.setUpdateBy(getUsername());
        return Result.judge(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @ApiOperation("删除菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public Result remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return Result.failure(ResultCode.DATA_MENU_CONTAIN_SON_MENU_NO_DEL.code(), ResultCode.DATA_MENU_CONTAIN_SON_MENU_NO_DEL.message());
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return Result.failure(ResultCode.DATA_MENU_ALREADY_DISTRIBUTION.code(), ResultCode.DATA_MENU_ALREADY_DISTRIBUTION.message());
        }
        return Result.judge(menuService.deleteMenuById(menuId));
    }
}