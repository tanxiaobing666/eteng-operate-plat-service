/**
 * Copyright (c) 2018 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 * 
 * 系统名称：ares-action-atom
 * 模块名称：cn.com.yitong.actions.atom
 * @version 1.0.0
 * @author zwb
 * @date 2018-7-10 19:44:57
 */
package cn.com.yitong.actions.atom;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.SeqGeneUtil;
import cn.com.yitong.util.common.StringUtil;

/**
 * 批量流水号序号生成，不依赖数据库，使用线程安全AtomicInteger进行生成 可通过传递流程内部参数：*seqPrefix进行流水号前缀赋值
 * 
 * <pre>
 * 内部参数:
 * 1、*seqType		流水号号类型，1：业务流水号，2：系统流水号，3：订单流水号，4：业务主键流水号，默认值1
 * 2、*seqPrefix		流水号前缀，默认值:S
 * 3、*seqVarName	流水号自定义变量名称，默认值：BUSI_KEY_SN
 * 
 * </pre>
 * @author zwb
 */
@Service
public class BatchSeqGeneOp implements IAresSerivce {
	
	/**
	 * 业务流水号
	 */
	private final static int BUSI_SN=1;
	
	/**
	 * 系统流水号
	 */
	private final static int SYS_SN=2;

	/**
	 * 生成订单号 
	 */
	private final static int ORDER_SN=3;

	/**
	 * 生成业务主键流水号
	 */
	private final static int BUSI_KEY_SN=4;


	/**
	 * 日志工具
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 执行任务
	 */
	@Override
	public int execute(IBusinessContext ctx) {
		logger.info(">>> 获取序列流水号 ...");
		
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = ctx.getParamDatas("LIST");
		
		for(Map<String, String> map:list)
		{
			
			// 获取流水号类型
			int type = StringUtil.getInt(ctx.getParamMap(), "*seqType", 1);
			// 获取序列号前缀
			String seqPrefix = StringUtil.getString(ctx.getParamMap(), "*seqPrefix",
					"S");
			// 获取序列号前缀
			String seqVarName = StringUtil.getString(ctx.getParamMap(),
					"*seqVarName", "BUSI_KEY_SN");
			String seqVarVal = map.get(seqVarName);
			if(!StringUtil.isEmpty(seqVarVal)){
				continue;
			}
			// 获取流水号
			String sn = "";

			switch (type) {
			case BUSI_SN:
				sn = SeqGeneUtil.geneBusiSN(seqPrefix);
				break;
			case SYS_SN:
				sn = SeqGeneUtil.geneSysSN(seqPrefix);
				break;
			case ORDER_SN:
				sn = SeqGeneUtil.geneOrderSN(seqPrefix);
				break;
			case BUSI_KEY_SN:
				sn = SeqGeneUtil.geneBusiKeySN(seqPrefix);
				break;
			default:
				break;
			}
			map.put(seqVarName, sn);
			
		}

		


		return NEXT;
	}

}
