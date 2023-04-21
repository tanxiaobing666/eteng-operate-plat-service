package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *修改关联资讯
 *@author
 * zss
 */
@Service
public class UpdateNewsChalRelOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int execute(IBusinessContext ctx) {
        logger.debug("-修改关联资讯-run--");
        Map<String, Object> map = new HashMap<>();
        String CLS_ID = ctx.getParam("CLS_ID");
        String CLS_NAME = ctx.getParam("CLS_NAME");
        List<Map<String,Object>> list = ctx.getParam("LIST");
        String SESS_USER_NAME = ctx.getSessionObject("SESS_USER_NAME");
        map.put("CLS_ID",CLS_ID);
        map.put("CLS_NAME",CLS_NAME);
        map.put("UPDATE_TIME",DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        map.put("UPDATE_BY",SESS_USER_NAME);
        //修改内部资讯名称
        this.getDao(ctx).update("newsSort.updateNewsSort",map);

        //删除之前已关联资讯
        List<Map<String,Object>> param = new ArrayList<>();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("CLS_ID",CLS_ID);
        param.add(hashMap);
        Map<String, Object> objMap = new HashMap<>();
        objMap.put("list",param);
        this.getDao(ctx).delete("newsSort.relNewsDel",objMap);
        //新增已选分类
        AddNewsChalRelOp op = new AddNewsChalRelOp();
        Map<String, Object> paramMap = op.setParamMap(ctx);
        paramMap.put("CLS_ID",CLS_ID);
        op.addNewsSort(list,paramMap,ctx);
        return NEXT;
    }
}
