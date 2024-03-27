package com.onlinexue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onlinexue.model.dao.CourseCategory;
import com.onlinexue.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    //使用递归来执行tree查询
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
