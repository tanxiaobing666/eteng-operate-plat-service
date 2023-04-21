/**
 * 
 */
package cn.com.yitong.actions.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.ICacheService;
import cn.com.yitong.ares.consts.SessR;
import cn.com.yitong.ares.flow.IAresSerivce;

/**
 * 获取系统日期及时间
 *
 * @author
 */
@Service
public class LogoutOp implements IAresSerivce {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	@Qualifier("redisService")
	ICacheService cacheService;

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("-会话消除-run--");
		String userId = ctx.getSessionObject(SessR.SESS_USER_ID); // 当前用户客户号
		// 根据客户号获取用户的会话ID，一个客户只能有一个会话ID，
		String redisSessId = cacheService.get(userId); // 当前用户保存在redis中sessionId
		// 当前会话id
		String currSessionId = ctx.getSessionId();

		// 如果两个会话id相同，才能重置时间
		if (currSessionId.equals(redisSessId)) {
			// 使会话失效
			cacheService.resetLiveTime(userId, 0);
		}
		// 会话消毁
		ctx.destorySession();
		return NEXT;
	}

}
