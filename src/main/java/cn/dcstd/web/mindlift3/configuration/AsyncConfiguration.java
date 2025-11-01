package cn.dcstd.web.mindlift3.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 * 用于语音识别等耗时操作的异步处理
 * 
 * @author NaNo1c
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean(name = "voiceRecognitionExecutor")
    public Executor voiceRecognitionExecutor() {
        logger.info("初始化语音识别异步线程池");
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(2);
        
        // 最大线程数
        executor.setMaxPoolSize(5);
        
        // 队列容量
        executor.setQueueCapacity(50);
        
        // 线程名称前缀
        executor.setThreadNamePrefix("VoiceRecognition-");
        
        // 空闲线程存活时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        
        logger.info("语音识别异步线程池初始化完成");
        
        return executor;
    }
}

