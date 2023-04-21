/**
 * Copyright (c) 2018 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-mobile-gateway
 * 模块名称：cn.com.yitong.router
 * @version 1.0.0
 * @author zwb
 * @date 2018-7-5 17:54:38
 */
package cn.com.yitong.ares;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.ICacheService;
import cn.com.yitong.ares.base.IParamOutCover;
import cn.com.yitong.ares.common.LogExecuterService;
import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.consts.SessConsts;
import cn.com.yitong.ares.core.AresResource;
import cn.com.yitong.ares.core.CtxUtil;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.ares.starter.properties.AresProperties;
import cn.com.yitong.util.common.F5ipUtil;

/**
 * @author zwb
 *
 */
@Controller
@RefreshScope
public class FlowRouter {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Value("${ares.application.code}")
	private String code;
	@Value("${none.param.check.url}")
	private String noneParamCheckUrl;

	@Autowired
	Environment env;
	
	@Autowired
	AresProperties aresProperties;

	/**
	 * 缓存服务
	 */
	@Autowired
	@Qualifier("redisService")
	private ICacheService cacheService;

	/** 流程编排服务 */
	@Autowired
	IFlowTool flowTool;
	
	@Resource
	private IParamOutCover mapParamOutCover;
	
	@Resource
	private IParamOutCover paramInCover;
	
	@Autowired
	private LogExecuterService service;
	
	private Map<String, Object> param = new HashMap<>();


	/**
	 * 执行.
	 *
	 * @param ctx     总线
	 * @param request http请求对象
	 * @return 响应结果
	 */
	@RequestMapping("/api/**")
	@ResponseBody
	public Map<String, Object> execute(@RequestBody IBusinessContext ctx, HttpServletRequest request) {
		try {
			if (null == ctx) {
				throw new AresRuntimeException("common.illegal.request");
			}
			
			
			ctx.setRequest(request);
			ctx.setSession(request.getSession());
			
			ctx.setCacheService(cacheService);

			ctx.setCache("time", System.currentTimeMillis() + "");
			
			cacheService.set("time", System.currentTimeMillis() + "");

			logger.info("sessionId={}", ctx.getSessionId());

			String requestPath = request.getServletPath();

			String transCode = requestPath.substring(1).split("\\.")[0];
			if (transCode.startsWith("api/")) {
				transCode = transCode.replaceFirst("api/", "");
			}

			ctx.setHead(AresR.TRANS_URL, transCode);

			initHeader(ctx, request);// 初始化公共信息

			logger.info("请求路径 : {}", requestPath);
			
			//会话校验前获取日志相关信息，防止会话拦截抛出异常后，获取不到相关信息
			this.param.put("OPER_PARAM", JSONObject.toJSONString(ctx.getParamMap()));
			this.param.put("OPER_IP", F5ipUtil.getIpAddr(request));
			this.param.put("TRANSCODE", transCode);
			this.param.put("OPER_URL", requestPath);
			this.param.put("REQUEST_METHOD", request.getMethod());

			//输入报文校验
			if (!transCode.matches(noneParamCheckUrl)){
				flowTool.execute(ctx, "transPrev");
			}
			
			//会话校验完成后获取用户相关信息
			initLogParam(ctx);

			// 交易处理
			String flowPath = String.format("%s", transCode);
			
			ctx.setTransCode(transCode);

			flowTool.execute(ctx, flowPath);

			// 过滤输出报文
			return CtxUtil.showSuccessResult(ctx, mapParamOutCover, transCode);

		} catch (AresRuntimeException e) {
			logger.error("AresRuntimeException: {}", e);
			return CtxUtil.showAresError(e, ctx);
		} catch (Exception e) {
			logger.error("error", e);
			return CtxUtil.showErrorResult(e, ctx);
		} finally {
			// saveLog(ctx);
			this.param.putAll(ctx.getParamMap());
			this.param.put("JSON_RESULT", JSONObject.toJSONString(ctx.getParamMap()));
			//异步执行-添加日志
			service.execute(this.param);
		}

	}
	
	/**
	 * 初始化日志参数
	 * @param ctx
	 */
	private void initLogParam(IBusinessContext ctx) {
		//
//		param.put("OPER_URL", ctx.getParam("transCode"));
//		this.param.put("OPER_PARAM", JSONObject.toJSONString(ctx.getParamMap()));
		
		//未创建会话接口捕获异常，不抛出
		String userId = "";
		try {
			userId = ctx.getSessionObject(SessConsts.SESS_USER_ID);
		}catch(Exception e) {
			
		}
		//未创建会话接口捕获异常，不抛出
		String userName = "";
		try {
			userName = ctx.getSessionObject(SessConsts.SESS_USER_NAME);
		}catch(Exception e) {
			
		}
		this.param.put("USER_ID", userId);
		this.param.put("OPER_NAME", userName);
		
	}

	/**
	 * 初始化总线公共信息
	 * 
	 * @param ctx
	 * @param request
	 */
	private void initHeader(IBusinessContext ctx, HttpServletRequest request) {
		// 设置客户端IP地址
		ctx.setHead(AresR.CLIENT_IP, F5ipUtil.getIpAddr(request));
		// 设置主机IP地址
		ctx.setHead(AresR.HOST_IP, request.getLocalAddr());
		// 设置访问入口
//		ctx.setHead(AresR.TRANS_URL, request.getServletPath());
		// 设备指纹
		ctx.setHead(AresR.DEVICE_FINGER, (String) ctx.getParam(AresR.DEVICE_FINGER));
		// 客户端详细信息 如魅族 小米等信息
		ctx.setHead(AresR.CLIENT_INFO, (String) ctx.getParam(AresR.CLIENT_INFO));
		// 客户端版本
		ctx.setHead(AresR.CLIENT_VER_NO, (String) ctx.getParam(AresR.CLIENT_VER_NO));
		// 客户端类型:MB: IPHONE、IPAD ,AD: ANDROID、ANDROID PAD
		ctx.setHead(AresR.CLIENT_TYPE, (String) ctx.getParam(AresR.CLIENT_TYPE));
		// 设备号
		ctx.setHead(AresR.CLIENT_NO, (String) ctx.getParam(AresR.CLIENT_NO));
		// 客户端操作系统 I:IOS A:ANDROID
		ctx.setHead(AresR.CLIENT_OS, (String) ctx.getParam(AresR.CLIENT_OS));
		// 经度
		ctx.setHead(AresR.X_LINE, (String) ctx.getParam(AresR.X_LINE));
		// 维度
		ctx.setHead(AresR.Y_LINE, (String) ctx.getParam(AresR.Y_LINE));
	}

}
