package cn.com.yitong.actions.atom.system;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.util.common.SeqGeneUtil;
import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
*新增菜单
*@author
*/
@Service
public class MenuAdd extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SystemCommonOp systemCommonOp;

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-新增菜单-run--");

		//校验级数是否超过三级
		if (systemCommonOp.checkMenuLevel(ctx)) {
			ctx.setParam("MENU_ID", SeqGeneUtil.geneBusiSN("M"));
			this.getDao(ctx).insert("menu.menuAdd",ctx.getParamMap());
		}else {
			throw new AresRuntimeException("system.menu.add.check");
		}
		return NEXT;
	}

}
