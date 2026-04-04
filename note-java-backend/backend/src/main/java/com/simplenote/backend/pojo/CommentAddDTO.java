package com.simplenote.backend.pojo;
import lombok.Data;
// 评论添加DTO类
@Data
public class CommentAddDTO {
    private Integer postId;          // 评论的哪篇笔记
    private String content;          // 评论内容
    private Long parentId;           // 父评论ID（如果不传，后端默认为0）
    private Integer replyToUserId;   // 被回复人的ID（可不传）
}