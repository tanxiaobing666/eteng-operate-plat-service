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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.util.common.FileUtil;
import cn.com.yitong.util.common.ListUtil;

public class FileUtils {
	
	/**
	 * 记录日志工具.
	 */
	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	/**
	 * 默认的contentType
	 */
	private static String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	
	
	/**
	 * 获取文件后缀
	 * @param fileName 文件名
	 * @return
	 */
	public static String getFileExtName(String fileName) {
		return FileUtil.getFileExtName(fileName);
	}
	
	/**
	 * 删除本地文件
	 * @param fileList
	 * @return
	 */
	public static void deleteFiles(List<String> filePathList){
		if(ListUtil.isEmpty(filePathList)) {
			return;
		}
		
		filePathList.forEach(filePath ->{
			File file = new File(filePath);
			if(file.exists()) {
				boolean isFileDeleted = file.delete();
				logger.debug("文件路径【{}】，删除{}",filePath, isFileDeleted?"成功":"失败" );			
			}						
		});				
	}
	
	/**
	 * 通过文件地址保存文件到本地
	 * @param file
	 * @param urlStr
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void saveFileToLocalByUrl(File file, String urlStr)
			throws MalformedURLException, IOException, FileNotFoundException {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			URL url = new URL(urlStr);
			/*此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流*/
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			in = new BufferedInputStream(connection.getInputStream());
			out = new BufferedOutputStream(new FileOutputStream(file));
			/*将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址*/
			byte[] buffer = new byte[4096];
			int count = 0;
			/*将输入流以字节的形式读取并写入buffer中*/
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();/*后面三行为关闭输入输出流以及网络资源的固定格式*/
			in.close();
			connection.disconnect();
			//返回内容是保存后的完整的URL
		}catch(Exception e){
			logger.error("通过文件地址将文件保存到本地失败",e);
		}finally {
			if(in!=null) {
				try{
					in.close();
				}catch(Exception e) {
					logger.error("流关闭异常",e);
				}
			}
			if(out!=null) {
				try{
					out.close();
				}catch(Exception e) {
					logger.error("流关闭异常",e);
				}
			}
		}
	}
	
	/**
	 * 根据文件路径获取文件名
	 * @param filePath
	 * @return
	 */
	public static String getName(String filePath) {
		if (null == filePath) {
			return null;
		}
		int len = filePath.length();
		if (0 == len) {
			return filePath;
		}
		if (isFileSeparator(filePath.charAt(len - 1))) {
			// 以分隔符结尾的去掉结尾分隔符
			len--;
		}

		int begin = 0;
		char c;
		for (int i = len - 1; i > -1; i--) {
			c = filePath.charAt(i);
			if (isFileSeparator(c)) {
				// 查找最后一个路径分隔符（/或者\）
				begin = i + 1;
				break;
			}
		}

		return filePath.substring(begin, len);
	}
	
	
	/**
	 * 是否为Windows或者Linux（Unix）文件分隔符<br>
	 * Windows平台下分隔符为\，Linux（Unix）为/
	 *
	 * @param c 字符
	 * @return 是否为Windows或者Linux（Unix）文件分隔符
	 */
	public static boolean isFileSeparator(char c) {
		return '/' == c || '\\' == c;
	}	
	
	/**
	 * 判断文件是否存在
	 * 
	 * @return
	 */
	public static boolean isFileExisted(String path) throws IOException{		
		String filePath = path.replaceAll("//", "/");
		Resource res = AresApp.getInstance().getApplicationContext().getResource(filePath);
        InputStream ins = null;
		try {
			ins = res.getInputStream();			
			return true;
		} catch (FileNotFoundException e) {
			logger.warn("Flow file undefined, file path: {}",filePath);
		} catch (IOException e) {
			logger.error("isFlowExisted throw IOException {}", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(ins);
		}
		return false;
	}
}
