package com.wsw.multihttprequestclient.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
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
                try {
                    log.info("第" + finalI + "个POST请求开始...");
                    ResponseEntity<String> response = postRequest(finalI);
                    System.out.println(response);
                    log.info("第" + finalI + "个POST请求结束...");
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
    }

    public ResponseEntity<String> postRequest(int number) throws Exception {
        ResponseEntity<String> response;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("number", number);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
            response = restTemplate.exchange(postRequestUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            throw new Exception("服务调用异常：" + e.getMessage());
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("调用返回 HTTP status " + response.getStatusCode());
        }

        return response;
    }
}
