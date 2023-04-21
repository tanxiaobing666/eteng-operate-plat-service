/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net
 * 文件名称：NetTools4SCOp.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:19:47
 */

package cn.com.yitong.ares.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.INetTools;
import cn.com.yitong.ares.flow.IAresSerivce;

@Component
public class NetTools4SCOp implements IAresSerivce{
	
	/**
	 * 记录日志.
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * The net tools.
	 */
	@Autowired
	@Qualifier("netTools4SC")
	private INetTools netTools;
	
	
	/**
	 * Execute.
	 *
	 * @param ctx the ctx
	 * @return the int
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		String transCode = ctx.getParam("*transCode");
		netTools.execute(ctx, transCode);
		return NEXT;
	}

}
	