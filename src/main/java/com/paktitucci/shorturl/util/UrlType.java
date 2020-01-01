package com.paktitucci.shorturl.util;

import com.paktitucci.shorturl.code.ExceptionCode;
import com.paktitucci.shorturl.converter.OriginalUrlConverter;
import com.paktitucci.shorturl.converter.ShortUrlConverter;
import com.paktitucci.shorturl.converter.UrlConverter;
import com.paktitucci.shorturl.dto.ConvertResult;
import com.paktitucci.shorturl.entity.ShortUrlEntity;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Getter
@AllArgsConstructor
public enum UrlType {

    SHORT_URL(Arrays.asList(getLocalhostPrefix()),
            (entity, urlType) -> ConvertResult.builder()
                                              .url(entity.getOriginalUrl())
                                              .urlType(urlType)
                                              .isNewUrl(entity.isGeneratedNewShortUrl())
                                              .build(),
            converters -> converters.stream()
                                    .filter(converter -> converter instanceof OriginalUrlConverter)
                                    .findFirst()
                                    .get()),

    ORIGINAL_URL(Collections.EMPTY_LIST,
            (entity, urlType) -> ConvertResult.builder()
                                              .url(getLocalhostPrefix() + entity.getShortUrl())
                                              .urlType(urlType)
                                              .isNewUrl(entity.isGeneratedNewShortUrl())
                                              .build(),
            converters -> converters.stream()
                                    .filter(converter -> converter instanceof ShortUrlConverter)
                                    .findFirst()
                                    .get());

    private final List<String> urlPrefixes;
    private final BiFunction<ShortUrlEntity, UrlType, ConvertResult> resultMaker;
    private final Function<List<UrlConverter>, UrlConverter> converterGetter;

    public static UrlType checkUrlTypeCode(String url) {
        if(StringUtils.isEmpty(url)) {
            log.error("There is no URL.");
            throw new UrlShorteningException("URL이 입력되지 않았습니다.", ExceptionCode.URL_PARAM_EMPTY.getCode());
        }
        UrlType urlType = isShortUrlPrefix(url) ? SHORT_URL : ORIGINAL_URL;

        return urlType;
    }

    private static String getLocalhostPrefix() {
        return "http://localhost/";
    }

    private static boolean isShortUrlPrefix(String url) {
        return SHORT_URL.urlPrefixes
                        .stream()
                        .anyMatch(prefix -> url.startsWith(prefix));
    }
}
