/**
 * Copyright (c) 2020 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	:ares-spring-boot-template</p>
 * <p>包名称    	:cn.com.yitong.ares.config</p>
 * <p>文件名称	:AresWebMvcConfigurer.java</p>
 * <p>创建时间	:2020-5-27 16:56:11 </p>
 */
package cn.com.yitong.ares.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.com.yitong.ares.interceptor.AresHandlerInterceptor;

/**
 * WEB MVC 配置管理.
 *
 * @author zwb
 * @version 1.0
 * @since 5.2.0
 */
@Configuration
public class AresWebMvcConfigurer implements WebMvcConfigurer {
	
	
	/**
	 * Gets the ares handler interceptor.
	 *
	 * @return the ares handler interceptor
	 */
	@Bean
	public AresHandlerInterceptor getAresHandlerInterceptor() {
		return new AresHandlerInterceptor();
	}
	
	/**
	 * 添加拦截器.
	 *
	 * @param registry the registry
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#addInterceptors
	 *      (org.springframework.web.servlet.config.annotation.InterceptorRegistry)
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getAresHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/test/**");
	}
	
	@Bean(name = "multipartResolver")
	public MultipartResolver getMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        //resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setResolveLazily(true);
        //设置了文件放入临时文件夹的最小大小限制
        resolver.setMaxInMemorySize(40960);
        //设置单个上传数据总大小25M
        resolver.setMaxUploadSizePerFile(10*1024*1024);
		return resolver;
	}

}
