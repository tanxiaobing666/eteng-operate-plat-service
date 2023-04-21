package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsListOp extends AbstractOp implements IAresSerivce {

    @Override
    public int execute(IBusinessContext ctx) {
        logger.debug("-资讯列表-run--");
        Map<String, Object> map = ctx.getParamMap();
        List<Map<String,Object>> list = this.getDao(ctx).pageQuery("news.newsList",map,ctx);
        List collect = list.stream().map(vo -> {
            List query = this.getDao(ctx).queryForList("newsLabel.labelListRel", vo);
            if(query !=null&& query.size()>0){
                vo.put("TAGLIST",query);
            }
            return vo;
        }).collect(Collectors.toList());
        ctx.setParam("LIST",collect);
        return NEXT;
    }
}
