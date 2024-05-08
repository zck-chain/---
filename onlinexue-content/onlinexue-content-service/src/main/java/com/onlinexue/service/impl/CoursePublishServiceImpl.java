package com.onlinexue.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CoursePublishMapper;
import com.onlinexue.model.dao.CourseBase;
import com.onlinexue.model.dao.CourseChapter;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.model.dao.CourseReviews;
import com.onlinexue.model.dto.CourseBasePage;
import com.onlinexue.model.dto.CourseChapterDto;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private CourseChapterService courseChapterService;//课程视频信息
    @Autowired
    private CourseReviewsService courseReviewsService;//评论信息

    private static AtomicInteger numberofviews;//浏览数

    @Override
    public Result coursePublishList(FormInline formInline) {
        int page = formInline.getPage();
        int limit = formInline.getLimit();
        String id = formInline.getId();
        String gmtCreateSort = formInline.getGmtCreateSort();//创建时间排序
        String key = Course_Base_Page + page + ":" + limit;
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
    @Transactional
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
        //评论信息
        List<CourseReviews> courseReviewsList = courseReviewsService.list(new LambdaQueryWrapper<CourseReviews>().eq(CourseReviews::getCourseId, id));
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("coursedata", coursePublish);//课程基本信息
        jsonObject.set("chapterVideoList", courseChapterDtoList);//课程信息
        jsonObject.set("reviewsdata", courseReviewsList);//课程评论信息
        //获取浏览数
        numberofviews = new AtomicInteger(coursePublish.getNumberofviews());
        int addAndGet = numberofviews.addAndGet(1);
        update(new LambdaUpdateWrapper<CoursePublish>().eq(CoursePublish::getId, id).set(CoursePublish::getNumberofviews, addAndGet));
        courseBaseService.update(new LambdaUpdateWrapper<CourseBase>().eq(CourseBase::getId, id).set(CourseBase::getNumberofviews, addAndGet));
        return Result.ok(jsonObject);
    }

    @Override
    public Result getPopularCoursesList() {
        //获取课程评分前5,评分相同看创建时间最新的
        List<CoursePublish> list = list(new LambdaQueryWrapper<CoursePublish>().orderByDesc(CoursePublish::getStart).orderByDesc(CoursePublish::getCreateDate).last("limit 8"));
        return Result.ok(list);
    }

    @Override
    public Result addComment(CourseReviews courseReviews) {
        if (courseReviews == null) {
            return Result.fail("请重新登录!");
        }
        courseReviewsService.save(courseReviews);
        return Result.ok();
    }

    @Override
    public Result getReviewsList(Long page, Long limit, String courseId) {
        IPage<CourseReviews> ipage = new Page<>(page, limit);
        LambdaQueryWrapper<CourseReviews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReviews::getCourseId, courseId).orderByDesc(CourseReviews::getCreateDate);
        IPage<CourseReviews> courseReviewsIPage = courseReviewsService.page(ipage, wrapper);
        List<CourseReviews> recordsCourseReviews = courseReviewsIPage.getRecords();
        long total = courseReviewsIPage.getTotal();
        //给页数
        // 计算总页数
        int totalPages = (int) Math.ceil((double) total / limit);
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pages.add(i);
        }
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("current", page);
        returnMap.put("total", total);
        returnMap.put("pages", pages);
        returnMap.put("recordsCourseReviews", recordsCourseReviews);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("reviewsList", returnMap);
        return Result.ok(jsonObject);
    }

    @Override
    public Result selectCouse(String courseName) {
        if (StrUtil.isEmpty(courseName)) {
            return Result.fail("请输入查询课程");
        }
        List<CoursePublish> coursePublishList = list(new LambdaQueryWrapper<CoursePublish>().like(CoursePublish::getName, courseName));
        return Result.ok(coursePublishList);
    }
}
