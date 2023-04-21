package cn.com.yitong.ares.routes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.beetl.core.fun.MutipleFunctionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.util.FileUtil;
import cn.com.yitong.util.common.DateUtil;
import cn.com.yitong.util.common.RandomUtil;
import cn.com.yitong.util.common.StringUtil;

/**
 * 1)对文件格式限制，只允许某些格式上传 2)对文件格式进行校验，前端跟服务器都要进行校验（前端校验扩展名，服务器校验扩展名、Content_Type等）
 * 3)将上传目录防止到项目工程目录之外，当做静态资源文件路径， 4)对文件的权限进行设定。禁止文件下的执行权限，可配置为只读
 *
 */
@Controller
public class FileController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 可用于上传文件分类，用于后续处理流程的识别
	 */
	@Value("${file.upload.modules}")
	private String modules;

	/**
	 * 合法的文件类型或后缀，小写
	 */
	@Value("${file.upload.minitypes}")
	private String contentTypes;

	@Value("${file.imgUpload.dir}")
	private String uploadDir;

	private final long MAX_SIZE = 1024 * 1024 * 2;
	
	private final String DEFAULT_CORP = "eteng";
	
	private final String MODULE = "image";

	/**
	 * 检查查询
	 * 
	 * @param module
	 * @return
	 */
	private boolean checkModule(String module) {
		String[] datas = modules.split(";|,|\\|");
		for (String data : datas) {
			if (data.equals(module)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查内容类型
	 * 
	 * @param contentType
	 * @return
	 */
	private boolean checkContentType(String contentType) {
		String[] datas = contentTypes.split(";|,|\\|");
		for (String data : datas) {
			if (contentType.contains(data)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "/api/file/upload")
	public void upload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		PrintWriter writer = null;
		String userAgent = request.getHeader("User-Agent");
		logger.info("userAgent:{}", userAgent);
		try {
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getServletContext());
			upload(request, multipartResolver, result);
			// 生成JSON响应消息
			String json = JSONObject.toJSONString(result);
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			writer = response.getWriter();
			writer.write(json);
		} catch (IOException e) {
			logger.error("io error", e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	/**
	 * 上传素材
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/file/uploadResources")
	public void uploadResources(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		PrintWriter writer = null;
		String userAgent = request.getHeader("User-Agent");
		logger.info("userAgent:{}", userAgent);
		try {
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getServletContext());
			uploadResources(request, multipartResolver, result);
			// 生成JSON响应消息
			String json = JSONObject.toJSONString(result);
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
			writer = response.getWriter();
			writer.write(String.format("(%s)", json));
		} catch (IOException e) {
			logger.error("io error", e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	/**
	 * 上传素材处理方法
	 * @param request
	 * @param multipartResolver
	 * @param result
	 */
	private void uploadResources(HttpServletRequest request, CommonsMultipartResolver multipartResolver, Map result) {
		// 如果是文件上传请求
		if (!multipartResolver.isMultipart(request)) {
			outputReturnMsg(result, "101", "非文件上传请求");
			return;
		}
		// 将请求转换为文件处理请求
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		
		String group = multipartRequest.getParameter("group");// 素材分组
		logger.info("group:{}", group);
		
		String isCommon = multipartRequest.getParameter("isCommon");

		String maxSize = multipartRequest.getParameter("maxSize");// 文件最大大小

		// 获取所有文件名称
		List<MultipartFile> list = multipartRequest.getFiles("PATH");
		List<Map<String, Object>> filePathList = new ArrayList<Map<String, Object>>();
		if (list == null || list.isEmpty()) {
			outputReturnMsg(result, "103", "文件清单为空");
			return;
		}
		boolean doFlag = false;
		for (MultipartFile multipartFile : list) {
			if (null != multipartFile) {
				String fileName = multipartFile.getOriginalFilename();
				String contentType = multipartFile.getContentType();
				if (!checkContentType(contentType)) {
					logger.warn("contentType validator:{}", contentType + "|" + fileName);
					outputReturnMsg(result, "104", "文件类型错误");
					return;
				}
				long size = multipartFile.getSize();
				logger.debug("upload file size :{}", size);
				if (StringUtil.isEmpty(maxSize)) {
					maxSize = String.valueOf(MAX_SIZE);
				}
				if (size > Long.valueOf(maxSize)) {
					logger.warn("upload file size too large:{}", size);
					outputReturnMsg(result, "105", "文件大小过大");
					return;
				}
	
				// 截取文件格式名
				String suffix = fileName.substring(fileName.indexOf("."));
				String name = fileName.substring(0,fileName.indexOf("."));
	            Date date = new Date();
	            String savePath = "/picture/" + (new SimpleDateFormat("yyyyMM")).format(date) + "/" + (new SimpleDateFormat("yyyyMMdd")).format(date) + "/";
	            String path = String.format("%s/%s", uploadDir, savePath);
	
	            String realName = UUID.randomUUID().toString() + suffix;
	
	            String fullPath = path + realName;
	            String shortPath = savePath + realName;
	            String savePath2 =(new SimpleDateFormat("yyyyMM")).format(date) + "/" + (new SimpleDateFormat("yyyyMMdd")).format(date) + "/";
	            String shortPath2=savePath2+ realName;
				// 文件保存
				File localFile = new File(fullPath);
				File parent = localFile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				
				try {
					Map<String, Object> map = Maps.newHashMap();
					multipartFile.transferTo(localFile);// 上传至服务器
					int[] imgWH = FileUtil.getImgWH(fullPath);
					logger.info("上传成功，类型：{},保存路径：{}", contentType, shortPath);
					outputReturnMsg(result, AresR.RTN_SUCCESS, "上传成功!");
					map.put("PATH", shortPath2);
					map.put("IMGW", imgWH[0] + "");
					map.put("IMGH", imgWH[1] + "");
					map.put("GROUP_ID", group);
					map.put("NAME", name);
					map.put("RESOURCES_DESC", name);
					map.put("IS_COMMON", isCommon);
					filePathList.add(map);
					doFlag = true;
				} catch (Exception e) {
					logger.error("上传文件异常", e);
				}
			}
		}
		if (!doFlag) {
			outputReturnMsg(result, "106", "上传失败!");
			return;
		}
		result.put("LIST", filePathList);
	}
	

	private void upload(HttpServletRequest request, CommonsMultipartResolver multipartResolver, Map result) {
		// 如果是文件上传请求
		if (!multipartResolver.isMultipart(request)) {
			outputReturnMsg(result, "101", "非文件上传请求");
			return;
		}
		
		// 将请求转换为文件处理请求
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

//		String module = request.getParameter("module");// 资源类别：用于识别存储目录及后续处
//		logger.info("module:{}", module);

//		if (!checkModule(module)) {
//			// TODO 系统异常，限制功能请求，操作次数及频度限制
//			outputReturnMsg(result, "102", "业务模型非法");
//			return;
//		}
		String maxSize = multipartRequest.getParameter("maxSize");// 文件最大大小
		// path内不能含../等内容
		long current = System.currentTimeMillis();// 批次

		// 获取所有文件名称
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		List<String> filePathList = new ArrayList<String>();
		if (fileMap == null || fileMap.isEmpty()) {
			outputReturnMsg(result, "103", "文件清单为空");
			return;
		}
		boolean doFlag = false;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile multipartFile = entity.getValue();
			if (null != multipartFile) {
				String fileName = multipartFile.getOriginalFilename();
				String contentType = multipartFile.getContentType();
				if (!checkContentType(contentType)) {
					logger.warn("contentType validator:{}", contentType + "|" + fileName);
					outputReturnMsg(result, "104", "文件类型错误");
					return;
				}
				long size = multipartFile.getSize();
				logger.debug("upload file size :{}", size);
				if (StringUtil.isEmpty(maxSize)) {
					maxSize = String.valueOf(MAX_SIZE);
				}
				if (size > Long.valueOf(maxSize)) {
					logger.warn("upload file size too large:{}", size);
					outputReturnMsg(result, "105", "文件大小过大");
					return;
				}

				// 截取文件格式名
				String suffix = fileName.substring(fileName.indexOf("."));
				String fullPath = null;
				String shortPath = null;
				String curDate = DateUtil.formatDateToStr(new Date());
				String seq = RandomUtil.randomInt(5);
				shortPath = MODULE + "/" + DEFAULT_CORP + "/" + curDate + "/" + current + "-" + seq + suffix;
				fullPath = String.format("%s/%s", uploadDir, shortPath);
				logger.info("upload file:{} to target:{}", fileName, fullPath);

				// 文件保存
				File localFile = new File(fullPath);
				File parent = localFile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				try {
					FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), localFile);
//					multipartFile.transferTo(localFile);// 上传至服务器
					filePathList.add(shortPath);
					logger.info("上传成功，类型：{},保存路径：{}", contentType, shortPath);
					outputReturnMsg(result, AresR.RTN_SUCCESS, "上传成功!");
					result.put("path", filePathList);
					doFlag = true;
				} catch (Exception e) {
					logger.error("上传文件异常", e);
				}
			}
		}
		if (!doFlag) {
			outputReturnMsg(result, "106", "上传失败!");
		}
	}

	private void outputReturnMsg(Map output, String errCode, String errMsg) {
		output.put(AresR.RTN_CODE, errCode);
		output.put(AresR.RTN_MSG, errMsg);
	}

	/**
	 * 下载图片
	 * 
	 * @param request
	 * @param response
	 * @param path
	 */
	@RequestMapping(value = "/download/image.do")
	public void downloadImage(HttpServletRequest request, HttpServletResponse response, @RequestParam String path) {
		RandomAccessFile raf = null;
		OutputStream os = null;
		try {
			response.setContentType("image/png");
			if (StringUtil.isEmpty(path) || path.indexOf("..") >= 0) {
				logger.warn("文件路径非法【{}】", path);
				return;
			}

			String fullPath = String.format("%s/%s/%s", uploadDir,"picture", path);
			File file = new File(fullPath);
			if (!file.exists() || file.isDirectory()) {
				logger.info("下载的文件不存在【{}】", fullPath);
				return;
			}
			response.setHeader("Content-Length", String.valueOf(file.length()));
			raf = new RandomAccessFile(file, "r");
			os = response.getOutputStream();
			byte[] b = new byte[2048];
			int length;
			while ((length = raf.read(b)) > 0) {
				os.write(b, 0, length);
			}
			logger.debug("文件【{}】下载成功", path);
		} catch (FileNotFoundException e) {
			logger.error("下载的文件不存在", e);
		} catch (Exception e) {
			logger.error("下载文件异常", e);
		} finally {
			try {
				if (raf != null) {
					raf.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
//			IOUtils.closeQuietly(raf);
			IOUtils.closeQuietly(os);
		}
	}

	/**
	 * 下载普通文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/download/file.do")
	public void downloadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam String path,
			@RequestParam String fileName) throws Exception {
		RandomAccessFile raf = null;
		OutputStream os = null;
		try {
			if (StringUtil.isEmpty(path) || path.indexOf("..") >= 0) {
				logger.warn("文件路径非法【{}】", path);
				return;
			}

			String fullPath = String.format("%s/%s", uploadDir, path);
			File file = new File(fullPath);
			if (!file.exists() || file.isDirectory()) {
				logger.info("下载的文件不存在【{}】", fullPath);
				return;
			}
			long fileLength = file.length();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition",
					"attachment;fileName=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
			response.setHeader("Content-Length", String.valueOf(fileLength));
			raf = new RandomAccessFile(file, "r");
			os = response.getOutputStream();
			byte[] b = new byte[2048];
			int length;
			while ((length = raf.read(b)) > 0) {
				os.write(b, 0, length);
			}
			logger.info("文件【{}】下载成功", path);
		} catch (FileNotFoundException e) {
			logger.error("下载的文件不存在", e);
		} catch (Exception e) {
			logger.error("下载文件异常", e);
		} finally {
			try {
				if (raf != null) {
					raf.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
//			IOUtils.closeQuietly(raf);
			IOUtils.closeQuietly(os);
		}

	}
	
}
