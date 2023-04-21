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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增关联资讯
 *
 * @author zss
 */
@Service
public class AddNewsChalRelOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int execute(IBusinessContext ctx) {
        logger.debug("-新增关联资讯-run--");
        Map<String, Object> map = setParamMap(ctx);
        //查询分类是否已存在
        Map queryForMap = this.getDao(ctx).queryForMap("newsSort.getNewsClsInfo", map);
        if (queryForMap != null) {
            String DEL_FLAG = (String) queryForMap.get("DEL_FLAG");
            if ("0".equals(DEL_FLAG)) {
                throw new AresRuntimeException("资讯分类已存在");
            } else {
                this.getDao(ctx).update("newsSort.updateNciStatus", queryForMap);
            }
        } else {
            //新增内部资讯分类信息
            this.getDao(ctx).insert("newsSort.addNewsClsInfo", map);
        }
        List<Map<String, Object>> list = ctx.getParam("LIST");
        addNewsSort(list, map, ctx);
//        //渠道分类状态改为已关联
//        this.getDao(ctx).update("newsSort.updateStatusList",map);
        return NEXT;
    }

    public void addNewsSort(List<Map<String, Object>> list,
                            Map<String, Object> map,
                            IBusinessContext ctx) {
        if (list.size() > 0) {
            for (Map<String, Object> paramMap : list) {
                paramMap.put("ID", SeqGeneUtil.geneBusiKeySN("nccr"));
                paramMap.put("TARGET_CLS_ID", map.get("CLS_ID"));
                String SESS_USER_NAME = ctx.getSessionObject("SESS_USER_NAME");
                paramMap.put("CREATE_BY", SESS_USER_NAME);
                paramMap.put("CREATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
                String sourceClsId = (String) paramMap.get("SOURCE_CLS_ID");
                //判断渠道资讯分类是否和内部资讯绑定
                if (StringUtil.isNotEmpty(sourceClsId)) {
                    Map forMap = this.getDao(ctx).queryForMap("newsSort.newsChnlList", paramMap);
                    if (forMap != null) {
                        throw new AresRuntimeException("渠道资讯已绑定");
                    }

                }
            }
            map.put("list", list);
            //新增渠道资讯分类和内部分类的关联
            this.getDao(ctx).insert("newsSort.addNewsRelList", map);
        }
    }

    public Map<String, Object> setParamMap(IBusinessContext ctx) {
        Map<String, Object> map = new HashMap<>();
        String SESS_USER_NAME = ctx.getSessionObject("SESS_USER_NAME");
        map.put("CLS_NAME", ctx.getParam("CLS_NAME"));
        map.put("IS_SHOW", ctx.getParam("IS_SHOW"));
        map.put("CLS_ID", SeqGeneUtil.geneBusiKeySN("nci"));
        map.put("CORP_ID", ctx.getParam("CORP_ID"));
        map.put("DEL_FLAG", "0");
        map.put("CREATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        map.put("CREATE_BY", SESS_USER_NAME);
        map.put("UPDATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        map.put("UPDATE_BY", SESS_USER_NAME);
        return map;
    }
}
