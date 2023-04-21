/**
 * Copyright (c) 2020 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	：ares-spring-boot-template</p>
 * <p>包名称    	：cn.com.yitong.ares.routes</p>
 * <p>文件名称	：DefaultRoutes.java</p>
 * <p>创建时间	：2020年5月27日 下午4:27:37 </p>
 */
package cn.com.yitong.ares.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 * 
 * @author zwb
 * @version 1.0
 * @since 6.0.0
 */
@RestController
@RefreshScope
public class DefaultRoutes {

	
//	@NacosValue(value = "${useLocalCache:false}",autoRefreshed = true)
//	private String useLocalCache;
	
	@Value("${useLocalCache:false}")
	private String useLocalCache;
	

	/**
	 * HEAD请求方法，可用于负载均衡设备进行探测
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/" }, method = RequestMethod.HEAD)
	public void head() {
	}

	/**
	 * 主页
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/" }, produces = { "text/plain;charset=UTF-8" })
	public String index() {
		return "ok";
	}
	
	@RequestMapping(value = { "/config" }, produces = { "text/plain;charset=UTF-8" })
	public String getConfig() {
		return useLocalCache;
	}

}
