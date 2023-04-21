/**
 * Copyright (c) 2020 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	:ares-spring-boot-template</p>
 * <p>包名称    	:cn.com.yitong.ares.test</p>
 * <p>文件名称	:PasswordUtilTest.java</p>
 * <p>创建时间	:2020-5-18 12:38:41 </p>
 */
package cn.com.yitong.ares.test;

import org.junit.jupiter.api.Test;

import cn.com.yitong.ares.starter.util.PasswordUtil;

/**
 * 密码工具测试类.
 */
public class PasswordUtilTest {

	/**
	 * 测试密码加密
	 */
	@Test
	void testEncryptPwd() {
		// 密码加密，参数1：密码原文，参数2：加密盐值
		System.out.println(PasswordUtil.encryptPwd("123456", "salt"));
	}
}
