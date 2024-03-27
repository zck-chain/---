package com.onlinexue.sercvice.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.BannerMapper;
import com.onlinexue.model.dao.Banner;
import com.onlinexue.sercvice.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.onlinexue.util.MinioUtils.MINIO_PATH;
import static com.onlinexue.util.RedisConstants.BANNER_KEY;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 获取所有横幅
     *
     * @return
     */
    @Override
    public Result getAllBanner() {
        String redisString = stringRedisTemplate.opsForValue().get(BANNER_KEY);
        if (!ObjectUtil.isEmpty(redisString) && !redisString.equals("[]")) {
            //返回redis中保存的数据
            List<Banner> parse = (List<Banner>) JSONArray.parse(redisString);
            return Result.ok(parse);
        }
        List<Banner> list = list();
        list.stream().forEach(item -> {
            item.setBannerUrl(MINIO_PATH + item.getBannerUrl());//拼接预览地址
        });
        String jsonStr = JSONUtil.toJsonStr(list);
        //保存到redis中
        stringRedisTemplate.opsForValue().set(BANNER_KEY, jsonStr);
        return Result.ok(list);
    }
}
