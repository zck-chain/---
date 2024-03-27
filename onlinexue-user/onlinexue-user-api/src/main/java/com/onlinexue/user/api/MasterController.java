package com.onlinexue.user.api;

import com.onlinexue.dto.Result;
import com.onlinexue.model.dto.MasterDto;
import com.onlinexue.service.MasterService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class MasterController {
    @Autowired
    MasterService masterService;

    @ApiOperation("管理员登录功能")
    @PostMapping("/masterlogin")
    public Result masterlogin(@RequestBody MasterDto masterDto, HttpServletRequest request) {
        return masterService.masterlogin(masterDto, request);
    }

    @ApiOperation("管理员退出功能")
    @DeleteMapping("/masterloginout")
    public Result masterloginout(@RequestParam(value = "token", required = false) String token) {
        return masterService.masterloginout(token);

    }
}
