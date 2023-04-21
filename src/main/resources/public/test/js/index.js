/** 全局的正则表达式** */
var glob_effect = "slide";
var glob_rs_c = /#/gi;
var glob_rs_panel = /#panel/gi;
var glob_rs_tab_name = /##tab_name##/gi;
var glob_rs_panel_name = /##panel_name##/gi;
var __serial_no = 1;

function getRandomCode() {
	return __serial_no++;
}
var main_tabs = null;
var main_panels = null;

var stockFlag = true; 
var pInfoFlag = true; 	// 为true 表示首页的个人信息需要被重新加载
var myAcctFlag = true; 	// 为true 表示首页的我的大丰賬戶需要被重新加载
var calendarFlag =true; //金融日历是否要被重新加载
/**
 * 功能切换，会更新会话监听
 * @param conf
 */
function changeTab(conf) {
	YT$.stockPageFlag = false
	// 重置会话监听
	YT$.ressetSessionListener();
	YT$.transTimeout = false;
	YT$.transTimePage=false;	
	if(YT$.transHandle){
		clearTimeout(YT$.transHandle);
	}
	try{
		if(setLevel){
			clearInterval(setLevel);
		}
	}catch(e){		
	}
	var tabs = $("#ui-footer-bar ul.tabs-bottom").find("a");
	var tab = tabs.filter("[href*=\"#" + conf.tab + "\"]");
	var url = conf.url;
	url = url.replace(glob_rs_c, "");
	if (tab && tab.length > 0) {
		tab.click();
		// 加载新页面内容
		var panel = $("#" + conf.tab).find(">div");
		var curUrl = panel.attr("url");
		//if (url == curUrl || conf.tab !== YT$.defaults.tab_ansy) {
		if (conf.tab != YT$.defaults.tab_ansy) {
			if ((conf.tab == "stock") && stockFlag) {
				stockFlag = false;
				YT$.stockPageFlag = true;
				$("#stock").load(url);
			}
			// 判断当前页面是否是否是股票买卖页面  add 10.9
			if (conf.tab == "stock"){
				YT$.stockPageFlag = true;
			}
			// add by liuqing 更新首页的个人信息的头像 以及  我的大丰賬戶
			if (conf.tab == "first") {
				if (pInfoFlag){
					//pInfoFlag = false;
					//userInfoForUpdate();
				}
				if(myAcctFlag){
					//myAcctFlag = false;
					//myAcctForUpdate();
				}
				if(calendarFlag){
					//calendarFlag = false;
					//calendarForUpdate();
				}
				myProdInfoForUpdate();
			}
		} else if (panel.length > 0) {
			panel.attr("url", url);
			ajaxLoad(panel.eq(0), {
				"url" : url,
				"func" : "closeWindow()"
			});
		}
	} else {
		// 创建新的TAB
		var html = $("#template_tab").html();
		html = html.replace(glob_rs_panel, conf.tab);
		html = html.replace(glob_rs_tab_name, "" + conf.tabname);
		main_tabs.append(html).end();

		// 创建新的PANEL
		var main_panels = $("#ui-content");
		html = $("#template_panel").html();
		html = html.replace(glob_rs_panel_name, "" + conf.name);
		html = html.replace(glob_rs_panel, conf.tab);
		main_panels.append(html).end();
		// 设置TAB聚焦
		var index = tabs.length;
		main_tabs.tabs("#ui-content > div", {
			initialIndex : index
		});
		// 加载新页面内容
		var panel = $("#" + conf.tab).find(">div");
		if (panel.length > 0) {
			panel.attr("url", url);
			ajaxLoad(panel.eq(0), {
				"url" : url,
				"func" : "closeWindow()"
			});
		}
		// 重新初始化TABS
		initTabs(index);
	}
}

function changeStyle(theme) {
	$.includeCss(theme);
}

