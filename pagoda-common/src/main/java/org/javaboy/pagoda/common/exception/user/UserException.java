package org.javaboy.pagoda.common.exception.user;

import org.javaboy.pagoda.common.exception.base.BaseException;

/**
 * 用户信息异常类
 *
 * @author pagoda
 */
public class UserException extends BaseException {
        private static final long serialVersionUID = 1L;

        public UserException(String code, Object[] args) {
                super("user", code, args, null);
        }
}
