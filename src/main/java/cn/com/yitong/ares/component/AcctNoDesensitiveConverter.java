package cn.com.yitong.ares.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.base.IBusinessContext;
import cn.com.yitong.ares.core.TransItem;
import cn.com.yitong.ares.net.web.desensitive.impl.AbstractDesensitiveConverter;
import cn.com.yitong.ares.util.SimpleCryptoUtil;

@Component
public class AcctNoDesensitiveConverter extends AbstractDesensitiveConverter{
	
	@Value("${ares.desensitive.acct-no:123456}")
	private String key;
	
	
	public static final String ACCT_NO="acctNo";

	/**
	 * 什么条件下满足使用该脱敏转换类，注意自定义的脱敏类型不要和已有的脱敏类型重复
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	@Override
	public boolean meet(TransItem item) {
		return ACCT_NO.equals(item.getDesensType());
	}

	/**
	 * 获取转换后的值
	 *
	 * @param originValue the origin value
	 * @param item the item
	 * @return the converted value
	 */
	@Override
	public Object getConvertedValue(IBusinessContext ctx, Object originValue, TransItem item) {
		if(originValue==null) {
			return null;
		}
		String convertValue=String.valueOf(convert(originValue,"(\\d{4})\\d+(\\d{4})","$1********$2"));
		String cipher=SimpleCryptoUtil.encryptToHex(String.valueOf(originValue), key);
		String outValue = convertValue+"|"+cipher;
        return outValue;
	}
}
