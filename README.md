## 并行且异步发送HTTP请求
### 项目结构
> 服务端: MultiHttpRequestServer
> 
> 客户端: MultiHttpRequestClient
### 项目框架
> 编程语言: Java8
> 
> 包管理工具: Maven
> 
> Web框架: SpringBoot2.2.2.RELEASE
### 第三方工具包
> lombok
> 
> fastjson
> 
> commons-lang3
> 
> commons-collections4
### 项目运行
1. 启动MultiHttpRequestServer模块下的MultihttprequestserverApplication主程序,这样服务端会提供一个供POST请求的REST接口:
```java
@RestController
@RequestMapping("/wsw")
public class MultiHttpRequestController {

    @ResponseBody
    @PostMapping("/hello")
    public Result sayHello(@RequestParam Map<String, Object> map) {
        return new Result(200, "success", "request " + MapUtils.getString(map, "number", ""));
    }

}
```
会运行在本地1998端口.
2. 启动MultiHttpRequestClient模块的MultihttprequestclientApplicationTests单元测试方法contextLoads():
```java
void contextLoads() throws Exception {
    multiHttpRequestClient.asyncSendPostRequest(10);
}
```
测试方法会调用asyncSendPostRequest()方法进行并行发送HTTP POST请求:
```java
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
```
可在控制台或者MultiHttpRequestClient模块的requestClient.log文件中看到运行信息.
