package com.paktitucci.shorturl.code;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    OUT_OF_RANGE_BASE62_SEQUENCE(-1000, "short.url.base62.range.out"),
    SEQUENCE_GENERATING_EXCEPTION(-1001, "sequence.generating.exception"),
    CONVERTED_SHORT_URL_NOT_EXISTS(-1002, "short.url.no.exists"),
    NO_MATCH_URL_FORMAT(-1003, "no.match.url.format"),
    URL_PARAM_EMPTY(-1004, "url.param.empty"),
    KOREAN_CHAR_ENCODE_EXCEPTION(-1006, "korean.char.encode.exception"),
    UNKNOWN_EXCEPTION(-9999, "unknown.exception");

    private final int code;
    private final String messageKey;

    ExceptionCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public static ExceptionCode getExceptionCodeByCode(int code) {
        for(ExceptionCode exceptionCode : ExceptionCode.values()) {
            if(exceptionCode.code != code) continue;

            return exceptionCode;
        }
        throw new IllegalArgumentException();
    }

    public static ExceptionCode getExceptionCodeByMessageKey(String messageKey) {
        for(ExceptionCode exceptionCode : ExceptionCode.values()) {
            if(!messageKey.equals(exceptionCode.messageKey)) continue;

            return exceptionCode;
        }
        throw new IllegalArgumentException();
    }
}
