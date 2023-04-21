/**
 * 元素集校验器
 * 
 * @param elems
 * @returns {Boolean}
 */
function validator(parent) {
	if(paoLock()) return false;
	validatorClean(parent,true);
	
	var rules= $(parent)
	.find("input, select, textarea, label[data-type],div[data-type],radio,ul")
	.not(":submit, :reset, :image, [disabled] ,:hidden");	
	rules.removeClass("x-form-invalid");
	$(parent).find(".x-form-warn").hide();
	
	var rule,elem,result = true;
	if (rules && rules.length > 0) {
		for (var i = 0; i < rules.length; i++) {
			// 单元分类
			rule = rules[i];
			try{
				result = validatorElement(rule,true);
				if(!result){
					break;
				}
			}catch(e){alert(e);}
		}
	}
	paoLockRelease();
	return result;
}

var _validCaches = [];
var _validLock=false;
/**
 * 取出待验证元素
 * @returns
 */
function shiftValidElem(){
	var v=_validCaches;
	return v.length>0?v.shift():null;
}
/**
 * 追加待验证的元素
 * @param elem
 */
function pushValidElem(elem){
	_validCaches.push(elem);
	ansyValidCaches();
}

/**
 * 检查待验证的元素
 */
function ansyValidCaches(){
	try{
		if(_validLock){
			_validLock=false;
			return;
		}
		_validLock=true;
		var elem = shiftValidElem();
		if(elem){
			if(validatorElement(elem, true)){
				var obj=$(elem);
				obj.removeClass("x-form-invalid");
				var parent=obj.parent();
				parent.find(".x-form-warn").hide();
			}else{
				// 等待2秒后,检查下一个
				setTimeout("ansyValidCaches()",2000);
			}
		}
	}catch(e){
		alert("ansyValidCaches error:\n"+e);
	}
	_validLock=false;
}

/**
 * 清理待验证队列
 */
function clearValidCaches(){
	_validCaches = [];
	_validLock=false;
}

function initValidate(parent){ 
	var rules= $(parent)
	.find("input, select, textarea, label,ul")
	.not(":submit, :reset, :image, [disabled]").bind("blur",function(){
		var jqObj = $(this);
		var init=$(parent).data("init");
		if(init=="true")
			return;
		pushValidElem(this);
	});
	rules.removeClass("x-form-invalid");
	
	// 初始化表單內容
	formInit(parent,false);
	$(parent).data("init",null);	
}

function clearValiDatorClass(panel){
	panel.find(".x-form-warn").remove();
	var rules = panel.find("input, select, textarea,div,ul,label");
	rules.removeClass("x-form-invalid");
}

function validatorClean(parent,onlycheck){
	// 清理验证队列
	clearValidCaches();
	// 恢复原始狀態
	$(parent).data("init","true");
	var rules= $(parent).find("input, select, textarea,div,ul");
	rules.removeClass("x-form-invalid");
	$(parent).find(".x-form-warn").hide();
	// 重置表單內容
	var doInit=(onlycheck==true)?false:true;
	if(doInit){
		formInit(parent,true);	
	}
	$(parent).data("init",null);
} 

/**
 * 单元校验
 * 
 * @param cfg
 * @returns
 */
