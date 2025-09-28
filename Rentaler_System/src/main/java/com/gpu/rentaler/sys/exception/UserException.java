package com.gpu.rentaler.sys.exception;

import com.gpu.rentaler.common.BusinessException;
import com.gpu.rentaler.common.ResultStatus;

/**
 * @author cjbi
 */
public class UserException extends BusinessException {

    public UserException(ResultStatus status) {
        super(status);
    }

    public UserException(ResultStatus status, String message) {
        super(status, message);
    }
}
