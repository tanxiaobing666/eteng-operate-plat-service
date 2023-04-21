/**
 * 测试场景说明, 按字母升序进行执行<br>
 * 场景编号/子顺序号。<br>
 * 单个子顺序目录里面可以包含多个可并发执行的交易，将有依赖关系的交易分拆到不同子顺序号中<br>
 * <code>
 {
	code : "YYM/01",
	desc : "YYM场景描述，登录前",
	trans : [ {
		transCode : "",
		//rtnCodeLabel : "STATUS",//缺省STATUS，也可以提定其它属性值进行比对
		rtnCode : "ERR00222",
		desc : "反案例"
	}]}
</code>
 */

var cases = [ 
{code : "", desc : "请选择测试场景" }
,{code : "icap/01", desc : "登录前" } 
,{code : "icap/02", desc : "登录后交易" } 



];