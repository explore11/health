package com.hr.health.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 统一返回状态码
 */
public enum ResultCode {

    /* 成功状态码 */
    SUCCESS(1, "成功"),
    FAILURE(2, "失败"),

    /* 参数错误：101-199 */
    PARAM_IS_INVALID(101, "参数无效"),
    PARAM_IS_BLANK(102, "参数为空"),
    PARAM_TYPE_BIND_ERROR(103, "参数类型错误"),
    PARAM_NOT_COMPLETE(104, "参数缺失"),
    PARAM_FORMAT_ERROR(105, "格式错误"),

    /* 用户错误：201-299*/
    USER_NOT_LOGGED_IN(201, "用户未登录"),
    USER_LOGIN_ERROR(202, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(203, "账号已被禁用"),
    USER_NOT_EXIST(204, "用户不存在"),
    USER_HAS_EXISTED(205, "用户已存在"),
    USER_TRY_TOO_MUCH(206, "尝试次数过多"),
    USER_NO_ACCESS(207, "用户无系统权限"),
    USER_IS_DELETE(208, "用户已被删除"),
    USER_ID_NOT_EMPTY(209, "用户ID不能为空"),
    USER_INFO_NOT_EMPTY(210, "用户信息不能为空"),
    USER_EXPIRE(211, "用户登录已过期"),
    USER_PHONE_EXIST(212, "用户手机号已存在"),
    USER_UPDATE_PASSWORD_FAILURE(213, "修改密码失败，旧密码错误"),
    USER_PASSWORD_NO_SAME(214, "新密码不能与旧密码相同"),
    USER_ROLE_PERMISSIONS_EXIST(215, "用户权限已存在"),
    USER_ROLE_EXIST(215, "用户角色已存在"),

    /* 业务错误：301-399 */
    SPECIFIED_QUESTIONED_USER_NOT_EXIST(301, "某业务出现问题"),

    /* 数据错误：401-499 */
    RESULT_DATA_NONE(401, "数据未找到"),
    DATA_IS_WRONG(402, "数据有误"),
    DATA_ALREADY_EXISTED(403, "数据已存在"),
    DATA_PARENT_DEPT_NO_SELF(405, "上级部门不能是自己"),
    DATA_DEPT_CONTAIN_NO_STOP_SON_DEPT(406, "该部门包含未停用的子部门"),
    DATA_DEPT_CONTAIN_SON_DEPT_NO_DEL(407, "存在下级部门,不允许删除"),
    DATA_DEPT_EXISTED_DEPT_USER_NO_DEL(408, "部门存在用户,不允许删除"),
    DATA_MENU_CONTAIN_SON_MENU_NO_DEL(409, "菜单存在下级菜单,不允许删除"),
    DATA_MENU_ALREADY_DISTRIBUTION(410, "菜单已分配,不允许删除"),
    DATA_POST_NAME_ALREADY_EXISTED(411, "岗位名称已存在"),
    DATA_POST_NUMBER_ALREADY_EXISTED(412, "岗位编码已存在"),

    /* 系统错误：501-599 */
    SYSTEM_INNER_ERROR(501, "系统内部出错，请稍后重试"),

    /* 接口错误：601-699 */
    INTERFACE_INNER_INVOKE_ERROR(601, "内部系统接口调用异常"),
    INTERFACE_OUTTER_INVOKE_ERROR(602, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(603, "该接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(604, "接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(605, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(606, "接口负载过高"),

    /* 权限错误：701-799 */
    PERMISSION_NO_ACCESS(701, "无访问权限");

    private Integer code;

    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static ResultCode getItemByCode(Integer code) {
        for (ResultCode item : ResultCode.values()) {
            if (item.code().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }

    //校验重复的code值
    public static void main(String[] args) {
        ResultCode[] values = ResultCode.values();
        List<Integer> codeList = new ArrayList<>();
        for (ResultCode resultCode : values) {
            if (codeList.contains(resultCode.code)) {
                System.out.println(resultCode.code);
            } else {
                codeList.add(resultCode.code());
            }
        }
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.name();
    }
}