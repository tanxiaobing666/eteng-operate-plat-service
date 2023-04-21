package cn.com.yitong.actions.atom.system;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.error.AresRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;

/**
*修改菜单
*@author
*/
@Service
public class MenuModi extends AbstractOp implements IAresSerivce {

	@Autowired
	private SystemCommonOp systemCommonOp;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-修改菜单-run--");
		//校验级数是否超过三级
		if (systemCommonOp.checkMenuLevel(ctx)) {
			this.getDao(ctx).insert("menu.menuModi",ctx.getParamMap());
		}else {
			throw new AresRuntimeException("system.menu.add.check");
		}
		return NEXT;
	}

}
