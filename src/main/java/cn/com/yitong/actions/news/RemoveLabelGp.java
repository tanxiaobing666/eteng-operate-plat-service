package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RemoveLabelGp extends AbstractOp implements IAresSerivce {

    @Override
    public int execute(IBusinessContext ctx) {
        Map<String, Object> map = ctx.getParamMap();
        List query = this.getDao(ctx).queryForList("newsLabel.labelList", map);
        if(query ==null ||query.size()==0){
            this.getDao(ctx).delete("newsLabel.removeLabelGp",map);
        }else{
            throw new AresRuntimeException("已有标签关联");
        }
        return NEXT;
    }
}
