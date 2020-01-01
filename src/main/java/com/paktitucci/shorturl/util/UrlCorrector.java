package com.paktitucci.shorturl.util;

import com.paktitucci.shorturl.code.ExceptionCode;
import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UrlCorrector {

    private final static String URL_FORMAT = "^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/?([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$";

    @Autowired
    private MessageUtils messageUtils;

    /**
     * 사용자가 입력한 URL이 유효한지 체크하고 교정해준다.
     * 1. 유효한 형식의 URL인지 확인한다.
     * 2. URL 끝에 slash(/)가 있으면 slash를 삭제한다.
     * */
    public void correctUrlFormat(ShortUrlParam param) {
        this.validateUrl(param.getUrl());
        String removedSlashEndOfUrl = this.removeSlashEndOfUrl(param.getUrl());
        param.setUrl(removedSlashEndOfUrl);
    }

    /**
     * 유효한 형식의 URL인지 확인한다.
     */
    private void validateUrl(String url) {
        if(StringUtils.isEmpty(url)) {
            log.error(messageUtils.getMessage(ExceptionCode.URL_PARAM_EMPTY));
            throw new UrlShorteningException(messageUtils.getMessage(ExceptionCode.URL_PARAM_EMPTY),
                    ExceptionCode.URL_PARAM_EMPTY.getCode());
        }
        String trimUrl = url.trim();

        Pattern urlPattern = Pattern.compile(URL_FORMAT);
        Matcher matcher = urlPattern.matcher(trimUrl);
        if(!matcher.matches()) {
            log.error(messageUtils.getMessage(ExceptionCode.NO_MATCH_URL_FORMAT));
            throw new UrlShorteningException(messageUtils.getMessage(ExceptionCode.NO_MATCH_URL_FORMAT),
                    ExceptionCode.NO_MATCH_URL_FORMAT.getCode());
        }
    }

    /**
     * URL 끝에 slash(/)가 있으면 slash를 삭제한다.
     */
    private String removeSlashEndOfUrl(String correctFormatUrl) {
        String endCharOfUrl = this.getEndCharOfUrl(correctFormatUrl);
        String removedSlashEndOfUrl = endCharOfUrl.equals("/") ?
                correctFormatUrl.substring(0, correctFormatUrl.length() - 1) : correctFormatUrl;

        return removedSlashEndOfUrl;
    }

    /**
     * URL 끝의 문자를 가져온다.
     */
    private String getEndCharOfUrl(String correctFormatUrl) {
        String engCharOfUrl = correctFormatUrl.substring(correctFormatUrl.length() - 1);

        return engCharOfUrl;
    }


}
