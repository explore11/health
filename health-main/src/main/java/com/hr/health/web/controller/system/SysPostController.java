package com.hr.health.web.controller.system;

import com.hr.health.common.annotation.Log;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.controller.BaseController;
import com.hr.health.common.core.domain.AjaxResult;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.page.TableDataInfo;
import com.hr.health.common.enums.BusinessType;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.system.domain.SysPost;
import com.hr.health.system.service.ISysPostService;
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
 * 岗位信息操作处理
 *
 * @author swq
 */
@Api(tags = "岗位信息")
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {
    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @ApiOperation("获取岗位列表")
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public Result<TableDataInfo> list(SysPost post) {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return Result.success(getDataTable(list));
    }

    /**
     * 导出
     *
     * @param response
     * @param post
     */
    @ApiOperation("导出")
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post) {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @ApiOperation("根据岗位编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public Result<SysPost> getInfo(@PathVariable Long postId) {
        return Result.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @ApiOperation("新增岗位")
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NAME_ALREADY_EXISTED);
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NUMBER_ALREADY_EXISTED);
        }

        post.setCreateBy(getUsername());
        return Result.judge(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @ApiOperation("修改岗位")
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NAME_ALREADY_EXISTED);
        } else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NUMBER_ALREADY_EXISTED);
        }
        post.setUpdateBy(getUsername());
        return Result.judge(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @ApiOperation("删除岗位")
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public Result remove(@PathVariable Long[] postIds) {
        return Result.judge(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @ApiOperation("获取岗位选择框列表")
    @GetMapping("/optionSelect")
    public Result<List<SysPost>> optionSelect() {
        List<SysPost> posts = postService.selectPostAll();
        return Result.success(posts);
    }
}
