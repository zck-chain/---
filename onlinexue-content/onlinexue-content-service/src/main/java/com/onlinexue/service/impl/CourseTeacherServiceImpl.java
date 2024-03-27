package com.onlinexue.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.exception.OnlineXuePlusException;
import com.onlinexue.mapper.CourseTeacherMapper;
import com.onlinexue.model.dao.CourseTeacher;
import com.onlinexue.service.CourseBaseService;
import com.onlinexue.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.onlinexue.util.RedisConstants.Course_Base_Page;

@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {
    @Autowired
    private CourseBaseService courseBaseServicea;

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
        courseBaseServicea.deleteKeysWithPatternUsingScan(Course_Base_Page);
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

}
