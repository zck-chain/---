package com.onlinexue.model.dto;

import lombok.Data;

/**
 * 前端页面保存的用户信息
 */
@Data
public class UserDtO {
    private String id;//用户id
    private Integer age;//年龄
    private String sex;//性别
    private String mobile;//手机号
    private String nickName;//昵称
    private String icon;//头像
    private String sign;//个性签名

}
