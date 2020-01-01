package com.paktitucci.shorturl.util;

import com.paktitucci.shorturl.code.ExceptionCode;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class UrlEncodingUtil {

    private final MessageUtils messageUtils;

    @Autowired
    public UrlEncodingUtil(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    /**
     * 리다이렉트 하는 URL을 인코딩한다.
     */
    public String encodeRedirectUrl(String url) {
        char[] redirectUrlChars = url.toCharArray();

        for (int i = 0; i < redirectUrlChars.length; i++) {
            if (!isKoreanChar(redirectUrlChars[i])) {
                continue;
            }
            url = encodeKoreanChar(url, String.valueOf(redirectUrlChars[i]));
        }

        return url;
    }

    /**
     * 한글 문자인지 체크한다.
     * */
    private boolean isKoreanChar(char c) {
        return c >= '\uAC00' && c <= '\uD7A3';
    }


    /**
     * 한글 문자를 인코딩한다.
     */
    private String encodeKoreanChar(String url, String targetText) {
        try {
            url = url.replace(targetText, URLEncoder.encode(targetText, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            log.error("Error while processing Korean Character encoding. message = [{}]", e.getMessage());
            throw new UrlShorteningException(
                    messageUtils.getMessage(ExceptionCode.KOREAN_CHAR_ENCODE_EXCEPTION.getMessageKey()),
                    ExceptionCode.KOREAN_CHAR_ENCODE_EXCEPTION.getCode());
        }
        return url;
    }
}