function initTabs(tabIndex) {
	// 初始化footer tabs delete按钮事件
	$("span.ui-corner-all span.ui-icon-delete").each(function(index, domEle) {
		$(this).bind("click", function(e) {
			var dataPanelId = $(this).attr("data-panel");
			var dataTabId = $(this).attr("data-tab");
			$(dataPanelId).remove();
			$(dataTabId).remove();
			// 重新构造，显示首页
			$("#ui-footer-bar ul.tabs-bottom").tabs("#ui-content > div", {
				initialIndex : 0
			});
			e.stopPropagation();
			return false;
		});
	});
}

// 公共的命名空间
var NS$ = {
	ACCT_TYPS : {
		CA : "CA",// 往来
		SA : "SA",// 储蓄
		FS : "FS",// 多币宝
		FD : "FD",// 定期
		MI : "MI",// 理财
		LN : "LN",// 贷款
		CR : "CR",// 信用卡賬戶
		ST : "ST"// 股票賬戶
	},
	AUTH_TYPS : {
		UN : "1",// 普通登录
		SMS : "2",// 短信认证
		TOKEN : "3"// TOKEN
	}
};

//股票命名空间
var STOCK$ = {
	CURRENCY : {
		CURR : "HKD"
	},
	//串流报价时所付金额
	STREAM_FEE : {
		HKD_FEE : "400"
	},
	BUY_SELL : {
		B : "買入",
		S : "賣出"
	},
	ORDER_STATUS : {
		FD : "全數成盤",
		NR : "尚未覆盤"
	},
	DEP_DIRECTION : {
		W : "由股票賬戶轉入關聯存款戶",
		D : "由關聯存款戶轉入股票賬戶"
	},
	SMS_LANGUAGE : {
		EN : "英文",
		TW : "繁體"
	},
	IPO_APPLY : {
		Y : "可申請",
		N : "不可申請",
		S : "暫停申請"
	},
	ACCT_TYPE : {
		A : "資金調撥",
		M : "按金調撥"
	}	
};

/**
 * 获取指定类型的賬號列表
 * <p>
 *var cfg={
 * 	 acctTypes:[NS.CA,NS.SA,NS.FS], -賬號类型列表
 *   callback:function(accounts){
 *     // acounts回传
 *   }
 * }</p>
 * <p>
 * NS$ = {
 ACCT_TYPS : {
 CA : "CA",// 往来
 SA : "SA",// 储蓄
 FS : "FS",// 多币宝
 FD : "FD",// 定期
 MI : "MI",// 理财
 LN : "LN",// 贷款
 CR : "CR",// 信用卡賬戶
 ST : "ST"// 股票賬戶
 }
 };
 * </p>
 * @param cfg
 */
function findAccouts(cfg) {
	// 检查参数
	if (!(cfg.acctTypes && cfg.callback && cfg.acctTypes.length > 0)) {
		YT$.log("index.findAccouts", "参数错误");
		return;
	}
	cfg.count = cfg.acctTypes.length;
	cfg.NEXT_PAGE = (cfg.nextPage ? cfg.nextPage : '0');
	
	// YT$.log("index.findAccouts","accTyps length is "+cfg.count);
	cfg.caches = [];
	var url = ctx + "data/00/CP002Op.do";
	$(cfg.acctTypes).each(function(index) {
		// YT$.log("index.findAccouts","url :"+url);
		var param = {
			"ACCT_TYP" : cfg.acctTypes[index],
			"ACCT_NO" : (cfg.acctNo ? cfg.acctNo : ''),
			"NEXT_PAGE" : (cfg.nextPage ? cfg.nextPage : '0'),
			"NEXT_KEY" : (cfg.nextKey ? cfg.nextKey : '')
		};
		var ajax = new TransAjax();
		ajax.setArgs(cfg);
		ajax.sendPostData(url, JsonToStr(param), function(rpdata) {
			// YT$.log("index.findAccouts","ajax callback2");
			var cfg = ajax.getArgs();
			// YT$.log("index.findAccouts","cfg.count:"+cfg.count);
			cfg.caches[cfg.caches.length] = {
				"ACCT_TYP" : rpdata.ACCT_TYP,
				"LIST" : rpdata.LIST,
				"HAS_NEXT_PAGE" : rpdata.HAS_NEXT_PAGE,
				"NEXT_PAGE" : (rpdata.NEXT_PAGE ? rpdata.NEXT_PAGE : '0')
			};
			cfg.count = cfg.count - 1;
			if (cfg.count == 0) {
				cfg.callback(cfg);
			}
		});
	});
}

