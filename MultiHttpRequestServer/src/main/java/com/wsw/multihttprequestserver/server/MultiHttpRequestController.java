package com.wsw.multihttprequestserver.server;

import com.wsw.multihttprequestserver.api.Result;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author WangSongWen
 * @Date: Created in 16:04 2021/5/20
 * @Description: 服务端---Http Post请求接口
 */
@RestController
@RequestMapping("/wsw")
public class MultiHttpRequestController {

    @ResponseBody
    @PostMapping("/hello")
    public Result sayHello(@RequestParam Map<String, Object> map) {
        return new Result(200, "success", "request " + MapUtils.getString(map, "number", ""));
    }

}
