package com.simplenote.backend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private String images;
    private String ipLocation;
    private Integer likesCount;
    private LocalDateTime createTime;

    @JsonProperty("isVideo")
    private Boolean isVideo;
}
