package cn.com.yitong.actions.login;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.ICacheService;
import cn.com.yitong.ares.core.AresResource;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.StringUtil;
import cn.com.yitong.util.common.ValidUtils;

/**
 * 
 * @author HEFAN
 *
 */
@Service
public class LoginDataCheckOp extends AbstractOp implements IAresSerivce {

	private static final String BigDecimal = null;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("redisService")
	ICacheService cacheService;
	
	@Autowired
	Environment env;

	@SuppressWarnings("unchecked")
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		// 1.查询 数据库信息
		String statementName = ValidUtils.validEmpty("*sqlId", ctx.getParamMap());
		Map paramMap = new HashMap();
		paramMap.putAll(ctx.getParamMap());
		paramMap.putAll(ctx.getHeadMap());
		Map<String,Object> rspMap = this.getDao(ctx).queryForMap(statementName, paramMap);

		// logger.info("userInfo:{}", rspMap);
		if (rspMap == null || rspMap.isEmpty()) {
			throw new AresRuntimeException("user.name.error"); // 用户名或密码错误
		}
		Integer statBigDecimal = (Integer) rspMap.get("STAT");
		if (statBigDecimal.toString().equals("1")) {
			throw new AresRuntimeException("user.forbifden"); // 当前用户被锁定
		}
		if (statBigDecimal.toString().equals("2")) {
			throw new AresRuntimeException("user.cancel"); // 当前用户被注销
		}
		if (AresResource.getBoolean("ares.debug")) {
			// 若为测试状态，则不进行验密等处理
			ctx.getParamMap().putAll(rspMap);
			return NEXT;
		}
		
		
		//1、判断当前密码是否正确
		//密码正确
		if (paramMap.get("USER_PWD").equals(rspMap.remove("PASSWORD"))) {
			//错误次数清零
			int errLgnCnt = 0;
			paramMap.put("ERR_LGN_CNT", errLgnCnt);
			this.getDao(ctx).update("login.upErrLgnCnt", paramMap);
		}else {
			//密码不正确
			Integer errLgnCnt = rspMap.get("ERR_LGN_CNT")==null?0:(Integer)rspMap.get("ERR_LGN_CNT");
			errLgnCnt++;
			paramMap.put("ERR_LGN_CNT", errLgnCnt);
			this.getDao(ctx).update("login.upErrLgnCnt", paramMap);
			
			//错误次数达到配置的次数，用户转态锁定，登录报错
			Integer errorTimesNum = Integer.parseInt(env.getProperty("ERROR_TIMES_WAIT"));
			if(errLgnCnt >= errorTimesNum) {
				paramMap.put("STAT", "1");
				this.getDao(ctx).update("login.updateState", paramMap);
				
				throw new AresRuntimeException("user.passWord.reach"); 
			}else {
				//抛异常-用户名或密码错误
				throw new AresRuntimeException("user.passWord.error"); 
			}
			
			
		}
		
		ctx.getParamMap().putAll(rspMap);

//		// 2.校验当前已错误次数 判断是否达到，如达到， 判断上次登录时间
//		String errorTimes = cacheService.get("ERROR_TIMES_" + (String) paramMap.get("LOGIN_ID"));
//		logger.info("userInfo:{},errors:{}", rspMap, errorTimes);
//		if (!StringUtil.isBlank(errorTimes) && errorTimes.compareToIgnoreCase(AresResource.getString("ERROR_TIMES")) >= 0) {
//			throw new AresRuntimeException("006"); // 3. 当前登录错误次数已超
//		}
//
//		// 4.判断登录是否正确，如正确 修改数据库信息，修改错误次数 ，不正确， 增加错误时间
//		if (paramMap.get("USER_PWD").equals(rspMap.remove("PASSWORD"))) {
//			cacheService.del("ERROR_TIMES_" + (String) paramMap.get("LOGIN_ID"));
//			ctx.getParamMap().putAll(rspMap);
//
//		} else {// 登录不正确
//			errorTimes = StringUtil.isBlank(errorTimes) ? "0" : errorTimes;
//
//			long timeWait = 300L;
//			if (!StringUtil.isBlank(env.getProperty("ERROR_TIMES_WAIT"))) {
//				timeWait = Long.parseLong(env.getProperty("ERROR_TIMES_WAIT"));
//			}
//			errorTimes = (1 + Integer.parseInt(errorTimes)) + "";
//			cacheService.set("ERROR_TIMES_" + (String) paramMap.get("LOGIN_ID"), errorTimes, timeWait);
//			throw new AresRuntimeException("EMKH006B"); // 用户名或密码错误
//		}

		return NEXT;
	}

}
