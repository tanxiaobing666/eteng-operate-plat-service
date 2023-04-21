/**
 * 输出为流类型
 */

package cn.com.yitong.ares.net.wechat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.INetTools;
import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.core.AresResource;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.util.common.StringUtil;

@Component
public class NetTools4WXPostExt implements INetTools {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${proxy.host}")
	private String proxyHost;
	
	@Value("${proxy.port}")
	private String proxyPort;
	
	@Value("${proxy.switch}")
	private boolean proxySwitch;
	
	
	@Value("${common.miniCode.download.windir}")
	private String baseWinPath;
	
	@Value("${common.miniCode.download.linuxdir}")
	private String baseLinuxPath;
	
	
//	@Value("${sftp.ip}")
//	private String sftpIp;
//	@Value("${sftp.port}")
//	private String sftpPort;
//	@Value("${sftp.username}")
//	private String sftpUsername;
//	@Value("${sftp.password}")
//	private String sftpPasword;
//	@Value("${sftp.path}")
//	private String sftpPath;
//	@Value("${sftp.prefix}")
//	private String sftpPrefix;

	@Override
	public boolean execute(IBusinessContext ctx, String transCode) {
		PrintWriter out = null;
		InputStream is = null;
		OutputStream os = null;
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
	            http.setRequestMethod("POST");
	            http.setRequestProperty("accept", "*/*");
				http.setRequestProperty("connection", "Keep-Alive");
				http.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				http.setDoOutput(true);
				http.setDoInput(true);
				http.setConnectTimeout(30000);
				http.setReadTimeout(60000);
				
				boolean ishttps = false;
	            int indexhttps = reqUrl.indexOf("https");
	            if (indexhttps == 0) {
	                ishttps = true;
	            }

	            if (ishttps) {
	                try {
						trustALLSSLCertificates(http);
					} catch (KeyManagementException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
	            }
				
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(http.getOutputStream());
				// 发送请求参数
				out.print((String) ctx.getParam("POST_REQ_DATA"));
				// flush输出流的缓冲
				out.flush();
				
				is = http.getInputStream();
				
			}else {
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
	            // 设置通用的请求属性
				 http.setRequestMethod("POST");
	            http.setRequestProperty("accept", "*/*");
				http.setRequestProperty("connection", "Keep-Alive");
				http.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				http.setDoOutput(true);
				http.setDoInput(true);
				http.setConnectTimeout(30000);
				http.setReadTimeout(60000);
				
				boolean ishttps = false;
	            int indexhttps = reqUrl.indexOf("https");
	            if (indexhttps == 0) {
	                ishttps = true;
	            }

	            if (ishttps) {
	                try {
						trustALLSSLCertificates(http);
					} catch (KeyManagementException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
	            }
				
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(http.getOutputStream());
				// 发送请求参数
				out.print((String) ctx.getParam("POST_REQ_DATA"));
				// flush输出流的缓冲
				out.flush();
				
				is = http.getInputStream();
				
			}
			
			//小程序码存放ftp位置
//			if (StringUtils.isNotBlank(sftpIp)) {
//				String[] ipStrings = sftpIp.split(",");
//				for (String ip : ipStrings) {
//					try {
//						
//						FtpUtil ftpUtil = new FtpUtil(ip, sftpPort,sftpUsername,sftpPasword);
//						ftpUtil.connect();
//						ftpUtil.uploadFile(is, sftpPath + "/" + sftpPrefix + "/", 
//								ctx.getParam("FILENAME"));
//						/*SFTPUtil sftp = new SFTPUtil(AresResource.getString("sftp.username"),
//								AresResource.getString("sftp.password"), ip, AresResource.getInt("sftp.port", 22));
//						sftp.login();
//						sftp.upload(
//								AresResource.getString("sftp.path") + "/" + AresResource.getString("sftp.prefix") + "/",
//								ctx.getParam("FILENAME"), is);
//						sftp.logout();*/
//					} catch (Exception e) {
//						logger.error("生成二维码失败!", e);
//					}
//					
//				}
//			}
			
			
			//小程序码存放服务器本地目录：服务器目录/小程序目录/客户经理编号
			BufferedInputStream bis = new BufferedInputStream(is);
			
			
			String systemType = System.getProperty("os.name");  
			if(systemType.toLowerCase().startsWith("win")){  
				os = new FileOutputStream(new File(baseWinPath + File.separator + ctx.getParam("ACCT_OPEN_ID") + File.separator + ctx.getParam("SCENE")+".png"));
				ctx.setParam("CODE_IMG", baseWinPath + File.separator + ctx.getParam("ACCT_OPEN_ID") + File.separator + ctx.getParam("SCENE")+".png");
			}else {
				os = new FileOutputStream(new File(baseLinuxPath + File.separator + ctx.getParam("ACCT_OPEN_ID") + File.separator + ctx.getParam("SCENE")+".png"));
				ctx.setParam("CODE_IMG", baseLinuxPath + File.separator + ctx.getParam("ACCT_OPEN_ID") + File.separator + ctx.getParam("SCENE")+".png");
			}  
			
			int size = is.available();
			int len;
			byte[] buf = new byte[size];
			while ((len = bis.read(buf)) != -1) {
				os.write(buf, 0, len);
				os.flush();
			}

			is.read(buf);
			String resp = new String(buf, "UTF-8");
			JSONObject map = null;
			//特殊处理获取小程序码的接口
			if(transCode.indexOf("getUnlimited")!=-1) {				
				try {
					map = JSONObject.parseObject(resp);
				}catch(Exception e) {
					map = new JSONObject();
				}
			}else {
				map = JSONObject.parseObject(resp);
			}
			logger.info("微信响应数据：{}", resp);
			
			if (StringUtil.isNotEmpty(map.getString("errcode")) && !"0".equals(map.getString("errcode"))) {
				throw new AresRuntimeException(map.getString("errcode"), map.getString("errmsg"));
			}
			logger.info("getParamMap>>{}", ctx.getParamMap());
			if (ctx.isDirect()) {
				ctx.getParamMap().clear();
			}
			logger.info("getParamMap>>{}", ctx.getParamMap());
		} catch (MalformedURLException e) {
			throw new AresRuntimeException("errcode", "MalformedURLException.urlGetError");
		} catch (IOException e) {
			e.printStackTrace();
			throw new AresRuntimeException("errcode", "IOException.urlGetError");
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException ex) {
				logger.error("关闭流失败!", ex);
			}
		}
		return true;
	}
	
	/**
     * 信任所有SSL证书
     * 
     * @param httpConnection
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private void trustALLSSLCertificates(HttpURLConnection con) throws NoSuchAlgorithmException, KeyManagementException {
        ((HttpsURLConnection) con).setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        // Ignore Certification
        TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {

            }

            public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {

            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };
        // Prepare SSL Context
        TrustManager[] tm = { ignoreCertificationTrustManger };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, tm, new java.security.SecureRandom());

        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        ((HttpsURLConnection) con).setSSLSocketFactory(ssf);

    }
}
