package com.onlinexue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onlinexue.model.dao.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Usermapper extends BaseMapper<User> {
}
