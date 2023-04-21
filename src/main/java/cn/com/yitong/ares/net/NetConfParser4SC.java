/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net
 * 文件名称：NetConfParser4SC.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:16:57
 */

package cn.com.yitong.ares.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.channel.NetConst;
import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.ares.core.TransConfBean;
import cn.com.yitong.ares.core.TransItem;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.net.constant.SpringCloudNetConst;
import cn.com.yitong.ares.net.request.TransApiBean;
import cn.com.yitong.ares.util.FileUtils;
import cn.com.yitong.util.common.StringUtil;
import cn.com.yitong.util.common.XmlUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.map.MapUtil;

@Component
public class NetConfParser4SC {
	
	/**
	 * The logger.
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * The rootpath.
	 */
	@Value("${ares.disgram-root-path:classpath:disgram}")
	private String rootpath;

	/**
	 * The template.
	 */
	private final String template = "%s/%s/%s.xml";

	/**
	 * The pre path.
	 */
	private final String PRE_PATH = "out";

	/**
	 * The tsf pre path.
	 */
	private final String TSF_PRE_PATH = "header";

	/**
	 * The head path.
	 */
	private final String HEAD_PATH = "tsfhead";

	/**
	 * 默认的缓存时间为30s.
	 */
	private final long DEFUALT_TIMEOUT = 30 * 1000;

	/**
	 * 创建缓存.
	 */
	private TimedCache<String, TransApiBean> timedCache = CacheUtil.newTimedCache(DEFUALT_TIMEOUT);

	/**
	 * URL匹配器.
	 */
	private AntPathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * Find trans conf by id.
	 *
	 * @param ctx the ctx
	 * @param transCode the trans code
	 * @return the trans api bean
	 */
	public TransApiBean findTransConfById(IBusinessContext ctx, String transCode) {
		// 判断缓存中是否存在该报文定义，如果有直接获取
		if (timedCache.containsKey(transCode)) {
			return timedCache.get(transCode);
		}

		TransApiBean apiBean = new TransApiBean();
		// 报文路径
		String msgFilePath = String.format(template, rootpath, PRE_PATH, transCode);
		Document doc = readXmlDoc(msgFilePath);

		// 报文内容不为空，直接读取报文内容
		if (doc != null && doc.hasContent()) {
			Element root = doc.getRootElement();
			logger.debug("load trancation defined file desc: {}", root.attributeValue(NetConst.AT_NAME));
			TransConfBean confbean = parseConfBean(root);
			putApiBeanInfo(ctx, confbean, apiBean);
			timedCache.put(transCode, apiBean);
			return apiBean;
		}
		throw new AresRuntimeException("net.tsf.msg.file.not.found", transCode);
	}

	/**
	 * 读取xml报文配置文件.
	 *
	 * @param msgFilePath the msg file path
	 * @return the document
	 */
	private Document readXmlDoc(String msgFilePath) {
		Document doc = null;
		logger.debug("load trancation defined file :{}", msgFilePath);
		String msgContent = null;
		try {
			if (FileUtils.isFileExisted(msgFilePath)) {
				// 报文内容
				msgContent = AresApp.getInstance().loadUtf8Resouce(msgFilePath);
				doc = XmlUtil.readText(msgContent);
			}
		} catch (Exception e1) {
			logger.warn("未找到对应的报文定义:{}", msgFilePath);
		}
		return doc;
	}

	/**
	 * 通过本地报文构造交易Api Bean.
	 *
	 * @param ctx the ctx
	 * @param confbean the confbean
	 * @param apiBean the api bean
	 */
	private void putApiBeanInfo(IBusinessContext ctx, TransConfBean confbean, TransApiBean apiBean) {
		Map<String, Object> paramMap = ctx.getParamMap();
		Integer apiMethod = MapUtil.getInt(paramMap, SpringCloudNetConst.API_METHOD);
		String backServiceName = ctx.getParam(SpringCloudNetConst.BACK_SERVICE_NAME);
		String backApiPath = ctx.getParam(SpringCloudNetConst.BACK_API_PATH);
		Integer mockType = MapUtil.getInt(paramMap, SpringCloudNetConst.MOCK_TYPE);
		String mockData = ctx.getParam(SpringCloudNetConst.MOCK_DATA);

		apiBean.setApiMethod(apiMethod);
		apiBean.setBackApiPath(backApiPath);
		apiBean.setBackServiceName(backServiceName);
		apiBean.setTransConfBean(confbean);
		apiBean.setMockType(mockType);
		apiBean.setMockData(mockData);
	}

	