/**
 * 更新会话：附属賬號列表
 * @param callback
 */
function updateAcountsSession(callback) {
	// YT$.log("index.generySessionAcccounts", "start");
	var url = ctx + "data/00/CP001Op.do";
	var ajax = new TransAjax();
	ajax.sendPostData(url, JsonToStr({}), callback);
}

/**
 * 获取一次性验证码
 * @param callback
 */
function transOnceSession(callback) {
	// YT$.log("index.transOnceSession", "start");
	var url = ctx + "common/RandomCode.do?method=singleSequnce";
	var ajax = new TransAjax();
	ajax.sendPostData(url, JsonToStr({}), callback);
}

/**
 * 合并CP001返回的不同类型的数据
 * @param callback
 */
function findAccoutsByTyps(cfg){
	var tempArr = [];
	if(cfg && cfg.acctTypes.length > 0) {
		for(var i = 0; i < cfg.acctTypes.length; i++){
			var acctTyp = cfg.acctTypes[i];
			var accts = USER_ACCTS[acctTyp];
			if(accts && accts.length){
				for(var j = 0; j < accts.length; j++){
					tempArr[tempArr.length] = accts[j];
				}
			}
		}
	}
	sortAcctList(tempArr);
	cfg.callback(tempArr);
}

/**
 * 获取单賬號的明细（幣別和金额   返回的是一个LIST）
 * @param acctNo  
 * @param acctType
 */
function loadAcctInfo(cfg){
	var url = ctx + "data/00/CP003Op.do";
	var ajax = new TransAjax();
	if(cfg){
		ajax.sendPostData(url, JsonToStr(cfg.reqJson), function(rpdata){
			//解决币种获取后，还需设置默认值问题
			if(cfg.callback instanceof Array){
				for(var i=0; i<cfg.callback.length;i++){
					cfg.callback[i](cfg,rpdata.LIST);
				}
			}else{
				cfg.callback(cfg, rpdata.LIST);
			}
		});
	}
}

/**
 * 加载缓存中的賬號信息（含类型\別名\狀態等   返回的是一个JSON）
 * @param acctNo
 * @param acctTyp
 * @returns
 */
function acctCacheInfo(acctNo,acctTyp){
	var accts = USER_ACCTS[acctTyp];
	if(accts && accts.length){
		for(var j = 0; j < accts.length; j++){
			var acct=accts[j];
			if(acct.ACCT_NO==acctNo){
				return acct;
			}
		}
	}
}

// 点击卡号  进入网上明细查询页面	
function webTransQuery(acctNo){
	var url = 'forward/PP03023.do?pid=PP03023&P_ACCT_NO='+acctNo;
	YT$.gotoPage(ctx + url);
}
// 进入贷款还款明细
function payFees(acctNo){
	var url = 'forward/PP08017.do?pid=PP03001&lend_acct='+acctNo;
	YT$.gotoPage(ctx + url);
}
// 进入贷款详情
function transferAcc(acctNo){
	var url = 'forward/PP08016.do?pid=PP03001&lend_acct='+acctNo;
	YT$.gotoPage(ctx + url);
}
// 进入缴费
function indexPayFees(acctNo){
	var url = 'forward/PP05002.do?pid=PP05002&P_ACCT_NO='+acctNo;
	YT$.gotoPage(ctx + url);
}
function creditCardPayment(acctNo){
	var url = 'forward/PP05002.do?pid=PP05002&P_ACCT_NO='+acctNo;
	YT$.gotoPage(ctx + url);
}
//进入帳戶一覽查看明細
function indexAccountDetail(){
	var url = 'forward/PP03001.do?pid=PP03001';
	YT$.gotoPage(ctx + url);
}
// 进入外汇买卖
function indexBuySell(acctNo){
	var url = 'forward/PP06004.do?pid=PP06004&P_ACCT_NO='+acctNo;
	YT$.gotoPage(ctx + url);
}

