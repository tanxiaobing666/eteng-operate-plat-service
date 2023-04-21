/**
 * Copyright (c) 2019 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	:ares-api-gateway</p>
 * <p>包名称    	:cn.com.yitong.ares.gateway.configuration</p>
 * <p>文件名称	:GatewayConfiguration.java</p>
 * <p>创建时间	:2019-3-22 18:51:58 </p>
 */
package cn.com.yitong.ares.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import cn.com.yitong.ares.channel.JsonHttpMessageConverter;

/**
 * 网关Spring配置.
 *
 * @author zwb
 * @version 1.0
 * @since 6.0.0
 */
@Configuration
public class AresConfiguration {

	/**
	 * 新增 HTTP message 转换器.
	 *
	 * @return the http message converters
	 */
	@Bean
	public HttpMessageConverters addHttpMessageConverter() {
		JsonHttpMessageConverter converter = new JsonHttpMessageConverter();
		return new HttpMessageConverters(converter);
	}
	
	/**
	 * 配置异步线程池
	 * @return
	 */
	@Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(5);
        //配置最大线程数
        executor.setMaxPoolSize(5);
        //配置队列大小
        executor.setQueueCapacity(100);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-service-");

        // 设置拒绝策略：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
	
}
