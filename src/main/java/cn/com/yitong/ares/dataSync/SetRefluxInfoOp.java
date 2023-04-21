package cn.com.yitong.ares.dataSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;

/**
*潜客数据和用户行为数据整合
*@author
*/
@Service
public class SetRefluxInfoOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-潜客数据和用户行为数据整合-run--");
		
		List<Map<String, Object>> custList = ctx.getParam("CUST_LIST");
		List<Map<String, Object>> visitList = ctx.getParam("VISIT_LIST");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CUST_LIST", custList);
		map.put("VISIT_LIST", visitList);
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map);
		
//		List<ArrayList<Map<String, Object>>> list = new ArrayList<ArrayList<Map<String,Object>>>();
//		list.add((ArrayList<Map<String, Object>>) custList);
//		list.add((ArrayList<Map<String, Object>>) visitList);
		
		ctx.setParam("LIST", list);
		return NEXT;
	}

}
