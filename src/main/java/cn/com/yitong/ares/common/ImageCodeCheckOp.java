package cn.com.yitong.ares.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.consts.SessR;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.StringUtil;

/**
 * 图形验证码检查<br>
 * 
 */
@Component
public class ImageCodeCheckOp implements IAresSerivce {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run---");
		// 会话取值及删除
		String sessCode = ctx.removeOnceSession(SessR.SESS_IMAGE_CODE);
		String imageCode = ctx.getParam(AresR.IMAGE_CODE);
		logger.info("imageCode is {};sessCode is {}", imageCode, sessCode);
		if (StringUtil.isNotEmpty(imageCode) && !imageCode.equals(sessCode)) {
			throw new AresRuntimeException("EMKH003B");
		}
		return NEXT;
	}
}
