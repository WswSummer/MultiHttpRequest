package com.wsw.multihttprequestclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author WangSongWen
 * @Date: Created in 16:15 2021/5/20
 * @Description: 请求线程池
 */
@Configuration
@EnableAsync  //异步
public class RequestThreadPool {
    // 获得Java虚拟机可用的处理器个数 + 1
    private static final int THREADS = Runtime.getRuntime().availableProcessors() + 1;

    private static final int corePoolSize = THREADS; // 核心线程数
    private static final int maximumPoolSize = 2 * THREADS; // 最大线程数
    private static final int blockingQueueCapacity = 1024;
    private static final long keepAliveTime = 100;
    TimeUnit unit = TimeUnit.SECONDS;

    @Bean
    public Executor taskExecutor() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(blockingQueueCapacity);
        ThreadFactory threadFactory = new NameTreadFactory();
        RejectedExecutionHandler handler = new MyIgnorePolicy();

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, handler);
    }

    private static class NameTreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "my-thread-" + mThreadNum.getAndIncrement());
            System.out.println(t.getName() + " has been created");
            return t;
        }
    }

    private static class MyIgnorePolicy implements RejectedExecutionHandler {

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            doLog(r, e);
        }

        private void doLog(Runnable r, ThreadPoolExecutor e) {
            // 可做日志记录等
            System.err.println(r.toString() + " rejected");
        }
    }
}
