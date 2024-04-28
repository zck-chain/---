package com.onlinexue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.exception.OnlineXuePlusException;
import com.onlinexue.mapper.CourseDictionaryMapper;
import com.onlinexue.model.dao.CourseDictionary;
import com.onlinexue.service.CourseDictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseDictionaryServiceImpl extends ServiceImpl<CourseDictionaryMapper, CourseDictionary> implements CourseDictionaryService {
    /**
     * 获取课程等级
     *
     * @return
     */
    @Override
    public Result getcourseGrade() {
        List<CourseDictionary> list = list(new LambdaQueryWrapper<CourseDictionary>().like(CourseDictionary::getCode, "cl_"));
        if (list == null) {
            OnlineXuePlusException.cast("服务器在维护");
            return Result.fail("服务器在维护!");
        }
        return Result.ok(list);
    }

    @Override
    public Result getcourseTeachmode() {
        List<CourseDictionary> list = list(new LambdaQueryWrapper<CourseDictionary>().like(CourseDictionary::getCode, "cp_"));
        if (list == null) {
            OnlineXuePlusException.cast("服务器在维护");
            return Result.fail("服务器在维护!");
        }
        return Result.ok(list);
    }

    /**
     * 获取教师的等级
     *
     * @return
     */
    @Override
    public Result getTeacherGrade() {
        List<CourseDictionary> teacherList = list(new LambdaQueryWrapper<CourseDictionary>().like(CourseDictionary::getCode, "th_"));
        if (teacherList == null) {
            OnlineXuePlusException.cast("服务器在维护");
            return Result.fail("服务器在维护!");
        }
        return Result.ok(teacherList);
    }
}
