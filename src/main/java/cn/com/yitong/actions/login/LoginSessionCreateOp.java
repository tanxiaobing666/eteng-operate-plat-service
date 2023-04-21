package cn.com.yitong.actions.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.consts.RedisKeyConst;
import cn.com.yitong.ares.consts.SessConsts;
import cn.com.yitong.ares.flow.IAresSerivce;

/**
 * 创建登录会话;
 * 
 */
@Component
public class LoginSessionCreateOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${ares.spring.session.timeout}")
	private int sessionTimeout;


	@Override
	public int execute(IBusinessContext ctx) {
		// 销毁会话
		ctx.destorySession();
		// 重新创建会话
		ctx.resetSession();
		logger.debug("用户会话创建");
		String userId = ctx.getParam("LOGIN_ID");
		String token = ctx.getSessionId();
		// 创建用户会话
		ctx.saveSessionObject(SessConsts.SESS_LOGIN_TOKEN, token);// 会话ID
		ctx.saveSessionObject(SessConsts.SESS_USER_ID, userId);
		ctx.saveSessionObject(SessConsts.SESS_USER_NAME, ctx.getParam("USER_NAME")); 
		ctx.saveSessionObject(SessConsts.SESS_ROLE_TYPE, ctx.getParam("ROLE_TYPE"));
		ctx.saveSessionObject(SessConsts.SESS_ROLE_ID, ctx.getParam("ROLE_ID")); 
		ctx.saveSessionObject(SessConsts.SESS_ROLE_NAME, ctx.getParam("ROLE_NAME")); 
		
		ctx.saveSessionObject("LOGIN_STATUS", "1"); // 登陆成功
		
		String sessKey = RedisKeyConst.CUST_NO_PREFIX + ":" + userId; //为防止多行拥有同一个后管账号，key中增加行号保证key的唯一性
		//设置客户对应的sessionToken
		ctx.setCacheValue(sessKey, token, sessionTimeout);
		ctx.submitSession();
		return NEXT;
	}
}
