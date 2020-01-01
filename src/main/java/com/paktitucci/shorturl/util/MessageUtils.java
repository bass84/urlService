package com.paktitucci.shorturl.util;

import com.paktitucci.shorturl.code.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtils {

    private final MessageSource messageSource;

    @Autowired
    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public String getMessage(String messageKey) {
        ExceptionCode exceptionCode = ExceptionCode.getExceptionCodeByMessageKey(messageKey);
        return this.getMessage(exceptionCode);
    }

    public String getMessage(int code) {
        ExceptionCode exceptionCode = ExceptionCode.getExceptionCodeByCode(code);
        return this.getMessage(exceptionCode);
    }


    public String getMessage(ExceptionCode exceptionCode) {
        String message = messageSource.getMessage(exceptionCode.getMessageKey(), null, Locale.KOREA);
        return message;
    }





}
