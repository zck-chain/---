package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.User;
import com.onlinexue.model.dto.UserBaseDto;
import com.onlinexue.model.dto.UserRegisterDto;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {

    /**
     * 发送手机,获取验证码
     *
     * @param phone
     * @return
     */
    Result sendCoderedis(String phone);

    /**
     * 登录功能
     *
     * @param userBaseDto 登录信息
     * @return
     */
    Result loginredis(UserBaseDto userBaseDto);

    //用户退出登录
    boolean loginout(HttpServletRequest request);

    /**
     * 注册功能
     *
     * @param userRegisterDto 用户注册信息
     * @return
     */
    Result register(UserRegisterDto userRegisterDto);

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    Result getLoginUserInfo(HttpServletRequest request);

    /**
     * 修改用户信息
     *
     * @param user
     * @param request
     * @return
     */
    Result updateMember(User user, HttpServletRequest request);

    /**
     * 修改密码
     *
     * @param mobile   手机号
     * @param password 密码
     * @return
     */
    Result updatePassword(String mobile, String password, HttpServletRequest request);

}
