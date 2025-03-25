package com.wishboard.server.common.dto;

import com.wishboard.server.common.success.SuccessCode;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {

    public static final SuccessResponse<String> SUCCESS = success(SuccessCode.SUCCESS, null);

    private int status;
    private boolean success;
    private String message;
    private T data;

    public static <T> SuccessResponse<T> success(SuccessCode successCode, T data) {
        return new SuccessResponse<>(successCode.getStatus(), true, successCode.getMessage(), data);
    }
}
