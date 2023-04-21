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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增/修改资讯标签
 *
 * @AUTHOR ZSS
 */
@Service
public class ChangeNewsLabelOp extends AbstractOp implements IAresSerivce {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int execute(IBusinessContext ctx) {
        logger.debug("-新增/修改资讯标签-run--");
        Map<String, Object> map = setMapParam(ctx);
        String GROUP_ID = (String) map.get("GROUP_ID");
        List<Map<String, Object>> list = ctx.getParam("LIST");
        if (StringUtil.isNotEmpty(GROUP_ID)) {
            //标签组修改
            updateLabel(list, ctx, map);
        } else {
            //标签分类、标签新增
            addLabel(ctx, map, list);
        }
        return NEXT;
    }

    public Map<String, Object> setMapParam(IBusinessContext ctx) {
        Map<String, Object> map = new HashMap<>();
        map.put("GROUP_NAME", ctx.getParam("GROUP_NAME"));
        map.put("IS_MUTEX", ctx.getParam("IS_MUTEX"));
        map.put("SORT", ctx.getParam("SORT"));
        map.put("CORP_ID", ctx.getParam("CORP_ID"));
        map.put("GROUP_ID", ctx.getParam("GROUP_ID"));
        String SESS_USER_NAME = ctx.getSessionObject("SESS_USER_NAME");
        map.put("SESS_USER_NAME", SESS_USER_NAME);
        map.put("LAST_MODI_BY", SESS_USER_NAME);
        map.put("LAST_MODI_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        map.put("CREATE_BY", SESS_USER_NAME);
        map.put("CREATE_TIME", DateUtil.todayStr(DateUtil.TIME_FORMATTER));
        return map;
    }

    public void addLabel(IBusinessContext ctx,
                         Map<String, Object> map,
                         List<Map<String, Object>> list) {
        //根据组名称查询资讯分类是否已存在
        Map<String, Object> selectMap = new HashMap<>(2);
        String GROUP_NAME = (String) map.get("GROUP_NAME");
        selectMap.put("GROUP_NAME", GROUP_NAME.trim());
        Map<String, Object> queryForMap = this.getDao(ctx).queryForMap("newsLabel.labelSortList", selectMap);
        //是否属于同一分类
        if (queryForMap == null) {
            //不属于同一资讯分类，新增
            String GROUP_ID = SeqGeneUtil.geneBusiKeySN("ntg");
            map.put("GROUP_ID", GROUP_ID);
            this.getDao(ctx).insert("newsLabel.addLabelSort", map);
        } else {
            map.put("GROUP_ID", queryForMap.get("GROUP_ID"));
        }
        //根据传入标签值判断是否新增标签
        List<Map<String, Object>> addList = new ArrayList<>();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map1 = list.get(i);
                map1.put("GROUP_ID", map.get("GROUP_ID"));
                //判断资讯标签是否存在
                Map queryMap = this.getDao(ctx).queryForMap("newsLabel.labelIsExist", map1);
                if (queryMap != null) {
                    throw new AresRuntimeException("资讯标签[" + queryMap.get("TAG_NAME") + "]已经存在");
                } else {
                    map1.put("TAG_ID", SeqGeneUtil.geneBusiKeySN("nt"));
                    addList.add(map1);
                }
            }
            if (addList.size() > 0) {
                map.put("LIST", addList);
                //批量新增标签
                this.getDao(ctx).insert("newsLabel.addLabel", map);
            }
        }
    }

    public void updateLabel(List<Map<String, Object>> list,
                            IBusinessContext ctx,
                            Map<String, Object> map) {
        List<Map<String, Object>> addList = new ArrayList<>();
        if (list.size() > 0) {
            for (Map<String, Object> paramMap : list) {
                paramMap.put("CREATE_BY", map.get("CREATE_BY"));
                paramMap.put("CREATE_TIME", map.get("CREATE_TIME"));
                paramMap.put("LAST_MODI_BY", map.get("LAST_MODI_BY"));
                paramMap.put("LAST_MODI_TIME", map.get("LAST_MODI_TIME"));
                paramMap.put("TAG_ID", SeqGeneUtil.geneBusiKeySN("nt"));
                addList.add(paramMap);
            }
            //批量删除标签
            map.put("LIST", addList);
            Map<String, Object> param = new HashMap<>();
            param.put("GROUP_ID", map.get("GROUP_ID"));
            this.getDao(ctx).delete("newsLabel.removeLabelByGp", param);
            //批量新增标签
            addLabel(ctx, map, list);
//            this.getDao(ctx).insert("newsLabel.addLabel", map);
        }else{
            Map<String, Object> param = new HashMap<>();
            param.put("GROUP_ID", map.get("GROUP_ID"));
            this.getDao(ctx).delete("newsLabel.removeLabelByGp", param);
        }
        //修改标签组名称
        this.getDao(ctx).update("newsLabel.updateGp",map);
    }
}
