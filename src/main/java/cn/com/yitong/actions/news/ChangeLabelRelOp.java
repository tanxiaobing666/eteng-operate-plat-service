package cn.com.yitong.actions.news;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.util.common.DateUtil;
import cn.com.yitong.util.common.SeqGeneUtil;
import cn.com.yitong.util.common.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 新增/修改标签关联
 *
 * @author zss
 */
@Service
public class ChangeLabelRelOp extends AbstractOp implements IAresSerivce {

    @Transactional
    @Override
    public int execute(IBusinessContext ctx) {
        logger.debug("-新增/修改标签关联-run--");
        Map<String, Object> map = ctx.getParamMap();
        Map<String, Object> param = setParam(map, ctx);
        String TAG_ID = (String) map.get("TAG_ID");
        //判断标签名称是否存在
        Map queryMap = this.getDao(ctx).queryForMap("newsLabel.labelIsExist",map);
        if(queryMap !=null)
            throw new AresRuntimeException("标签名称已经存在!");
        if(StringUtil.isNotEmpty(TAG_ID)){
            this.getDao(ctx).update("newsLabel.updateNewsLabel",param);
        }else{
            TAG_ID=SeqGeneUtil.geneBusiKeySN("nt");
            param.put("TAG_ID",TAG_ID);
            this.getDao(ctx).insert("newsLabel.addNewsLabel",param);
        }
        //新增/修改资讯分类
        addNewsSort(map,ctx);
        //新增修改关键词
        addKeys(map,ctx);
        return NEXT;
    }
    private Map<String,Object> setParam(Map<String, Object> map,IBusinessContext ctx){
        String TAG_ID = (String) map.get("TAG_ID");
        String SESS_USER_NAME = ctx.getSessionObject("SESS_USER_NAME");
        if (StringUtil.isBlank(TAG_ID)) {
            map.put("CREATE_BY", SESS_USER_NAME);
            map.put("CREATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        }
        map.put("LAST_MODI_BY", SESS_USER_NAME);
        map.put("LAST_MODI_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        return map;
    }
    private void addNewsSort(Map<String, Object> map,IBusinessContext ctx) {
        String TAG_ID = (String) map.get("TAG_ID");
        map.put("TAG_ID",TAG_ID);
        List<Map<String,Object>> CLSLIST = (List<Map<String, Object>>) map.get("CLSLIST");
        if(CLSLIST !=null&&CLSLIST.size()>0){
            this.getDao(ctx).delete("newsLabel.removeNewsSort",map);
            map.put("list",CLSLIST);
            this.getDao(ctx).insert("newsLabel.addNewsSort",map);
        }
    }
    private void addKeys(Map<String, Object> map,IBusinessContext ctx) {
        String TAG_ID = (String) map.get("TAG_ID");
        map.put("TAG_ID",TAG_ID);
        List<Map<String,Object>> KEYLIST = (List<Map<String, Object>>) map.get("KEYLIST");
        this.getDao(ctx).delete("newsLabel.removeKeys",map);
        if(KEYLIST !=null&&KEYLIST.size()>0){
            map.put("list",KEYLIST);
            this.getDao(ctx).insert("newsLabel.addKeys",map);
        }
    }
}

