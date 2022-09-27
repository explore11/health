package com.hr.health.system.service.impl;

import com.hr.health.common.config.HealthConfig;
import com.hr.health.common.constant.UserConstants;
import com.hr.health.common.core.domain.Result;
import com.hr.health.common.core.domain.entity.SysRole;
import com.hr.health.common.core.domain.entity.SysUser;
import com.hr.health.common.core.domain.model.LoginUser;
import com.hr.health.common.enums.ResultCode;
import com.hr.health.common.exception.ServiceException;
import com.hr.health.common.utils.SecurityUtils;
import com.hr.health.common.utils.StringUtils;
import com.hr.health.common.utils.bean.BeanValidators;
import com.hr.health.common.utils.file.FileUploadUtils;
import com.hr.health.common.utils.file.MimeTypeUtils;
import com.hr.health.system.domain.SysPost;
import com.hr.health.system.domain.SysUserPost;
import com.hr.health.system.domain.SysUserRole;
import com.hr.health.system.mapper.*;
import com.hr.health.system.service.ISysConfigService;
import com.hr.health.system.service.ISysPostService;
import com.hr.health.system.service.ISysRoleService;
import com.hr.health.system.service.ISysUserService;
import com.hr.health.system.utils.poi.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author swq
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    protected Validator validator;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;


    /**
     * 修改状态
     * @param user
     * @return
     */
    @Override
    public Result changeStatus(SysUser user) {
        this.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return Result.judge(this.updateUserStatus(user));
    }

    /**
     * 修改用户
     * @param user
     * @return
     */
    @Override
    public Result edit(SysUser user) {
        this.checkUserAllowed(user);
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.NOT_UNIQUE.equals(this.checkPhoneUnique(user))) {
            return Result.failure(ResultCode.USER_PHONE_EXIST.code(), ResultCode.USER_PHONE_EXIST.message());
        } else if (StringUtils.isNotEmpty(user.getEmail()) && UserConstants.NOT_UNIQUE.equals(this.checkEmailUnique(user))) {
            return Result.failure(ResultCode.USER_EMAIL_EXIST.code(), ResultCode.USER_EMAIL_EXIST.message());
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return Result.judge(this.updateUser(user));
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @Override
    public Result add(SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkUserNameUnique(user.getUserName()))) {
            return Result.failure(ResultCode.USER_HAS_EXISTED.code(), ResultCode.USER_HAS_EXISTED.message());
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.NOT_UNIQUE.equals(this.checkPhoneUnique(user))) {
            return Result.failure(ResultCode.USER_PHONE_EXIST.code(), ResultCode.USER_PHONE_EXIST.message());
        } else if (StringUtils.isNotEmpty(user.getEmail()) && UserConstants.NOT_UNIQUE.equals(this.checkEmailUnique(user))) {
            return Result.failure(ResultCode.USER_EMAIL_EXIST.code(), ResultCode.USER_EMAIL_EXIST.message());
        }

        //操作数据
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return Result.judge(this.insertUser(user));
    }

    /**
     * 导入
     *
     * @param file
     * @param updateSupport
     * @return
     * @throws IOException
     */
    @Override
    public Result importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operationName = SecurityUtils.getUsername();
        String message = this.importUser(userList, updateSupport, operationName);
        return Result.success(message);
    }

    /**
     * 头像上传
     *
     * @param file
     * @return
     */
    @Override
    public Result updateAvatar(MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(HealthConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if (this.updateUserAvatar(loginUser.getUsername(), avatar)) {
                return Result.success(avatar);
            }
        }


        return Result.failure();
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @Override
    public Result updateProfile(SysUser user) {
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && UserConstants.NOT_UNIQUE.equals(this.checkPhoneUnique(user))) {
            return Result.failure(ResultCode.USER_PHONE_EXIST.code(), ResultCode.USER_PHONE_EXIST.message());
        }
        //修改用户
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        user.setAvatar(null);
        user.setDeptId(null);
        return Result.judge(this.updateUserProfile(user));
    }

    /**
     * 获取岗位列表
     *
     * @return
     */
    @Override
    public Map<String, Object> profile() {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        //数据返回
        Map<String, Object> map = new HashMap<>();
        map.put("roleGroup", this.selectUserRoleGroup(loginUser.getUsername()));
        map.put("postGroup", this.selectUserPostGroup(loginUser.getUsername()));
        map.put("user", user);

        return map;
    }

    /**
     * 重置密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public Result updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return Result.failure(ResultCode.USER_UPDATE_PASSWORD_FAILURE.code(), ResultCode.USER_UPDATE_PASSWORD_FAILURE.message());
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return Result.failure(ResultCode.USER_PASSWORD_NO_SAME.code(), ResultCode.USER_PASSWORD_NO_SAME.message());
        }
        return Result.judge(this.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)));
    }

    /**
     * 用户授权角色
     *
     * @param userId
     * @param roleIds
     */
    @Override
    public void insertAuthRole(Long userId, Long[] roleIds) {
        this.insertUserAuth(userId, roleIds);
    }

    /**
     * 根据用户编号获取授权角色
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> authRole(Long userId) {
        Map<String, Object> map = new HashMap<>();
        SysUser user = this.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        map.put("user", user);
        map.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return map;
    }

    /**
     * 根据用户编号获取详细信息
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> getInfo(Long userId) {
        Map<String, Object> map = new HashMap<>();
        //查询角色
        List<SysRole> roles = roleService.selectRoleAll();
        map.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        map.put("posts", postService.selectPostAll());
        // 查询用户
        SysUser sysUser = this.selectUserById(userId);
        map.put("user", sysUser);
        map.put("postIds", postService.selectPostListByUserId(userId));
        map.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));

        return map;
    }

    /**
     * 获取用户信息
     */
    public LoginUser getLoginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();
        //设置用户信息
        SysUser sysUser = this.selectUserByUserName(username);
        loginUser.setUser(sysUser);
        return loginUser;
    }


    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        int count = userMapper.checkUserNameUnique(userName);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotEmpty(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length);
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            userPostMapper.batchUserPost(list);
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, user);
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, user);
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
