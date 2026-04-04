package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {

    // 改为动态项目路径，存放在后端项目的 uploads 文件夹下
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    // 必须加上 @RequestParam("file") 才能接到前端的图！
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 3. 加了 file == null 的双重防御，彻底杜绝空指针异常
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
            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
            if (!allowedExtensions.contains(extension)) {
                return Result.error("上传失败：仅支持 jpg, jpeg, png, gif, webp 格式的图片");
            }

            String newFileName = UUID.randomUUID().toString() + extension;

            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); // 如果 uploads 文件夹不存在，自动帮你建一个
            }

            File destFile = new File(UPLOAD_DIR + newFileName);
            file.transferTo(destFile);

            String imageUrl = "http://localhost:8080/uploads/" + newFileName;
            return Result.success(imageUrl);

        } catch (Exception e) {
            // 🌟 4. 加上 try-catch，以后就算报错也能在网页看到具体原因，不再报 500！
            e.printStackTrace();
            return Result.error("服务器内部错误：" + e.getMessage());
        }
    }
}