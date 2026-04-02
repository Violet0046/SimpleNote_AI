package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserLikes {
    private Integer id;
    private Integer userId;
    private Integer postId;
    private LocalDateTime createTime;
}