package cn.com.yitong.actions.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.ICacheService;
import cn.com.yitong.ares.consts.SessConsts;
import cn.com.yitong.ares.flow.IAresSerivce;

/**
 * 设置单用户回话
 *
 * @author
 */
@Service
public class LoginSingleUserOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	@Qualifier("redisService")
	ICacheService cacheService;

	@Value("${ares.spring.session.timeout}")
	private long sessionTimeout;

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("-设置单用户会话标记-run--");

		String sessId = ctx.getSessionId();
		String userId = ctx.getSessionObject(SessConsts.SESS_USER_ID); // 当前用户客户号
		
		String sessKey = userId; //为防止多行拥有同一个后管账号，key中增加行号保证key的唯一性
		cacheService.set(sessKey, sessId, sessionTimeout); // 当前用户保存在redis中sessionId

		ctx.resetCacheTime(sessionTimeout);
		return NEXT;
	}

}
