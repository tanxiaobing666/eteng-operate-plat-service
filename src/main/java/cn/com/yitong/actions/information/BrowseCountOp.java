package cn.com.yitong.actions.information;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;


/**
 * 浏览统计
 * 
 * @author
 */
@Service
public class BrowseCountOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-浏览统计-run--");
		Map<String, Object> map = new HashMap<>();
		
		map.put("BANK_ID", ctx.getParam("BANK_ID"));
		map.put("CORP_ID", ctx.getParam("CORP_ID"));
		map.put("START_TIME", ctx.getParam("START_TIME"));
		map.put("END_TIME", ctx.getParam("END_TIME"));			
	
		List<Map<String, Object>> BROWSE_LIST = this.getDao(ctx).queryForList("information.browseSum", map);												
		List<Map<String, Object>> SHARE_LIST = this.getDao(ctx).queryForList("information.shareSum", map);																
		
		ctx.setParam("BROWSE_LIST", BROWSE_LIST);
		ctx.setParam("SHARE_LIST", SHARE_LIST);
		return NEXT;
	}

}
