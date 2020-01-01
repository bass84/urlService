package com.paktitucci.shorturl.exception;

import lombok.Getter;

@Getter
public class UrlShorteningException extends RuntimeException{

    private Integer code;

    public UrlShorteningException() {
        super();
    }
    public UrlShorteningException(String message) {
        super(message);
    }

    public UrlShorteningException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public UrlShorteningException(Throwable throwable) {
        super(throwable);
    }

    public UrlShorteningException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UrlShorteningException(String message, Integer code, Throwable throwable) {
        super(message, throwable);
    }

}
