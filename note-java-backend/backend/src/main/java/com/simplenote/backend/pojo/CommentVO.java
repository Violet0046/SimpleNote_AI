package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;                  // 评论ID
    private Integer userId;           // 评论人ID
    private String authorName;        // 评论人昵称
    private String authorAvatar;      // 评论人头像
    private String content;           // 评论内容
    private String ipLocation;        // IP 属地
    
    // 供后端分组使用
    private Long parentId;
    
    // 针对软删除的魔法状态位
    private Integer isDeleted;        
    
    // 回复对象的展示信息（比如：张三 回复 李四）
    private Integer replyToUserId;    // 被回复人ID
    private String replyToUserName;   // 被回复人昵称
    
    private LocalDateTime createTime; // 评论时间
    
    // 统计该一级评论下到底有多少条子评论 (用于前端展示 "展开剩余 X 条回复")
    private Integer childCount;
    
    // 存放这条根评论下的前 3 条子评论预览
    private List<CommentVO> replies;  
}