//定期新造
function creaNewFD(acctno){
	YT$.gotoPage(ctx+"forward/PP06016.do?pid=PP06016&acctno="+acctno);
}

//定期報失
function reportLoss(acctno,certnum){
	YT$.gotoPage(ctx+"forward/PP06066.do?pid=PP06066&acctno="+acctno+"&certnum="+certnum);
}

//定期提取
function extractionRegular(vchr_no,acctNo){
	YT$.gotoPage(ctx+"forward/PP06022.do?pid=PP06022&vchr_no="+vchr_no+"&acctno="+acctNo);
}

function changeRenewalStyle(data,num){
	if(num==1){
		data=eval('('+data.replace(/\@/g,"\"")+')');
		data["CERT_NO"]=data["CERT_NUM"];
		data["IN_CURR"]=data["CCY_DESC"];
		data=JsonToStr(data);
		data=data.replace(/\"/g,"@");
	}
	YT$.gotoPage(ctx+"forward/PP06030.do?pid=PP06030&data="+data);
}


//股票买卖
function stockBuySell(acctno){
	YT$.gotoPage(ctx+"stock/overview.do?pid=stock&acctno="+acctno);
}

//投资组合
function showAllPartC(acctno){
	YT$.gotoPage(ctx+"stock/overview.do?pid=stock&flagC=all&acctno="+acctno);
}


//资金调动
function fundsShift(acctno){
	YT$.gotoPage(ctx+"stock/PP12027.do?pid=PP12027&acctno="+acctno);
}

// 同名户转账
function sameNameTransfer(acctno){
	if(acctno && acctno.length==14){
		acctno="000"+acctno;//00010110123894
	}
	YT$.gotoPage(ctx+"forward/PP04011.do?pid=PP04011&P_ACCT_NO="+acctno);
}

/*// 贷款还款明细
function loanRepayDetail(acctno){
	var url = 'forward/PP08016.do?pid=PP03001&lend_acct='+acctNo;
	YT$.gotoPage(ctx + url);
}*/
//进入定期
function loanRepayDing(acctNo){
	YT$.gotoPage(ctx+"forward/PP06028.do?pid=PP06028&acctno="+acctNo);
}

//更新首页的头像
function userInfoForUpdate(){
	var dataLink = $("#userInfoForUpdate").attr("data-Link");
	var panel = $("#userInfoForUpdate").find(".portlet-content");
	
	if (panel && dataLink) {
		panel.load(dataLink);
	}
}

//更新首页的头像
function calendarForUpdate(){
	var dataLink = $("#calendarForUpdate").attr("data-Link");
	var panel = $("#calendarForUpdate").find(".portlet-content");
	
	if (panel && dataLink) {
		panel.load(dataLink);
	}
}

//更新首页我的大丰賬戶
function myAcctForUpdate(){
	var dataLink = $("#myAcctForUpdate").attr("data-Link");
	var panel = $("#myAcctForUpdate").find(".portlet-content");
	
	if (panel && dataLink) {
		panel.load(dataLink);
	}
}

//更新首页产品资讯
function myProdInfoForUpdate(){
	var dataLink = $("#prodInfoForUpdate").attr("data-Link");
	var panel = $("#prodInfoForUpdate").find(".portlet-content");
	
	if (panel && dataLink) {
		panel.load(dataLink);
	}
}

/**
 * 香港身份證為1(字母)˜2字母或者数字+6位數字+(1位數字或字符)
 */
function checkIdtHK(s){
	return /^[a-zA-Z]{1}[a-zA-Z0-9]{1}\d{5}\([a-zA-Z0-9]{1}\)$/.test(s);
}

/**
 * 澳門身份證為：7位數字+（一位數字）澳门特別行政区身份证
 */
function checkIdtMO(s){
	return /^(\d){7}\(\d\)$/.test(s);
}


/**
 * 澳门居民身份证
 * @param s
 */
function checkIdtMacao(s){
	return /^[137]\/\d{6}\/[0-9]$/.test(s);
}

/**
 * 大陸居民身份證
 */
var Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ];// 加权因子   
var ValideCode = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ];// 身份证验证位值.10代表X   

