package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;

import java.util.List;
import java.util.Map;

/**
*同步记录
*@author
*/
@Service
public class NewsSyncRecordOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-同步记录-run--");
		List<Map<String,Object>> list = this.getDao(ctx).pageQuery("news.newsSyncRecords", ctx.getParamMap(),ctx);
		ctx.getParamMap().put("LIST",list);
		return NEXT;
	}

}
