package com.wsw.multihttprequestclient.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author WangSongWen
 * @Date: Created in 16:07 2021/5/20
 * @Description: 客户端---并行且异步发送n个Http Post请求
 */
@Slf4j
@Component
public class MultiHttpRequestClient {
    @Value("${post.request.url}")
    private String postRequestUrl;

    @Resource
    private RestTemplate restTemplate;

    public void asyncSendPostRequest() {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPool.submit(() -> {
                log.info("第" + finalI + "个POST请求开始...");
                try {
                    ResponseEntity<String> response = postRequest();
                    System.out.println(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("第" + finalI + "个POST请求结束...");
            });
        }
        threadPool.shutdown();
    }

    public ResponseEntity<String> postRequest() throws Exception {
        ResponseEntity<String> response;
        Map<String, Object> body = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            response = restTemplate.exchange(postRequestUrl, HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            throw new Exception("服务调用异常：" + e.getMessage());
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("调用返回 HTTP status " + response.getStatusCode());
        }

        return response;
    }
}
