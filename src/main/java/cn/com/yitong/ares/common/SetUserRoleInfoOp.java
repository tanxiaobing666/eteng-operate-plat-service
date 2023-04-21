package cn.com.yitong.ares.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.consts.SessConsts;
import cn.com.yitong.ares.flow.IAresSerivce;

/**
*处理用户多角色信息
*@author
*/
@Service
public class SetUserRoleInfoOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-处理用户多角色信息-run--");
		
		
		String userId = ctx.getSessionObject(SessConsts.SESS_USER_ID);
		String corpId = ctx.getSessionObject("CORP_ID");
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("LOGIN_ID", userId);
		userMap.put("CORP_ID", corpId);
		
		//通过当前登录用户对应的角色信息
		String roleIdInfo = this.getDao(ctx).queryForStr("login.queryUserRoleInfo", userMap);
		List<String> list = Arrays.asList(roleIdInfo.split(","));
		List<Map<String, Object>> roleMenuList = new ArrayList<>();//菜单列表 
		String menuIds = "";//菜单编号列表字符串
		//判断切割完字符串是否为空
		if(list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++) {
				Map<String, Object> roleMap = new HashMap<String, Object>();
				String roleString = list.get(i);
				roleMap.put("ROLE_ID", roleString);
				roleMap.put("CORP_ID", corpId);
				roleMap.put("LOGIN_ID", userId);
				List<Map<String, Object>> menuInRoleList = this.getDao(ctx).queryForList("login.selRoleMenuInfo", roleMap);
				for(Map<String, Object> menuInRoleMap : menuInRoleList) {
					if(menuIds.indexOf("," + menuInRoleMap.get("MENU_ID").toString() + ",") < 0) {
						menuIds += "," + menuInRoleMap.get("MENU_ID").toString() + ",";
						roleMenuList.add(menuInRoleMap);
					}
				}
			}
		}
		
		ctx.setParam("LIST", roleMenuList);
		
		return NEXT;
	}

}
