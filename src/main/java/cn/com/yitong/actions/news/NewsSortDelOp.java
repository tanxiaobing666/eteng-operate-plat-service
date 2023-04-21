package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 删除资讯分类
 *
 * @author zss
 */
@Service
public class NewsSortDelOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int execute(IBusinessContext ctx) {
        logger.debug("-删除资讯分类-run--");
        List<Map<String, Object>> list = ctx.getParam("LIST");
        List<Map<String, Object>> paramList = new LinkedList<>();
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                String ALL_NEWS_COUNT = (String) map.get("ALL_NEWS_COUNT");
                int count = Integer.parseInt(ALL_NEWS_COUNT);
                if (count == 0) {
                    paramList.add(map);
                }
            }
        }

//        Integer relSortCount = ctx.getParam("RELATION_SORT_COUNT");
        if (paramList.size() > 0) {
            //1.删除资讯分类
            this.getDao(ctx).batch4Update("newsSort.newsSortDel", paramList);
//            //2.相关资讯改成下架
//            this.getDao(ctx).batch4Update("newsSort.updateStatus",list);
            //判断是否存在关联数
        /*if(relSortCount>0){
            //3.资讯来源渠道取消关联内部分类
            Map<String, Object> hashMap = new HashMap<>(2);
            hashMap.put("TARGET_CLS_ID",CLS_ID);
            Map<String, Object> map = this.getDao(ctx).queryForObject("newsSort.newsChnlList", hashMap);
            if(!map.isEmpty()){
                this.getDao(ctx).update("newsSort.updateChnlCls",map);
            }

        }*/
            //3.删除关联表
            this.getDao(ctx).batch4Update("newsSort.relNewsDel",paramList);
        }
        return NEXT;
    }
}
