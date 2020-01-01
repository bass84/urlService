package com.paktitucci.shorturl.dto;


import com.paktitucci.shorturl.util.UrlType;
import lombok.*;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class ConvertResult {
    private String url;
    private UrlType urlType;
    private boolean isNewUrl;
}
