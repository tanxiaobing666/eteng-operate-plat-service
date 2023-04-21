/**
 * Copyright (c) 2020 ShangHai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * <p>项目名称	:ares-spring-boot-template</p>
 * <p>包名称    	:cn.com.yitong.ares</p>
 * <p>文件名称	:AresSpringCloudApplication.java</p>
 * <p>创建时间	:2020-5-26 15:31:07 </p>
 */
package cn.com.yitong.ares;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;

/**
 * 
 * 应用启动类
 * 
 * @author zwb
 * @version 1.0
 * @since 6.0.0
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = MultipartAutoConfiguration.class)
public class AresSpringCloudApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(AresSpringCloudApplication.class, args);
	}
}
