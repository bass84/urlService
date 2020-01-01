package com.paktitucci.shorturl.convert.util;

import com.paktitucci.shorturl.dto.ShortUrlParam;
import com.paktitucci.shorturl.exception.UrlShorteningException;
import com.paktitucci.shorturl.util.Base62Utils;
import com.paktitucci.shorturl.util.UrlEncodingUtil;
import com.paktitucci.shorturl.util.UrlCorrector;
import com.paktitucci.shorturl.util.UrlType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilTest {

    @Autowired
    private UrlCorrector urlCorrector;

    @Autowired
    private UrlEncodingUtil urlEncodingUtil;

    @Autowired
    private Base62Utils base62Utils;

    /**
     * 한글 URL 인코딩하는 테스트.
     * */
    @Test
    public void koreanCharEncodingUtilTest() {
        String url = "https://popit.kr/실무에서-lombok-사용법/";
        String encodeRedirectUrl = urlEncodingUtil.encodeRedirectUrl(url);

        assertThat(encodeRedirectUrl, is("https://popit.kr/%EC%8B%A4%EB%AC%B4%EC%97%90%EC%84%9C-lombok-%EC%82%AC%EC%9A%A9%EB%B2%95/"));
    }


    /**
     * 유효한 형식의 URL인지 체크한다. Exception 발생해야 성공.
     */
    @Test(expected = UrlShorteningException.class)
    public void urlCorrectorExceptionTest() {
        urlCorrector.correctUrlFormat(ShortUrlParam.of("www.daum.net"));
    }


    /**
     * 입력한 URL이 Shortening URL 인지 여부를 체크하는 테스트.
     */
    @Test
    public void shortUrlTypeTest() {
        String url = "http://localhost/dkgb";
        UrlType urlType = UrlType.checkUrlTypeCode(url);
        assertThat(urlType, is(UrlType.SHORT_URL));
    }


    /**
     * sequence를 Shortening URL로 변환하는 base62 알고리즘 테스트.
     */
    @Test
    public void sequenceToShorteningUrlTest() {
        long sequence = 1000097L;
        String shortUrl = base62Utils.sequenceToShortUrl(sequence);
        assertThat(shortUrl, is("emkB"));
    }


    /**
     * sequence를 Shortening URL로 변환하는 base62 알고리즘 테스트.
     */
    @Test
    public void shorteningUrlToSequenceTest() {
        String shortUrl = "emkG";
        long sequence = base62Utils.shortUrlToSequence(shortUrl);
        assertThat(sequence, is(1000102L));
    }
}
