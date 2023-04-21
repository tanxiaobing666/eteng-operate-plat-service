package cn.com.yitong.actions.information;

import java.util.ArrayList;
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
*企业查询列表
*@author
*/
@Service
public class CorpListOp extends AbstractOp implements IAresSerivce  {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-企业查询列表-run--");
		Map<String, Object> map1 = new HashMap<>();
		String BANK_TYPE = ctx.getParam("BANK_TYPE");
		map1.put("BANK_TYPE", BANK_TYPE);
		List<Map<String, Object>> BANK_LIST = new ArrayList<>();
		List<Map<String, Object>> list = this.getDao(ctx).queryForList("information.corpList", map1);
		List<Map<String, Object>> list2 = this.getDao(ctx).queryForList("information.corpListID", map1);
		
		for(int i=0;i<list2.size();i++) {
			String BANK_ID1 = (String) list2.get(i).get("BANK_ID");
			String BANK_NAME = (String) list2.get(i).get("BANK_NAME");
			Map<String, Object> map2 = new HashMap<>();
			List<Map<String, Object>> CORP_WECHAT_LIST = new ArrayList<>();
			for(int j=0;j<list.size();j++) {
				String BANK_ID2 = (String) list.get(j).get("BANK_ID");
				if(BANK_ID1!=null&&BANK_ID2!=null&&BANK_ID1.equals(BANK_ID2)) {
					Map<String, Object> map = new HashMap<>();
					String CORP_ID = (String) list.get(j).get("CORP_ID");
					String CORP_WECHAT_NAME = (String) list.get(j).get("CORP_WECHAT_NAME");
					map.put("CORP_ID", CORP_ID);
					map.put("CORP_WECHAT_NAME", CORP_WECHAT_NAME);
					CORP_WECHAT_LIST.add(map);
				}else {
					continue;
				}
			}			
			
			map2.put("BANK_ID", BANK_ID1);
			map2.put("BANK_NAME", BANK_NAME);
			map2.put("CORP_WECHAT_LIST", CORP_WECHAT_LIST);
			BANK_LIST.add(map2);			
		}
		
		ctx.setParam("BANK_LIST", BANK_LIST);
		return NEXT;
	}

}
