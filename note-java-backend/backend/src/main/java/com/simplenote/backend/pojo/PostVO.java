package com.simplenote.backend.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostVO extends Post {
    // 继承了 Post 的所有基本字段 (id, title, content, images 等)
    
    // 额外扩展前端需要的展示字段
    private String authorName;   // 作者名字
    private String authorAvatar; // 作者头像
    private Integer likeCount;   // 点赞数
    private String title;         // 帖子标题
    private String images;        // 第一张配图

}