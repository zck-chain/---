package com.onlinexue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.Master;
import com.onlinexue.model.dto.MasterDto;

import javax.servlet.http.HttpServletRequest;

public interface MasterService extends IService<Master> {
    /**
     * 管理员登录
     *
     * @param masterDto
     * @param request
     * @return
     */
    Result masterlogin(MasterDto masterDto, HttpServletRequest request);

    Result masterloginout(String token);
}
