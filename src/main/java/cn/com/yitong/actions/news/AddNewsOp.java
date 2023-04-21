package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.DateUtil;
import cn.com.yitong.util.common.SeqGeneUtil;
import cn.com.yitong.util.common.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增资讯
 *
 * @author zss
 */
@Service
public class AddNewsOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int execute(IBusinessContext ctx) {
        logger.debug("-新增资讯-run--");
        //新增资讯
        Map<String, Object> map = setNewsParam(ctx);
        map.put("NEWS_ADD_TYPE","1");
        List<Map<String,Object>> LIST = ctx.getParam("LIST");
        String NEWS_ID = (String) map.get("NEWS_ID");
        this.getDao(ctx).insert("news.addNews", map);
        //新增标签关联
        ctx.setParam("NEWS_ID",NEWS_ID);
        changeNewsLabelRel(LIST,ctx,NEWS_ID);
        return NEXT;
    }

    public Map<String, Object> setNewsParam(IBusinessContext ctx) {
        Map<String, Object> map = new HashMap<>();
        String NEWS_ID = ctx.getParam("NEWS_ID");
        String SESS_USER_NAME = ctx.getSessionObject("SESS_USER_NAME");
        if (StringUtil.isBlank(NEWS_ID)) {
            NEWS_ID = SeqGeneUtil.geneBusiKeySN("NI");
            map.put("CREATE_BY", SESS_USER_NAME);
            map.put("CREATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        }
        map.put("NEWS_ID", NEWS_ID);
        map.put("TITLE", ctx.getParam("TITLE"));
        map.put("NEWS_COMMENT", ctx.getParam("NEWS_COMMENT"));
        map.put("NEWS_SOURCES", ctx.getParam("NEWS_SOURCES"));
        map.put("NEWS_STATUS", ctx.getParam("NEWS_STATUS"));
        map.put("CLS_ID", ctx.getParam("CLS_ID"));
        map.put("NEWS_IMAGE", ctx.getParam("NEWS_IMAGE"));
        map.put("ABSTRACT", ctx.getParam("ABSTRACT"));
        map.put("CONTENT", ctx.getParam("CONTENT"));
        map.put("CORP_ID", ctx.getParam("CORP_ID"));
        map.put("DEL_FLAG", "0");
        map.put("UPDATE_BY", SESS_USER_NAME);
        map.put("UPDATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        //因为前端懒不从数据字典读然后传值,所以我在后端写死了
        map.put("SYN_CHNL_NO","0");
        map.put("SYN_CHNL_NAME","平台添加");
        return map;
    }

    public void changeNewsLabelRel(List<Map<String,Object>> list,IBusinessContext ctx,String NEWS_ID){
        HashMap<String, Object> query = new HashMap<>();
        query.put("NEWS_ID",NEWS_ID);
        this.getDao(ctx).delete("newsLabel.deleteLabelRel",query);
        if (list !=null &&list.size()>0) {
            for (Map<String,Object> param:list) {
                query.put("ID",SeqGeneUtil.geneBusiKeySN("ntr"));
                query.put("CORP_ID",ctx.getParam("CORP_ID"));
                query.put("NEWS_ID",NEWS_ID);
                query.put("TAG_ID",param.get("TAG_ID"));
                query.put("TAG_NAME",param.get("TAG_NAME"));
                query.put("CRT_TMS",DateUtil.todayStr(DateUtil.TIME_FORMATTER));
                query.put("UPD_TMS",DateUtil.todayStr(DateUtil.TIME_FORMATTER));
                this.getDao(ctx).insert("newsLabel.addLabelRel",query);
            }
        }
    }
}
