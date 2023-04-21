package cn.com.yitong.actions.information;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.hutool.core.util.NumberUtil;

/**
 * 简单任务
 * 
 * @author
 */
@Service
public class TrendChartOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	static final String[] weeks = new String[] {"星期天", "星期一", "星期二", "星期三", "星期四","星期五", "星期六" };

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-资讯趋势图-run--");
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> trendMap = new HashMap<>();
		String CORP_ID = ctx.getParam("CORP_ID");
		String BANK_ID = ctx.getParam("BANK_ID");
		String START_TIME = ctx.getParam("START_TIME");
		String END_TIME = ctx.getParam("END_TIME");
		
		
		SimpleDateFormat fromat=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
		Date START_TIME1= null;
		Date END_TIME1= null;
		//默认是近一周
		if(END_TIME==null ||START_TIME==null) {
			Calendar calendar= Calendar.getInstance();
			END_TIME = fromat.format(calendar.getTime());
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
			START_TIME = fromat.format(calendar.getTime());	       
		}
		
		try {
			START_TIME1 = fromat.parse(START_TIME);
			END_TIME1 = fromat.parse(END_TIME);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		map.put("CORP_ID", CORP_ID);
		map.put("BANK_ID", BANK_ID);
		map.put("START_TIME", START_TIME);
		map.put("END_TIME", END_TIME);
//		long TIME = (END_TIME1.getTime() - END_TIME1.getTime()) / (1000 * 60 * 60 * 24);
//		String INTERVAL_TIME =  String.valueOf(TIME);;
//		Map<String, Object> map1 = new HashMap<>();
//		map1.put("CORP_ID", CORP_ID);
//		map1.put("BANK_ID", BANK_ID);
//		map1.put("START_TIME", START_TIME);
//		map1.put("INTERVAL_TIME", INTERVAL_TIME);
		
			
		List<Map<String, Object>> list1 = this.getDao(ctx).queryForList("information.trendChartOne", map);
//		List<Map<String, Object>> list2 = this.getDao(ctx).queryForList("trendChart.trendChartTwo", map1);
				
		Map<String, Object> map3 = new HashMap<>();
		map3.put("CORP_ID", CORP_ID);
		map3.put("BANK_ID", BANK_ID);
		
		List<Map<String, Object>> list3 = this.getDao(ctx).queryForList("information.trendChartThree", map3);
		List<Map<String, Object>> list4 = this.getDao(ctx).queryForList("information.trendChartFour", map3);
		
		Map<String, Object> percent1 = this.percent(list1);
//		Map<String, Object> percent2 = this.percent(list2);
		Map<String, Object> percent3 = this.percent(list3);
		Map<String, Object> percent4 = this.percent(list4);
		
		List<Map<String,Object>> listOne = (List<Map<String, Object>>) percent1.get("list");
//		List<Map<String,Object>> listTwo = (List<Map<String, Object>>) percent2.get("list");
		
		BigDecimal BROWSE_SUM =  (BigDecimal) percent3.get("BROWSE_SUM");
		BigDecimal VISITOR_COUNT = (BigDecimal) percent3.get("VISITOR_COUNT");
		
		BigDecimal BROWSE_SUM_Two = (BigDecimal) percent4.get("BROWSE_SUM");
		BigDecimal VISITOR_COUNT_Two = (BigDecimal) percent4.get("VISITOR_COUNT");
		
		
				
//		同比增长计算公式:
//	    同比增长率=(本期数-同期数)÷同期数×100%
		Double SUM_PERCENT = 0.0;
		Double VISITOR_PERCENT = 0.0;
		if (BROWSE_SUM_Two != null &&  !(BROWSE_SUM_Two.equals(BigDecimal.ZERO)) ) {
			BigDecimal sub = NumberUtil.sub(BROWSE_SUM, BROWSE_SUM_Two);
			BigDecimal PERCENT = NumberUtil.div(sub, BROWSE_SUM_Two);
			SUM_PERCENT = NumberUtil.round(PERCENT, 4).doubleValue();
		} else {
			SUM_PERCENT = 1.0;
		}

		if (VISITOR_COUNT_Two != null &&  !(VISITOR_COUNT_Two.equals(BigDecimal.ZERO)) ) {
			BigDecimal sub = NumberUtil.sub(VISITOR_COUNT, VISITOR_COUNT_Two);
			BigDecimal PERCENT = NumberUtil.div(sub, VISITOR_COUNT_Two);
			VISITOR_PERCENT = NumberUtil.round(PERCENT, 4).doubleValue();
		} else {
			VISITOR_PERCENT = 1.0;
		}

		trendMap.put("BROWSE_SUM", BROWSE_SUM);
		trendMap.put("VISITOR_COUNT", VISITOR_COUNT);
		trendMap.put("SUM_PERCENT", SUM_PERCENT);
		trendMap.put("VISITOR_PERCENT", VISITOR_PERCENT);

		ctx.setParam("TREND_LIST", listOne);
		trendMap.put("TREND_LIST", listOne);
		ctx.getParamMap().putAll(trendMap);

		return NEXT;
	}

	public Map<String,Object> percent(List<Map<String,Object>> list1){
		Map<String, Object> map = new HashMap<>();
		BigDecimal BROWSE_SUM = new BigDecimal(0);
		BigDecimal VISITOR_COUNT = new BigDecimal(0);
		if (list1 != null) {
			for (int i = 0; i < list1.size(); i++) {
				Long param = (Long) list1.get(i).get("BROWSE_NUM");
				Long param1 = (Long) list1.get(i).get("BROWSE_VISITOR");
				Double BROWSE_NUM = param.doubleValue();
				Double BROWSE_VISITOR = param1.doubleValue();
				BigDecimal b1 = new BigDecimal(Double.toString(BROWSE_NUM));
				BigDecimal b2 = new BigDecimal(Double.toString(BROWSE_VISITOR));
				BROWSE_SUM = NumberUtil.add(b1, BROWSE_SUM);
				VISITOR_COUNT = NumberUtil.add(b2, VISITOR_COUNT);

				
				String BROWSE_TIME = (String) list1.get(i).get("BROWSE_TIME");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			    Date time = null;
			    try {
			        time = format.parse(BROWSE_TIME);
			    } catch (ParseException e) {
			        e.printStackTrace();
			    }			    		
				Calendar cale = Calendar.getInstance();	
				cale.setTime(time);
				cale.setFirstDayOfWeek(Calendar.SUNDAY);//将每周第一天设为星期天，默认是星期天
				int week = cale.get(Calendar.DAY_OF_WEEK)-1;// 获取指定日期的周几
				String WEEK_NAME = weeks[(week)];
				list1.get(i).put("WEEK_NAME", WEEK_NAME);

			}
		}
		map.put("BROWSE_SUM", BROWSE_SUM);
		map.put("VISITOR_COUNT", VISITOR_COUNT);
		map.put("list", list1);
		return map;
	}
	
	
}
