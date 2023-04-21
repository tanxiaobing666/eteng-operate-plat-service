package cn.com.yitong.ares.net.wechat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.INetTools;
import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.error.AresRuntimeException;

@Component
public class NetTools4WXGet implements INetTools {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${proxy.host}")
	private String proxyHost;
	
	@Value("${proxy.port}")
	private String proxyPort;
	
	@Value("${proxy.switch}")
	private boolean proxySwitch;

	@Override
	public boolean execute(IBusinessContext ctx, String transCode) {
		InputStream is = null;
		try {
			
			//通过模板动态渲染参数
			ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/template/");
			Configuration cfg = Configuration.defaultConfiguration();
			cfg.setCharset("utf-8");
			GroupTemplate groupTemplate = new GroupTemplate(resourceLoader, cfg);
			Template t = groupTemplate.getTemplate(transCode + ".htm");
			
			t.binding(ctx.getParamMap());
			String reqUrl = t.render();
			ctx.setParam(AresR.TRANS_URL, reqUrl);
			
			URL url = new URL(reqUrl.toString());
			
			//是否创建代理服务器
			if(proxySwitch){
	            InetSocketAddress addr = new InetSocketAddress(proxyHost,Integer.parseInt(proxyPort));  
	            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
	            HttpURLConnection http = (HttpURLConnection) url.openConnection(proxy);
	            // 设置通用的请求属性
	            http.setRequestProperty("accept", "*/*");
				http.setRequestProperty("connection", "Keep-Alive");
				http.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				http.setDoInput(true);
				http.setConnectTimeout(30000);
				http.setReadTimeout(60000);
				is = http.getInputStream();
			}else {
	            URLConnection http = url.openConnection();
	            // 设置通用的请求属性
	            http.setRequestProperty("accept", "*/*");
				http.setRequestProperty("connection", "Keep-Alive");
				http.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				http.setDoInput(true);
				http.setConnectTimeout(30000);
				http.setReadTimeout(60000);
				is = http.getInputStream();
			}
			
			int size = is.available();
			byte[] buf = new byte[size];
			is.read(buf);
			String resp = new String(buf, "UTF-8");
			JSONObject map = JSON.parseObject(resp);
			
			logger.info("微信请求响应数据：{}", resp);
			
			if(StringUtils.isNotEmpty(map.getString("errcode"))&&!"0".equals(map.getString("errcode"))) {
				throw new AresRuntimeException(map.getString("errcode"), map.getString("errmsg"));
			}
			ctx.getParamMap().putAll(map);
		} catch (MalformedURLException e) {
			throw new AresRuntimeException("errcode", "MalformedURLExceptionError");
		} catch (IOException e) {
			throw new AresRuntimeException("errcode", "IOExceptionError");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return true;
	}
}
