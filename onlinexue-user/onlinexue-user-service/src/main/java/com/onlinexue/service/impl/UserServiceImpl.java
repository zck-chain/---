package com.onlinexue.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.Usermapper;
import com.onlinexue.model.dao.Master;
import com.onlinexue.model.dao.User;
import com.onlinexue.model.dto.UserBaseDto;
import com.onlinexue.model.dto.UserDtO;
import com.onlinexue.model.dto.UserRegisterDto;
import com.onlinexue.service.UserService;
import com.onlinexue.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.onlinexue.util.MinioUtils.*;
import static com.onlinexue.util.RedisConstants.*;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<Usermapper, User> implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static Map<String, Object> getStringObjectMap(Object object) {
        Map<String, Object> userRedisMap = BeanUtil.beanToMap(object, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue) -> {
                    if (fieldValue == null) {
                        fieldValue = " ";
                    } else {
                        fieldValue = fieldValue + " ";
                    }
                    return fieldValue;
                }));
        return userRedisMap;
    }

    @Override
    public Result sendCoderedis(String mobile) {
        //1.校验手机号
        if (RegexUtils.isPhoneInvalid(mobile)) {
            //2.不符合返回
            return Result.fail("手机格式不正确");
        }
        //校验手机号是否存在
        User user = query().eq("mobile", mobile).one();
        if (user != null) {
            return Result.fail("手机号已经注册过");
        }
        //3.符合,随机生成验证码
        String code = RandomUtil.randomNumbers(6);
        //4.放入redis中
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + mobile, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);//设置两分钟过期
        //5.发送验证码
        log.debug("发送验证码为:{}", code);
        return Result.ok(code);
    }

    /**
     * 登录功能
     *
     * @param userBaseDto 登录信息
     * @return
     */
    @Override
    public Result loginredis(UserBaseDto userBaseDto) {
        //1.校验手机号
        String mobile = userBaseDto.getMobile();
        if (RegexUtils.isPhoneInvalid(mobile)) {
            //2.不符合返回
            return Result.fail("手机格式不正确");
        }
        //2.获取密码,对密码加密
        String password = MD5.create().digestHex(userBaseDto.getPassword());
        //3.查询用户是否存在
        User user = query().eq("mobile", mobile).one();
        if (!user.getPassword().equals(password)) {
            return Result.fail("用户密码输入错误");
        }
        //4.如果不存在,创建用户
        if (user == null) {
            return Result.fail("用户不存在,去创建一个账号!");
        }
        //5.如果存在,把user信息放入到redis中
        String token = UUID.randomUUID().toString();//创建唯一token放入redis做key
        //存储
        String tokenKey = LOGIN_USER_KEY + token;
        //将user对象转换为hash存储
        UserDtO userDtO = BeanUtil.copyProperties(user, UserDtO.class);
        //发送到redis中
        saveRedis(userDtO, tokenKey, null);
        //设计有效时间
        return Result.ok(token);
    }

    //用户退出登录
    @Override
    public boolean loginout(HttpServletRequest request) {
        String toke = request.getHeader("Token");
        //清除redis中缓存的user数据和验证码数据
        String tokeKey = LOGIN_USER_KEY + toke;
        Boolean delete = stringRedisTemplate.delete(tokeKey);//删除在redis中保存的user数据
        return delete;
    }

    /**
     * 注册功能
     *
     * @param userRegisterDto 用户注册信息
     * @return
     */
    @Transactional
    @Override
    public Result register(UserRegisterDto userRegisterDto) {
        //1.获取手机号
        String mobile = userRegisterDto.getMobile();
        //1.1获取验证码
        String code = userRegisterDto.getCode();
        //1.2获取名称
        String nickName = userRegisterDto.getNickName();
        //2.校验手机号
        if (RegexUtils.isPhoneInvalid(mobile)) {
            return Result.fail("手机输入格式错误！");
        }
        //3.校验是否存在该用户的名称或手机号
        Long count = query().eq("nick_name", nickName).count();
        if (count > 0) {
            return Result.fail("用户名称重复,请重新输入");
        }
        //判断验证码是否相同
        String redisCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + mobile);
        if (!redisCode.equals(code)) {
            return Result.fail("验证码输入错误");
        }
        //对密码加密
        User user = new User();
        String password = MD5.create().digestHex(userRegisterDto.getPassword());
        userRegisterDto.setPassword(password);
        BeanUtil.copyProperties(userRegisterDto, user);
        //给默认的头像
        user.setIcon(MINIO_PATH + BUCKET_MEDIAFILES + MRICON);
        //给默认的性别
        user.setSex("男");
        save(user);
        return Result.ok();
    }

    /**
     * 根据前端给的token查询用户信息
     *
     * @param request 请求
     * @return
     */
    @Override
    public Result getLoginUserInfo(HttpServletRequest request) {
        String tokenKey = getTokenKey(request);
        if (tokenKey.isEmpty()) {
            return Result.fail("没有token,去登录");
        }
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (userMap.isEmpty()) {
            return Result.fail("token过期");
        }
        //将查询到的hash数据转为UserDto对象
        UserDtO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDtO(), false);
        return Result.ok(userDTO);
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @param request
     * @return
     */
    @Transactional
    @Override
    public Result updateMember(User user, HttpServletRequest request) {
        //获取用户的id
        String id = user.getId();
        if (user.getSex().equals("0")) {
            user.setSex("女");
        } else {
            user.setSex("男");
        }
        //去判断minio中保存的文件是否修改
        //去修改数据库
        boolean b = updateById(user);
        if (!b) {
            return Result.fail("服务器出现问题,修改失败");
        }
        String token = request.getHeader("token");
        String tokenKey = LOGIN_USER_KEY + token;//拼接redis的key获取用户信息
        UserDtO userDtO = BeanUtil.copyProperties(user, UserDtO.class);
        saveRedis(userDtO, tokenKey, null);
        return Result.ok();
    }

    /**
     * 修改密码
     *
     * @param mobile   手机号
     * @param password 密码
     * @return
     */
    @Transactional
    @Override
    public Result updatePassword(String mobile, String password, HttpServletRequest request) {
        String tokenKey = getTokenKey(request);
        //去redis拿去用户的id
        String id = String.valueOf(stringRedisTemplate.opsForHash().get(tokenKey, "id"));
        //对新密码加密
        String newpassword = MD5.create().digestHex(password);
        //去修改密码
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("password", newpassword);
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            return Result.fail("修改密码失败!");
        }
        return Result.ok();
    }

    /**
     * 发送到redis中
     *
     * @param userDtO
     * @param tokenKey
     */
    public void saveRedis(UserDtO userDtO, String tokenKey, Master master) {
        if (userDtO == null) {
            //在后台登录
            Map<String, Object> stringMasterMap = getStringObjectMap(master);
            stringRedisTemplate.opsForHash().putAll(tokenKey, stringMasterMap);
        } else {
            Map<String, Object> stringUserMap = getStringObjectMap(userDtO);
            stringRedisTemplate.opsForHash().putAll(tokenKey, stringUserMap);
        }
        //去修改redis中的信息
        stringRedisTemplate.expire(tokenKey, 60, TimeUnit.MINUTES);//一个小时
    }

    private String getTokenKey(HttpServletRequest request) {
        String token = request.getHeader("token");//获取请求头的token信息
        if (StrUtil.isBlank(token)) {
            return " ";
        }
        return LOGIN_USER_KEY + token;//拼接redis的key获取用户信息
    }


}
