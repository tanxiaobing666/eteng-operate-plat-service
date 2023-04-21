package cn.com.yitong.ares.common;

import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.INetConfParser;
import cn.com.yitong.ares.core.TransConfBean;
import cn.com.yitong.ares.dao.IbatisDao;
import cn.com.yitong.util.common.SeqGeneUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 异步添加日志
 * @author HEFAN
 *
 */

@Service
public class LogExecuterService {
	
	@Autowired
	Executor execute;
	
	@Autowired
	IbatisDao ibatisDao;
	
	@Autowired
	@Qualifier("netConfParser")
	INetConfParser confParser;
	
	public void execute(Map<String, Object> map) {
//		map.put("OPER_ID", SeqGeneUtil.geneBusiKeySN("L"));
		execute.execute(()->{
			TransConfBean bean = confParser.findTransConfById((String)map.get("TRANSCODE"));
			map.put("TITLE", bean.getProperty("desc"));
			map.put("METHOD", bean.getProperty("desc"));
			map.put("BUSINESS_TYPE", bean.getProperty("busi"));
//			map.put("OPER_ID", SeqGeneUtil.geneBusiKeySN("L"));
			String status = (String)map.get("STATUS");
			map.put("STATUS", StrUtil.equals("1", status)?0:1);
			map.put("ERROR_MSG", map.get("MSG"));
			map.put("OPERATOR_TYPE", 1);
			ibatisDao.insert("log.insert", map);
		});
	}

}
