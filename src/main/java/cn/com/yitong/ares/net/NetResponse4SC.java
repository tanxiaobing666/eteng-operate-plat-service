/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net
 * 文件名称：NetResponse4SC.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:19:06
 */


package cn.com.yitong.ares.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.core.TransConfBean;
import cn.com.yitong.ares.core.TransItem;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.ares.net.constant.SpringCloudNetConst;
import cn.com.yitong.ares.net.request.TransApiBean;
import cn.com.yitong.ares.util.HttpNetUtils;
import cn.com.yitong.util.common.StringUtil;
import cn.hutool.core.util.StrUtil;

@Component
public class NetResponse4SC {

	/**
	 * The logger.
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * The conf parser.
	 */
	@Autowired
	private NetConfParser4SC confParser;
	
	/**
	 * The msg filter.
	 */
	@Autowired
	private NetMsgFilter4SC msgFilter;

	/**
	 * 解析响应数据.
	 *
	 * @param ctx the ctx
	 * @param transCode the trans code
	 * @return true, if successful
	 * @see cn.com.yitong.ares.base.IResponseParser#parserResponseData(cn.com.yitong.ares.base.IBusinessContext,
	 *      cn.com.yitong.ares.base.INetConfParser, java.lang.String)
	 */
	public boolean parseResponseData(IBusinessContext ctx, String transCode) {
		//响应体
		ResponseEntity<byte[]> responseEntity = ctx.getResponseEntry();
		
		//http状态码异常
		HttpStatus httpStatus = responseEntity.getStatusCode();
		if (httpStatus.isError()) {
			throw new OtherRuntimeException(httpStatus.value() + "", httpStatus.name());
		}
		
		Map<String,Object> resultMap = null;
		try {
		   resultMap = HttpNetUtils.parseResponse2Map(responseEntity);
		}catch(Exception e) {
			logger.error("[TSF Net Response] error", e);
			throw new AresRuntimeException("500000");
		}
		
		// 获取交易Api bean
		TransApiBean transApiBean = confParser.findTransConfById(ctx, transCode);
		// 交易配置bean
		TransConfBean transConfBean = transApiBean.getTransConfBean();	
		
		Map<String,Object> paramMap = ctx.getParamMap();
		paramMap.putAll(resultMap);
		List<TransItem> transItems = transConfBean.getRcv();
		Map<String,Object> responseEntryMap = new HashMap<String,Object>();
				
		msgFilter.buildMsgInfo(ctx, paramMap, transItems, responseEntryMap);
		if(null !=paramMap.get("code") ) {//兼容eteng-wechat
			//返回码校验
			String rtnCode = StringUtil.getString(resultMap, "code", "");
			String rtnMsg = StringUtil.getString(resultMap, "msg", "");
			//返回状态码和配置的成功状态码不同
			if (!StrUtil.equals(rtnCode, "200")) {
				throw new OtherRuntimeException(rtnCode, rtnMsg);
			}
		}else {
			//返回码校验
			String rtnCode = StringUtil.getString(resultMap, SpringCloudNetConst.STATUS, "");
			String rtnMsg = StringUtil.getString(resultMap, SpringCloudNetConst.MESSAGE, "");
			//返回状态码和配置的成功状态码不同
			if (!StrUtil.equals(rtnCode, SpringCloudNetConst.STATUS_SUCCESS)) {
				throw new OtherRuntimeException(rtnCode, rtnMsg);
			}
		}
		
		ctx.setResponseEntry(responseEntryMap);
		return true;
	}

	
}
