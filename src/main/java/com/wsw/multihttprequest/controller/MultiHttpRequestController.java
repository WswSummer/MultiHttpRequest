package com.wsw.multihttprequest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author WangSongWen
 * @Date: Created in 16:04 2021/5/20
 * @Description: 服务端---Http Post请求接口
 */
@RestController
@RequestMapping("/wsw")
public class MultiHttpRequestController {
    @PostMapping("/hello")
    public String sayHello(){
        return "hello";
    }
}
