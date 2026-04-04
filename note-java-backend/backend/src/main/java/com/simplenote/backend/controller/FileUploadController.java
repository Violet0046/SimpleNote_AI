package com.simplenote.backend.controller;

import com.simplenote.backend.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {

    // 定义一个你电脑上的绝对路径，专门用来存上传的图片
    private static final String UPLOAD_DIR = "D:/simplenote_uploads/";

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        // 1. 检查文件是不是空的（比如用户没选文件就点了上传）
        if (file.isEmpty()) {
            return Result.error("上传失败：请选择一个文件");
        }

        // 2. 获取文件的原始名字
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("上传失败：文件名无效");
        }
        // 3. 验证文件后缀名是否合法（只允许图片格式）
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return Result.error("上传失败：文件没有后缀名");
        }
        String extension = originalFilename.substring(lastDotIndex).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
        if (!allowedExtensions.contains(extension)) {
            return Result.error("上传失败：仅支持 jpg, jpeg, png, gif, webp 格式的图片");
        }

        // 4. 生成全网唯一的 UUID 文件名 (防止张三和李四都传了 1.jpg 导致相互覆盖)
        String newFileName = UUID.randomUUID().toString() + extension;

        // 5. 确保你电脑上存在这个文件夹，如果没有，Java 会自动帮你建一个
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 6. 把内存中的图片流，真正写入到你电脑的硬盘里！
        File destFile = new File(UPLOAD_DIR + newFileName);
        file.transferTo(destFile);

        // 7. 拼装出这张图片的网络访问 URL (假设你的后端跑在 8080 端口)
        String imageUrl = "http://localhost:8080/uploads/" + newFileName;

        // 8. 把这个完美的 URL 还给前端！
        return Result.success(imageUrl);
    }
}