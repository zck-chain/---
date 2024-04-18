package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.CourseTeacher;
import com.onlinexue.model.dto.FormInline;

public interface CourseTeacherService extends IService<CourseTeacher> {
    /**
     * 按课程id查询教师信息
     *
     * @param courseId 课程id
     * @return
     */
    Result courseTeacherByid(String courseId);

    /**
     * 更新课程教师信息
     *
     * @param courseTeacher
     * @return
     */
    Result courseTeacherUpdataorAdd(CourseTeacher courseTeacher);


    Result getPopularTeacherList();

    Result getTeacherList(FormInline formInline);

    Result getTeacherById(String teacherId);
}
