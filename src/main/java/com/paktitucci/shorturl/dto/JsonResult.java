package com.paktitucci.shorturl.dto;

import com.paktitucci.shorturl.code.JsonResultCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonResult<T> {

    private static final JsonResult<Boolean> SUCCESS_RESULT = new JsonResult<>(JsonResultCode.SUCCESS);
    private static final JsonResult<Boolean> FAIL_RESULT = new JsonResult<>(JsonResultCode.FAIL);

    private int code;
    private String message;
    private T body;

    public JsonResult(JsonResultCode jsonResultCode) {
        this.code = jsonResultCode.getCode();
        this.message = jsonResultCode.getMessage();
    }

    public JsonResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResult(T body, int code, String message) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public static JsonResult success() {
        return SUCCESS_RESULT;
    }

    public static <T> JsonResult<T> success(T t) {
        return new JsonResult<>(t,JsonResultCode.SUCCESS.getCode(), JsonResultCode.SUCCESS.getMessage());
    }

    public static JsonResult<Boolean> fail() {
        return FAIL_RESULT;
    }

    public static <T> JsonResult<T> fail(T body) {
        return new JsonResult<>(body, JsonResultCode.FAIL.getCode(), JsonResultCode.FAIL.getMessage());
    }


}
