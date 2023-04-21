package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 *删除标签及关联
 *@author
 */
@Service
public class RemoveLabelOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional
    public int execute(IBusinessContext ctx) {
        Map<String, Object> map = ctx.getParamMap();
        this.getDao(ctx).delete("newsLabel.removeLabel",map);
        this.getDao(ctx).delete("newsLabel.removeLabelRel",map);
        return NEXT;
    }
}
