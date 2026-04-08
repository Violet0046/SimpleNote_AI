package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @PostMapping("/upload")
    // 必须加上 @RequestParam("file") 才能接到前端的图！
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 加了 file == null 的双重防御，彻底杜绝空指针异常
            if (file == null || file.isEmpty()) {
                return Result.error("上传失败：请选择一个文件");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("上传失败：文件名无效");
            }

            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex == -1) {
                return Result.error("上传失败：文件没有后缀名");
            }

            String extension = originalFilename.substring(lastDotIndex).toLowerCase();
            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".mp4", ".mov", ".avi", ".webm");
            if (!allowedExtensions.contains(extension)) {
                return Result.error("上传失败：仅支持图片(jpg/png)和视频(mp4/mov)格式");
            }

            String newFileName = UUID.randomUUID().toString() + extension;

            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // 在当前 backend 目录下创建 uploads
            }

            // 使用 .getAbsolutePath() 获取真实绝对路径
            File destFile = new File(directory.getAbsolutePath(), newFileName);
            
            file.transferTo(destFile);

            String imageUrl = "http://localhost:8080/uploads/" + newFileName;
            return Result.success(imageUrl);

        } catch (Exception e) {
            //  加上 try-catch，以后就算报错也能在网页看到具体原因，不再报 500！
            e.printStackTrace();
            return Result.error("服务器内部错误：" + e.getMessage());
        }
    }
}