function IdCardValidate(idCard) {   
    idCard = trim(idCard.replace(/ /g, ""));   
    if (idCard.length == 15) {
        return /^\d{14}\w{1}$/.test(idCard);
    } else if (idCard.length == 18) {   
        var a_idCard = idCard.split("");// 得到身份证数组   
        if(isTrueValidateCodeBy18IdCard(a_idCard)){   
            return true;   
        }else {   
            return false;   
        }   
    } else {   
        return false;   
    }   
}   
/**  
 * 判断身份证号码为18位时最后的验证位是否正确  
 * @param a_idCard 身份证号码数组  
 * @return  
 */  
function isTrueValidateCodeBy18IdCard(a_idCard) {   
    var sum = 0; // 声明加权求和变量   
    if (a_idCard[17].toLowerCase() == 'x') {   
        a_idCard[17] = 10;// 将最后位为x的验证码替换为10方便后续操作   
    }   
    for ( var i = 0; i < 17; i++) {   
        sum += Wi[i] * a_idCard[i];// 加权求和   
    }   
    valCodePosition = sum % 11;// 得到验证码所位置   
    if (a_idCard[17] == ValideCode[valCodePosition]) {   
        return true;   
    } else {   
        return false;   
    }   
}

/**  
 * 通过身份证判断是男是女  
 * @param idCard 15/18位身份证号码   
 * @return 'F'-女、'M'-男  
 */  
function maleOrFemalByIdCard(idCard){   
    idCard = trim(idCard.replace(/ /g, ""));// 对身份证号码做处理。包括字符间有空格。   
    if(idCard.length==15){   
        if(idCard.substring(14,15)%2==0){   
            return 'F';   
        }else{   
            return 'M';   
        }   
    }else if(idCard.length ==18){   
        if(idCard.substring(14,17)%2==0){   
            return 'F';   
        }else{   
            return 'M';   
        }   
    }else{   
        return null;   
    }   
}

 /**  
  * 獲取18位身份證的出生日期
  */  
function getBrithByIdCard(idCard){
	idCard = idCard+"";
	if(idCard.length == 18){
	    var year =  idCard.substring(6,10);   
	    var month = idCard.substring(10,12);   
	    var day = idCard.substring(12,14);  
		return year+"-"+month+"-"+day;
	}else if(idCard.length == 15){
		  var year =  idCard.substring(6,8);   
	      var month = idCard.substring(8,10);   
	      var day = idCard.substring(10,12);   
		  return  "19"+year+"-"+month+"-"+day;
	}
	 return null;
}   

//去掉字符串头尾空格   
function trim(str) {   
    return str.replace(/(^\s*)|(\s*$)/g, "");   
} 

/**
 *對于電話號碼的限制：+853為8位，+86最高為11位
 */
function checkMobileNo4tfb(s){
	return (/^(\+853){0,1}(\d){8}$/.test(s))||(/^(\+86){0,1}(\d){11}$/.test(s));
}

/**
 * 賬號显示保护
 * @param acctno
 * @returns
 */
function fmtAcctNoIndex(acctno){
	if(acctno){
		if(acctno.length ==13){
			acctno = acctno.substring(0,6)+"***"+acctno.substring(9);
		}
		if(acctno.length ==16){
			acctno = acctno.substring(0,10)+"***"+acctno.substring(13);
		}
	}	
	return acctno;
}

/**
 * 賬號排序
 * @param acctList
 */
