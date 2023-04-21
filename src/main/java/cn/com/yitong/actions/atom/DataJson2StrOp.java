package cn.com.yitong.actions.atom;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.jstl.JstlUtil;
import cn.com.yitong.logger.AresLogger;
import cn.com.yitong.logger.api.IAresLogger;

/**
 * json转String
 * @author byjiang
 *
 */

@Service
public class DataJson2StrOp implements IAresSerivce{
	
    public Logger logger;
	
	public DataJson2StrOp()
    {
        logger = LoggerFactory.getLogger(getClass());
    }
	
	@Override
	public int execute(IBusinessContext ctx) {
		String jsonKey = ctx.getParam("*jsonKey", "json");

		String str = "";

		if (jsonKey.contains(".")) {
			int last = jsonKey.lastIndexOf(".");
			String parentKey = jsonKey.substring(0, last);
			Map parent = ctx.getParam(parentKey);
			if (parent == null || !(parent instanceof Map)) {
				throw new AresRuntimeException("ctx.parent.not.exits", new Object[] {
						"\u7236\u7EA7\u8282\u70B9\u4E3A\u7A7A,\u6570\u636E\u5E93\u53D6\u503C\u4FDD\u5B58\u5931\u8D25" });
			}
			str = JSONObject.toJSONString(parent.get(jsonKey.substring(last + 1)));

		} else {
			str = JSONObject.toJSONString(ctx.getParam(jsonKey));
		}
		
		ctx.setParam(jsonKey, str);
		
		ctx.removeParam("*jsonKey");
		return NEXT;
	}
	
}
