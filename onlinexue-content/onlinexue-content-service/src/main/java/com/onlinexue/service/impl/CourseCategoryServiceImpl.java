package com.onlinexue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinexue.dto.Result;
import com.onlinexue.mapper.CourseCategoryMapper;
import com.onlinexue.model.dao.CourseCategory;
import com.onlinexue.model.dto.CourseCategoryTreeDto;
import com.onlinexue.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {
    private static final String COURSE_CATEGORY = "course:category:tree:";
    @Resource
    private CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;//redis做缓存

    /**
     * 构建树
     *
     * @param list
     * @return
     */
    public static List<CourseCategoryTreeDto> buildTree(List<CourseCategoryTreeDto> list, String parentId) {
        ArrayList<CourseCategoryTreeDto> treeDtos = new ArrayList<>();
        for (CourseCategoryTreeDto courseCategory : list) {
            if (courseCategory.getParentid().equals(parentId)) {
                List<CourseCategoryTreeDto> children = buildTree(list, courseCategory.getId());
                courseCategory.setChildrenTreeNodes(children);
                treeDtos.add(courseCategory);
            }
        }
        return treeDtos;
    }

    /**
     * @param id 根节点
     * @return 课程分类按Tree结构
     */
    @Override
    public Result queryTreaNode(String id) {
        //通过sql来获取表的所有信息
        List<CourseCategoryTreeDto> categoryTreeDtoList = courseCategoryMapper.selectTreeNodes(id);
        //转换为map来做字典,filter(item->!id.equals(item.getId()))过滤根节点
        Map<String, CourseCategoryTreeDto> treeDtoMap = categoryTreeDtoList.stream().filter((item -> !id.equals(item.getId()))).
                collect(Collectors.toMap((key -> key.getId()), value -> value, (key1, key2) -> key2));

        //定义list做为返回值，filter(item->!id.equals(item.getId()))过滤根节点
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = new ArrayList<>();
        for (int i = 0; i < categoryTreeDtoList.size(); i++) {
            CourseCategoryTreeDto courseCategoryTreeDto = categoryTreeDtoList.get(i);
            if (courseCategoryTreeDto.getId().equals(id)) {
                continue;//找到根节点
            }
            if (courseCategoryTreeDto.getParentid().equals(id)) {
                courseCategoryTreeDtoList.add(courseCategoryTreeDto);
            }
            CourseCategoryTreeDto parent = treeDtoMap.get(courseCategoryTreeDto.getParentid());
            if (parent != null) {
                if (parent.getChildrenTreeNodes() == null) {
                    parent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                parent.getChildrenTreeNodes().add(courseCategoryTreeDto);
            }
        }
        //List<CourseCategoryTreeDto> courseCategoryTreeDtos = buildTree(categoryTreeDtoList, id);
        return Result.ok(courseCategoryTreeDtoList);
    }

    /**
     * 查询所有一级分类
     *
     * @return
     */
    @Override
    public Result getOneTree() {
        List<CourseCategory> list = list(new LambdaQueryWrapper<CourseCategory>().eq(CourseCategory::getParentid, '1'));
        return Result.ok(list);
    }
}
