package com.restkeeper.controller;

import com.aliyun.oss.OSSClient;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author MORRIS --> Java
 * @date 2022-12-12 19:37:45
 */
@Slf4j
@RestController
@Api(tags = {"图片上传通用接口"})
@RefreshScope
public class FileUploadController {

    @Value("${bucketName}")
    private String bucketName;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Autowired
    private OSSClient ossClient;

    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        Result result = new Result();
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        try {
            ossClient.putObject(bucketName, fileName, multipartFile.getInputStream());
            String logoPath = "https://" + bucketName + "." + endpoint + "/" + fileName;

            result.setStatus(ResultCode.success);
            result.setDesc("上传成功");
            result.setData(logoPath);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());

            result.setStatus(ResultCode.error);
            result.setDesc("文件上传失败");
            return result;
        }
    }

    @PostMapping(value = "/imageUploadResize")
    @ApiImplicitParam(paramType = "form", dataType = "file", name = "fileName", value = "上传文件", required = true)
    public String imageUploadResize(@RequestParam("fileName") MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String imagePath = "https://" + bucketName + "." + endpoint + "/" + fileName + "?x-oss-process=image/resize,m_fill,h_100,w_200";
        return imagePath;
    }
}
