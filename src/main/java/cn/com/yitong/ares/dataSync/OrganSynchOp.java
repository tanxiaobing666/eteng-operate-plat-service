package cn.com.yitong.ares.dataSync;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;

/**
*社群通-机构数据同步
*@author
*/
@Service
public class OrganSynchOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-社群通-机构数据同步-run--");
		
		//获取社群通传递过来的机构信息，分批处理-并判断数据是否为空
		List<Map<String, Object>> organList = ctx.getParam("LIST");
		if(organList != null && organList.size() != 0) {
			
		}
		return NEXT;
	}

}
