/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net
 * 文件名称：NetConnect4SC.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:17:22
 */


package cn.com.yitong.ares.net;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.INetConnect;
import cn.com.yitong.ares.core.AutoTestTool;
import cn.com.yitong.ares.core.ThreadContext;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.net.constant.ApiMethod;
import cn.com.yitong.ares.net.constant.SpringCloudNetConst;
import cn.com.yitong.ares.util.HttpNetUtils;
import cn.com.yitong.logger.api.MDCConst;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;

@Component
public class NetConnect4SC implements INetConnect {

	/**
	 * 记录日志工具.
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 自动保存日志工具.
	 */
	@Autowired
	private AutoTestTool att;

	/**
	 * Rest 调用模板.
	 */
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 通讯服务.
	 *
	 * @param ctx       the ctx
	 * @param transCode the trans code
	 * @return true, if successful
	 */
	@Override
	public boolean connect(IBusinessContext ctx, String transCode) {
		long start = System.currentTimeMillis();
		Map<String, Object> paramMap = ctx.getParamMap();
		Map<String, Object> requestEntryMap = ctx.getRequestEntry();
		String serviceUrl = null;
		ResponseEntity<byte[]> responseEntity = null;
		String resultStr = "";
		try {
			String serviceName = MapUtil.getStr(paramMap,SpringCloudNetConst.BACK_SERVICE_NAME);
			String apiMethod = MapUtil.getStr(paramMap,SpringCloudNetConst.API_METHOD);
			String backApiPath = MapUtil.getStr(paramMap,SpringCloudNetConst.BACK_API_PATH);
			String mockType = MapUtil.getStr(paramMap,SpringCloudNetConst.MOCK_TYPE);
			String mockData = MapUtil.getStr(paramMap,SpringCloudNetConst.MOCK_DATA);

			// 请求服务URL
			serviceUrl = String.format("http://%s%s", serviceName, backApiPath);
			if(SpringCloudNetConst.MOCK_TYPE_Y.equals(mockType) 
					&& StrUtil.isNotEmpty(mockData)) {
				responseEntity = new ResponseEntity<byte[]>(mockData.getBytes(),HttpStatus.OK);
				resultStr = mockData;
				return true;
			} 
			
			HttpMethod method = ApiMethod.getMethodByCode(apiMethod).getMethod();
			HttpHeaders httpHeaders = (HttpHeaders)requestEntryMap.get(SpringCloudNetConst.HTTP_HEAD);
			Map<String,Object> body = (Map<String,Object>)requestEntryMap.get(SpringCloudNetConst.BODY);
			
			//服务调用地址

			if(HttpMethod.GET.equals(method)) {
				serviceUrl = HttpNetUtils.getCombineUrlByBodyMap(serviceUrl,body);
			}
			serviceUrl = URLUtil.normalize(serviceUrl);
			logger.info("[SC net tool] back service url: {}", serviceUrl);
			
			
			HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<Map<String,Object>>(body, httpHeaders);
						
			responseEntity = this.restTemplate.exchange(serviceUrl, method, httpEntity, byte[].class);
			
			ctx.setResponseEntry(responseEntity);
			
			MediaType mediaType = responseEntity.getHeaders().getContentType();
			if (responseEntity.hasBody()) {
				 byte[] rsp = responseEntity.getBody();
				if (mediaType != null && mediaType.includes(MediaType.APPLICATION_JSON)) {
					resultStr = new String(rsp, "UTF-8");
				}
			}	
			return true;
		} catch (Exception e) {
			logger.error("[SC net tool] connect error", e);
			throw new AresRuntimeException("net.sc.connect.error");
		} finally {		
			long end = System.currentTimeMillis();
			logger.info("\nurl:{} times:{} ms \nreq:{},\nrsp:{}",
					serviceUrl, (end - start), 
					JSONUtil.formatJsonStr(JSON.toJSONString(requestEntryMap)),
					resultStr);
		}
	}
	
	/**
	 * 构造post请求httpEntity.
	 *
	 * @param requestEntryMap the request entry map
	 * @return the http entity
	 */
	private HttpEntity<Map<String, Object>> buildHttpEntity4PostRequest(Map<String, Object> requestEntryMap) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SpringCloudNetConst.GLOBAL_SEQ_NO, 
				(String)ThreadContext.get(MDCConst.BUSI_SEQ_ID));
		HttpEntity<Map<String,Object>> entity = 
				new HttpEntity<Map<String,Object>>(requestEntryMap,headers);
		return entity;
	}
	
	/**
	 * Gets the combine url.
	 *
	 * @param serviceUrl the service url
	 * @param requestEntryMap the request entry map
	 * @return the combine url
	 */
	private String getCombineUrl(String serviceUrl, Map<String,Object> requestEntryMap) {
		if(requestEntryMap == null || requestEntryMap.isEmpty()) {
			return serviceUrl;
		}
		
		Map<String,Object> bodyMap = (Map<String,Object>)requestEntryMap.get(SpringCloudNetConst.BODY);
		
		StringBuilder urlBuilder = new StringBuilder(serviceUrl);
		//追加全局流水号
		urlBuilder.append("?").append(SpringCloudNetConst.GLOBAL_SEQ_NO).append("=")
		.append(ThreadContext.get(MDCConst.BUSI_SEQ_ID)).append("&");
		for(Entry<String,Object> paramEntry: bodyMap.entrySet()) {
			urlBuilder.append(paramEntry.getKey());
			urlBuilder.append("=");
			urlBuilder.append(paramEntry.getValue());
			urlBuilder.append("&");
		}
		String url = urlBuilder.toString();
		return url.substring(0,url.length()-1);
	}

}
