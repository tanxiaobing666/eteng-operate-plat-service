package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 修改资讯上下架
 *
 * @author zss
 */
@Service
public class UpdateNewsStatusOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int execute(IBusinessContext ctx) {
        logger.debug("-修改资讯上下架-run--");
        String SESS_USER_NAME = ctx.getSessionObject("SESS_USER_NAME");
        Map<String, Object> map = ctx.getParamMap();
        map.put("PUT_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        map.put("UPDATE_BY", SESS_USER_NAME);
        map.put("UPDATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("LIST");
        String newsStatus = (String) map.get("ALT_NEWS_STATUS");
        List<Map<String, Object>> paramList = list.stream().filter((Map param) -> (!"0".equals(param.get("NEWS_STATUS").toString()))||!"2".equals(newsStatus)).collect(Collectors.toList());
        map.put("LIST",paramList);
        if(paramList !=null&& paramList.size()>0){
            this.getDao(ctx).update("news.updateNewsStatus",map);
        }
        return NEXT;
    }
}
