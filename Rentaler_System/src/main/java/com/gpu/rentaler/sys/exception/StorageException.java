package com.gpu.rentaler.sys.exception;

import com.gpu.rentaler.common.BusinessException;
import com.gpu.rentaler.common.ResultStatus;

/**
 * @author wzq
 */
public class StorageException extends BusinessException {

    public StorageException(ResultStatus status) {
        super(status);
    }

    public StorageException(ResultStatus status, String message) {
        super(status, message);
    }
}