function sortAcctList(acctList){
	acctList.sort(function(a,b){
		var aAcNo=a.ACCT_NO;
		var bAcNo=b.ACCT_NO; 
		aAcNo=fmtAcctSort(aAcNo);
		bAcNo=fmtAcctSort(bAcNo); 
		//alert("aAcNo: "+ aAcNo +"  \nbAcNo: " +bAcNo + " \naAcNo>bAcNo rst: "+(aAcNo>bAcNo));
		return aAcNo>bAcNo||-1;
	});
}

/**
 * 更新利率
 */
function updateRate(){
	if(form_rate){
		form_rate.action = ctx+"common/rate.do?v="+(new Date().getTime());
		form_rate.submit();
	}
}

function logout(){
	location.href=ctx+"login.do";
}

/**
 * 更新SSID
 */
function updateSsid(){
	//if(SSID && form_ssid){
	//	form_ssid.action = ctx+"common/ssid.do?v="+(new Date().getTime());
	//	form_ssid.submit();
	//}
} 

/**
 * 賬號狀態
 * @param acct
 * @returns {String}
 */
function showAcctStatus(acct,lagg){
	if(acct){ 
		return acct.ACCT_STATUS_DESC;
	}
	return '';
}

var sysList="";
function initSysPoint(){
	// 初始化系统公告
	var url = ctx+"data/01/sysPoint.do";
	var ajax = new TransAjax();
	ajax.sendPostData(url, JsonToStr({}), function(rpdata) {
		var sysPoint_div=$(".sysPoint_div");
		if(rpdata.STATUS=="1"){
			sysPoint_div.removeClass("hiden");
			sysList=rpdata.LIST;
			var _height=$(window).height()-sysPoint_div.height()-10;
			sysPoint_div.animate({top:_height+"px"},1500);
			sysPoint_div.find(".sysPoint_div_menu_left").html(sysList[0].NOTE_TITLE);
			sysPoint_div.find(".sysPoint_div_body span").html(sysList[0].NOTE_CONTENT);
			var li_buttons=sysPoint_div.find("li");
				li_buttons.filter("[data-role='minimize']").bind("click",function(){
				var this_li=$(this);
				var param=sysPoint_div.data("mark");
				var init=sysPoint_div.data("init");
				if(init && init=="true"){
					return;
				}
				sysPoint_div.data("init","true");
				if(param && param=="R"){
					sysPoint_div.data("mark","");
					var _height=$(window).height()-sysPoint_div.height()-10;
					sysPoint_div.css("z-index","101");
					sysPoint_div.animate({top:_height+"px"},1500,function(){
						this_li.attr("class","minimize");
						this_li.attr("title","最小化");
						sysPoint_div.data("init","");
					});
				}else{
					sysPoint_div.data("mark","R");
					var sysmeg=sysPoint_div.find(".sysPoint_div_message");
					var _height=$(window).height()-($(sysPoint_div).height()-$(sysmeg).height());
					sysPoint_div.css("z-index","0");
					sysPoint_div.animate({top:_height+"px"},1500,function(){
						this_li.attr("class","maximize");
						this_li.attr("title","還原");
						sysPoint_div.data("init","");
					});
				}
			});
			li_buttons.filter("[data-role='close']").bind("click",function(){
				var _height=$(window).height()+sysPoint_div.height()+20;
				sysPoint_div.animate({top:_height+"px"},1500,function(){
					sysPoint_div.remove();
				});
			});
		}else{
			sysPoint_div.remove();
		}
	});
}

/**
 * 取ifame
 * @param ifm
 * @returns
 */
function frameWindow(ifm){
	if ($.browser.mozilla) {
		return ifm.contentWindow;
	}
	return ifm;
}

var sysList="";
function initUserBirthday(){
	var ajax = new TransAjax();
	var url = ctx+"data/01/userBirthday.do";
	ajax.sendPostData(url, JsonToStr({}), function(rpdata) {
		if(rpdata.STATUS=='1'){//查询正确
			sysList=rpdata;
			showWindow({
				width : 455,
				height : 215,
				title : "生日祝福",
				url : "inner/PP02001.do"
			});
		}
	});
}