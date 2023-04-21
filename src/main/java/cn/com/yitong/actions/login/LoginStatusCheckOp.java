package cn.com.yitong.actions.login;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.consts.RedisKeyConst;
import cn.com.yitong.ares.consts.SessConsts;
import cn.com.yitong.ares.consts.SessR;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.StringUtil;

/**
 * 用户会话检查
 * @author HEFAN
 *
 */
@Service
public class LoginStatusCheckOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	

	@Override
	public int execute(IBusinessContext ctx) {

		logger.debug("-登录状态判断-run--");
		
		String sessionToken = ctx.getSessionObject(SessConsts.SESS_LOGIN_TOKEN); //登录TOKEN(sessionId)
		String sessionUserId=ctx.getSessionObject(SessR.SESS_USER_ID);
		String sessKey = RedisKeyConst.CUST_NO_PREFIX + ":" + sessionUserId;
		String redisSessId = ctx.getCache(sessKey); // 当前用户保存在redis中sessionId
		if(StringUtil.isEmpty(sessionToken) || StringUtil.isEmpty(redisSessId)){
			ctx.setParam("LOGIN_STATUS", "0");
		}else{
			ctx.setParam("LOGIN_STATUS", "1");
		}
		return NEXT;
	}
}
