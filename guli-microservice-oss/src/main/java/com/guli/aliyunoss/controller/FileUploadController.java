package com.guli.aliyunoss.controller;

import com.guli.aliyunoss.impl.FileServcieImpl;
import com.guli.aliyunoss.service.FileService;
import com.guli.common.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api(description="阿里云文件管理")
@RestController
@RequestMapping("/admin/oss/file")
@CrossOrigin //跨域
public class FileUploadController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     *
     * @param file
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    public R upload(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file) {

        String uploadUrl = null;
        try {
            uploadUrl = fileService.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回r对象
        return R.ok().message("文件上传成功").data("url", uploadUrl);

    }
}