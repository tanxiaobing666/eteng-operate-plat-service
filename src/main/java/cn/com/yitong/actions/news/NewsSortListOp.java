package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsSortListOp extends AbstractOp implements IAresSerivce {

    @Override
    public int execute(IBusinessContext ctx) {
        logger.debug("-资讯分类列表-run--");
        List<Map<String,Object>> list = this.getDao(ctx).queryForList("newsSort.sortList", null);
        List<Map<String, Object>> mapList = list.stream().map(param -> {
            NewsChnlListOp op = new NewsChnlListOp();
            String CLS_ID = param.get("CLS_ID").toString();
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("TARGET_CLS_ID",CLS_ID);
            List queryForList = this.getDao(ctx).queryForList("newsSort.newsChnlList", map);
            if(queryForList.size()>0){
                List<Map<String, Object>> paramList = op.setParam(ctx, param.get("CLS_ID").toString());
                param.put("sortList", paramList);
            }
            return param;
        }).collect(Collectors.toList());
        ctx.setParam("LIST",mapList);
        return NEXT;
    }
}
