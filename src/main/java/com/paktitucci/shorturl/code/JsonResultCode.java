package com.paktitucci.shorturl.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JsonResultCode {
    SUCCESS(true, 0, "success"),
    FAIL(false, -9000, "fail");

    private final boolean isSuccess;
    private final int code;
    private final String message;

}
