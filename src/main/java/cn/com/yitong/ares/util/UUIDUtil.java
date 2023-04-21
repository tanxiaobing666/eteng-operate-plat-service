/**
 * Copyright (c) 2020 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-file-server
 * 模块名称：cn.com.yitong.ares.file.server.core
 * 文件名称：FileUtils.java
 * @version 1.0.0
 * @author yangjiayu
 * @date 2020-5-12 10:46:43
 */
package cn.com.yitong.ares.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

	/**
	 * @Description: UUID 工具类
	 */
	public class UUIDUtil {
	    /**
	     * 获取一个UUID值
	     * @return UUID值[String]
	     */
	    public  static String getUUID(){
	        return UUID.randomUUID().toString().replaceAll("-","");
	    }

	    /**
	     * 获取多个UUID值
	     * @param number 所需个数
	     * @return UUID集合
	     */
	    public static List<String> getUUID(Integer number){
	        List<String> list = new ArrayList<>();
	        while (0 <= (number--)){
	            list.add(getUUID());
	        }
	        return list;
	    }
	    
	}
	
