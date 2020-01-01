package com.paktitucci.shorturl.converter;

import com.paktitucci.shorturl.code.ExceptionCode;
import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.entity.ShortUrlEntity;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import com.paktitucci.shorturl.service.ShortUrlService;
import com.paktitucci.shorturl.util.Base62Utils;
import com.paktitucci.shorturl.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OriginalUrlConverter implements UrlConverter {

    private final ShortUrlService shortUrlService;
    private final MessageUtils messageUtils;
    private final Base62Utils base62Utils;

    @Autowired
    public OriginalUrlConverter(ShortUrlService shortUrlService, MessageUtils messageUtils,
                                Base62Utils base62Utils) {
        this.shortUrlService = shortUrlService;
        this.messageUtils = messageUtils;
        this.base62Utils = base62Utils;
    }


    /**
     * Shortening URL을 Original URL로 변환하는 기능.
     */
    @Override
    public ShortUrlEntity convert(ShortUrlParam param) {
        long sequence = base62Utils.shortUrlToSequence(param.getUrl());
        ShortUrlEntity result = shortUrlService.getShortUrlEntityBySequence(sequence);
        this.validateShortUrlEntity(result, param.getUrl());

        return result;
    }

    /**
     * 사용자가 입력한 Shortening URL이 존재하는지 유효성 체크.
     */
    private void validateShortUrlEntity(ShortUrlEntity resultEntity, String shortUrl) {
        if(resultEntity != null) {
            return;
        }

        log.error(messageUtils.getMessage(ExceptionCode.CONVERTED_SHORT_URL_NOT_EXISTS.getMessageKey()) + "shortUrl = [{}]", shortUrl);
        throw new UrlShorteningException(messageUtils.getMessage(ExceptionCode.CONVERTED_SHORT_URL_NOT_EXISTS.getMessageKey()),
                ExceptionCode.CONVERTED_SHORT_URL_NOT_EXISTS.getCode());
    }
}
