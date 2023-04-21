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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 新增企业号
 *
 * @author
 */
@Service
public class EntUrlOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger ( getClass ( ) );

	@Value ( "${ent-url}" )
	private String entUrl;

	@Override
	public int execute ( IBusinessContext ctx ) {
		// TODO Auto-generated method stub
		logger.debug ( "-企业端URL-run--" );
		ctx.setParam ( "ENT_URL" , entUrl );
		return NEXT;
	}

}
