package com.simplenote.backend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private Integer gender;
    
    private String avatar; 
    private String intro;  
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}