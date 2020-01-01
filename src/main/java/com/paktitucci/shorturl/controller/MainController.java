package com.paktitucci.shorturl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@Slf4j
public class MainController {

    /**
     * 메인 페이지 이동
     */
    @GetMapping("/")
    public String goIndexPage() {
        return "index";
    }

}
