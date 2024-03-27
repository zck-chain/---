package com.onlinexue.sercvice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinexue.dto.Result;
import com.onlinexue.model.dao.Banner;

public interface BannerService extends IService<Banner> {
    /**
     * 获取所有横幅
     *
     * @return
     */
    Result getAllBanner();
}
