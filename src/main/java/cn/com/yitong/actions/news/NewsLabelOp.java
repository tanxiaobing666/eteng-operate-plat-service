package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资讯分类标签、资讯标签列表分页查询
 *
 * @author zss
 */
@Service
public class NewsLabelOp extends AbstractOp implements IAresSerivce {

    @Override
    public int execute(IBusinessContext ctx) {
        logger.debug("-资讯分类标签、资讯标签列表分页查询-run--");
        Map paramMap = new HashMap<String, Object>(3);
        paramMap.putAll(ctx.getParamMap());
        paramMap.putAll(ctx.getHeadMap());
        List<Map<String, Object>> sortList = this.getDao(ctx).queryForList("newsLabel.labelSortList", paramMap);
        for (int i = 0; i < sortList.size(); i++) {
            Map<String, Object> map1 = sortList.get(i);
            paramMap.put("GROUP_ID",map1.get("GROUP_ID"));
            List<Map<String, Object>> list = this.getDao(ctx).queryForList("newsLabel.labelList", paramMap);
            map1.put("LABELLIST", list);
        }
        ctx.setParam("LIST", sortList);
        return NEXT;
    }
}
