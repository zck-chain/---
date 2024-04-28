package com.onlinexue.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.exception.OnlineXuePlusException;
import com.onlinexue.mapper.CourseTeacherMapper;
import com.onlinexue.model.dao.CoursePublish;
import com.onlinexue.model.dao.CourseTeacher;
import com.onlinexue.model.dto.FormInline;
import com.onlinexue.service.CourseBaseService;
import com.onlinexue.service.CoursePublishService;
import com.onlinexue.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlinexue.util.RedisConstants.Course_Base_Page;

@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {
    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private CoursePublishService coursePublishService;

    /**
     * 按课程id查询教师信息
     *
     * @param courseId 课程id
     * @return
     */
    @Override
    public Result courseTeacherByid(String courseId) {
        if (ObjectUtil.isEmpty(courseId)) {
            return Result.ok();
        }
        CourseTeacher teacher = query().eq("course_id", courseId).one();
        if (teacher == null) {
            return Result.fail("暂时没有教师!");
        }

        return Result.ok(teacher);
    }

    /**
     * 更新课程教师信息
     *
     * @param courseTeacher
     * @param
     * @return
     */
    @Transactional
    @Override
    public Result courseTeacherUpdataorAdd(CourseTeacher courseTeacher) {
        if (courseTeacher == null) {
            OnlineXuePlusException.cast("参数有问题!");
            return null;
        }
        if (StrUtil.isEmpty(courseTeacher.getId())) {
            //表示新增
            save(courseTeacher);
        } else {
            updateById(courseTeacher);
        }
        courseBaseService.deleteKeysWithPatternUsingScan(Course_Base_Page);
        return Result.ok();
    }

    /**
     * 返回热门的讲师
     *
     * @return
     */
    @Override
    public Result getPopularTeacherList() {
        List<CourseTeacher> list = list(new LambdaQueryWrapper<CourseTeacher>().orderByDesc(CourseTeacher::getCreateDate).last("limit 4"));
        return Result.ok(list);
    }

    /**
     * 查询教师的所有信息
     *
     * @param formInline
     * @return
     */
    @Override
    public Result getTeacherList(FormInline formInline) {
        String selectname = formInline.getSelectname();
        int current = formInline.getPage();
        int limit = formInline.getLimit();
        Page page = new Page<CourseTeacher>(current, limit);
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        if (!StrUtil.isEmpty(selectname)) {
            wrapper.eq(CourseTeacher::getPosition, selectname);
        }
        Page teacherPage = page(page, wrapper);
        Map<String, Object> map = new HashMap<>();
        long total = teacherPage.getTotal();
        map.put("total", total);
        map.put("recodes", teacherPage.getRecords().stream().distinct().collect(Collectors.toList()));
        map.put("current", current);
        // 计算总页数
        int totalPages = (int) Math.ceil((double) total / limit);
        map.put("pages", totalPages);
        return Result.ok(map);
    }

    /**
     * 按id查询教师信息
     *
     * @param teacherId
     * @return
     */
    @Override
    public Result getTeacherById(String teacherId) {
        if (StrUtil.isEmpty(teacherId)) {
            return Result.fail("数据报错");
        }
        CourseTeacher teacher = getById(teacherId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("teacher", teacher);
        //获取老师讲的课程
        String teacherName = teacher.getTeacherName();
        List<String> courseIdList = new ArrayList<>();
        list(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getTeacherName, teacherName)).forEach(item -> {
            String courseId = item.getCourseId();
            courseIdList.add(courseId);
        });
        List<CoursePublish> coursePublishList = coursePublishService.listByIds(courseIdList);
        map.put("courseList", coursePublishList);
        return Result.ok(map);
    }

}