function validatorElement(el,nofocus) {
	var Y = YT$;
	var MSG = YT$.messages; 
	// 必输项required
	// 最大值max、最小值min
	// 最大长度maxLength、最小长度minLength
	var label, val, disabled, data = '';
	var jqObj = $(el);
	var element=jqObj;
	// alert(element.parent().html());
	val = jqObj.val();
	var dataType = jqObj.attr("data-type");
	var initwarn=jqObj.attr("initwarn");
	if(initwarn){
		jqObj.data("initwarn","true");
	}
	var label = jqObj.attr("data-label");
	if(dataType=='label'){
		val=jqObj.attr("data-value");
	}
	
	if("radio"==dataType){
		var checked=jqObj.find(".selected");
		if(checked){
			val=checked.attr("data-value");
		}
	}
	
	/**
	 * ljy add for 普通的单选按钮
	 * 格式
	 *  <div data-type='oldradio'>
	 *  	<input  type='radio' data-value='' >
	 *  </div>
	 */
	if("oldradio"==dataType){   //
		jqObj.find("input").each(function(){
			var checked=$(this).attr("checked");
			if(checked){
				val=$(this).attr("data-value");
			}
		});
	}
	if("questradio"==dataType){   //
		jqObj.find("input").each(function(){
				var checked=$(this).attr("checked");
				if(checked){
					val=$(this).attr("data-value");
				}
			});
	}
	
	val=val?val:"";// 防止出现undefined错误；
	if($.browser.msie) {  
		var placeholder=jqObj.attr("placeholder");
		if(placeholder==val){
			val="";
		}
	}	
	// 必输项校验
	if (val.trim().length == 0) {
		if ("true"==jqObj.attr("data-required")) {
			var msg;
			if(dataType=="select"){
				 msg = Y.msgjoin(MSG.MsgSelect, label, []);  
				 showPinLabel(el, msg, nofocus);
			}else if("oldradio"==dataType){
				msg = Y.msgjoin(MSG.MsgSelect, label, []);  
				el=$(el).find("input:first");
				showPinLabel(el, msg, nofocus);
			}else {
				msg = Y.msgjoin(MSG.MsgMustInput, label, []); 
				showPinLabel(el, msg, nofocus);
			}
			return false;
		}
	}	
	// 不能在這裡格式化，否則驗證為非數字了 ljy update
	if('money' == dataType){
		val = unfmtAmt(val);
	}
	// 检查特殊字符
	var patt_str=/\'|\"|,|=|:|\\{|\\}|\\[|\\]|\$/;
	var jsonType = jqObj.attr("data-json");
	if(jsonType == "db" ){
		patt_str=/\'|\"/;		
	}
	
	if(patt_str.exec(val)){
	   var msg = Y.msgjoin(MSG.MsgStr, label, []);
	   showPinLabel(el, msg, nofocus);
	   return false;
	}
	
	
	var len = stringLength(val); 
	// 最大长度maxLength
	var maxL = jqObj.attr("data-maxlength");
	if (maxL && maxL * 1 > 0) {
		maxL = maxL*1;
		len = tfbStringLength(len,jqObj);
		if (maxL && len > maxL ) { 
			var msg = Y.msgjoin(MSG.MsgMaxLength, label, [maxL]);
			showPinLabel(el, msg, nofocus);
			return false;
		} 
		var must = jqObj.attr("data-inputall");
		if (must && maxL > len) {
			var msg = Y.msgjoin(MSG.MsgMustInputAll, label, [maxL]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
	}
	// 最小长度minLength
	var minL = jqObj.attr("data-minlength");
	if (minL && len < minL * 1) { 
		var msg = Y.msgjoin(MSG.MsgMinLength, label, [minL]);
		showPinLabel(el, msg, nofocus);
		return false;
	} 	
	// 金额类型
	if('money' == dataType){
		if(isNaN(val)){
			var msg=Y.msgjoin(MSG.MsgInteger, label, []);
			showPinLabel(el, msg, nofocus);
			return false;
		}
		if ("true"==element.attr("data-required")&& val <= 0){
			var msg = Y.msgjoin(MSG.MsgMoney, label, []);
			showPinLabel(el, msg, nofocus);
			return false;
		}		
	}
	// 数字类型
	var patt_num = /^\d*$/;
	if('int' == dataType){
		if(!patt_num.test(val)){
			var msg = Y.msgjoin(MSG.MsgInteger, label, []);
			el.value = "";
			showPinLabel(el, msg, nofocus);
			return false;
		}		
	}
	//整数
	if('number' == dataType){
		if(!patt_num.test(val)){
			var msg = Y.msgjoin(MSG.MsgNumber, label, []);
			showPinLabel(el, msg, nofocus);
			return false;
		}		
	}
	
	// 最小值
	var minV = jqObj.attr("data-min");
	if (minV && minV * 1 > 0) {
		log("minV:" + minV);
		if (minV > val * 1) {
			var msg = Y.msgjoin(MSG.MsgMinValue, label,[minV]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
	}	
	// 最大值
	var maxV = jqObj.attr("data-max");
	if (maxV && maxV * 1 > 0) {
		if (maxV < val * 1) {
			var msg = Y.msgjoin(MSG.MsgMaxValue, label,[maxV]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
	}
	// 自定义的验证方法
	var funcPlus = jqObj.attr("data-valid");
	// 密碼验证是否是数字和字符的组合 大小写不区分
	if (funcPlus=="num_char") {
		if((val.length*1<8)||(val.length*1>14)){
			var msg = Y.msgjoin(MSG.MsgNumChar, label,[]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
		var i=0;
		if(/[0-9]/.test(val) ){
			i++;
		}// 数字
		if(/[A-Za-z]/.test(val)){
			i++;
		}// 字母
		if(/^[A-Za-z0-9]*$/.test(val)==false){
			i--;
		}
		if(i!=2){
			var msg = Y.msgjoin(MSG.MsgNumChar, label,[]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
		if(/^[A-Za-z0-9]*$/.test(val)==false){
			var msg = Y.msgjoin(MSG.MsgNumChar, label,[]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
	}
	//安全問題  ljy add
	//[\u4e00-\u9fa50-9A-Za-z\s]
	if(funcPlus=="question"){
		if(!/[\u4e00-\u9fa50-9A-Za-z\s]+$/.test(val)){
			var msg = Y.msgjoin(MSG.MsgStrQue, label,[]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
	}
	//只能输入英文 数组
	if(funcPlus=="english"){
		if(/[^A-Za-z\s]/.test(val)){
			var msg = Y.msgjoin(MSG.MsgEnglish, label,[]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
	}
	//汉字的检验
	if(funcPlus=="china"){
		if(!/^[\u4e00-\u9fa5]+$/.test(val)){
			var msg = Y.msgjoin(MSG.MsgChina, label,[]);
			showPinLabel(el, msg, nofocus);
			return false;
		}
	}
	
	// 自定义pattern类型
	var pattern = jqObj.attr("data-pattern"); 
	if(pattern){
		var reg = eval(pattern);
		if(null != dataType){
			if(!reg.test(val)){
				var msg = Y.msgjoin(MSG.MsgIntegerOrStr, label, []);
				el.value = "";
				showPinLabel(el, msg, nofocus);
				return false;
			}		
		}
	}
	
	// 金额小数点最多输入小数点后4位
	var decimal = jqObj.attr("data-decimal"); 
	if(decimal){
		var reg = eval(decimal);
		if(null != dataType){
			if(!reg.test(val)){
				var msg = Y.msgjoin(MSG.MsgDecimal, label, []);
				showPinLabel(el, msg, nofocus);
				return false;
			}		
		}
	}
	
	return true;
}

function getFormJson(panel,sendObj){
	panel = $(panel);
	// debugger;
	sendObj=sendObj?sendObj:{};
	var listArray = [];
	var listMap={};
	var subListArray = [];
	var subListMap={};
	var listName;
	var subListName;
	var isList=false;
	// 表单异步提交
	panel.find("[data-type]").each(function(){
		// debugger;
		var jqObj=$(this);
		var dataType=jqObj.attr("data-type");
		if(jqObj.attr("data-flag") == '0'){
			return ;
		}
		var name='',value='';
		if("radio"==dataType){
			name=jqObj.attr("data-name");
			var checked=jqObj.find(".selected");
			if(checked){
				value=checked.attr("data-value");
			}
		}
		else if("checkbox"==dataType){
			name=jqObj.attr("data-name");
			var checked=jqObj.find(".selected");
			if(checked){
				value=checked.attr("data-value");
			}
		}else if("label"==dataType){
			name=jqObj.attr("data-name");
			value=jqObj.attr("data-value");
		}else if("check-box"==dataType){// input check-box
			name=jqObj.attr("name");
			var checked=jqObj.attr("checked");
			value= checked=="checked"?'1':'0';
		}else if("nodeList"==dataType){
			isList = true;
			name=jqObj.attr("name");
			value=jqObj.val();
			listName = name;
		}else if("subNodeList"==dataType){
			isList = true;
			name=jqObj.attr("name");
			value=jqObj.val();
			subListName = name;
			//listMap[subListName] = [];
		}else if("textList"==dataType){
			name=jqObj.attr("name");
			value=jqObj.val();
			listMap[name] =value;
		}else if("subTextList"==dataType){
			name=jqObj.attr("name");
			value=jqObj.val();
			subListMap[name] =value;
		}
		else if("mapEnd"==dataType){
			var obj = {};
			$.extend(true,obj,listMap);
			listArray[listArray.length] = obj;
			listMap = {};
		}else if("listEnd"==dataType){
			var array = [];
			$.extend(true,array,listArray);
			sendObj[listName]= array;
			listArray = [];
		}
		else if("subMapEnd"==dataType){
			var obj = {};
			$.extend(true,obj,subListMap);
			subListArray[subListArray.length] = obj;
			subListMap = {};
		}else if("subListEnd"==dataType){
			var array = [];
			$.extend(true,array,subListArray);
			listMap[subListName] = array;
			//sendObj[listName]= array;
			subListArray = [];
		}
		else{
			isList = false;
			name=jqObj.attr("name");
			value=jqObj.val();					
		}
		
		if(name && value){
			if($.browser.msie) {  
				var placeholder=jqObj.attr("placeholder");
				if(placeholder==value){
					value="";
				}
			}
			// 去掉破坏JSON格式的内容；
			var jsonType = jqObj.attr("data-json");
			if(jsonType=="db"){
				value = value.replace(/\'|\"/i, "");
			}else{
				//上送数据中需要包含逗号的时候会被处理掉
//				value = value.replace(/\'|\"|,|=|:|\\{|\\}|\\[|\\]/i, "");	
				//上送数据中需要包含逗号的时候不会被处理掉
				//value = value.replace(/\'|\"|=|:|\\{|\\}|\\[|\\]/i, "");	
			}
			//value = value.replace(/</i, "&lt;");
			//value = value.replace(/>/i, "&gt;");
			if(!isList){
				sendObj[name] = value;
			}
		}
	});	
	return sendObj;
}



/**
 * 字符长度
 * @param str
 * @returns
 */
function stringLength(str) {
	return str.replace(/[^\x00-\xff]/g, "**").length;
}

/**
 * 重复字符
 * 
 * @param num
 * @param str
 * @returns
 */
function repeatStr(num,str){
	var sb=[];
	for(var i=0;i<num;i++){
		sb[sb.length]=str;
	}
	return sb.join("");
}

function paoLock(){
	var layer=$("#paopao_layer");
    var animing=layer.data("animing");
    if(animing){ 
    	return true;
    }
    layer.data("animing","true");
    return false;
}
function paoLockRelease(){
	var layer=$("#paopao_layer"); 
    layer.removeData("animing");
    return false;
}

/**
 * 显示错误信息
 * @param el
 * @param msg
 * @param nofocus
 */
function showPinLabel(el, msg, nofocus) { 
	try{
		var layer=$("#paopao_layer"); 
		$(el).addClass("x-form-invalid");
		var initwarn=$(el).data("initwarn");
		var parent=$(el).parent();
		if(initwarn!="true"){
			$(el).data("initwarn","true");
			$('<span class="x-form-warn"></span>').insertAfter($(el)); 
		}
		parent.find(".x-form-warn").show().click(
				function () {
				  var rules=$(this).parent().find(".x-form-invalid");
				  validatorElement(rules.eq(0),true);
			  });
		var focus=(nofocus==true)?false:true;
		if(focus){
			$(el).focus();
		}
		// 定位到某元素
		var offset = $(el).offset(); 
		layer.css("left", $(el).width()+offset.left+15);
		layer.css("top",offset.top-5);
		layer.width(150);
		layer.show();
		layer.find(".pao_msg").html(msg);
		layer.stop().animate({opacity: 1},{duration: 1500,
			complete: function(){ 
			    $(this).stop().hide();
			}
		}); 
	}catch(e){}
}

function log(str) { 
}

/**
 * fmtAmt(1000) ---> 1,000.00 金额千位格式化，保留两位小数
 */
function fmtAmt(s){
	try{
		return _fmtMoney(1.0*s,2,".",",");
	}catch(e){
		return "0.00";
	}
} 

/**
 * fmtAmt(1000) ---> 1,000.00 金额千位格式化，保留三位小数
 */
function fmtAmt3s(s){
	try{
		return _fmtMoney(1.0*s,3,".",",");
	}catch(e){
		return "0.000";
	}
} 

/**
 * fmtAmt(1000) ---> 1,000.0000 金额千位格式化，保留四位小数
 */
function fmtAmts4(s){
	try{
		return _fmtMoney(1.0*s,4,".",",");
	}catch(e){
		return "0.0000";
	}
} 

/**
 * fmtAmt(1000) ---> 1,000.000000 兑率百万分位格式化，保留六位小数，用于利率等
 */
function fmtAmt4s(s){
	try{
		return _fmtMoney(1.00000*s,6,".",",");
	}catch(e){
		return "0.000000";
	}
}
/**
 * 日期格式化 yyyy-MM-dd
 * @param s
 */
function fmtDate(s){
	try{
		if(s){
			if(s=="00000000"){
				return "";
			}else{
				return s.substring(4,s.length)+"-"+s.substring(2,4)+"-"+s.substring(0,2);
			}
			
		}else{
			return "0000-00-00";
		}
	}catch(e){
		return "0000-00-00";
	}
	
}

/**
 * 金额去格式化
 * 
 * @param s
 * @returns
 */
function unfmtAmt(s){
	return s.replace(/,/g,"");
}

function fmtZore(s){
	var s = s.replace(/\b(0+)/gi,"");
	return s;
}

function fmtNum(s){
	var reg = /(-?\d+)(\d{3})/;
	s+='';
	while (reg.test(s)) {
	s=s.replace(reg, "$1,$2");
	}
	return s;
}

/**
 * eg: _fmtMoney(18299.00, 2, ".", ",");
 * 
 * @param n
 * @param c
 * @param d
 * @param t
 * @returns
 * @ignore
 */
function _fmtMoney(n, c, d, t) {
	var p = n < 0 ? "-" : "";
	n = n.toFixed(c);
	c = Math.abs(c) + 1 ? c : 2;
	d = d || ",";
	t = t || ".";
	var m = (/(\d+)(?:(\.\d+)|)/.exec(n + ""));
	x = m[1].length > 3 ? m[1].length % 3 : 0;
	return p + (x ? m[1].substr(0, x) + t : "")
			+ m[1].substr(x).replace(/(\d{3})(?=\d)/g, "$1" + t)
			+ (c ? d + (+m[2] || 0).toFixed(c).substr(2) : "");
}
 
/**
 * 金额大写转换函数
 * 
 * @param n
 * @returns {String}
 */
function cnyNumber4CN(n) {
	if (!/^(0|[1-9]\d*)(\.\d+)?$/.test(n))
		return "零元整";
	var unit = "万仟佰拾亿仟佰拾万仟佰拾元角分", str = "";
	n += "00";
	var p = n.indexOf('.');
	if (p >= 0)
		n = n.substring(0, p) + n.substr(p + 1, 2);
	unit = unit.substr(unit.length - n.length);
	for ( var i = 0; i < n.length; i++) {
		str += '零壹贰叁肆伍陆柒捌玖'.charAt(n.charAt(i)) + unit.charAt(i);
	}
	str = str.replace(/零(仟|佰|拾|角)/g, "零")
			 .replace(/(零)+/g, "零")
			 .replace(/零(万)/g, "万零")
			 .replace(/零(亿)/g, "亿零")
			 .replace(/零(元)/g, "元零")		 
			 .replace(/(零)+/g, "零")	 
			 .replace(/^元零?|零分/g, "")
			 .replace(/零元/g, "元")
			 .replace(/零(万|亿)/g, "")
			 .replace(/亿万/g, "亿")
			 .replace(/元$/g, "元整 ");
	if (str == "分") {
		str = "零元整";
	}
	return str;
}

/**
 * JSON对象转String
 * 
 * @param o
 * @returns {String}
 */
function JsonToStr(obj) {
	if (obj == null) {
		return '""';
	}
	switch (typeof (obj)) {
		default:
		case 'number':
		case 'string':
			return '"' + obj + '"';
		case 'object': {
			if (obj instanceof Array) {
				var strArr = [];
				var len = obj.length;
				for ( var i = 0; i < len; i++) {
					strArr.push(JsonToStr(obj[i]));
				}
				return '[' + strArr.join(',') + ']';
			} else {
				var arr = [];
				for ( var i in obj) {
					arr.push('"' + i + '":' + JsonToStr(obj[i]));
				}
				return "{" + arr.join(',') + "}";
			}
		}
	}
	return '""';
}

/**
 * @description 公共交易请求 TransAjax
 * @return
 */
var TransAjax = function() {
	return this;
};
TransAjax.prototype = {
	timeout : 120000,// 超时时长
	timeoutflg : false,// 是否超时信息的标记
	_args : null, // 自定义参数
	_rpdata : null,
	_showError : false,
	_resetSessTimer: true,// 是否更新会话定时器
	getRpdata : function() {
		return this._rpdata;
	},
	setArgs : function(obj) {
		this._args = obj;
	},
	resetSessTimer : function(flag) {
		this._resetSessTimer = flag;
	},
	getArgs : function() {
		return this._args;
	},
	setShowError : function(s) {
		// 设置是否显示错误信息
		this._showError = s;
	},
	start : function() {
		// 开启超时提醒
		this.timeoutflg = true;
	},
	clear : function() {
		// 取消超时提醒时，需调用此方法。
		this.timeoutflg = false;
		if (this._timeoutHandle) {
			clearTimeout(this._timeoutHandle);
		}
	},
	getXmlHttpObj : function() {
		var xmlHttpObj;
		try {
			xmlHttpObj = new XMLHttpRequest();
		} catch (e) {
			try {
				xmlHttpObj = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				xmlHttpObj = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
        return xmlHttpObj;
	},
	sendPostData : function(url, param, callback) {
		if(YT$.transTimeout){
			YT$.showTransTime();
			return;
		}
		var _ajax = this;
		if(_ajax._resetSessTimer){
			YT$.ressetSessionListener();
		}
		_ajax.start();  
		var xhr = this.getXmlHttpObj();
		xhr.onreadystatechange = function() {
			if (this.readyState == 4) {
				if (this.status == 200) {  
					if(_ajax.abort){
						alert("timeout is abort!");
						return;
					}
					var rpdata = eval("(" + this.responseText + ")"); 
					_ajax._rpdata = rpdata;
					var run = true;
					try{
						if (rpdata && rpdata.STATUS) { 
							if (rpdata.STATUS == "005") {
								showTimeOut();// session超时;
								//run = false;
							} else if (rpdata.STATUS == "100") {
								run = false;
							} else if (rpdata.STATUS != "1") {
								if (_ajax._showError) {
									showMsgPanel("" + rpdata.MSG);
								}
							} 
						} 
						if(run)	callback(rpdata,_ajax._args);
					}catch(e){
						alert(e);
//						YT$.log("TransAjax",e);
					}
					_ajax.clear();
				}
			}
		};
		xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type","application/json");
        xhr.setRequestHeader("Accept","application/json");
		xhr.send(param);
		// Timeout checker
		if (_ajax.timeout > 0) {
			_ajax._timeoutHandle = setTimeout(function() {
				// Check to see if the request is still happening
				if (xhr && _ajax.timeoutflg) {
					_ajax.abort=true;
					clearWaitPanel();
					xhr.abort();
					//showMsgPanel(MSG$.ALETT_AJAX_TIMEOUT);
					//alert("交易超时！");
				}
			}, _ajax.timeout);
		}
	}
}; 

// 设置服务器响应码及响应信息
function responseMessage(panel, cfg) {
	cfg = cfg ? cfg : {
		show : false,// 显示狀態
		icon: "",// 成功、警告
		msg : ""
	};
	var display = cfg.show;
	var iconClass = cfg.icon;
	var msg = cfg.msg;
	var msgbar = panel.find(".msg_div");
	msgbar.find(".msg_span").html(msg);
	msgbar.find(".msg_pic").removeClass("success").removeClass("warn").addClass(iconClass);	
	if(display){
		msgbar.slideDown();// show
	}else{
		msgbar.slideUp();// hide
	}
}
  
var MSG$ = { 
	PAGE_FIRST : "首頁",
	PAGE_LAST : "尾頁",
	PAGE_NO_PREV : "當前第",
	PAGE_NO_LAST : "頁", 
	PAGE_TOTAL_PREV : "共",
	PAGE_TOTAL_LAST : "頁", 
	PAGE_LOADED : "已加載",  
	PAGE_UP : "向前遍歷頁碼",
	PAGE_DOWN : "向後遍歷頁碼",
	EMPTY_DATA : "查詢無相關數據",
	PAGE_DOWNLOAD : "下載",
	PAGE_PRINT : "打印",
	TITLE_ALETT : "系统提示",
	TITLE_WARN : "安全提示",
	LOADING : "加载中,请稍候...",
	ALETT_AJAX_TIMEOUT : "系统繁忙，资金类交易请及时核对賬戶信息!",
	ALETT_TRAN_TIMEOUT : "您15秒内未完成交易，請重試！",
	BTN_OK : "确 定",
	BTN_CANCLE : "取 消"
};

$.PNC = function(options) {
	this.settings = $.extend(true, {}, $.PNC.defaults, options);
};

(function($) {
	$.extend($.PNC, {
		defaults: {
			tab_index: "first",
			tab_stock: "stock",
			tab_ansy: "tab_ansy",
			ignore: ":hidden",
			downlock:false,
			downlockTime:10000,
			transTime:15000,
			logable:true,
			backpage:"index"
		},
		// http://docs.jquery.com/Plugins/Validation/Validator/setDefaults
		init: function(settings) {
			$.extend( $.PNC.defaults, settings );
		},
		gotoIndex:function(){
			changeTab({
				'tab' : this.defaults.tab_index,
				'url': ctx+"inners/02/PP02000.do"
			});
		},
		gotoStock:function(){
			changeTab({
				'tab' : this.defaults.tab_stock
			});
		},
		downText:function(pageId, content, type) {
			if(form_downtext == null){
				alert("---form_downtext-not found--");
				return false;
			}
			if(this.defaults.downlock){
				return;
			}
			this.defaults.downlock = true;
			try{
				type = type ? type : "text";
				form_downtext.pageId.value = pageId;
				form_downtext.content.value = content.replace(/\(|\)|\'|\"/g,"");
				form_downtext.type.value = type;
				form_downtext.submit();
			}catch(e){
				alert("downText error :\n"+e);
			}
			setTimeout(function(){
				YT$.defaults.downlock = false;
			},this.defaults.downlockTime); 
		},
		// 清除服务端消息
		clearMessageBar:function (panel){			
			responseMessage(panel,{show:false});
		},
		rspSuccessMessage:function(panel,msg){
			responseMessage(panel,{show:true,icon:"success", msg:msg});
		},
		rspWarnMessage: function(panel,msg){
			responseMessage(panel,{show:true,icon:"warn", msg:msg});
		},
		log: function(model,msg){
			//alert(model + "\n" + msg);
		}, 
		msg: function(key,defmsg){
			if ($.MSG[key]) {
				return $.MSG[key];
			}
			return defmsg;
		},
		sessionHandle: null,
		sessionTimeout: false,
		ressetSessionListener: function(){
			if(YT$.loginPage){
				return;
			}
			if(typeof(updateSsid) == "function"){
				if($.browser.safari){
				}else{
					updateSsid();
				}
			}
			// session定时器检查更新
			if(this.sessionHandle){
				clearTimeout(this.sessionHandle);
			}
			this.sessionHandle = setTimeout(function(){
				if(YT$.sessionTimeout){
					return;
				}
				// 十五秒会话超时预警
				YT$.showSessionWarnPanel();
			}, 480000);
		},
		showSessionWarnPanel: function(){
			if (YT$.STREAM_FLAG == "0" && YT$.stockPageFlag == true){
				hiddenStreamPageOT(); // 股票买卖串流页面时隐藏B区 
			}
			this.sessionTimeout = true;
			var win = $("#window_panel_sess");
			win.find("div.timeout_title ._div1").text(MSG$.TITLE_WARN);
			win.find("div.timeout_title ._div2 img").attr("src",ctx+"css/images/icon/exit.png");
			win.parent().fadeIn();
			var width = 400;
			var height = 300;
			var url = ctx + "error/sessionWarn.jsp";
			var toLeft = hCenterPosition(width);
			var clientHeight = win.parent().height();
			var toTop = (clientHeight - height) / 2;  
			win.width(width).height(height).fadeIn().animate({
				opacity : 1,
				left : toLeft,
				top : toTop 
			}).draggable();
			var panel = win.find(".panel-body");
			panel.height(height-50);
			panel.html(MSG$.LOADING);
			panel.hide().load(url).show();
			// _div2
			win.find("div._div2 img").bind("click", function() {
				location.href=ctx+"login.do";
			});
		},
		transHandle: null,
		traqnsTimeout: false,
		ressetTransListener: function(pid){
			this.defaults.backpage=pid;
			if(!YT$.transTimePage){
				return;
			}
			if(this.transHandle){
				clearTimeout(this.transHandle);
			}
			this.transHandle = setTimeout(function(){
				if(YT$.transTimeout){
					return;
				}
				// 十五秒会话超时
				YT$.showTransTime();
			}, this.defaults.transTime);
		},
		showTransTime: function (){
			YT$.transTimeout = true; 
			showMsgPanel(MSG$.ALERT_TRAN_TIMEOUT,MSG$.TITLE_ALERT,{funcOk:"YT$.transTimeIndex()"});
		},
		transTimeIndex: function(){
			YT$.transTimeout = false; 
			//跳转到输入页面，否则跳转到首页
			var pid=this.defaults.backpage;
			if(pid=="index"){
				YT$.gotoIndex();
			}else{
				var parent = $("#"+pid);
				var button= parent.find("button.backfor15s");
				if(button){
					button.trigger("click");
				}else{
					YT$.gotoIndex();
				}
			}
			this.defaults.backpage="index";
			closeWindow();
		},
		gotoPage: function(url,tabname,title){
			changeTab({
				'tab' : this.defaults.tab_ansy,
				'url' : url,
				'tabname' : tabname,
				'name' : title
			});
		},
		messages: {
			MsgMustInput : "请输入{0}!",
			MsgSelect : "请选择{0}！",
			MsgMinLength : "{0}长度不小于{1}位!",
			MsgMustInputAll : "{0}必须输入{1}位!",
			MsgMinValue : "{0}不能小于{1}!",
			MsgMaxValue : "{0}不能大于{1}!",
			MsgMoney : "{0}必须大于0!",
			MsgInteger : "{0}必须是数字!",
			MsgNumber : "{0}必须是整数!",
			MsgMaxLength : "{0}输入超过{1}位,单汉字按2位长度计算!",
			MsgStr : "{0}不能包含特殊字符!",
			MsgNumChar:"{0}長度為 8-14位，包含最少一個數字及英文字母!",
			MsgIntegerOrStr : "{0}必须是字母或数字!",
			MsgStrQue : "{0}不能包含特殊字符!",  //[\u4e00-\u9fa50-9A-Za-z\s]  安全問題
			MsgIntegerAndStr : "{0}必须是字母和数字!",
			MsgEnglish : "{0}必须是字母!",
			MsgChina : "{0}必须是汉字!",
			MsgDecimal:"{0}最多可輸入4位小數"	
			
		},
		msgjoin : function(msg, label, params) {
			log("PNC.msgjoin:" + msg);
			var label = label ? "'" + label + "'" : "";
			var s = msg.replace("{0}", label);
			if (params) {
				for (var i = 1; i <= params.length; i++) {
					s = s.replace("{" + i + "}", params[i - 1]);
				}
			}
			return s;
		}
	});
}(jQuery));

var YT$=$.PNC;

function showKeyboard (dataObj){
	this.ctrlObj=dataObj; 
	this.ctrlKeyPass="";
	this.ctrlKeyAvailable=false;
	var win = $("#window_keyboard");
	var open = win.attr("open");
	if (open || open=="open") {
		return false;
	}
	win.attr("open" , true);
	win.data("keyboard",this);
	win.parent().fadeIn();
	var width = 525;
	var height = 200;
	var xy = getKeyBoardPoint(dataObj.get(0));
	var toLeft = xy.x-200;
	var toTop = (xy.y + dataObj.get(0).offsetHeight+20);
	var url = ctx + "keyBoard/keyBoard.jsp";
	win.width(width).height(height).fadeIn().animate({
		opacity : 1,
		left : toLeft,
		top : toTop 
	});
	var panel = win.find(".panel-body");
	panel.hide().load(url).show();
}

function getKeyBoardPoint(e){
    var x = e.offsetLeft;
    var y = e.offsetTop;
    while (e = e.offsetParent) {
        x += e.offsetLeft;
        y += e.offsetTop;
    }
    return {
        "x": x,
        "y": y
    };
};

/**
 * 
 * 格式化帐号
 * 
 * @param acctNo
 * @returns
 *//*
function formatAcct(acctNo){
	var newAcctNo = "";
	if(acctNo){
		var len = acctNo.length;
		if(len==16 || len==17){
			if(1==1){	// 格式化成10位
				var a = acctNo.substring(len-14).split("");
				var b = [a[6],a[4],a[5],a[3],a[8],a[9],a[10],a[11],a[12],a[13]];
				
				var tmp = b.join("");
				newAcctNo = tmp.substr(0,3)+"-"+tmp.substr(3,1)+"-"+tmp.substr(4,5)+"-"+tmp.substr(9);
			}else if(1==2){	// 格式化成12位
				
			}else if(1==3){	// 格式化成14位
				
			}
		}else{
			return acctNo;
		}
	}
	return newAcctNo;
};*/

/**
 * 更改攀枝花商业銀行的 10 位 12位 14位 16位 的帐号到17位 存储的时候用
 * @param accStr
 * @returns
 */
function unFormatAcct(accStr) {
	var newAcc = "";
	if (accStr){
		accStr = accStr.trim().replace(new RegExp('-','g'), "");
	}else{
		accStr="";
	}
	var len=accStr.length;
	if(len==10){
		newAcc = "000000" + accStr.substring(3, 4)
		+ accStr.substring(1, 3) + accStr.substring(0, 1) + "0"
		+ accStr.substring(4);
	}else if(len==12||len==14||len==16){
		var tmp= "00000" + accStr;
		newAcc = tmp.substring(tmp.length-17);
	}else{
		newAcc=accStr;
	}
	return newAcc;
}

function fmtAcctSort(accStr){
	// 系统-幣別-机构-賬號-狀態
	if (accStr) {
		accStr=formatAcct(accStr).replace(/\-/g,"");
		if(accStr.length==14){
			accStr = accStr.substring(2);
		}
		if(accStr.length==12){
			//０２－０１－２－１０００４２　－７
			//└┬┘　  └┬┘　　│　　└────┬────┘　　│
			//系统　行別　幣別　　     帐号　　检核数字
			accStr=accStr.substring(0,2) //系统
				+accStr.substring(4,5)//幣別
				+accStr.substring(2,4)//机构
				+accStr.substring(5)//机构
				;
		}else{
			//１　０　１－１－　０００５２　－　２
			//│　　└┬┘　　│　　　└───┬───┘　　　│
			//幣別行別　系统　　　    帐号　　检核数字
			accStr="0"+accStr.substring(3,4)//系统
				+ accStr.substring(0,1) //幣別
				+ accStr.substring(1,3) //机构
				+ "0"+ accStr.substring(4)   //賬號
				;
		}
		//alert(accStr);
	}
	return accStr;
}

/**
 * 格式化攀枝花商业銀行的帳號17,16,14位到 12位 10位
 * */
 function formatAcct(accStr) {
	if (accStr) {
		accStr = accStr.trim();
		if (accStr.length == 17) {
			accStr = accStr.substring(3);
		}
		if (accStr.length == 16) {
			accStr = accStr.substring(2);
		}
		if (accStr.length == 14) {
			if ("0"==accStr.substring(7, 8)) {
				// 老帳號顯示10位的 000-0-00000-0,以下是規則
				accStr = accStr.substring(6, 7) + 
						accStr.substring(4, 6)
						+ "-" + accStr.substring(3, 4) + "-"
						+ accStr.substring(8, 13) + "-"
						+ accStr.substring(13);
			} else {
				// 新帳號的顯示14位的 00-00-00-0-000000-0
				accStr = accStr.substring(0, 2) + "-"
						+ accStr.substring(2, 4) + "-"
						+ accStr.substring(4, 6) + "-"
						+ accStr.substring(6, 7) + "-"
						+ accStr.substring(7, 13) + "-"
						+ accStr.substring(13);
				if ("00-"==accStr.substring(0, 3)) {
					// 12位的顯示方式 00-00-0-000000-0
					accStr = accStr.substring(3);
				}
			}
		}
	}
	return accStr;
}
/**
 * 格式化中银賬號
 */
function  formatBocAcct(noStr) {
		if (noStr&&noStr!='') {
			noStr = noStr.trim();
			var noStrLength = noStr.length;
			var res="";
			if (noStrLength == 12) {
				res=noStr.substring(0, 2)
					+"-"+noStr.substring(2, 4)
					+"-"+noStr.substring(4, 6)
					+"-"+noStr.substring(6);
				return res;
			} else {
				return noStr;
			}
		}
		return "";
}

/**
 * 反格式化中银賬號
 */
function  unFormatBocAcct(noStr) {
	
		if (noStr&&noStr!='') {
			noStr = noStr.trim();
			var noStrLength = noStr.length;
			if (noStrLength == 15) {
				return noStr.replace(new RegExp('-','g'), "");
			}else{
				return noStr;
			}
		}else{
			return "";
		}
		
}

/**
 * 获取第三者帐号的幣別 1-港币 2-澳门币
 * 
 * @param acctNo
 */
function getAcctCcy(acctNo){
	var len = acctNo.length;
	var ccy="";
	var ccyDesc="";
	if(len==16 || len==17){
		var tmpAcctNo=formatAcct(acctNo);
		if(tmpAcctNo.length>13){
			var pre=tmpAcctNo.substr(6,1);
		}else{
			var pre=tmpAcctNo.substr(0,1);
		}
		if(pre=='1'){
			ccy="344";
			ccyDesc="HKD";
		}else if(pre=='2'){
			ccy="446";
			ccyDesc="MOP";
		}else{
			ccy="000";
			ccyDesc="000";
		}
	}
	var json={ccy:ccy,ccyDesc:ccyDesc};
	return json;
}

/**
 * form初始化及重置方法
 * 
 * @param panel
 * @param reset
 *            false&true
 */
function formInit(panel,reset){
	$(panel).data("init","true");
	reset = (reset==true)?true:false;
	panel.find("[data-type]").each(function(){
		var jqObj = $(this);
		var dataType=jqObj.attr("data-type");
		var keyupActs=jqObj.attr("data-keyup");
		keyupActs = (keyupActs==undefined)?"":keyupActs;
		var name='',value='';
		if("radio"==dataType){
			if(reset){
				var initValue = jqObj.data("data-init");
				jqObj.find("li").each(function(){
					var item = $(this);
					var itemValue = item.attr("data-value");
					if(itemValue == initValue){
						item.addClass("selected");
					}else{
						item.removeClass("selected");
					}
				});
			}else{
				var checked=jqObj.find(".selected");
				if(checked){
					value=checked.attr("data-value");
					jqObj.data("data-init",value);
				}
			}
		}else if("checkbox"==dataType){
			var checked=jqObj.find(".selected");
			if(checked){
				value=checked.attr("data-value");
			}
		}else if("label"==dataType){
			if(reset){
				jqObj.attr("data-value",jqObj.data("data-init"));
				jqObj.html(jqObj.data("data-initHtml"));
			}else{
				value=jqObj.attr("data-value");
				jqObj.data("data-init",value);
				
				valueHtml=jqObj.html();
				jqObj.data("data-initHtml",valueHtml);
			}
		}else if("check-box"==dataType){// input check-box
			if(reset){
				var isChecked=jqObj.data("data-init");
				// 点击事件处理不了，故不作处理
				/*
				 * if(isChecked=='1'){ jqObj.attr("checked","checked"); }else{
				 * jqObj.attr("checked",false); }
				 */
			}else{
				var checked=jqObj.attr("checked");
				value = checked=="checked"?'1':'0';
				jqObj.data("data-init",value);
			}
		}else if("labelStock"==dataType){ // add by snow for stock orders
			if(reset){
				jqObj.html(jqObj.data("data-init"));	
			}else{
				value=jqObj.html();
				jqObj.data("data-init",value);				
			}
		}else if("data-display"==dataType){//表单类初始化是否显示
			if(reset){
				jqObj.css("display",jqObj.data("data-init"));
			}else{
				value=jqObj.css("display");
				jqObj.data("data-init",value);	
			}
		}else if("data-disabled"==dataType){//表单类初始化是否显示
			if(reset){
				jqObj.prop("disabled",jqObj.data("data-init"));
			}else{
				value=jqObj.prop("disabled");
				jqObj.data("data-init",value);	
			}
		}
		else{
			if(reset){
				jqObj.val(jqObj.data("data-init"));	
			}else{
				value=jqObj.val();
				jqObj.data("data-init",value);				
				// 綁定maxLength事件
				var maxLength=jqObj.attr("data-maxlength");
				if(undefined != maxLength || "money" == dataType){
					keyupActs = "maxLengthCheck " + keyupActs;
				}
			}	
		}
		if($.browser.msie) {  
			var placeholder=jqObj.attr("placeholder");
			if(placeholder && placeholder.length>0){
				var val=jqObj.val();
				if(val==""){
					jqObj.val(placeholder);
				}
				jqObj.bind("focus",function(){
					var obj=$(this);
					var placeholder=obj.attr("placeholder");
					var val=obj.val();
					if(val==placeholder){
						obj.val("");
					}
				});
				jqObj.bind("blur",function(){
					var obj=$(this);
					var val=obj.val();
					if(val==""){
						var placeholder=obj.attr("placeholder"); 
						obj.val(placeholder);
					}
				});
			}
		} 
		if(keyupActs.trim().length > 0){		
			jqObj.attr("data-keyup", keyupActs);
			jqObj.bind("keyup",function(event){
				var acts = $(this).attr("data-keyup");
				try{ 
					if(acts.indexOf("maxLengthCheck")>=0){
						maxLengthCheck(jqObj,event);
					}
				}catch(e){
					YT$.log("formInit keyup bind",e);
				}
			});
		}
		
	});	
	$(panel).data("init",null);
}
 
/**
 * 檢測中英文漢子長度并截取
 * 
 * @param obj
 * @param maxLen
 */
function maxLengthCheck(obj,event){
	if(event){
		var code = event.keyCode;
		// Skip alt,tab, chinese , Esc, End, Home, Left, Right, ..
		if (event.altKey || event.shiftKey || code == 9 || code == 13 || code == 27
				|| (code >= 33 && code <= 40)) {
			return;
		}
	}
	var tmpObj = $(obj);
	var value= tmpObj.val();
	if($.browser.msie) {  
		var placeholder=tmpObj.attr("placeholder");
		if(placeholder==value){
			return;
		}
	}
	var free = tmpObj.attr("data-free");
	if(free=="true"){
		return;
	}
	var maxLen= tmpObj.attr("data-maxlength");
	maxLen = maxLen*1;
	var dataType = tmpObj.attr("data-type");
	var dataStyle = tmpObj.attr("data-style");
	if("int"==dataType){
		value=value.replace(/[^0-9]/g,""); 	
		tmpObj.val(value);	
	}
	if("money"==dataType){
		value=value.replace(/[^0-9\.]/g,""); 
		value=value.replace(/\.+/g,"."); 
		tmpObj.val(value);
		value = unfmtAmt(value);
		var left = 11;
		var right = 2;
		// 股票价格需小数点后3位  add ypy 20130909
		if (dataStyle == "stock"){
		    right=3;
		} 
		try{
			var mleft=tmpObj.attr("data-left");
			left=mleft?(1*mleft):left;
		}catch(e){
		}
		try{
			var mright=tmpObj.attr("data-right");
			right=mright?(1*mright):right;
		}catch(e){
		}
		var index=value.indexOf(".");
		if(right>0 && index>=0){
			maxLen = left+right+1;
			var maxLen2 = index+right+1;
			maxLen = Math.min(maxLen,maxLen2);
		}else{
			maxLen=left;
		}
	}
	var w = 0;  
	var perStep = tmpObj.attr("data-perStep");//中文字的字节长度
	var perLen = 2;		
	if(perStep){
		perLen = perStep*1;
	}
	var specdo = tmpObj.attr("data-specdo");
	if(specdo){ 
		//字符是否为汉字:1汉字；0非汉字；
		var pre=0; //上一个字符
		var cur=0; //当前字符
		try{
			var tmpVal = value.replace(/[^\x00-\xff]/g, "$");
			for (var i=0,j=tmpVal.length; i<j; i++) {
			   var c = tmpVal.charAt(i);
			   if(c == '$'){
				  w += perLen; 
				  cur=1;
				  if(pre<cur){//新汉字词组开始
					 w+=2;
				  }
			   }else{
				  w++; 
				  cur=0;
			   }
			   if (w > maxLen) {
				  var val = value.substr(0,i);
				  if(val.length > 0){
					  tmpObj.val(val);
				  }
				  break;
			   }
			   pre=cur;
			}
		}catch(e){
			alert(" specdo maxlength error:"+e);
		}
	}else{
		var tmpVal = value.replace(/[^\x00-\xff]/g, "$");
		for (var i=0,j=tmpVal.length; i<j; i++) {
		   var c = tmpVal.charAt(i);
		   if(c == '$'){
			   w+=perLen;  
		   }else {// 漢字加2
			   w++;
		   }
		   if (w > maxLen) {
			  var val=value.substr(0,i);
			  if(val.length>0){
				  tmpObj.val(val);
			  }
			  break;
		   }    
		}
	}
}

/**
 * TFB汉字检查
 * @param obj
 */
function tfbStringLength(oldLength,obj){
	var tmpObj = $(obj);
	var w = 0;  
	var perStep = tmpObj.attr("data-perStep");//中文字的字节长度
	var perLen = 2;		
	if(perStep){
		perLen = perStep*1;
	}
	var specdo = tmpObj.attr("data-specdo");
	if(specdo){ 
		var value = tmpObj.val();
		//字符是否为汉字:1汉字；0非汉字；
		var pre=0; //上一个字符
		var cur=0; //当前字符
		try{
			var tmpVal = value.replace(/[^\x00-\xff]/g, "$");
			for (var i=0,j=tmpVal.length; i<j; i++) {
			   var c = tmpVal.charAt(i);
			   if(c == '$'){
				  w += perLen; 
				  cur=1;
				  if(pre<cur){//新汉字词组开始
					 w+=2;
				  }
			   }else{
				  w++; 
				  cur=0;
			   }
			   pre=cur;
			}
		}catch(e){
			alert(" specdo maxlength error:"+e);
		}
		oldLength=Math.max(w,oldLength);
	}
	return oldLength;
}

function isCn(c){
	   if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) {
		   return false;
	   }else {
		   return true;
	   }
}

/**
 * 
 * @param str
 * @param targLen
 * @returns
 */
function cutStrByLen(str,targLen){
	if(str && targLen){
		var len = str.length;
		var newStr = str;
		if(len > targLen && targLen > 0){
			newStr = str.substring(0,targLen)+"...";
		}
		return newStr;
	}
	return "";
}

/**
 * 格式化日期
 * 
 * @param oldvalue
 * @returns add by ypy 06/27/2013
 */
function formatDatetime(oldvalue){	
	if(oldvalue == null){
		return "";
	}else if(oldvalue.length == 8){
		return oldvalue.substring(0,4) +
		       "-" + oldvalue.substring(4,6) + 
		       "-" + oldvalue.substring(6,8);
	}else if(oldvalue.length == 14){
		return oldvalue.substring(0,4) +
		       "-" + oldvalue.substring(4,6) + 
		       "-" + oldvalue.substring(6,8) + 		       
		       " " + oldvalue.substring(8,10) + 
		       ":" + oldvalue.substring(10,12) + 
		       ":" + oldvalue.substring(12,14);
	}else if(oldvalue.length == 6){
		return oldvalue.substring(0,2) +
		       ":" + oldvalue.substring(2,4) + 
		       ":" + oldvalue.substring(4,6);
	}else{
		return oldvalue;
	}
}
/**
 * 屏蔽NULL值
 * @param txt
 * @param def
 * @returns
 */
function showValue(txt, def) {
	return txt ? txt : (def ? def : "");
}
