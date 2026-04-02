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
    // Windows，可以是 "D:/simplenote_uploads/" （注意路径最后要有一个斜杠）
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
        
        // 3. 提取文件后缀名 (比如 ".jpg" 或 ".png")
        // originalFilename.substring(...) 会截取从最后一个点开始到末尾的字符串
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
        if (!allowedExtensions.contains(extension)) {
            return Result.error("上传失败：请上传图片");
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
        // ⚠️ 注意这里的 "/uploads/" 是一个虚拟路径，我们下一步去配置它
        String imageUrl = "http://localhost:8080/uploads/" + newFileName;

        // 8. 把这个完美的 URL 还给前端！
        return Result.success(imageUrl);
    }
}