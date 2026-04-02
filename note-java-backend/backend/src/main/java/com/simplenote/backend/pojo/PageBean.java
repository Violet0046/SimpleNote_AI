package com.simplenote.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean<T> {
    private Long total;      // 总记录数（告诉前端一共有多少条）
    private List<T> items;   // 当前页面的数据列表
}