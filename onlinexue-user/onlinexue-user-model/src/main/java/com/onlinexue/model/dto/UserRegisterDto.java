package com.onlinexue.model.dto;

import lombok.Data;

/**
 * 用户注册信息
 */
@Data
public class UserRegisterDto {
    private String mobile;//手机号
    private String code;//验证码
    private String nickName;//名称
    private String password;//密码
    private String icon = "http://1.94.21.227:9000/icon/mr.jpg";//默认头像
}
