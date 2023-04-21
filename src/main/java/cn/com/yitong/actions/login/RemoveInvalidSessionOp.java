package cn.com.yitong.actions.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.consts.SessR;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.StringUtil;

/**
 *清空非法会话（非法会话被复用）
 *@author
 */
@Service
public class RemoveInvalidSessionOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("-清空非法会话（非法会话被复用）-run--");
		//登录交易存在之前登录的记录，则清空session，重新生成
		if(!StringUtil.isEmpty((String)ctx.getParam(SessR.SESS_LOGIN_TOKEN))){
			logger.warn("会话非法复用");
			//删除当前非法会话
			ctx.destorySession();
			//重新生成会话
			ctx.resetSession();
		}
		return NEXT;
	}
}
