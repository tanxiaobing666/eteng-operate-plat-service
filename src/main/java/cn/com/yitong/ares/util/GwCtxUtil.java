package cn.com.yitong.ares.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.ares.entity.Message;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.logger.api.MDCConst;

public class GwCtxUtil {

	/**
	 * 根据报文体返回成功信息
	 * 
	 * @param body Map结构报文体
	 * @return 包装后的报文Map对象
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Map<String, Object> showSuccessResult(Map body) {
		Map<String, Object> head = new HashMap<String, Object>();
//		head.put(GwConstant.STATUS, AresR.RTN_SUCCESS);
//		head.put(GwConstant.MSG, msg);
		body.put(GwConstant.STATUS, "1");
		Map<String, Object> rspMap = new HashMap<String, Object>();
//		rspMap.put(GwConstant.HEAD, head);
		rspMap.put(GwConstant.BODY, body);
		return rspMap;
	}

	/**
	 * 根据错误码构建错误报文对象
	 * 
	 * @param errorCode
	 * @return 包装后的报文Map对象
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, Object> showErrorResult(String errorCode) {
		Map<String, Object> rspMap = new HashMap<String, Object>();
		rspMap.put(GwConstant.BODY, new HashMap<String, Object>());
		return rspMap;
	}

	/**
	 * 根据错误码构建错误报文对象
	 * 
	 * @param errorCode
	 * @param body
	 * @return 包装后的报文Map对象
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> showErrorResult(String errorCode, Map body) {
		Map<String, Object> rspMap = showErrorResult(errorCode);
		rspMap.put(GwConstant.BODY, body);
		return rspMap;
	}

	/**
	 * 简单成功信息返回
	 * 
	 * @param ctx 总线
	 * @return 包装后的报文Map对象
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, Object> showSuccessResult(IBusinessContext ctx) {

		String msg = AresApp.getInstance().getMessage(AresR.RTN_SUCCESS, AresR.EMPTY_PARAMS);
		Map<String, Object> head = getMsgHead(ctx);
		head.put(GwConstant.STATUS, AresR.RTN_SUCCESS);
		head.put(GwConstant.MSG, msg);

		Object body = ctx.getOutputEntry();

		Map<String, Object> rspMap = new HashMap<String, Object>();

		rspMap.put(GwConstant.HEAD, head);
		rspMap.put(GwConstant.BODY, body);

		MDC.put(MDCConst.SUCCESS, "true");
		return rspMap;
	}

	/**
	 * 错误信息显示
	 * 
	 * @param e   AresRuntimeException异常对象
	 * @param ctx 总线
	 * @return 包装后的报文Map对象
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, Object> showAresError(AresRuntimeException e, IBusinessContext ctx) {
		Map<String, Object> head = getMsgHead(ctx);
		if (e instanceof OtherRuntimeException) {
			OtherRuntimeException other = (OtherRuntimeException) e;
			head.put(GwConstant.STATUS, other.getErrorCode());
			head.put(GwConstant.MSG, other.getErrorMessage());
		} else {
			String msg = AresApp.getInstance().getMessage(e.getMessageKey(), e.getArgs(), ctx.getLocale());
			head.put(GwConstant.STATUS, e.getMessageKey());
			head.put(GwConstant.MSG, msg);
		}

		Object body = ctx.getOutputEntry();
		if (body == null) {
			body = new HashMap<String, Object>();
		}
		Map<String, Object> rspMap = new HashMap<String, Object>();

		rspMap.put(GwConstant.HEAD, head);
		rspMap.put(GwConstant.BODY, body);
		return rspMap;
	}

	/**
	 * 错误信息显示
	 * 
	 * @param e   Exception异常对象
	 * @param ctx 总线
	 * @return 包装后的报文Map对象
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, Object> showErrorResult(Exception e, IBusinessContext ctx) {
		if (e instanceof AresRuntimeException) {
			return showAresError((AresRuntimeException) e, ctx);
		}
		String error = "common.system.error";
		String msg = AresApp.getInstance().getMessage(error, AresR.EMPTY_PARAMS, ctx.getLocale());
		Map<String, Object> head = getMsgHead(ctx);
		head.put(GwConstant.STATUS, msg);
		head.put(GwConstant.MSG, msg);

		Object body = ctx.getOutputEntry();
		if (body == null) {
			body = new HashMap<String, Object>();
		}
		Map<String, Object> rspMap = new HashMap<String, Object>();
		rspMap.put(GwConstant.HEAD, head);
		rspMap.put(GwConstant.BODY, body);
		return rspMap;
	}

	/**
	 * 根据总线获取报文头
	 * 
	 * @param ctx
	 * @return Map结构的报文头
	 */
	private static Map<String, Object> getMsgHead(IBusinessContext ctx) {
		Map<String, Object> head = new HashMap<String, Object>();
		Message message = ctx.getInputEntry();

		if (message != null) {
			head.putAll(message.getHead());
		}

		head.put(GwConstant.H_RSP_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
		head.put(GwConstant.H_RSP_SEQ_NO, ctx.getMainSeqNo());
		return head;
	}
}
