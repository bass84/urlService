package com.paktitucci.shorturl.converter;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.paktitucci.shorturl.code.ExceptionCode;
import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.entity.ShortUrlEntity;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import com.paktitucci.shorturl.service.ShortUrlService;
import com.paktitucci.shorturl.service.UrlCheckService;
import com.paktitucci.shorturl.util.Base62Utils;
import com.paktitucci.shorturl.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ShortUrlConverter implements UrlConverter{

    @Value("${codec.base62.value.min}")
    private long codecMinValue;

    @Value("${codec.base62.value.max}")
    private long codecMaxValue;


    private final UrlCheckService urlCheckService;
    private final MessageUtils messageUtils;
    private final ShortUrlService shortUrlService;
    private final Base62Utils base62Utils;

    @Autowired
    public ShortUrlConverter(UrlCheckService urlCheckService, MessageUtils messageUtils,
                             ShortUrlService shortUrlService, Base62Utils base62Utils) {
        this.urlCheckService = urlCheckService;
        this.messageUtils = messageUtils;
        this.shortUrlService = shortUrlService;
        this.base62Utils = base62Utils;
    }


    /**
     * Original URL을 Shortening URL로 변환하는 기능.
     */
    @Override
    public ShortUrlEntity convert(ShortUrlParam param) {
        String originalUrlHash = Hashing.sha1().hashString(param.getUrl(), Charsets.UTF_8).toString();
        ShortUrlEntity alreadyExistsShortUrl = this.getAlreadyExistsShortUrlEntity(originalUrlHash);
        if(alreadyExistsShortUrl != null) {
            return alreadyExistsShortUrl;
        }

        ShortUrlEntity newShortUrlEntity = this.makeShortUrlEntity(param.getUrl(), originalUrlHash);
        return newShortUrlEntity;
    }

    /**
     * 이미 변환되어 있는 URL인지 확인한다.
     */
    private ShortUrlEntity getAlreadyExistsShortUrlEntity(String originalUrlHash) {
        ShortUrlEntity alreadyExistsShortUrl = urlCheckService.getShortUrlIfExists(originalUrlHash);
        if(alreadyExistsShortUrl == null) {
            return null;
        }
        String shortUrl = base62Utils.sequenceToShortUrl(alreadyExistsShortUrl.getSequence());

        return ShortUrlEntity.builder()
                .shortUrl(shortUrl)
                .sequence(alreadyExistsShortUrl.getSequence())
                .originalUrl(alreadyExistsShortUrl.getOriginalUrl())
                .originalUrlHash(alreadyExistsShortUrl.getOriginalUrlHash())
                .isGeneratedNewShortUrl(Boolean.FALSE)
                .build();
    }

    /**
     * DB에서 생성한 sequence를 이용해서 base62 알고리즘으로 Shortening URL을 생성한다.
     */
    private ShortUrlEntity makeShortUrlEntity(String originalUrl, String originalUrlHash) {
        ShortUrlEntity shortUrlEntity = this.saveNewShortUrl(originalUrl, originalUrlHash);
        this.validateSequence(shortUrlEntity.getSequence());
        log.info("sequence encoding start. sequence = [{}]", shortUrlEntity.getSequence());

        String shortUrl = base62Utils.sequenceToShortUrl(shortUrlEntity.getSequence());
        log.info("new short url = {}", shortUrl);

        ShortUrlEntity result = ShortUrlEntity.builder()
                                              .sequence(shortUrlEntity.getSequence())
                                              .shortUrl(shortUrl)
                                              .originalUrl(shortUrlEntity.getOriginalUrl())
                                              .originalUrlHash(shortUrlEntity.getOriginalUrlHash())
                                              .isGeneratedNewShortUrl(shortUrlEntity.isGeneratedNewShortUrl())
                                              .build();

        return result;
    }

    /**
     * 새 Shortening URL을 만들기 위한 sequence를 저장하고 조회한다.
     */
    private ShortUrlEntity saveNewShortUrl(String originalUrl, String originalUrlHash) {
        ShortUrlEntity newShortUrl = ShortUrlEntity.builder()
                                                   .originalUrl(originalUrl)
                                                   .originalUrlHash(originalUrlHash)
                                                   .isGeneratedNewShortUrl(Boolean.TRUE)
                                                   .build();
        ShortUrlEntity shortUrlEntity = shortUrlService.saveShortUrl(newShortUrl);

        return shortUrlEntity;
    }

    /**
     * sequence가 유효한 값인지 체크한다.
     */
    private void validateSequence(long sequence) {
        if(sequence >= codecMinValue && sequence <= codecMaxValue) {
            return;
        }

        log.error(messageUtils.getMessage(ExceptionCode.OUT_OF_RANGE_BASE62_SEQUENCE.getMessageKey()) +
                        " The sequence must be between [{}] and [{}], inclusive. current sequence = [{}].",
                        codecMinValue, codecMaxValue, sequence);
        throw new UrlShorteningException(messageUtils.getMessage(ExceptionCode.OUT_OF_RANGE_BASE62_SEQUENCE.getMessageKey()),
                ExceptionCode.OUT_OF_RANGE_BASE62_SEQUENCE.getCode());
    }




}
