package com.onlinexue.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * 和前端约定返回的异常信息模型
 */
@Data
public class RestErrorResponse implements Serializable {
    private String errMessage;
}
