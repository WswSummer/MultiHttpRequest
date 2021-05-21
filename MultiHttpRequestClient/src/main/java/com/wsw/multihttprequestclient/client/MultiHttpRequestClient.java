package com.wsw.multihttprequestclient.client;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author WangSongWen
 * @Date: Created in 16:07 2021/5/20
 * @Description: 客户端---并行且异步发送n个Http Post请求
 */
@Component
public class MultiHttpRequestClient {
    private final int coreThreads = 8;
    private final Logger log = LoggerFactory.getLogger(MultiHttpRequestClient.class);

    @Value("${post.request.url}")
    private String postRequestUrl;

    @Resource
    private RestTemplate restTemplate;

    public void asyncSendPostRequest(int n) throws Exception {
        CountDownLatch latch = new CountDownLatch(n); // 协调多个线程之间的同步, 以免子线程执行过程中被主线程阻断
        ExecutorService threadPool = Executors.newFixedThreadPool(8); // 固定大小线程池
        Map<Integer, Object> failMsgMap = new HashMap<>(); // 请求失败信息

        log.info("并发请求开始!");
        for (int i = 1; i <= n; i++) {
            int finalI = i;
            // 线程池执行
            threadPool.execute(() -> {
                try {
                    long startRequestTime = System.currentTimeMillis();
                    Map<String, Object> responseMap = postRequest(finalI); //发送请求
                    long requestTime = System.currentTimeMillis() - startRequestTime;
                    // 在各自规定时间内获得response 第一个request 在发出去的一秒内收到，第二个request在两秒内… 第n个request在n秒内收到
                    if (requestTime <= finalI * 1000L) {
                        log.info(Thread.currentThread().getName() + " -> 第" + finalI + "个POST请求成功, 请求返回数据: " + responseMap);
                    } else {
                        String failMsg = "第" + finalI + "个POST请求失败! 失败原因: 未能在规定时间内返回respose!";
                        failMsgMap.put(finalI, failMsg);
                        log.info(Thread.currentThread().getName() + " -> " + failMsg);
                    }
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        latch.await();
        threadPool.shutdown();

        if (failMsgMap.size() == 0) {
            log.info("并发请求结束! 请求结果: SUCCESS!");
        } else {
            log.info("并发请求结束! 请求结果: FAIL! 原因: ");
            for (Object value : failMsgMap.values()) {
                log.info((String) value);
            }
        }
    }

    /**
     * @description: POST请求
     * @author: wangsongwen
     * @date: 2021/5/21 17:02
     **/
    public Map<String, Object> postRequest(int number) throws Exception {
        ResponseEntity<String> response;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("number", number);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
            response = restTemplate.exchange(postRequestUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            throw new Exception("服务调用异常: " + e.getMessage());
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("调用返回 HTTP status " + response.getStatusCode());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = JSON.parseObject(response.getBody(), Map.class);

        return responseMap;
    }
}
