package com.onlinexue.user.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.User;
import com.onlinexue.model.dto.UserBaseDto;
import com.onlinexue.model.dto.UserRegisterDto;
import com.onlinexue.service.MasterService;
import com.onlinexue.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value = "用户管理系统", tags = "用户管理接口")
@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    MasterService masterService;

    @ApiOperation("按id查询用户")
    @GetMapping("/getUserInfo/{id}")
    public Result selectById(@PathVariable String id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail("用户没登录");
        }
        return Result.ok(user);
    }

    /**
     * 发送手机验证码
     */
    @ApiOperation("发送手机验证码")
    @PostMapping("code")
    public Result sendCode(@RequestParam("mobile") String mobile) {
        // TODO 发送短信验证码并保存验证码
        return userService.sendCoderedis(mobile);
    }

    /**
     * 登录功能
     *
     * @param userBaseDto 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @ApiOperation("普通用户登录功能")
    @PostMapping("/login")
    public Result login(@RequestBody UserBaseDto userBaseDto) {
        return userService.loginredis(userBaseDto);
    }


    /**
     * 退出功能
     *
     * @return 无
     */
    @ApiOperation("退出功能")
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request) {
        // TODO 实现登出功能
        boolean loginout = userService.loginout(request);
        if (!loginout) {
            return Result.fail("服务器在维护");
        }
        return Result.ok();
    }

    /**
     * 注册用户
     *
     * @param userRegisterDto 用户注册信息
     * @return
     */
    @ApiOperation("注册用户")
    @PostMapping("register")
    public Result register(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @ApiOperation("获取用户信息")
    @GetMapping("getMemberInfo")
    public Result getLoginUserInfo(HttpServletRequest request) {
        return userService.getLoginUserInfo(request);
    }


    /**
     * 修改用户信息
     *
     * @param user    用户信息
     * @param request 请求头
     * @return
     */
    @ApiOperation("修改用户信息")
    @PostMapping("updateMember")
    public Result updateMember(@RequestBody User user, HttpServletRequest request) {
        return userService.updateMember(user, request);
    }


    /**
     * 修改密码
     *
     * @param mobile 手机号
     * @param newPwd 密码
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestParam("mobile") String mobile, @RequestParam("newPwd") String newPwd, HttpServletRequest request) {
        return userService.updatePassword(mobile, newPwd, request);
    }


}
