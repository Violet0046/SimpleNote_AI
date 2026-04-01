package com.simplenote.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code; // 业务状态码：1-成功，0-失败
    private String msg;   // 提示信息
    private T data;       // 真正要返回的数据（比如帖子列表、用户信息）

    public static <E> Result<E> success(E data) {
        return new Result<>(1, "操作成功", data);
    }

    public static <E> Result<E> success() {
        return new Result<>(1, "操作成功", null);
    }
    
    public static <E> Result<E> error(String msg) {
        return new Result<>(0, msg, null);
    }
}
