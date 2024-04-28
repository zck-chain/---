package com.onlinexue.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.exception.OnlineXuePlusException;
import com.onlinexue.mapper.CourseBaseMapper;
import com.onlinexue.model.dao.*;
import com.onlinexue.model.dto.CourseBasePage;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.onlinexue.util.CourseUtils.*;
import static com.onlinexue.util.RedisConstants.Course_Base_Page;
import static com.onlinexue.util.RedisConstants.LOGIN_MASTER_KEY;

@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CourseMarketService courseMarketService;//课程收费信息
    @Autowired
    private CourseDictionaryService courseDictionaryService;//课程级别信息
    @Autowired
    private CourseTeacherService courseTeacherService;//课程教师信息
    @Autowired
    private CoursePublishService coursePublishService;//课程发布信息

    /**
     * 查询基本课程信息
     *
     * @param formInline 查询实体类
     * @return
     */
    @Override
    public Result getCourseBaseList(FormInline formInline) {
        int page = formInline.getPage();
        int limit = formInline.getLimit();
        String selectname = formInline.getSelectname();
        String selectuser = formInline.getSelectuser();
        String key = Course_Base_Page + String.valueOf(page) + ":" + String.valueOf(limit);
        if (StrUtil.isNotEmpty(selectname) || StrUtil.isNotEmpty(selectuser)) {
            //删除缓存
            deleteKeysWithPatternUsingScan(Course_Base_Page);
        }
        if (StrUtil.isEmpty(selectuser) && StrUtil.isEmpty(selectname)) {
            //删除缓存
            deleteKeysWithPatternUsingScan(Course_Base_Page);
        }
        String redisString = stringRedisTemplate.opsForValue().get(key);
        if (!ObjectUtil.isEmpty(redisString)) {
            //返回redis中保存的数据
            CourseBasePage courseBasePageRedis = JSONUtil.toBean(redisString, CourseBasePage.class);
            return Result.ok(courseBasePageRedis);
        }
        IPage<CourseBase> courseBase = new Page<>(page, limit);
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        //后端的查询条件
        wrapper.like(CourseBase::getName, selectname).like(CourseBase::getUsers, selectuser);
        IPage<CourseBase> courseBasePage = page(courseBase, wrapper);
        List<CourseBase> records = courseBasePage.getRecords();
        long total = courseBasePage.getTotal();
        CourseBasePage basePage = new CourseBasePage(page, records, total, null, null);
        String jsonStr = JSONUtil.toJsonStr(basePage);
        stringRedisTemplate.opsForValue().set(key, jsonStr);
        stringRedisTemplate.expire(key, 3, TimeUnit.MINUTES);
        return Result.ok(basePage);
    }

    /**
     * @param id 按课程id查询
     * @return
     */
    @Override
    public Result getCourseById(String id) {
        if (StrUtil.isEmpty(id)) {
            return Result.fail("访问失败!");
        }
        //按id获取基本课程信息
        CourseBase courseBase = query().eq("id", id).one();
        if (courseBase == null) {
            OnlineXuePlusException.cast("数据库没有这数据");
            return Result.fail("数据库没有这数据");
        }
        CreateCourseBase(courseBase, "select");
        CoursePublish coursePublish = new CoursePublish();
        BeanUtil.copyProperties(courseBase, coursePublish);
        //按id获取付费信息
        CourseMarket courseMarket = courseMarketService.query().eq("course_id", id).one();
        if (!ObjectUtil.isEmpty(courseMarket)) {
            coursePublish.setPrice(courseMarket.getPrice());
            coursePublish.setOriginalPrice(courseMarket.getOriginalPrice());
            coursePublish.setValidDays(courseMarket.getValidDays());
        }
        //获取课程讲师信息
        CourseTeacher courseTeacher = courseTeacherService.query().eq("course_id", id).one();
        if (!ObjectUtil.isEmpty(courseTeacher)) {
            coursePublish.setTeacherName(courseTeacher.getTeacherName());
            coursePublish.setPosition(courseTeacher.getPosition());
            coursePublish.setPhotograph(courseTeacher.getPhotograph());
        }
        return Result.ok(coursePublish);
    }

    public void CreateCourseBase(CourseBase courseBase, String status) {
        String gradenum = "";
        String teachmodenum = "";
        String grade = courseBase.getGrade();//获取课程级别
        String teachmode = courseBase.getTeachmode();//获取课程播放方式
        if ("select".equals(status)) {
            //查询(code值转化为name)
            CourseDictionary courseGrade = courseDictionaryService.getById(grade);
            gradenum = courseGrade.getName();//获取课程级别名称
            CourseDictionary courseTeachmode = courseDictionaryService.getById(teachmode);//获取课程播放方式
            teachmodenum = courseTeachmode.getName();
        } else if ("update".equals(status)) {
            //修改(前端传来的值转化为code)
            CourseDictionary courseGrade = courseDictionaryService.query().eq("name", grade).one();
            gradenum = courseGrade.getCode();
            CourseDictionary courseTeachmode = courseDictionaryService.query().eq("name", teachmode).one();
            teachmodenum = courseTeachmode.getCode();
        }
        courseBase.setGrade(gradenum);
        courseBase.setTeachmode(teachmodenum);

    }


    /**
     * 更新课程信息
     *
     * @param coursePublish
     * @return
     */
    @Transactional
    @Override
    public Result couresUpdata(CoursePublish coursePublish) {
        if (coursePublish == null) {
            OnlineXuePlusException.cast("系统在维护!");
        }
        //更新课程基本表
        CourseBase courseBase = new CourseBase();
        BeanUtil.copyProperties(coursePublish, courseBase);
        CreateCourseBase(courseBase, "update");
        updateById(courseBase);//更新课程信息
        //更新课程价格表
        CourseMarket courseMarket = new CourseMarket();
        courseMarket.setCourseId(courseBase.getId());
        courseMarket.setOriginalPrice(coursePublish.getOriginalPrice());
        courseMarket.setPrice(coursePublish.getPrice());
        courseMarket.setValidDays(coursePublish.getValidDays());
        courseMarketService.saveOrUpdate(courseMarket, new LambdaQueryWrapper<CourseMarket>().eq(CourseMarket::getCourseId, courseBase.getId()));
        return Result.ok();
    }

    /**
     * 删除课程
     *
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public Result couresDelete(List<String> ids) {
        if (ids.isEmpty()) {
            return Result.fail("请选中数据删除!");
        }
        removeBatchByIds(ids);//批量删除课程信息
        ids.forEach(item -> {
            //2.删除课程价格信息
            courseMarketService.remove(new LambdaQueryWrapper<CourseMarket>().eq(CourseMarket::getCourseId, item));
            //3.删除课程教师信息
            courseTeacherService.remove(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, item));
        });
        //4.删除redis缓存
        deleteKeysWithPatternUsingScan(Course_Base_Page);
        return Result.ok();
    }

    /**
     * 课程添加
     *
     * @param coursePublish
     * @return
     */
    @Transactional
    @Override
    public Result courseAdd(CoursePublish coursePublish, HttpServletRequest request) {
        String token = request.getHeader("token");//拿起token
        String tokenKey = LOGIN_MASTER_KEY + token;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (ObjectUtil.isEmpty(entries)) {
            //在redis拿到的是null
            return Result.fail("登录身份过期,请重新登录!", "0");
        }
        Master master = BeanUtil.fillBeanWithMap(entries, new Master(), false);//拿到用户信息
        if (ObjectUtil.isEmpty(coursePublish)) {
            return Result.fail("请填写数据");
        }
        CourseBase courseBase = new CourseBase();//课程基本信息
        CourseMarket courseMarket = new CourseMarket();//课程售价信息
        BeanUtil.copyProperties(coursePublish, courseBase);
        CreateCourseBase(courseBase, "update");
        courseBase.setCreatePeople(master.getUsername());
        courseBase.setChangePeople(master.getUsername());
        //新增课程信息
        save(courseBase);
        String id = courseBase.getId();//课程id
        BeanUtil.copyProperties(coursePublish, courseMarket);
        courseMarket.setCourseId(id);
        courseMarketService.save(courseMarket);
        deleteKeysWithPatternUsingScan(Course_Base_Page);
        stringRedisTemplate.expire(tokenKey, 60, TimeUnit.MINUTES);//重置一个小时
        return Result.ok(id);
    }


    /**
     * 获取热门课程
     *
     * @return
     */
    public void deleteKeysWithPatternUsingScan(String prefix) {
        List<String> keysToDelete = new ArrayList<>();
        // 构造匹配模式
        String pattern = prefix + "*";
        // 使用execute方法访问底层连接执行scan命令
        stringRedisTemplate.execute((RedisCallback<Object>) connection -> {
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build());
            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                String keyStr = new String(key, StandardCharsets.UTF_8); // 将字节数组转换为字符串
                keysToDelete.add(keyStr);
            }
            try {
                cursor.close(); // 关闭游标
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
        // 删除找到的键
        if (!keysToDelete.isEmpty()) {
            stringRedisTemplate.delete(keysToDelete);
        }
    }


    @Override
    @Transactional
    public Result courseSubmit(CourseBase courseBase) {
        if (ObjectUtil.isEmpty(courseBase)) {
            return Result.fail("课程不存在");
        }
        courseBase.setAuditStatus(NOW_REVIEWED);
        updateById(courseBase);
        return Result.ok();
    }

    @Override
    @Transactional
    public Result coursePublish(List<String> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return Result.fail("服务器在维护中!");
        }
        List<CourseBase> courseBaseList = listByIds(ids);
        List<CoursePublish> coursePublishList = new ArrayList<>();
        for (CourseBase courseBase : courseBaseList) {
            if (!(REVIEWED).equals(courseBase.getAuditStatus())) {
                return Result.fail("审核未通过,不能发布!");
            }
            courseBase.setStatus(RELEASED);
            CoursePublish coursePublish = new CoursePublish();
            BeanUtil.copyProperties(courseBase, coursePublish);
            String courseBaseId = courseBase.getId();
            createPublish(courseBaseId, coursePublish);
            coursePublishList.add(coursePublish);
        }
        coursePublishService.saveBatch(coursePublishList);
        updateBatchById(courseBaseList);
        return Result.ok();
    }

    @Override
    @Transactional
    public Result courseOffline(List<String> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return Result.fail("服务器在维护中!");
        }
        List<CourseBase> courseBaseList = listByIds(ids);
        List<String> coursePublishIds = new ArrayList<>();
        for (CourseBase courseBase : courseBaseList) {
            if (!(RELEASED).equals(courseBase.getStatus())) {
                return Result.fail("课程未发布!");
            }
            courseBase.setStatus(UNRELEASED);
            coursePublishIds.add(courseBase.getId());
        }
        updateBatchById(courseBaseList);
        coursePublishService.removeBatchByIds(coursePublishIds);
        return Result.ok();
    }

    private void createPublish(String courseBaseId, CoursePublish coursePublish) {
        CourseMarket courseMarket = courseMarketService.query().eq("course_id", courseBaseId).one();
        if (!ObjectUtil.isEmpty(courseMarket)) {
            coursePublish.setPrice(courseMarket.getPrice());
            coursePublish.setOriginalPrice(courseMarket.getOriginalPrice());
            coursePublish.setValidDays(courseMarket.getValidDays());
        }
        //获取课程讲师信息
        CourseTeacher courseTeacher = courseTeacherService.query().eq("course_id", courseBaseId).one();
        if (!ObjectUtil.isEmpty(courseTeacher)) {
            coursePublish.setTeacherName(courseTeacher.getTeacherName());
            coursePublish.setPosition(courseTeacher.getPosition());
            coursePublish.setPhotograph(courseTeacher.getPhotograph());
        }
    }
}
