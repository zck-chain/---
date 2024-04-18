package com.onlinexue.media.api;

import com.onlinexue.dto.Result;
import com.onlinexue.sercvice.BannerService;
import com.onlinexue.sercvice.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MediaController {
    @Autowired
    private MediaService mediaService;

    @Autowired
    private BannerService bannerService;


    //上传图片
    @PostMapping("/upload/{id}")
    public Result upload(@RequestParam("file") MultipartFile file, @PathVariable("id") String id, @RequestParam(defaultValue = "userIcon") String status, HttpServletRequest request) {
        return mediaService.upload(file, id, status, request);
    }

    /**
     * 获取欢迎页
     *
     * @return
     */
    @GetMapping("/getAllBanner")
    public Result getAllBanner() {
        return bannerService.getAllBanner();
    }

    /**
     * 上传小节视频
     *
     * @return
     */
    @PostMapping("/uploadVideo/{courseId}/{chapterIndex}/{sectionIndex}")
    public Result uploadVideo(@PathVariable String courseId, @PathVariable int chapterIndex, @PathVariable int sectionIndex, @RequestParam("video") MultipartFile videoFile) {
        return mediaService.uploadVideo(courseId, chapterIndex, sectionIndex, videoFile);
    }

}
