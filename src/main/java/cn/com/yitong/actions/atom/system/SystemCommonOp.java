package cn.com.yitong.actions.atom.system;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.dao.IbatisDao;
import cn.hutool.core.bean.BeanUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaozhi
 * @title: SystemCommonOp
 * @projectName S_ENT_SERVICE
 * @description: 系统公共服务抽取
 * @date 2021/9/18 16:29
 */
@Service
public class SystemCommonOp  {
    @Autowired
    private IbatisDao ibatisDao;

    /**
     * @description: 菜单层级校验,不超过三级
     * @param: ctx
     * @return: java.lang.Boolean
     * @author xiaozhi
     * @date: 2021/9/18 16:36
     */
    public Boolean checkMenuLevel (IBusinessContext ctx){
        Map<String,Object> resultMap = new HashMap<>();
        Integer count=0;
        HashMap<String, Object> whereMap = new HashMap<>();
        whereMap.put("PARENT_ID",ctx.getParam("PARENT_ID"));
        whereMap.put("CORP_ID",ctx.getParam("CORP_ID"));
        while (count<3){
            resultMap = ibatisDao.queryForMap("menu.findMenuByParentId", whereMap);
            if (BeanUtil.isEmpty(resultMap)|| BeanUtil.isEmpty(resultMap.get("PARENT_ID"))){
                break;
            }
            whereMap.put("PARENT_ID",resultMap.get("PARENT_ID"));
            ++count;
        }
        if (count<2){
            return true;
        }else {
            return false;
        }
    }
}
