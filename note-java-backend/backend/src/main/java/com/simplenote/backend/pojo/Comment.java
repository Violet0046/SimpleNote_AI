package com.simplenote.backend.pojo;
import lombok.Data;
import java.time.LocalDateTime;
// 评论实体类
@Data
public class Comment {
    private Long id;    // 评论ID
    private Integer postId;   // 评论所属的帖子ID
    private Integer userId;   // 评论人ID
    private String content;   // 评论内容
    private Long parentId;    // 父评论ID，0表示一级评论
    private Integer replyToUserId;    // 被回复人的ID，一级评论可不填
    private String ipLocation;       // 定格发布时的IP
    private Integer isDeleted;       // 软删除标志，0正常，1已删除
    private LocalDateTime createTime;   // 创建时间
}