	/**
	 * 将报文定义解析为bean.
	 *
	 * @param el the el
	 * @return the trans conf bean
	 */
	private TransConfBean parseConfBean(Element el) {
		TransConfBean transConf = new TransConfBean();
		transConf.setName(el.attributeValue(NetConst.AT_NAME));
		List<Attribute> datas = el.attributes();
		for (Attribute data : datas) {
			transConf.setPropery(data.getName(), data.getValue());
		}
		if (el.hasContent()) {
			List<Element> list = el.elements();
			for (Element e : list) {
				if (NetConst.XT_SEND.equals(e.getName())) {
					parserSnd(e, transConf);
				} else if (NetConst.XT_RCV.equals(e.getName())) {
					parserRce(e, transConf);
				}
			}
		}
		return transConf;
	}

	

	/**
	 * 解析发送配置节点.
	 *
	 * @param el the el
	 * @param transConf the trans conf
	 */
	public void parserSnd(Element el, TransConfBean transConf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			// 仅支持非列表的字段解析
			TransItem item = new TransItem();
			parseCommonItem(e, item);
			logger.trace("send item :{}", item);
			parseListChildItem(item, e);
			transConf.addSedItem(item);
		}
	}

	/**
	 * 解析发送配置节点.
	 *
	 * @param el the el
	 * @param transConf the trans conf
	 */
	public void parserSndHeader(Element el, TransConfBean transConf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			if (e.getName().equals(NetConst.XT_ITEM)) {
				// 仅支持非列表的字段解析
				TransItem item = new TransItem();
				parseCommonItem(e, item);
				logger.trace("send item :{}", item);
				parseListChildItem(item, e);
				transConf.addSendHeaderItem(item);
			}
		}
	}

	/**
	 * 解析请求头信息.
	 *
	 * @param el the el
	 * @param transConf the trans conf
	 * @return the list
	 */
	public List<TransItem> parseSndHeader(Element el, TransConfBean transConf) {
		List<TransItem> itemList = new ArrayList<TransItem>();
		List<Element> list = el.elements();
		for (Element e : list) {
			if (e.getName().equals(NetConst.XT_ITEM)) {
				// 仅支持非列表的字段解析
				TransItem item = new TransItem();
				parseCommonItem(e, item);
				logger.trace("send item :{}", item);
				parseListChildItem(item, e);
				itemList.add(item);
			}
		}
		return itemList;
	}

	/**
	 * 解析接受配置节点.
	 *
	 * @param el the el
	 * @param transConf the trans conf
	 */
	public void parserRce(Element el, TransConfBean transConf) {
		List<Element> list = el.elements();
		for (Element e : list) {
			TransItem item = new TransItem();
			parseCommonItem(e, item);
			logger.debug("rcv item :", item);
			if (isListItem(e)) {
				// 如果类型是list 则进行 子节点解析
				List<Element> children = e.elements();
				List<TransItem> subList = new ArrayList<TransItem>();
				for (Element mapel : children) {
					TransItem subItem = new TransItem();
					parseCommonItem(mapel, subItem);
					logger.trace("subItem item :", subItem);
					parseListChildItem(subItem, mapel);
					subList.add(subItem);
				}
				item.setChildren(subList);
			}
			transConf.addRcvItem(item);
		}
	}

	/**
	 * 解析响应头信息.
	 *
	 * @param el the el
	 * @param transConf the trans conf
	 * @return the list
	 */
	public List<TransItem> parseRcvHeader(Element el, TransConfBean transConf) {
		List<TransItem> itemList = new ArrayList<TransItem>();
		List<Element> list = el.elements();
		for (Element e : list) {
			TransItem item = new TransItem();
			parseCommonItem(e, item);
			logger.debug("rcv Header item :{}", item);
			if (isListItem(e)) {
				// 如果类型是list 则进行 子节点解析
				List<Element> children = e.elements();
				List<TransItem> subList = new ArrayList<TransItem>();
				for (Element mapel : children) {
					TransItem subItem = new TransItem();
					parseCommonItem(mapel, subItem);
					logger.trace("subItem Header item :{}", subItem);
					parseListChildItem(subItem, mapel);
					subList.add(subItem);
				}
				item.setChildren(subList);
			}
		}
		return itemList;
	}

	/**
	 * 解析报文字段定义.
	 *
	 * @param e    元素
	 * @param item 报文解析对象
	 * @return true, if successful
	 */
	private boolean parseCommonItem(Element e, TransItem item) {
		String name = e.attributeValue(NetConst.AT_NAME);
		String targetName = e.attributeValue(NetConst.AT_TARGET);
		String desc = e.attributeValue(NetConst.AT_DESC);
		String type = e.attributeValue(NetConst.AT_TYPE);
		String length = e.attributeValue(NetConst.AT_LEN);
		String required = e.attributeValue(NetConst.AT_REQUIRED);
		String defaultValue = e.attributeValue(NetConst.AT_DEFAULT);
		String plus = e.attributeValue(NetConst.AT_PLUS);
		String comment = e.attributeValue(NetConst.AT_COMMENT);
		String xpath = e.attributeValue(NetConst.AT_XPATH);
		String format = e.attributeValue(NetConst.AT_FORMAT);
		String reg = e.attributeValue(NetConst.AT_REG);
		String desensType = e.attributeValue(NetConst.AT_DESENSITIVE_TYPE);
		String desensReg = e.attributeValue(NetConst.AT_DESENSITIVE_REG);
		String desensReplacement = e.attributeValue(NetConst.AT_DESENSITIVE_REPLACEMENT);
		item.setName(name);
		item.setType(StringUtil.isEmpty(type) ? NetConst.FILED_TYPE_C : type);
		item.setTarget(StringUtil.isEmpty(targetName) ? name : targetName);
		item.setLength(StringUtil.parseInt(length));
		item.setRequired("true".equalsIgnoreCase(required));
		item.setDesc(desc);
		item.setPlus(plus);
		item.setDefaultValue(defaultValue);
		item.setXpath(StringUtil.isEmpty(xpath) ? item.getTarget() : xpath);
		item.setComment(comment);
		item.setFormat(format);
		item.setReg(reg);
		item.setDesensType(desensType);
		item.setDesensReg(desensReg);
		item.setDesensReplace(desensReplacement);
		return true;
	}

	/**
	 * 是否为列表结构.
	 *
	 * @param elem the elem
	 * @return true, if is list item
	 */
	private boolean isListItem(Element elem) {
		String type = elem.attributeValue(NetConst.AT_TYPE);
		return NetConst.FILED_TYPE_M.equals(type) || NetConst.FILED_TYPE_E.equals(type);
	}

	/**
	 * 递归取子结构.
	 *
	 * @param item the item
	 * @param elem the elem
	 */
	private void parseListChildItem(TransItem item, Element elem) {
		if (isListItem(elem)) {
			// 如果类型是list 则进行 子节点解析
			List<Element> mapchild = elem.elements();
			List<TransItem> subList = new ArrayList<TransItem>();
			for (Element mapel : mapchild) {
				TransItem subItem = new TransItem();
				parseCommonItem(mapel, subItem);
				parseListChildItem(subItem, mapel);
				logger.trace("subItem item :{}", subItem);
				subList.add(subItem);
			}
			String type = elem.attributeValue(NetConst.AT_TYPE);
			item.setType(type);
			item.setChildren(subList);
		}
	}
}
