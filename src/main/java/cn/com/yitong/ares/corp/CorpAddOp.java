package cn.com.yitong.ares.corp;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.ares.util.UUIDUtil;
import cn.com.yitong.util.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 新增企业
 *
 * @author
 */
@Service
public class CorpAddOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger ( getClass ( ) );

	@Override
	public int execute ( IBusinessContext ctx ) {
		// TODO Auto-generated method stub
		logger.debug ( "-新增企业-run--" );
		//首先根据企业名称判断该企业是否已经注册
		Map map = this.getDao ( ctx ).queryForMap ( "corp.querySingleCorp" , ctx.getParamMap ( ) );
		if ( map != null ){
			//说明该企业已经注册过了
			throw new AresRuntimeException ( "corp.corpAdd.exist" );
		}
		//新增企业的主键
		ctx.setParam ( "BANK_ID" , UUIDUtil.getUUID ( ) );
		//插入到数据库
		this.getDao ( ctx ).insert ( "corp.corpAdd" , ctx.getParamMap ( ) );
		return NEXT;
	}

}
