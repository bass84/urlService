package com.paktitucci.shorturl.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Utils {

    @Value("${codec.base62}")
    private String codec;

    @Value("${short.url.prefix}")
    private String shortUrlPrefix;

    /**
     * sequence를 base62 알고리즘을 이용하여 Shortening URL로 변환.
     */
    public String sequenceToShortUrl(long sequence) {
        StringBuilder resultBuilder = new StringBuilder();
        int codecLength = codec.length();

        do{
            int codecIndex = (int) (sequence % codecLength);
            resultBuilder.append(codec.charAt(codecIndex));
            sequence /= codecLength;
        }while(sequence > 0);

        String newShortUrl = resultBuilder.reverse().toString();

        return newShortUrl;
    }

    /**
     * Shortening URL을 base62 알고리즘을 이용하여 sequence로 변환.
     */
    public long shortUrlToSequence(String url) {
        String shortUrl = url.startsWith(shortUrlPrefix) ? url.substring(shortUrlPrefix.length()) : url;
        int codecLength = codec.length();
        long sequence = 0L;
        for(int i = 0; i < shortUrl.length(); i++) {
            sequence += codec.indexOf(shortUrl.charAt(i)) * (Math.pow(codecLength, shortUrl.length() - 1 - i));
        }

        return sequence;
    }
}
