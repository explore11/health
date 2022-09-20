package com.hr.health.common.exception.user;

import com.hr.health.common.exception.base.BaseException;

/**
 * 用户信息异常类
 *
 * @author swq
 */
public class UserException extends BaseException {
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
