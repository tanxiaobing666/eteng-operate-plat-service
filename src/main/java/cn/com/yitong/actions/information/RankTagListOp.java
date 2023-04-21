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
*标签统计排行
*@author
*/
@Service
public class RankTagListOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-排行榜-run--");
		String BANK_ID = ctx.getParam("BANK_ID");
		String CORP_ID = ctx.getParam("CORP_ID");
		String RANK_TIME = ctx.getParam("RANK_TIME");
		String PAGE_SIZE = ctx.getParam("PAGE_SIZE");
		String NEXT_KEY = ctx.getParam("NEXT_KEY");		
		Map<String, Object> map = new HashMap<>();
		
		map.put("BANK_ID", BANK_ID);
		map.put("CORP_ID", CORP_ID);
		map.put("RANK_TIME",RANK_TIME);
		map.put("PAGE_SIZE", PAGE_SIZE);
		map.put("NEXT_KEY", NEXT_KEY);
		int SIZE = Integer.parseInt(PAGE_SIZE);
		int KEY = Integer.parseInt(NEXT_KEY);		
		
		List<Map<String, Object>> LIST = this.getDao(ctx).pageQuery("information.rankTagList", map, ctx);
		for(int i=0;i<LIST.size();i++) {
			LIST.get(i).put("RANK_ID",  (KEY-1)*SIZE+i+1);
		}
		
		 ctx.setParam("LIST",LIST);
	
		return NEXT;
	}

}
