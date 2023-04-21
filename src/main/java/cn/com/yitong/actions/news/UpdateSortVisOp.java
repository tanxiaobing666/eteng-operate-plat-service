package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 修改资讯分类是否可见
 *
 * @AUTHOR zss
 */
@Service
public class UpdateSortVisOp extends AbstractOp implements IAresSerivce {

    @Transactional
    @Override
    public int execute(IBusinessContext ctx) {
        Map<String, Object> map = ctx.getParamMap();
        map.put("UPDATE_BY",ctx.getSessionObject("SESS_USER_NAME"));
        map.put("UPDATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        this.getDao(ctx).update("newsSort.updateSortVis",map);
        return NEXT;
    }
}
