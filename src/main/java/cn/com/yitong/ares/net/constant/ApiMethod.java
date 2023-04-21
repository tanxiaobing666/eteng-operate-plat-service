/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net.constant
 * 文件名称：ApiMethod.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:20:34
 */

package cn.com.yitong.ares.net.constant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

import cn.com.yitong.ares.error.AresRuntimeException;

public enum ApiMethod {
	
	/**
	 * GET请求.
	 */
	GET("1", HttpMethod.GET),
	
	/**
	 * POST请求.
	 */
	POST("2", HttpMethod.POST),

	/**
	 * PUT请求.
	 */
	PUT("3", HttpMethod.PUT),
	
	/**
	 * PATCH请求.
	 */
	PATCH("4", HttpMethod.PATCH),
	
	/**
	 * DELETE请求.
	 */
	DELETE("5", HttpMethod.DELETE),
	
	/**
	 * HEAD请求.
	 */
	HEAD("6", HttpMethod.HEAD),
	
	/**
	 * OPTIONS请求.
	 */
	OPTIONS("7", HttpMethod.OPTIONS);
	
	/**
	 * The code.
	 */
	private String code;
	
	/**
	 * The method.
	 */
	private HttpMethod method;

	/**
	 * 构造方法.
	 *
	 * @param code the code
	 * @param method the method
	 */
	private ApiMethod(String code, HttpMethod method) {
		this.code = code;
		this.method = method;
	}

	/**
	 * 获取code值.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * 获取方法.
	 *
	 * @return the method
	 */
	public HttpMethod getMethod() {
		return method;
	}
	
	/**
	 * The map.
	 */
	private static Map<String, ApiMethod> map = new HashMap<>();

    static {
        for (ApiMethod e : ApiMethod.values()) {
            map.put(e.code, e);
        }
    }

    /**
     * Gets the status by code.
     *
     * @param value the value
     * @return the status by code
     */
    public static ApiMethod getMethodByCode(String value) {
    	ApiMethod apiMethod =  map.get(value);
    	//默认POST请求
    	if(apiMethod == null) {
    		apiMethod = ApiMethod.POST;
    	}
    	return apiMethod;
    }
	
}
