package cn.com.yitong.ares.corp;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.base.INetTools;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.util.UUIDUtil;
import cn.com.yitong.util.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 新增企业号
 *
 * @author
 */
@Service
public class CorpWeChatAddOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger ( getClass ( ) );

	@Autowired
	@Qualifier ( "netTools4SC" )
	private INetTools netTools;

	@Transactional
	@Override
	public int execute ( IBusinessContext ctx ) {
		// TODO Auto-generated method stub
		logger.debug ( "-新增企业号-run--" );

		//先根据企业号名称和所属企业编号判断该企业号是否已经注册
		Map map = this.getDao ( ctx ).queryForMap ( "corpWechat.queryWechatCorpId" , ctx.getParamMap ( ) );
		if ( map != null ) {
			//说明该企业下已经注册过这个企业微信号，不可以在注册
			throw new AresRuntimeException ( "corp.corpWechatAdd.exist" );
		}
		//生成新增企业号主键
		ctx.setParam ( "CORP_WECHAT_ID" , UUIDUtil.getUUID ( ) );
		this.getDao ( ctx ).insert ( "corpWechat.corpWechatAdd" , ctx.getParamMap ( ) );

		//插入新增企业号后，调用eteng-ent-service服务初始化管理用户
		//传参CORP_ID,返回状态码STATUS
		//报文路径
		String transCode = "system/sysUserInit";
		//访问的服务名
		ctx.setParam ( "backServiceName" , "eteng-ent-service" );
		//访问的接口
		ctx.setParam ( "backApiPath" , "/api/system/sysUserInit" );
		netTools.execute ( ctx , transCode );
		String status = ctx.getParam ( "STATUS" );
		//如果状态码为1，则初始化成功，否则初始化失败
		if ( ! status.equals ( "1" ) ) {
			throw new AresRuntimeException ( "corpWechatUserInit.error" );
		}
		return NEXT;
	}

}
