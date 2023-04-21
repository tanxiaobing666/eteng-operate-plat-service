/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net
 * 文件名称：NetRequest4SC.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:18:47
 */

package cn.com.yitong.ares.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.consts.SessR;
import cn.com.yitong.ares.core.ThreadContext;
import cn.com.yitong.ares.core.TransConfBean;
import cn.com.yitong.ares.core.TransItem;
import cn.com.yitong.ares.net.constant.SpringCloudNetConst;
import cn.com.yitong.ares.net.request.TransApiBean;
import cn.com.yitong.logger.api.MDCConst;
import cn.com.yitong.util.common.StringUtil;
import cn.hutool.core.util.StrUtil;

@Component
public class NetRequest4SC {

	/**
	 * 定义输出日志.
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
	 * 建立 send message.
	 *
	 * @param ctx       the ctx
	 * @param transCode the trans code
	 * @return true, if successful
	 */
	public boolean buildSendMessage(IBusinessContext ctx, String transCode) {
		// 获取交易Api bean
		TransApiBean transApiBean = confParser.findTransConfById(ctx, transCode);
		// 交易配置bean
		TransConfBean transConfBean = transApiBean.getTransConfBean();
		Map<String, Object> paramMap = ctx.getParamMap();

		// API信息设置到ParamMap中
		processParamMap(transApiBean, paramMap);

		// 关闭过滤，直接返回报文
		String filter = transConfBean.getProperty("filter");
		if (StringUtil.isNotEmpty(filter) && "false".equals(filter)) {
			ctx.setRequestEntry(paramMap);
			return true;
		}

		Map<String, Object> requestEntryMap = new HashMap<String, Object>();
		Map<String, Object> bodyMap = new HashMap<String, Object>();

		// 请求体过滤
		List<TransItem> transItems = transConfBean.getSed();
		msgFilter.buildMsgInfo(ctx, paramMap, transItems, bodyMap);

		// 构造http请求头
		HttpHeaders headers = buildHttpHeaders(ctx);

		requestEntryMap.put(SpringCloudNetConst.BODY, bodyMap);
		requestEntryMap.put(SpringCloudNetConst.HTTP_HEAD, headers);

		// 将过滤后的属性放入requestEntry中
		ctx.setRequestEntry(requestEntryMap);
		return true;
	}

	/**
	 * 构造HTTP headers.
	 *
	 * @param ctx the ctx
	 * @return the http headers
	 */
	public HttpHeaders buildHttpHeaders(IBusinessContext ctx){
		HttpHeaders headers=new HttpHeaders();
		//全局流水号
		String globSeqNo = (String) ThreadContext.get(MDCConst.BUSI_SEQ_ID);
		//用户ID
		String userId = ctx.getSessionObject(SessR.SESS_USER_ID);
		//法人编号
		String company = ctx.getSessionObject(SessR.SESS_LEGAL_BRANCH_ID);
		//渠道号
		String chnlId = ctx.getHead(AresR.H_CHNL_ID);
		
		if(StrUtil.isNotEmpty(globSeqNo)) {
			headers.add(getHeaderKey(AresR.BUSI_TRACE_NO), globSeqNo);
		}
		
		if(StrUtil.isNotEmpty(userId)) {
			headers.add(getHeaderKey(AresR.H_USER_ID), userId);
		}
		
		if(StrUtil.isNotEmpty(chnlId)) {
			headers.add(getHeaderKey(AresR.H_CHNL_ID), chnlId);
		}
		
		if(StrUtil.isEmpty(company)) {
			headers.add(getHeaderKey(SpringCloudNetConst.HEADER_COMPANY), company);
		}
			
		return headers;
	}
	
	/**
	 * 获取header key.
	 *
	 * @param key the key
	 * @return the header key
	 */
	private String getHeaderKey(String key) {
		if(StrUtil.isEmpty(key)) {
			return null;
		}
		return key.replace("_", "-");		
	}
	
	


	/**
	 * 处理paramMap，将api信息设置到ParamMap中.
	 *
	 * @param transApiBean the trans api bean
	 * @param paramMap     the param map
	 */
	private void processParamMap(TransApiBean transApiBean, Map<String, Object> paramMap) {
		paramMap.put(SpringCloudNetConst.API_METHOD, transApiBean.getApiMethod());
		paramMap.put(SpringCloudNetConst.BACK_API_PATH, transApiBean.getBackApiPath());
		paramMap.put(SpringCloudNetConst.BACK_SERVICE_NAME, transApiBean.getBackServiceName());
		paramMap.put(SpringCloudNetConst.MOCK_TYPE, transApiBean.getMockType());
		paramMap.put(SpringCloudNetConst.MOCK_DATA, transApiBean.getMockData());
	}

}
