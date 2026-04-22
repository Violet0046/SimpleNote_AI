package com.simplenote.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeStateVO {
    private boolean liked;
    private boolean changed;
    private Integer likeCount;
}
