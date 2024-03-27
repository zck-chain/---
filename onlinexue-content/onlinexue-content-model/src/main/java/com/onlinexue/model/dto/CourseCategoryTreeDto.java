package com.onlinexue.model.dto;

import com.onlinexue.model.dao.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    //子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
