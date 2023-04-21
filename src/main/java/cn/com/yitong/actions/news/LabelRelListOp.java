package cn.com.yitong.actions.news;


import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 查询标签关联
 *
 * @author zss
 */
@Service
public class LabelRelListOp extends AbstractOp implements IAresSerivce {

    @Override
    public int execute(IBusinessContext ctx) {
        logger.debug("根据TAG_ID查询标签分类");
        Map<String, Object> map = ctx.getParamMap();
        //查询标签
        Map query = this.getDao(ctx).queryForMap("newsLabel.labelList", map);
        //查询标签相关分类
        List clsList = this.getDao(ctx).queryForList("newsLabel.labelClsList",query);
        //查询标签相关关键词
        List keyList = this.getDao(ctx).queryForList("newsLabel.labelKeysList",query);
        ctx.setParam("GROUP_ID",query.get("GROUP_ID"));
        ctx.setParam("TAG_ID",query.get("TAG_ID"));
        ctx.setParam("TAG_NAME",query.get("TAG_NAME"));
        ctx.setParam("TAG_TYPE",query.get("TAG_TYPE"));
        ctx.setParam("CLSLIST",clsList);
        ctx.setParam("KEYLIST",keyList);
        return NEXT;
    }
}
