/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net
 * 文件名称：NetTools4SC.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:19:28
 */

package cn.com.yitong.ares.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.INetConnect;
import cn.com.yitong.ares.base.INetTools;

@Component
public class NetTools4SC implements INetTools{
	
	/**
	 * 记录日志.
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * 通讯连接.
	 */
	@Autowired
	@Qualifier("netConnect4SC")
	INetConnect connect;
	
	
	/**
	 * 生成请求报文.
	 */
	@Autowired
	NetRequest4SC netRequest4SC;
	
	
	/**
	 * 响应解析.
	 */
	@Autowired
	NetResponse4SC responseParser;
	
	
	/**
	 * 配置文件解析.
	 */
	@Autowired
	NetConfParser4SC netConfParser4SC;
	
	
	
	/**
	 * Execute.
	 *
	 * @param ctx the ctx
	 * @param transCode the trans code
	 * @return true, if successful
	 */
	@Override
	public boolean execute(IBusinessContext ctx, String transCode) {
		Long start = System.currentTimeMillis();
		logger.debug("总线数据:{}", ctx.getParamMap());
		
		netRequest4SC.buildSendMessage(ctx, transCode);
		connect.connect(ctx, transCode);
		responseParser.parseResponseData(ctx, transCode);
		
		logger.info("[SC net tool] call provider:{},耗时:{}ms！", transCode, (System.currentTimeMillis() - start));
		return true;
	}

}
