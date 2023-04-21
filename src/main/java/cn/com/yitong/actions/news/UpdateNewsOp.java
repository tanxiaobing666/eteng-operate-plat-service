package cn.com.yitong.actions.news;


import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 修改资讯
 *
 * @author zss
 */
@Service
public class UpdateNewsOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int execute(IBusinessContext ctx) {
        logger.debug("-修改资讯-run--");
        AddNewsOp addNewsOp = new AddNewsOp();
        Map<String, Object> map = addNewsOp.setNewsParam(ctx);
        String NEWS_ID = (String) map.get("NEWS_ID");
        this.getDao(ctx).update("news.updateNews", map);
        List<Map<String,Object>> LIST = ctx.getParam("LIST");
        addNewsOp.changeNewsLabelRel(LIST,ctx,NEWS_ID);
        return NEXT;
    }
}
