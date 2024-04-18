package com.onlinexue.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CoursePublishMapper;
import com.onlinexue.model.dao.*;
import com.onlinexue.model.dto.CourseBasePage;
import com.onlinexue.model.dto.CourseChapterDto;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.onlinexue.util.CourseUtils.*;
import static com.onlinexue.util.RedisConstants.Course_Base_Page;

/**
 * @author 赵承康
 * @date 2024/3/13
 */
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements CoursePublishService {
    @Autowired
    private CourseBaseService courseBaseService;//课程基本信息
    @Autowired
    private CourseMarketService courseMarketService;//课程售价信息
    @Autowired
    private CourseTeacherService courseTeacherService;//课程教师信息
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private CourseChapterService courseChapterService;//课程视频信息

    @Override
    @Transactional
    public Result coursePublish(CourseBase courseBase) {
        if (ObjectUtil.isEmpty(courseBase)) {
            return Result.fail("服务器在维护中!");
        }
        if (!(REVIEWED).equals(courseBase.getAuditStatus())) {
            return Result.fail("审核未通过,不能发布!");
        }
        courseBase.setStatus(RELEASED);
        CoursePublish coursePublish = new CoursePublish();
        //课程基本信息
        BeanUtil.copyProperties(courseBase, coursePublish);
        String courseBaseId = courseBase.getId();
        BeanUtil.copyProperties(courseBase, coursePublish);
        createPublish(courseBaseId, coursePublish);
        save(coursePublish);
        courseBaseService.updateById(courseBase);
        return Result.ok();
    }

    @Override
    public Result coursePublishList(FormInline formInline) {
        int page = formInline.getPage();
        int limit = formInline.getLimit();
        String id = formInline.getId();
        String gmtCreateSort = formInline.getGmtCreateSort();//创建时间排序
        String key = Course_Base_Page + String.valueOf(page) + ":" + String.valueOf(limit);
        if (StrUtil.isNotEmpty(id) || StrUtil.isNotEmpty(gmtCreateSort)) {
            //删除缓存
            courseBaseService.deleteKeysWithPatternUsingScan(key);
        }
        if (StrUtil.isEmpty(id) && StrUtil.isEmpty(gmtCreateSort)) {
            //删除缓存
            courseBaseService.deleteKeysWithPatternUsingScan(key);
        }
        String redisString = stringRedisTemplate.opsForValue().get(key);
        if (!ObjectUtil.isEmpty(redisString)) {
            //返回redis中保存的数据
            CourseBasePage courseBasePageRedis = JSONUtil.toBean(redisString, CourseBasePage.class);
            return Result.ok(courseBasePageRedis);
        }
        IPage<CoursePublish> coursePublish = new Page<>(page, limit);
        LambdaQueryWrapper<CoursePublish> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(id)) {
            // 判断 id 是 mt 还是 st 字段的值
            wrapper.and(lqw -> lqw.eq(CoursePublish::getMt, id).or().eq(CoursePublish::getSt, id));
            if (StrUtil.isNotEmpty(gmtCreateSort)) {
                wrapper.orderByDesc(CoursePublish::getCreateDate);
            }
        }
        IPage<CoursePublish> coursePublishPage = page(coursePublish, wrapper);
        List<CoursePublish> recordsPublish = coursePublishPage.getRecords();
        long total = coursePublishPage.getTotal();
        //给页数
        // 计算总页数
        int totalPages = (int) Math.ceil((double) total / limit);
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pages.add(i);
        }
        CourseBasePage basePage = new CourseBasePage(page, null, total, pages, recordsPublish);
        String jsonStr = JSONUtil.toJsonStr(basePage);
        stringRedisTemplate.opsForValue().set(key, jsonStr);
        stringRedisTemplate.expire(key, 3, TimeUnit.MINUTES);
        return Result.ok(basePage);
    }

    @Override
    public Result getCoursePublish(String id) {
        if (StrUtil.isEmpty(id)) {
            return Result.fail("数据有问题!");
        }
        CoursePublish coursePublish = getById(id);
        String mt = coursePublish.getMt();//一级
        String mtName = courseCategoryService.getById(mt).getName();
        coursePublish.setMt(mtName);
        String st = coursePublish.getSt();//一级
        String stName = courseCategoryService.getById(st).getName();
        coursePublish.setSt(stName);
        List<CourseChapter> courseChapterList = courseChapterService.list(new LambdaQueryWrapper<CourseChapter>().eq(CourseChapter::getCourseId, id));//课程信息
        List<CourseChapterDto> courseChapterDtoList = CourseChapterServiceImpl.getCourseChapterDtoList(courseChapterList);
        courseChapterDtoList.stream().sorted(Comparator.comparing(CourseChapterDto::getSort));
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("Coursedata", coursePublish);
        jsonObject.set("chapterVideoList", courseChapterDtoList);
        return Result.ok(jsonObject);
    }

    @Override
    public Result getPopularCoursesList() {
        //获取课程评分前5,评分相同看创建时间最新的
        List<CoursePublish> list = list(new LambdaQueryWrapper<CoursePublish>().orderByDesc(CoursePublish::getStart).orderByDesc(CoursePublish::getCreateDate).last("limit 8"));
        return Result.ok(list);
    }

    /**
     * 下架方法
     *
     * @param coursePublish
     * @return
     */
    @Transactional
    @Override
    public Result courseOffline(CoursePublish coursePublish) {
        if (ObjectUtil.isEmpty(coursePublish)) {
            return Result.fail("数据为空!");
        }
        boolean removeById = removeById(coursePublish);
        if (!removeById) {
            return Result.fail("下架失败!");
        }
        String id = coursePublish.getId();
        courseBaseService.lambdaUpdate()
                .set(CourseBase::getStatus, UNRELEASED)
                .eq(CourseBase::getId, id).update();
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
