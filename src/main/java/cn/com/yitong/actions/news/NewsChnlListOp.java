package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.StringUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询渠道列表
 *
 * @author zss
 */
@Service
public class NewsChnlListOp extends AbstractOp implements IAresSerivce {

    @Override
    public int execute(IBusinessContext ctx) {
        logger.debug("-查询渠道列表-run--");
        String CLS_ID = ctx.getParam("CLS_ID");
        setParam(ctx, CLS_ID);
        return NEXT;
    }

    public List<Map<String, Object>> setParam(IBusinessContext ctx, String CLS_ID) {
        Map<String, Object> hashMap = new HashMap<>();
        if (StringUtil.isNotEmpty(CLS_ID)) {
            //查询内部分类名称
            hashMap.put("CLS_ID", CLS_ID);
            Map queryForMap = this.getDao(ctx).queryForMap("newsSort.newsClsInfo", hashMap);
            if (queryForMap != null) {
                ctx.setParam("NEWS_CLS_ID", CLS_ID);
                ctx.setParam("NEWS_CLS_NAME", queryForMap.get("CLS_NAME"));
            }
            //根据分类ID查询关联渠道分类、渠道列表
            queryForMap.put("TARGET_CLS_ID", CLS_ID);
            List list = this.getDao(ctx).queryForList("newsSort.newsChnlList", queryForMap);
            hashMap.put("list", list);
        }

        //查询渠道下的渠道分类列表
        List<Map<String, Object>> list = this.getDao(ctx).queryForList("newsSort.newsChnlSortList", hashMap);
        for (Map<String, Object> map : list) {
            hashMap.put("CHNL_NO", map.get("CHNL_NO"));
            List<Map<String, Object>> query = this.getDao(ctx).queryForList("newsSort.chnlList", hashMap);
            map.put("CHNLLIST", query);
        }
        ctx.setParam("LIST", list);
        return list;
    }
}
