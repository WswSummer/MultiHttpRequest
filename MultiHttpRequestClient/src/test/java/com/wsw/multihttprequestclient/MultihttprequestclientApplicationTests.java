package com.wsw.multihttprequestclient;

import com.wsw.multihttprequestclient.client.MultiHttpRequestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MultihttprequestclientApplicationTests {
    @Autowired
    private MultiHttpRequestClient multiHttpRequestClient;

    @Test
    void contextLoads() throws Exception {
        multiHttpRequestClient.asyncSendPostRequest();
    }

}
