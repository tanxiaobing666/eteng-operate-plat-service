package cn.com.yitong.ares.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.ICacheService;
import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.consts.RedisKeyConst;
import cn.com.yitong.ares.consts.SessR;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.StringUtil;
import cn.com.yitong.util.common.ValidUtils;

/**
 * 用户会话校验
 * @author HEFAN
 *
 */
@Service
@Configuration
public class UserSessCheckOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 无需检查会话的交易URL规则
	 */
	@Value("${chnl.none.session.check.url:}")
	private String noneSessCheckUrl;

	/**
	 * 会话超时时间
	 */
	@Value("${ares.spring.session.timeout:1200}")
	private int sessionTimeout;

	/**
	 * 缓存工具
	 */
	@Autowired
	@Qualifier("redisService")
	ICacheService cacheService;

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("-用户会话校验-run--");

		String transUrl = ValidUtils.validEmpty(AresR.TRANS_URL, ctx.getHeadMap());
		// 过滤无会话交易
		if (transUrl.matches(noneSessCheckUrl)) {
			logger.debug("session check pass!{}", transUrl);
			// ctx.resetCacheTime(sessionTimeout);
			return NEXT;
		}

		String token = ctx.getSessionId();// token
		String userId = ctx.getSessionObject(SessR.SESS_USER_ID); // 当前用户客户号
		String userIdKey = RedisKeyConst.CUST_NO_PREFIX + ":" + userId;
		// 当前用户保存在redis中sessionId
		String redisSessId = ctx.getCache(userIdKey);

//		// 005 会话超时
//		if (StringUtil.isEmpty(token) || StringUtil.isEmpty(redisSessId)) {
//			logger.warn("Session [{}] time out 005 ! ", token);
//			throw new AresRuntimeException("005");
//		}
		// 009 会话无效，用户已在其他设备上登录
//		if (!token.equals(redisSessId)) { // 如果当前用户sessionId与redis中保存的sessionId不一致，说明该用户在其他设备上登录
//			ctx.destorySession();
//			logger.warn("Session [{}] time out 009 ! ", token);
//			throw new AresRuntimeException("009");
//		}
		
		// 实时更新redis超时时间
		cacheService.resetLiveTime(userIdKey, sessionTimeout);
		return NEXT;
	}

}
