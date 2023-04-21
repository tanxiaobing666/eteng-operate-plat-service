/**
 * Copyright (c) 2019 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	:ares-api-gateway</p>
 * <p>包名称    	:cn.com.yitong.ares.gateway.error</p>
 * <p>文件名称	:RestThrowErrorHandler.java</p>
 * <p>创建时间	:2019-4-3 16:02:55 </p>
 */
package cn.com.yitong.ares.component;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * 自定义RestTemplate 异常处理
 * 
 * @author zwb
 * @version 1.0
 * @since 5.2.0
 */
public class RestThrowErrorHandler implements ResponseErrorHandler {

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.client.ResponseErrorHandler#hasError(org.
	 *      springframework.http.client.ClientHttpResponse)
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		// 返回false表示不管response的status是多少都返回没有错
		// 这里可以自己定义那些status code你认为是可以抛Error
		return false;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.
	 *      springframework.http.client.ClientHttpResponse)
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		// 这里面可以实现你自己遇到了Error进行合理的处理
	}

}
