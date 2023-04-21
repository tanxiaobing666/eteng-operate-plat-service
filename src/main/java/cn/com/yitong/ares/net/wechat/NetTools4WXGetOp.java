package cn.com.yitong.ares.net.wechat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.INetTools;
import cn.com.yitong.ares.flow.IAresSerivce;

@Component
public class NetTools4WXGetOp extends AbstractOp implements IAresSerivce {

	@Autowired
	@Qualifier("netTools4WXGet")
	INetTools netTools;

	@Override
	public int execute(IBusinessContext ctx) {
		String transCode = ctx.getParam("*transCode");
		netTools.execute(ctx, transCode);
		return NEXT;
	}
}
