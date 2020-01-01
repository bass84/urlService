package com.paktitucci.shorturl.service.impl;

import com.paktitucci.shorturl.code.ExceptionCode;
import com.paktitucci.shorturl.converter.UrlConverter;
import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.entity.ShortUrlEntity;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import com.paktitucci.shorturl.service.UrlService;
import com.paktitucci.shorturl.util.MessageUtils;
import com.paktitucci.shorturl.util.UrlCorrector;
import com.paktitucci.shorturl.util.UrlType;
import com.paktitucci.shorturl.dto.ConvertResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final UrlCorrector urlCorrector;
    private final List<UrlConverter> urlConverters;
    private final MessageUtils messageUtils;

    @Autowired
    public UrlServiceImpl(UrlCorrector urlCorrector, UrlConverter shortUrlConverter, UrlConverter originalUrlConverter,
                          MessageUtils messageUtils) {
        this.urlCorrector = urlCorrector;
        this.urlConverters = Arrays.asList(shortUrlConverter, originalUrlConverter);
        this.messageUtils = messageUtils;
    }

    /**
     * URL을 변환한다.
     * 1. Original URL이 들어오면 Shortening URL을 반환한다.
     * 2. 'http://localhost'로 시작하는 Shortening URL이 들어오면 Original URL을 반환한다.
     */
    @Transactional
    @Override
    public ConvertResult convertUrl(ShortUrlParam param) {
        try {
            return this.convertAndSaveUrl(param);
        }catch(UrlShorteningException e) {
            throw e;
        }catch(RuntimeException e) {
            log.error("Unknown exception occurred. message = [{}]", e.getMessage());
            throw new UrlShorteningException(messageUtils.getMessage(ExceptionCode.UNKNOWN_EXCEPTION.getMessageKey()),
                    ExceptionCode.UNKNOWN_EXCEPTION.getCode());
        }
    }

    /**
     * 위 convertUrl 메서드의 실제 수행 메서드. try문의 wrapping 메서드이다.
     */
    private ConvertResult convertAndSaveUrl(ShortUrlParam param) {
        urlCorrector.correctUrlFormat(param);
        UrlType urlType = UrlType.checkUrlTypeCode(param.getUrl());
        UrlConverter urlConverter = urlType.getConverterGetter()
                .apply(urlConverters);
        ShortUrlEntity convertedUrlEntity = urlConverter.convert(param);

        ConvertResult result = urlType.getResultMaker()
                                      .apply(convertedUrlEntity, urlType);

        return result;
    }

    /**
     * 사용자가 입력한 Shortening URL을 이용해 Original URL을 조회한다.
     */
    @Override
    public String getOriginalUrl(String shortUrl) {
        UrlConverter urlConverter = UrlType.SHORT_URL
                                           .getConverterGetter()
                                           .apply(urlConverters);
        ShortUrlEntity convertedUrlEntity = urlConverter.convert(ShortUrlParam.of(shortUrl));

        return convertedUrlEntity.getOriginalUrl();
    }

}
