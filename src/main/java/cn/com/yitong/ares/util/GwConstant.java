/**
 * Copyright (c) 2018 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-mobile-gateway
 * 模块名称：cn.com.yitong.ares.gw.constant
 * @version 1.0.0
 * @author zwb
 * @date 2018-7-14 15:28:48
 */
package cn.com.yitong.ares.util;

/**
 * 网关常量定义
 * 
 * @author zwb
 *
 */
public interface GwConstant {
	/**
	 * 报文头字段
	 */
	public static final String HEAD = "head";

	/**
	 * 报文体字段
	 */
	public static final String BODY = "body";

	/**
	 * 请求路径，示例： <code>/mbank/transfer.do</code>
	 */
	public static final String TRANS_URL = "TRANS_URL";

	/**
	 * 渠道ID，渠道代码和法人号，区别唯一渠道ID
	 */
	final static String H_CHNL_ID = "H_CHNL_ID";

	/**
	 * URL或者FORM表单传参的渠道ID字段名称
	 */
	final static String URL_H_CHNL_ID = "chnlId";

	/**
	 * 渠道代码
	 */
	final static String H_CHNL_CODE = "H_CHNL_CODE";

	/**
	 * 法人号
	 */
	final static String H_LEGAL_ID = "H_LEGAL_ID";

	/**
	 * 客户端时间戳，示例：1531552980
	 */
	final static String H_TIMESTAMP = "H_TIME";

	/**
	 * 客户端与服务端时间偏差值，使用服务端的时间戳-客户端时间戳，例如1531552980-1531552870=110
	 */
	final static String H_TIMESTAMP_OFFSET = "H_TIME_OFFSET";

	/**
	 * 请求唯一ID，可使用UUID，示例：0d50d634-b891-48c5-a05c-e8176c4141bc
	 */
	final static String H_NONCE = "H_NONCE";

	/**
	 * 服务端响应时间戳，示例：1531464785525
	 */
	final static String H_RSP_TIMESTAMP = "H_RSP_TIME";

	/**
	 * 响应流水号，示例：20180717091118001000000000002
	 */
	final static String H_RSP_SEQ_NO = "H_RSP_SEQ";

	/**
	 * 响应状态码，1表示成功，其他表示失败 ，示例：1
	 */
	final static String STATUS = "STATUS";

	/**
	 * 响应状态描述 ，示例：交易成功
	 */
	final static String MSG = "MSG";

	/**
	 * 报文加密方式，0：不加密，1：国际三段式加密，2：国密三段式加密V1，3：国密三段式加密V2
	 */
	final static String CHNL_ENCRYPT_TYPE = "CHNL_ENCRYPT_TYPE";

	/**
	 * 后端系统会话ID
	 */
	final static String H_UPS_SID = "H_UPS_SID";
	
	/**
	 * URL或者FORM表单传参后端系统会话ID字段名称
	 */
	final static String URL_H_UPS_SID = "sid";

	/**
	 * 无需解密标识
	 */
	final static String NO_DECRYPT = "_NO_DECRYPT";
	
	/**
	 * 无需加密标识
	 */
	final static String NO_ENCRYPT = "_NO_ENCRYPT";
}
