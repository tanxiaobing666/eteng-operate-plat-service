package cn.com.yitong.ares.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.util.common.StringUtil;

public class HttpNetUtils {
	
	
	/**
	 * 
	 * @param serviceUrl
	 * @param paramMap
	 * @return
	 */
	public static String getCombineUrlByBodyMap(String serviceUrl, Map<String,Object> bodyMap) {
		if(bodyMap == null || bodyMap.isEmpty()) {
			return serviceUrl;
		}
			
		StringBuilder urlBuilder = new StringBuilder(serviceUrl);
		urlBuilder.append("?");
		for(Entry<String,Object> paramEntry: bodyMap.entrySet()) {
			urlBuilder.append(paramEntry.getKey());
			urlBuilder.append("=");
			urlBuilder.append(paramEntry.getValue());
			urlBuilder.append("&");
		}
		String url = urlBuilder.toString();
		return url.substring(0,url.length()-1);
	}
	
	/**
	 * 
	 * @param serviceUrl
	 * @param paramMap
	 * @return
	 */
	public static String getCombineUrl(String serviceUrl, Map<String,String> queryMap) {
		if(queryMap == null || queryMap.isEmpty()) {
			return serviceUrl;
		}
			
		StringBuilder urlBuilder = new StringBuilder(serviceUrl);
		urlBuilder.append("?");
		for(Entry<String,String> paramEntry: queryMap.entrySet()) {
			urlBuilder.append(paramEntry.getKey());
			urlBuilder.append("=");
			urlBuilder.append(paramEntry.getValue());
			urlBuilder.append("&");
		}
		String url = urlBuilder.toString();
		return url.substring(0,url.length()-1);
	}
	
	/**
	 * 响应解析
	 * @param responseEntity
	 * @param mediaType
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, Object> parseResponse2Map(ResponseEntity<byte[]> responseEntity)
			throws UnsupportedEncodingException {
		MediaType mediaType = responseEntity.getHeaders().getContentType();
		Map<String, Object> resultMap = new HashMap<String,Object>();
		if (responseEntity.hasBody()) {
			 byte[] rsp = responseEntity.getBody();
			// logger.info("响应报文：{}",new String(rsp,"UTF-8"));
			if (mediaType != null && mediaType.includes(MediaType.APPLICATION_JSON)) {
				String jsonString = new String(rsp, "UTF-8");
				if (StringUtil.isJSONObj(jsonString)) {
					resultMap = JSONObject.parseObject(rsp, Map.class);
				} else if (StringUtil.isJSONArray(jsonString)) {
					JSONArray jsonArray = JSONObject.parseArray(jsonString);
					resultMap.put("LIST", jsonArray);
				}
			}
		}
		return resultMap;
	}
}
