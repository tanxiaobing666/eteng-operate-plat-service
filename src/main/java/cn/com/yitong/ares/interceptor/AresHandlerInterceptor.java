/**
 * Copyright (c) 2020 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	:ares-spring-boot-template</p>
 * <p>包名称    	:cn.com.yitong.ares.interceptor</p>
 * <p>文件名称	:AresHandlerInterceptor.java</p>
 * <p>创建时间	:2020-5-27 16:55:31 </p>
 */

package cn.com.yitong.ares.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.com.yitong.logger.AresLogger;
import cn.com.yitong.util.common.F5ipUtil;
import cn.com.yitong.util.common.SeqGeneUtil;
import cn.com.yitong.util.common.SystemUtil;

/**
 * 拦截器.
 *
 * @author zwb
 * @version 1.0
 * @since 6.0.0
 */
@Component
public class AresHandlerInterceptor implements HandlerInterceptor {

	/**
	 * 日志埋点工具.
	 */
	private AresLogger aresLogger = new AresLogger();


	/**
	 * Pre handle.
	 *
	 * @param request the request
	 * @param response the response
	 * @param handler the handler
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		initContext(request);
		return true;
	}

	/**
	 * 初始网关上下文.
	 *
	 * @param request the request
	 */
	public void initContext(HttpServletRequest request) {
		// 获取前端请求路径
		String frontPath = request.getRequestURI();

		// 生成全局流水号
		String gwSeq = SeqGeneUtil.geneBusiSN("T");
		// 生成系统调用流水号
		String sysSeq = SeqGeneUtil.geneSysSN("S");
		// 服务端IP地址
		String serverAddr = SystemUtil.getLocalIP();
		// 客户端IP地址
		String clientAddr = F5ipUtil.getIpAddr(request);

		aresLogger.setTransCode(frontPath);
		aresLogger.setBusiSeq(gwSeq);
		aresLogger.setSysSeq(sysSeq);
		aresLogger.setClientAddr(clientAddr);
		aresLogger.setServerAddr(serverAddr);


		// 日志埋点开始
		aresLogger.start();
	}


	/**
	 * (non-Javadoc).
	 *
	 * @param request the request
	 * @param response the response
	 * @param handler the handler
	 * @param modelAndView the model and view
	 * @throws Exception the exception
	 * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.
	 *      http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 *      java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		aresLogger.end();
	}

	/**
	 * (non-Javadoc).
	 *
	 * @param request the request
	 * @param response the response
	 * @param handler the handler
	 * @param ex the ex
	 * @throws Exception the exception
	 * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.
	 *      servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 *      java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
	}
}
