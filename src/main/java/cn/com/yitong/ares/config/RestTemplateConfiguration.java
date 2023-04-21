/**
 * Copyright (c) 2020 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-tsf-pre-channel
 * 模块名称：cn.com.yitong.ares.config
 * 文件名称：TsfConfiguration.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2020-11-18 18:24:34
 */
package cn.com.yitong.ares.config;

import java.nio.charset.StandardCharsets;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import cn.com.yitong.ares.component.RestThrowErrorHandler;

@Configuration
public class RestTemplateConfiguration {
	
	/**
	 * 连接超时时间.
	 */
	@Value("${service.connect.timeout:10000}")
	private Integer connectTimeout;
	
	/**
	 * 读取时间.
	 */
	@Value("${service.read.timeout:60000}")
	private Integer readTimeout;
	
	
	/**
	 * Rest服务调用模板.
	 *
	 * @param factory the factory
	 * @return the rest template
	 */
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
		RestTemplate template = new RestTemplate(factory);
		template.setErrorHandler(new RestThrowErrorHandler());
		StringHttpMessageConverter t = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		t.setWriteAcceptCharset(false);
		template.getMessageConverters().add(0, t);
		return template;
	}
	
	@Bean
	public RestTemplate restTemplate1(ClientHttpRequestFactory factory) {
		RestTemplate template = new RestTemplate(factory);
		template.setErrorHandler(new RestThrowErrorHandler());
		StringHttpMessageConverter t = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		t.setWriteAcceptCharset(false);
		template.getMessageConverters().add(0, t);
		return template;
	}
	
	/**
	 * Rest模板，实际调用的ClientHttpRequestFactorysssss.
	 *
	 * @return the client http request factory
	 */
	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(200);
		connManager.setDefaultMaxPerRoute(200);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).disableCookieManagement()
				.disableRedirectHandling().build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

		factory.setHttpClient(httpClient);
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		return factory;
	}
	
	
}
