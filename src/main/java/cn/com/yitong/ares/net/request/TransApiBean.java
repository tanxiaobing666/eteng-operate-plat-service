/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net.request
 * 文件名称：TransApiBean.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:20:06
 */

package cn.com.yitong.ares.net.request;

import java.util.List;

import cn.com.yitong.ares.core.TransConfBean;
import cn.com.yitong.ares.core.TransItem;

public class TransApiBean {
	
	/**
	 * 请求方法.
	 */
	private Integer apiMethod;
	
	/**
	 * 交易配置bean.
	 */
	private TransConfBean transConfBean;
	
	/**
	 * 初始API路径.
	 */
	private String apiPath;
	
	/**
	 * 后端API路径.
	 */
	private String backApiPath;
	
	/**
	 * 后端服务名.
	 */
	private String backServiceName;
	
	/**
	 * 挡板类型.
	 */
	private Integer mockType;
	
	/**
	 * 挡板数据.
	 */
	private String mockData;
	
	/**
	 * 系统头.
	 */
	private List<TransItem> sysHead;
	
	/**
	 * 应用头.
	 */
	private List<TransItem> appHead;
	
	/**
	 * 系统响应头.
	 */
	private List<TransItem> sysRcvHead;
	

	/**
	 * Gets the api method.
	 *
	 * @return the api method
	 */
	public Integer getApiMethod() {
		return apiMethod;
	}

	/**
	 * Sets the api method.
	 *
	 * @param apiMethod the new api method
	 */
	public void setApiMethod(Integer apiMethod) {
		this.apiMethod = apiMethod;
	}

	/**
	 * Gets the trans conf bean.
	 *
	 * @return the trans conf bean
	 */
	public TransConfBean getTransConfBean() {
		return transConfBean;
	}

	/**
	 * Sets the trans conf bean.
	 *
	 * @param transConfBean the new trans conf bean
	 */
	public void setTransConfBean(TransConfBean transConfBean) {
		this.transConfBean = transConfBean;
	}

	/**
	 * Gets the api path.
	 *
	 * @return the api path
	 */
	public String getApiPath() {
		return apiPath;
	}

	/**
	 * Sets the api path.
	 *
	 * @param apiPath the new api path
	 */
	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	/**
	 * Gets the back api path.
	 *
	 * @return the back api path
	 */
	public String getBackApiPath() {
		return backApiPath;
	}

	/**
	 * Sets the back api path.
	 *
	 * @param backApiPath the new back api path
	 */
	public void setBackApiPath(String backApiPath) {
		this.backApiPath = backApiPath;
	}
	
	
	/**
	 * Gets the back service name.
	 *
	 * @return the back service name
	 */
	public String getBackServiceName() {
		return backServiceName;
	}

	/**
	 * Sets the back service name.
	 *
	 * @param backServiceName the new back service name
	 */
	public void setBackServiceName(String backServiceName) {
		this.backServiceName = backServiceName;
	}

	/**
	 * Gets the mock type.
	 *
	 * @return the mock type
	 */
	public Integer getMockType() {
		return mockType;
	}

	/**
	 * Sets the mock type.
	 *
	 * @param mockType the new mock type
	 */
	public void setMockType(Integer mockType) {
		this.mockType = mockType;
	}

	/**
	 * Gets the mock data.
	 *
	 * @return the mock data
	 */
	public String getMockData() {
		return mockData;
	}

	/**
	 * Sets the mock data.
	 *
	 * @param mockData the new mock data
	 */
	public void setMockData(String mockData) {
		this.mockData = mockData;
	}

	/**
	 * Gets the sys head.
	 *
	 * @return the sys head
	 */
	public List<TransItem> getSysHead() {
		return sysHead;
	}

	/**
	 * Sets the sys head.
	 *
	 * @param sysHead the new sys head
	 */
	public void setSysHead(List<TransItem> sysHead) {
		this.sysHead = sysHead;
	}

	/**
	 * Gets the app head.
	 *
	 * @return the app head
	 */
	public List<TransItem> getAppHead() {
		return appHead;
	}

	/**
	 * Sets the app head.
	 *
	 * @param appHead the new app head
	 */
	public void setAppHead(List<TransItem> appHead) {
		this.appHead = appHead;
	}

	/**
	 * Gets the sys rcv head.
	 *
	 * @return the sys rcv head
	 */
	public List<TransItem> getSysRcvHead() {
		return sysRcvHead;
	}

	/**
	 * Sets the sys rcv head.
	 *
	 * @param sysRcvHead the new sys rcv head
	 */
	public void setSysRcvHead(List<TransItem> sysRcvHead) {
		this.sysRcvHead = sysRcvHead;
	}
	
}
