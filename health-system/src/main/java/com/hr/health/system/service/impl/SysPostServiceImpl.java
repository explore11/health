package com.hr.health.system.service.impl;

import java.util.List;

import com.hr.health.common.core.domain.Result;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.exception.ServiceException;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.system.domain.SysPost;
import com.hr.health.system.mapper.SysPostMapper;
import com.hr.health.system.mapper.SysUserPostMapper;
import com.hr.health.system.service.ISysPostService;

/**
 * 岗位信息 服务层处理
 *
 * @author swq
 */
@Service
public class SysPostServiceImpl implements ISysPostService {
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;


    /**
     * 修改岗位
     * @param post
     * @return
     */
    @Override
    public Result edit(SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkPostNameUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NAME_ALREADY_EXISTED);
        } else if (UserConstants.NOT_UNIQUE.equals(this.checkPostCodeUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NUMBER_ALREADY_EXISTED);
        }
        //修改操作
        post.setUpdateBy(SecurityUtils.getUsername());
        return Result.judge(this.updatePost(post));
    }

    /**
     * 新增岗位
     * @param post
     * @return
     */
    @Override
    public Result add(SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkPostNameUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NAME_ALREADY_EXISTED);
        } else if (UserConstants.NOT_UNIQUE.equals(this.checkPostCodeUnique(post))) {
            return Result.failure(ResultCode.DATA_POST_NUMBER_ALREADY_EXISTED);
        }
        //新增操作
        post.setCreateBy(SecurityUtils.getUsername());
        return Result.judge(this.insertPost(post));
    }

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return postMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll() {
        return postMapper.selectPostAll();
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId) {
        return postMapper.selectPostById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostNameUnique(SysPost post) {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostCodeUnique(SysPost post) {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId) {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId) {
        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds) {
        for (Long postId : postIds) {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        return postMapper.deletePostByIds(postIds);
    }

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post) {
        return postMapper.insertPost(post);
    }

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post) {
        return postMapper.updatePost(post);
    }
}
