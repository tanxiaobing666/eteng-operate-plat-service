/**
 * Copyright (c) 2021 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-spring-cloud-template
 * 模块名称：cn.com.yitong.ares.net
 * 文件名称：NetMsgFilter4SC.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2021-3-11 9:17:43
 */

package cn.com.yitong.ares.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.core.TransItem;
import cn.com.yitong.ares.net.web.desensitive.DesensitiveConverterFactory;
import cn.com.yitong.ares.net.web.desensitive.IDesensitiveConverter;
import cn.com.yitong.util.common.MapUtil;
import cn.com.yitong.util.common.StringUtil;


@Component
public class NetMsgFilter4SC {
	

	/**
	 * The converter factory.
	 */
	@Autowired
	private DesensitiveConverterFactory converterFactory;
	
	/**
	 * 生成报文信息.
	 *
	 * @param srcParam the src param
	 * @param items the items
	 * @param targetParam the target param
	 */
	public void buildMsgInfo(IBusinessContext ctx, Map<String, Object> srcParam, List<TransItem> items,
			Map<String, Object> targetParam) {
		for (TransItem item : items) {
			buildItem(ctx, srcParam, item, targetParam);
		}
	}

	/**
	 * 生成请求数据.
	 *
	 * @param srcParam    源数据
	 * @param item        字段定义对象
	 * @param targetParam 目标数据
	 */
	public void buildItem(IBusinessContext ctx, Map<String, Object> srcParam,  TransItem item,
			Map<String, Object> targetParam) {

		// 字段类型
		String type = item.getType();
		if (NetConst.FILED_TYPE_E.equals(type)) { // 列表/数组/循环 结构数据
			// 取列表值
			List<Map<String, Object>> datas = MapUtil.getMapValue(srcParam, item.getName(),
					new ArrayList<Map<String, Object>>());
			if (null == datas) {
				return;
			}
			List<TransItem> children = item.getChildren();
			String target = item.getTarget();

			List<Map<String, Object>> targetDatas = new ArrayList<Map<String, Object>>();
			targetParam.put(target, targetDatas);

			if (children == null || children.size() == 0) {
				return;
			}
			for (Map<String, Object> data : datas) {
				Map<String, Object> subTargetData = new HashMap<String, Object>();
				for (TransItem childItem : children) {
					buildItem(ctx, data, childItem, subTargetData);
				}
				targetDatas.add(subTargetData);
			}
		} else if (NetConst.FILED_TYPE_M.equals(type)) { // 对象/Map 结构数据
			// 获取对象值
			Map<String, Object> data = MapUtil.getMapValue(srcParam, item.getName(), new HashMap<String, Object>());
			if (null == data) {
				return;
			}
			List<TransItem> children = item.getChildren();
			String target = item.getTarget();

			Map<String, Object> targetData = new HashMap<String, Object>();
			targetParam.put(target, targetData);
			if (children == null || children.size() == 0) {
				return;
			}

			for (TransItem childItem : children) {
				buildItem(ctx, data, childItem, targetData);
			}
		} else { // 其他类型，一般为字符串结构
			String name = item.getName();
			// 字段转换，如果设置了targetName则上送targetName对应的字段，否则送name字段
			String targetName = item.getTarget();
			Object valueObj = MapUtil.getMapValue(srcParam, name, item.getDefaultValue());
			//脱敏类型
			String desensType = item.getDesensType();
			//脱敏的正则表达式，需要配合replacement使用
			String desensReg = item.getDesensReg();
			//脱敏要替换成的字符串
			String desensReplacement = item.getDesensReplace();
			
			if(StringUtil.isNotEmpty(desensType) || (StringUtil.isNotEmpty(desensReplacement) &&
					StringUtil.isNotEmpty(desensReg))) {
				// 数据脱敏处理
				valueObj = getDesensitiveValue(ctx, valueObj,item);
			}

			if (null == valueObj) {
				valueObj = "";
			}
			buildElement(targetParam, targetName, valueObj);
		}
	}

	/**
	 * 获取脱敏值.
	 *
	 * @param valueObj the value obj
	 * @param item the item
	 * @return the desensitive value
	 */
	private Object getDesensitiveValue(IBusinessContext ctx, Object valueObj, TransItem item) {
		IDesensitiveConverter converter = converterFactory.getConverter(item);
		//未取到converter
		if(converter == null) {
			return valueObj;
		}
		return converter.getConvertedValue(ctx, valueObj, item);
	}

	/**
	 * 组装节点数据.
	 *
	 * @param rootData the root data
	 * @param name     the name
	 * @param value    the value
	 */
	public void buildElement(Map<String, Object> rootData, String name, Object value) {
		rootData.put(name, value);
	}
}
