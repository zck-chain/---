package com.onlinexue.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Result {
    private Integer code;//1为成功,0为失败
    private String errorMsg;
    private Object data;
    private Long total;
    private String token;

    public Result(Integer code, String errorMsg, Object data, Long total, String token) {
        this.code = code;
        this.errorMsg = errorMsg;
        this.data = data;
        this.total = total;
        this.token = token;
    }

    public static Result ok() {
        return new Result(1, null, null, null, null);
    }

    public static Result ok(Object data) {
        return new Result(1, null, data, null, null);
    }

    public static Result ok(String token, Object data) {
        return new Result(1, null, data, null, token);
    }

    public static Result ok(List<?> data, Long total) {
        return new Result(1, null, data, total, null);
    }

    public static Result fail(String errorMsg) {
        return new Result(0, errorMsg, null, null, null);
    }

    public static Result fail(String errorMsg, String token) {
        return new Result(0, errorMsg, null, null, token);
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}

