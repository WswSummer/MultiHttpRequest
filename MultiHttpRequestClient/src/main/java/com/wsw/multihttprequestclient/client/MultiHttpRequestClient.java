package com.wsw.multihttprequestclient.client;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author WangSongWen
 * @Date: Created in 16:07 2021/5/20
 * @Description: 客户端---并行且异步发送n个Http Post请求
 */
@Component
public class MultiHttpRequestClient {
    @Value("${post.request.url}")
    private String postRequestUrl;

    @Resource
    private RestTemplate restTemplate;

    @Async("requestThreadPool")
    public void asyncSendPostRequest() throws Exception {
        for (int i = 0; i < 10; i++) {
            ResponseEntity<String> responseEntity = postRequest(i);
            System.out.println(responseEntity);
        }
    }

    public ResponseEntity<String> postRequest(int i) throws Exception {
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("requestNumber", i);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params,
                headers);
        response = restTemplate.exchange(postRequestUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("调用返回 HTTP status: " + response.getStatusCode());
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = JSON.parseObject(response.getBody(), Map.class);
        if (MapUtils.isEmpty(responseMap)) {
            throw new Exception("调用返回 body is null!");
        }

        if (MapUtils.getInteger(responseMap, "code") != 200) {
            throw new Exception("调用返回 失败!");
        }

        return response;
    }
}
