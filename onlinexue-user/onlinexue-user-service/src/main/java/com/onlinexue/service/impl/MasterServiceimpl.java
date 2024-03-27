package com.onlinexue.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.Mastermapper;
import com.onlinexue.model.dao.Master;
import com.onlinexue.model.dto.MasterDto;
import com.onlinexue.service.MasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

import static com.onlinexue.util.RedisConstants.LOGIN_MASTER_KEY;

@Slf4j
@Service
public class MasterServiceimpl extends ServiceImpl<Mastermapper, Master> implements MasterService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserServiceImpl userService;

    public static void main(String[] args) {
        String x1 = new String("c") + new String("d");
        x1.intern();
        String x2 = "cd";

        //问
        System.out.println(x1 == x2);//false，x1在堆中，x2在串池中

    }

    /**
     * 管理员登录
     *
     * @param masterDto
     * @param request
     * @return
     */
    @Override
    public Result masterlogin(MasterDto masterDto, HttpServletRequest request) {
        String code = masterDto.getCode();
        String codeimg = masterDto.getCodeimg();
        if (!code.equals(codeimg)) {
            return Result.fail("验证码输入错误");
        }
        String md5 = MD5.create().digestHex(masterDto.getPassword());
        masterDto.setPassword(md5);
        //1.去redis中拿数据
        String tokenRequest = request.getHeader("token");
        if (!StrUtil.isEmpty(tokenRequest)) {
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(LOGIN_MASTER_KEY + tokenRequest);
            MasterDto redismasterDto = BeanUtil.fillBeanWithMap(entries, new MasterDto(), true);
            return Result.ok(tokenRequest, redismasterDto);
        }
        //去数据库拿取数据
        Master one = query().eq("username", masterDto.getUsername()).eq("password", masterDto.getPassword()).one();
        if (ObjectUtil.isEmpty(one)) {
            return Result.fail("账号密码错误,重新输入!");
        }
        //1.生成token
        String token = UUID.randomUUID().toString();//创建唯一token放入redis做key
        String tokenKey = LOGIN_MASTER_KEY + token;
        //保持信息到redis中
        userService.saveRedis(null, tokenKey, one);
        return Result.ok(token, masterDto);
    }

    @Override
    public Result masterloginout(String token) {
        if (token.equals("null")) {
            log.debug("token令牌失效,退出登录!");
            return Result.ok();
        }
        String tokenKey = LOGIN_MASTER_KEY + token;
        stringRedisTemplate.delete(tokenKey);
        return Result.ok();
    }
}
