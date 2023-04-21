package cn.com.yitong.actions.information;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.hutool.core.util.NumberUtil;

/**
*资讯统计
*@author
*/
@Service
public class MessageCountOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	static final String[] weeks = new String[] {"星期天", "星期一", "星期二", "星期三", "星期四","星期五", "星期六" };

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-资讯趋势图-run--");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map1 = new HashMap<>();
		String CORP_ID = ctx.getParam("CORP_ID");
		String BANK_ID = ctx.getParam("BANK_ID");
		String START_TIME = ctx.getParam("START_TIME");
		String END_TIME = ctx.getParam("END_TIME");
		map.put("CORP_ID", CORP_ID);
		map.put("BANK_ID", BANK_ID);
		map.put("START_TIME", START_TIME);
		map.put("END_TIME", END_TIME);
		
		map1.put("CORP_ID", CORP_ID);
		map1.put("BANK_ID", BANK_ID);
		
		
		Map<String,Object> browseCount = this.getDao(ctx).queryForMap("information.countBrowse", map1);
		Map<String,Object> NickNameCount = this.getDao(ctx).queryForMap("information.countNickName", map1);
		Map<String,Object> shareCount = this.getDao(ctx).queryForMap("information.countShare", map1);
		

//		浏览次数
		Long BROWSE_SUM = 0L;
//		浏览人数
//		Long BROWSE_COUNT = 0L;
//		授权昵称数
		Long AUTH_SUM = 0L;
//		授权手机号
		Long AUTH_COUNT = 0L;
//		分享次数
		Long SHARE_SUM = 0L;
//		分享人数
//		Long SHARE_COUNT = 0L;
		BROWSE_SUM = (Long) browseCount.get("BROWSE_SUM");
		SHARE_SUM = (Long) shareCount.get("SHARE_SUM");
		AUTH_SUM = (Long) NickNameCount.get("AUTH_SUM");
		AUTH_COUNT = (Long) NickNameCount.get("AUTH_COUNT");
		
		List<Map<String,Object>> browseList = this.getDao(ctx).queryForList("information.messBrowse", map);
		List<Map<String,Object>> NickNameList = this.getDao(ctx).queryForList("information.messNickName", map);
		List<Map<String,Object>> shareList = this.getDao(ctx).queryForList("information.messShare", map);
//      声明list用来接收日期		
		List<Map<String, Object>> list = new ArrayList<>();
//		声明list1用来接收整理好的数据
		List<Map<String, Object>> list1 = new ArrayList<>();
		list = getDays(START_TIME, END_TIME);
		
		
		for(int i=0;i<list.size();i++) {
			String COUNT_TIME = (String) list.get(i).get("COUNT_TIME");
			Map<String,Object> map2 =  new HashMap<>();
			Long BROWSE_NUM =0L;
			Long BROWSE_VISITOR =0L;
			Long AUTH_NICKNAME_NUM =0L;
			Long AUTH_PHONE_NUM =0L;
			Long SHARE_NUM =0L;
			Long SHARE_VISITOR =0L;
			
			boolean flag = false;
			map2.put("COUNT_TIME", COUNT_TIME);
			for(int j=0;j<browseList.size();j++) {
				
				String BROWSE_TIME = (String) browseList.get(j).get("BROWSE_TIME");
				if(COUNT_TIME.equals(BROWSE_TIME)) {
					BROWSE_NUM = (Long) browseList.get(j).get("BROWSE_NUM");
					BROWSE_VISITOR = (Long) browseList.get(j).get("BROWSE_VISITOR");			
					flag = true;
				}
				if(flag==true) {
					flag = false;
					break;
				}
			}
			
			for(int k=0;k<NickNameList.size();k++) {

				String SHARE_TIME = (String) NickNameList.get(k).get("CREATE_TIME");
				if(COUNT_TIME.equals(SHARE_TIME)) {
					AUTH_NICKNAME_NUM = (Long) NickNameList.get(k).get("AUTH_NICKNAME_NUM");
					AUTH_PHONE_NUM = (Long) NickNameList.get(k).get("AUTH_PHONE_NUM");								
					flag = true;
				}
				if(flag==true) {
					flag = false;
					break;
				}
			}
			
			for(int k=0;k<shareList.size();k++) {

				String SHARE_TIME = (String) shareList.get(k).get("SHARE_TIME");
				if(COUNT_TIME.equals(SHARE_TIME)) {
					SHARE_NUM = (Long) shareList.get(k).get("SHARE_NUM");
					SHARE_VISITOR = (Long) shareList.get(k).get("SHARE_VISITOR");								
					flag = true;
				}
				if(flag==true) {
					flag = false;
					break;
				}
			}
			
			
			
			
			
			
			
			map2.put("COUNT_TIME", COUNT_TIME);
			map2.put("BROWSE_NUM", BROWSE_NUM);
			map2.put("SHARE_NUM", SHARE_NUM);
			map2.put("AUTH_NICKNAME_NUM", AUTH_NICKNAME_NUM);
			map2.put("AUTH_PHONE_NUM", AUTH_PHONE_NUM);
			list1.add(i, map2);
		}

		
		ctx.setParam("BROWSE_SUM", BROWSE_SUM);
		ctx.setParam("SHARE_SUM", SHARE_SUM);
		ctx.setParam("AUTH_SUM", AUTH_SUM);
		ctx.setParam("AUTH_COUNT", AUTH_COUNT);
		ctx.setParam("MESSAGE_LIST", list1);
			
	

		return NEXT;
	}
	
	public static List<Map<String, Object>> getDays(String startTime, String endTime) {
        // 返回的日期集合
        List<Map<String, Object>> days = new ArrayList<>();
      

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
            	Map<String, Object> dayMap = new HashMap<>();
            	dayMap.put("COUNT_TIME", dateFormat.format(tempStart.getTime()));
                days.add(dayMap);
                tempStart.add(Calendar.MONTH, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

}
