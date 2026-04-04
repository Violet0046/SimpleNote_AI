package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;
// 用户点赞实体类
@Data
public class UserLikes {
    private Integer id;
    private Integer userId;  // 点赞人ID
    private Integer postId;  // 被点赞的笔记ID
    private LocalDateTime createTime;  // 点赞时间
}