var trimRe = /^\s+|\s+$/g;
var decimalRe = /,/g;

String.prototype.trim = function() {
	return function() {
		return this.replace(trimRe, "");
	};
}();

String.prototype.decimal = function() {
	return function() {
		return this.replace(decimalRe, "");
	};
}();

/**
 * 在原字符串前面补充字符 3,0==>000XX
 */
function lpadStr(str, len, def) { 
	var tlen = len - str.length; 
	tlen = Math.max(tlen, 0);
	def = def ? def : "0";
	var pad = "";
	for ( var i = 0; i < tlen; i++) {
		pad = pad + def;
	}
	var tmp = pad + str;
	return tmp;
}

/**
 * 在原字符串后面补充字符 3,0==>XX000
 */
function rpadStr(str, len, def) { 
	var tlen = len - str.length; 
	tlen = Math.max(tlen, 0);
	def = def ? def : "0";
	var pad = "";
	for ( var i = 0; i < tlen; i++) {
		pad = pad + def;
	}
	var tmp = str + pad;
	return tmp;
}

function g(o) {
	return document.getElementById(o);
}

function gotoPage(form, pageNo) {
	form.pageNo.value = pageNo;
	showWaitPanel('');
	setTimeout(form.name + ".submit()", 100);
}

function loadPage(divId, url) {
	showWaitPanel('');
	_loadPage(divId, url, "closeWindow()");
}

function ajaxLoad(dom, conf) {
	var elem = $(dom);
	if (elem.length == 0) {
		showErrorMsg([ "对象", elem.id, "不存在!" ].join(""));
		return;
	}
	elem.html(MSG$.LOADING);
	elem.ajaxError(function(event, request, settings) {
		$(this).html("出错页面:" + settings.url);
		closeWindow();
	}); 
	elem.load(conf.url,{},function(){
		setTimeout(conf.func, speed);
	});
}

var speed = 500;
function _loadPage(divId, url, func, errMsg) {
	var elem = $("#" + divId);
	if (elem.length == 0) {
		showErrorMsg([ "对象", divId, "不存在!" ].join(""));
		return;
	}
	elem.html(MSG$.LOADING);
	elem.ajaxError(function(event, request, settings) {
		$(this).html("出错页面:" + settings.url);
		closeWindow();
	});
	elem.ajaxSuccess(function(evt, request, settings) {
		setTimeout(func, speed);
	});
	elem.load(conf.url,{},function(){
		setTimeout(func, speed);
	});
}

var isIe = (document.all) ? true : false;
function setSelectState(state) {
	var objs = document.getElementsByTagName('select');
	for ( var i = 0, j = objs.length; i < j; i++) {
		objs[i].style.visibility = state;
	}
}

// 显示对话框
function showDialog(url, w, h) {
	w = w ? w : "580";
	h = h ? h : "530";
	window.showModalDialog(url, window, "dialogWidth:" + w + "px;dialogHeight:"
			+ h + "px;help:no;scroll:no; status:no; center:yes");
}

// 等待信息提示
function showWaitPanel(s) {
	var msg = s == null || s.trim() == '' ? '' : s + "，";
	var messContent = "<div id='YT_MSG'><div id='YT_LOAD_MSG' class='loading'>"
			+ msg + MSG$.LOADING +"</div></div>";
	showMessageBox(messContent, 200, 150);
}
// 更新系統等待信息
function updateWaitPanel(s) {
	var msgDiv = g("YT_LOAD_MSG");
	if (msgDiv) {
		msgDiv.innerHTML = s;
	} else {
		showWaitPanel(s);
	}
}
// 隐藏消息框
function clearWaitPanel(){
	closeWindow();
}
// 
/**
 * 提示信息
 *  showMsgPanel("交易成功！","系统提示");
 *  var cfgs={
 *  	funcOk:"alert(' success ');closeWindow();",
 *  	funcCancle:"alert(' failure ');"
 *  };
 *  showMsgPanel("交易成功！","系统提示",cfgs);
 * @param msg
 * @param title
 * @param confs
 */
function showMsgPanel(msg, title, confs) {
	var okFunc = confs && confs.funcOk ? confs.funcOk : "closeWindow();";
	_showMsgPanel(msg, title, okFunc, confs);
}
/**
 * 对于有控件的提示框  隐藏输入
 * @param msg
 * @param title
 * @param confs
 */
function showMsgPanelforbid(msg, title, confs) {
	var okFunc = confs && confs.funcOk ? confs.funcOk : "closeWindow(null,null,'1');";
	_showMsgPanel(msg, title, okFunc, confs);
}

/**
 * 提示错误信息，方字将用红字字体显示
 *  showMsgPanel("交易成功！","系统提示");
 *  var cfgs={
 *  	funcOk:"alert(' success ');closeWindow();",
 *  	funcCancle:"alert(' failure ');"
 *  };
 *  showMsgPanel("交易成功！","系统提示",cfgs);
 * @param msg
 * @param title
 * @param confs
 */
function showErrorMsg(msg, title, confs) {
	msg = "<span class='FontRed'>" + msg + "</span>";
	var okFunc = confs && confs.funcOk ? confs.funcOk : "closeWindow();";
	_showMsgPanel(msg, title, okFunc, confs);
}
// 提示确定窗口
function showConfirm(msg, title, funcOk, funcCancle) {
	_showMsgPanel(msg, title, funcOk, {
		"funcCancle" : funcCancle
	});
}

function _showMsgPanel(msg, title, func, confs) {
	var defW = 300;
	var defH = 180;
	var w = confs && confs.width ? confs.width : defW;
	var h = confs && confs.height ? confs.height : defH;
	w = w < defW ? defW : w;
	h = h < defH ? defH : h;
	var sb = [];
	sb[sb.length] = '<table width="' + w + '" height="' + h  + '" cellpadding="0" cellspacing="0" style="background:#fff;border: 1px solid #aaa;box-shadow: 5px 5px 5px rgba(0,0,0,0.5); font-size:14px;" >';
	sb[sb.length] = '<tr><td height="40" style="cursor:move;" valign="top"><div align="left" class="panel-header_msg">';
	sb[sb.length] = title ? title : MSG$.TITLE_ALETT;
	sb[sb.length] = '</div><div style="height: 3px;line-height: 3px;background-color: rgb(5, 65, 119);"><div style="background-color: rgb(255, 203, 0); width: 100px;height: 3px;line-height: 3px"></div></div></td></tr>';
	sb[sb.length] = '<tr><td valign="middle" align="left"><div style="padding:5px 5px 10px 5px;text-indent:2em;">' + msg + '</div></td></tr>';
	sb[sb.length] = '<tr><td height="40" align="center">';
	sb[sb.length] = '<button id="YT_MSG_BTN_OK" style="line-height:25px;padding:0px 15px;margin:0px;font-size:14px;" class="ui-corner-all">'+MSG$.BTN_OK+'</button>';
	if (confs && confs.funcCancle) {
		sb[sb.length] = '<button id="YT_MSG_BTN_CANCLE" style="line-height:25px;padding:0px 15px;margin:0px;font-size:14px;" class="ui-corner-all">'+MSG$.BTN_CANCLE+'</button>';
	}
	sb[sb.length] = '</td></tr></table>';
	var msgDiv = g("YT_MSG");
	if (msgDiv) {
		$(msgDiv).html(sb.join(""));
	} else {
		var messContent = "<div id='YT_MSG'>" + sb.join("") + "</div>";
		showMessageBox(messContent, w, h + 160);
	}
	var btnOk = g("YT_MSG_BTN_OK");
	if (btnOk) {
		btnOk.focus();
		btnOk.onkeydown = function() {
			var event = window.event;
			if (event.keyCode == 9) {
				event.returnValue = false;
				event.keyCode = 0;
			}
		};
		btnOk.onclick = function() {
			eval(func);
		};
	}
	var btnCancle = g("YT_MSG_BTN_CANCLE");
	if (btnCancle) {
		btnCancle.onclick = function() {
			eval(confs.funcCancle);
		};
	}
}
// 弹出消息框
function showMessageBox(content, dialogWidth, dialogHeight, bgColor) {
	_forbid = true;
	closeWindow(null, null, true);
	hideOcxArea();
	_forbid = false;
	
	if (isIe) {
		setSelectState('hidden');
	} 	
	// 生成操作屏蔽层
	var body = $(window);
	var bodyWidth = body.width();
	var bodyHeight = body.height();
	createForbidLayer("backDivX", bodyWidth, bodyHeight,null,bgColor);
	// 居中定位
	var left = hCenterPosition(dialogWidth);
	var ch = YT$.clientHeight;
	var top = ch?((ch - dialogHeight) / 2): vCenterPosition(dialogHeight);  
	top = top < 30 ? 30 : top;
	// 生成消息框
	createMessageWindow(content, left, top, dialogWidth);
}

// 水平居中定位
function hCenterPosition(dialogWidth){
	var body = $(window);
	var bodyLeft = body.scrollLeft();
	var bodyWidth = body.width();
	if (window.dialogArguments) {
		bodyLeft = 0;
		bodyWidth = parseInt(dialogWidth);
	}
	return (bodyLeft + (bodyWidth - dialogWidth) / 2);
}

// 垂直居中定位
function vCenterPosition(dialogHeight){
	var body = $(window); 
	var bodyHeight = body.height(); 
	// 定位
	var top = (bodyHeight - dialogHeight) / 2; 
	top = top < 30 ? 30 : top;
	return top;
}

function createForbidLayer(divId, bWidth, bHeight,zIndex,bgColor) {
	var zIndex = zIndex ? zIndex : 125;
	var panel = document.createElement("div");
	panel.id = divId;
	var bgColor=bgColor?bgColor:"#000";
	var sb = [];
	sb[sb.length] = 'top:0px;left:0px;z-index:'+zIndex+';position: fixed;overflow:visible;background:'+bgColor+';';
	sb[sb.length] = 'width:100%;';
	sb[sb.length] = 'height:100%;';
	sb[sb.length] = (isIe) ? 'filter:alpha(opacity=0);' : 'opacity:0;';

	panel.style.cssText = sb.join("");
	document.body.appendChild(panel);
	showBackground(panel, 50);
	return panel;
}

function createMessageWindow(content, left, top, wWidth, divId) {
	divId = divId ? divId : "mesWindow";
	var mesW = document.createElement("div");
	mesW.id = divId;
	mesW.className = divId;

	var sb = [];
	sb[sb.length] = '<div id="mesWindowContent">';
	sb[sb.length] = content;
	sb[sb.length] = '</div>';
	mesW.innerHTML = sb.join("");

	sb = [];
	sb[sb.length] = 'left:' + left + 'px;';
	sb[sb.length] = 'top:' + top + 'px;';
	sb[sb.length] = 'z-index:126;position: fixed;width:' + wWidth + 'px;';
	mesW.style.cssText = sb.join("");
	document.body.appendChild(mesW);
	$("#mesWindowContent").draggable();
}

// 让背景渐渐变暗
function showBackground(obj, endInt) {
	$(obj).fadeTo(speed, 0.3);
}

var _forbid = false;
// 关闭窗口
function closeWindow(containId, panelId ,forbid) {
	containId = containId ? containId : "backDivX";
	var backDivX = g(containId);
	if (backDivX != null) {
		backDivX.parentNode.removeChild(backDivX);
	}
	panelId = panelId ? panelId : "mesWindow";
	var mesWindow = g(panelId);
	if (mesWindow != null) {
		mesWindow.parentNode.removeChild(mesWindow);
	}
	if (isIe) {
		setSelectState('');
	}
	// 密碼控件显示
	if(forbid=="1"){
		hideOcxArea();
	}else{
		if(!_forbid){
			showOcxArea();
		}	
	}
	
}

function showOcxArea(){
	try{
		$(".resetPwdOcx_blank").hide();
	}catch(e){}
	try{
		if(showStreamPageOT)showStreamPageOT();
	}catch(e){}	
	try{
		if($.browser.safari){
			$("iframe").contents().find("div").show(); 
		}
	}catch(e){
		alert(e);
	}	
}

function hideOcxArea(){
	try{
		$(".resetPwdOcx_blank").show();
	}catch(e){}
	try{
		if(hiddenStreamPageOT)hiddenStreamPageOT();
	}catch(e){}
	try{
		if($.browser.safari){
			$("iframe").contents().find("div").hide(); 
		}
	}catch(e){
		alert(e);
	}	
}

function hideWindow(pid) {
	var win = $(pid ? pid : "#win_panel");
	try{
		var p = win.position();
		var toTop = p.top;
		var toLeft = p.left;
		var width = win.width();
		var height = win.height();
		win.attr({
			"open" : false
		}).animate({
			left : toLeft + (width / 4),
			top : toTop + (height / 4),
			width : (width / 2),
			height : (height / 2),
			"opacity":0
		},{
			duration : 'fast',
			easing : 'easeInOutSine'
		});
		win.find(".panel-body").html(""); 
	}catch(e){}
	
	setTimeout(function(){
		closeWindow('backDivX2');
		win.stop().parent().hide();
	},200);
 
	showOcxArea();
}

function showWindow(cfg) {
	// 生成操作屏蔽层
	var body = $(window);
	var bodyWidth = body.width();
	var bodyHeight = body.height();
	closeWindow('backDivX2');
	createForbidLayer("backDivX2", bodyWidth, bodyHeight, 119, "#000");
	
	var win = $("#win_panel");
	var open = win.attr("open");
	if (open) {
		return;
	}
	win.attr({
		"open" : true
	});
	win.stop().parent().show();  
	
	var cfg = cfg ? cfg : {};
	var title = cfg.title ? cfg.title : "Modal Window" ;
	if (title){
		win.find("div.panel-title").text(title);
	}
	var width = cfg.width ? cfg.width : 650;
	var height = cfg.height ? cfg.height : 350;
	var url = cfg.url;
	var toLeft = hCenterPosition(width);
	var ch = YT$.clientHeight;
	var toTop = ch?((ch - height) / 2):vCenterPosition(height);
	topTop = Math.max(toTop,30);
	win.css({
		left : toLeft + (width / 4),
		top : toTop + (height / 4),
		width : (width / 2),
		height : (height / 2),
		"opacity":0
	}).show(); 
	var panel = win.find(".panel-body");
	win.stop().parent().show();	
	win.animate({
		"opacity":1,
		left : toLeft,
		top : toTop,
		width : width,
		height : height
	}, {
		duration : 'fast',
		easing : 'easeInOutSine',
		complete : function(){
			panel.height(height-50);
			if (url) {
				panel.html(MSG$.LOADING);
				panel.hide().load(url).show();
			}
		}
	});
	win.find("div.panel-tool a").bind("click", function() {
		var role = $(this).attr("data-role");
		if (role == "close") {
			var win = $("#win_panel");
			win.attr({
				"open" : false
			}).animate({
				left : toLeft + (width / 4),
				top : toTop + (height / 4),
				width : (width / 2),
				height : (height / 2),
				"opacity":0
			},{
				duration : 'fast',
				easing : 'easeInOutSine'
			});
			setTimeout(function(){
				closeWindow('backDivX2');
				win.stop().parent().hide().css({left:0,top:0});
			},200);
			win.find(".panel-body").html("");
			
		}else{
			alert(role);
		}
	}); 
}

// 换肤
$.extend({
	includeCss : function(file) {
		var files = (typeof file == "string") ? [ file ] : file;
		$.each(files, function(index, css_href) { 
			var csstag = document.createElement("link");
			csstag.setAttribute('type', 'text/css');
			csstag.setAttribute('rel', 'stylesheet');
			csstag.setAttribute('href', css_href);
			$("head")[0].appendChild(csstag);
		});
	}
}); 

//获取指定名称的cookie的值
function getCookie(objName){
	var arrStr = document.cookie.split("; ");  

	for(var i = 0;i < arrStr.length;i ++){  
		var temp = arrStr[i].split("=");  
		if(temp[0] == objName) return unescape(temp[1]);  
	}   
}  
//新建cookie
function setCookie(name,value) {  //两个参数，一个是cookie的名子，一个是值  
	var Days = 30; //此 cookie 将被保存 30 天  
	var exp = new Date();    //new Date("December 31, 9998");  
	exp.setTime(exp.getTime() + Days*24*60*60*1000);  
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();  
 }  

//设置步骤栏：步进值
function changeStepBar(panel, stepIndex) {
	var steps=panel.find("div.stepbar ul li");
	
	panel.find("div.stepbar li").each(function(index) {
		if(index==steps.length-1 && stepIndex == steps.length){
			$(this).removeClass("last-step").addClass("over-last-step");
		}else if(index==steps.length-1){
			$(this).addClass("last-step");
		}else if(index < stepIndex){
			$(this).addClass("over-step");
		}else{
			$(this).removeClass("over-step");
			$(this).removeClass("over-last-step");
		}
	});
	//設置頁面編號
	panel.find(".navbar .pageNo >span").text(stepIndex);
}

/**
 * 个人贷款的步骤 
 * 区別于上面步骤
 */
function changeStep(panel, stepIndex){
	var steps=panel.find("div.stepbar ul li");
	panel.find("div.stepbar li").each(function(index) {
		index=index+1;
		if(index<=stepIndex){
			$(this).addClass("over-step");
		}else{
			$(this).removeClass("over-step");
		}
	});
	//設置頁面編號
	panel.find(".navbar .pageNo >span").text(stepIndex);
}

// 设置页面显示区域
function chagePageState(panel, index) {
	var pages = panel.find("div.pagebar > div");
	pages.filter(".current").removeClass("current").hide();
	pages.eq(index - 1).addClass("current").show();
}

function checkRepetition(panel,d){
	var grade_class1="";
	var grade_class2="";
	var grade_class3="";
	
	var h = 0;
	var i = 0;
	var t = 0;
	var x = 0;
	var j = 0;
	var u = 0;
	var k = 0;
	var n = 0;
	var l = 0;
	var r = 0;
	var y = 0;
	var z = 0;
	var v = 0;
	var S = "";
	var T = "";
	var U = "";
	var bk = "abcdefghijklmnopqrstuvwxyz";
	var bk2 = "zyxwvutsrqponmlkjihgfedcba";
	var bl = "01234567890";
	var bl2 = "09876543210";

	if (document.all) {
		var bp = 0;
	} else {
		var bp = 1;
	}
	if (d) {
		h = parseInt(d.length * 4);   //  密碼總字數得分  （次數*4）
		i = d.length;   //密碼長度
		var bq = d.replace(/\s+/g, "").split(/\s*/);
		var br = bq.length;
		for (var a = 0; a < br; a++) {
			if (bq[a].match(new RegExp(/[A-Z]/g))) {   //大寫英文字母數
				if (S !== "") {
					if ((S + 1) == a) {
						t++;
						x++;
					}
				}
				S = a;
				j++;
			}else if (bq[a].match(new RegExp(/[a-z]/g))) {   //小寫英文字母數
				if (T !== "") {
					if ((T + 1) == a) {
						u++;
						x++;
					}
				}
				T = a;
				k++;
			}else if (bq[a].match(new RegExp(/[0-9]/g))) {   //数字出现次数
				if (a > 0 && a < (br - 1)) {
					n++;
				}
				if (U !== "") {
					if ((U + 1) == a) {
						v++;
						x++;
					}
				}
				U = a;
				l++;
			}
			for (var b = 0; b < br; b++) {
				if (bq[a].toLowerCase() == bq[b].toLowerCase() && a != b) {
					r++;
				}
			}
		}
		for (var s = 0; s < 23; s++) {
			var bs = bk.substring(s, parseInt(s + 3));
			var bs2 = bk2.substring(s, parseInt(s + 3));
			//var bt = bs.strReverse();
			if (d.toLowerCase().indexOf(bs) != -1 || d.toLowerCase().indexOf(bs2) != -1) {
				y++;
			}
		}

	}
	
	//数字连续次数  123
	for (var s = 0; s < 8; s++) {
		var bs = bl.substring(s, parseInt(s + 3));
		var bs2 = bl2.substring(s, parseInt(s + 3));
		//var bt = bs.strReverse();
		if (d.toLowerCase().indexOf(bs) != -1 || d.toLowerCase().indexOf(bs2) != -1) {
			z++;
		}
	}

	if (j > 0 && j < i) {   //大寫英文字母數   +((長度-次數)*2)
		h = parseInt(h + ((i - j) * 2));
	}
	if (k > 0 && k < i) {   // 	小寫英文字母數  +((長度-次數)*2)
		h = parseInt(h + ((i - k) * 2));
	}
	if (l > 0 && l < i) {   //數字出現次數  +(次數*2)
		h = parseInt(h + (l * 2));
	}
	if (n > 0) {   //密碼的中間部份出現數字  +(次數*2)
		h = parseInt(h + (n * 2));
	}

	//（得分）
	if ((k > 0 || j > 0) && l === 0) {  //只有英文字母    -次數
		h = parseInt(h - i);
	}
	if (k === 0 && j === 0 && l > 0) {  //只有數字    -次數
		h = parseInt(h - i);
	}

	if (v > 0) {   //數字依序出現（例如13）    -次數*2
		h = parseInt(h - (v * 2));
	}

	//三次以上(得分)
	if (y > 0) {   //字母依順序出現 (三個以上，例如abc）   -次數*3
		h = parseInt(h - (y * 3));
		bi = "- " + parseInt(y * 3);
	}
	if (z > 0) {   //數字依順序出現 (三個以上，例如123）    -次數*3
		h = parseInt(h - z*3);
	}
	
	if (d==null||d==''){ 
		grade_class1=grade_class2=grade_class3="grade_span";
	} else{ 
		S_level=checkStrong(h); 
		switch(S_level) { 
			case 1: 
				grade_class1="grade_span1";
				grade_class2=grade_class3="";
			break; 
			case 2: 
				grade_class1=grade_class2="grade_span2";
				grade_class3="";
				break; 
			default: 
				grade_class1=grade_class2=grade_class3="grade_span3";
		} 
	}
	
	panel.find(".grade_div span:eq(0)").attr("class",grade_class1);
	panel.find(".grade_div span:eq(1)").attr("class",grade_class2);
	panel.find(".grade_div span:eq(2)").attr("class",grade_class3);
}


//根據得分 判斷密碼強的付 
function checkStrong(h){ 
	var strong=0;
	if(h<40){
		strong = 1;
	}else if(h < 80){
		strong = 2;
	}else{
		strong = 3;
	}
	return strong; 
}

/*var formatJson = function(json, options) {
	var reg = null, formatted = '', pad = 0, PADDING = '    '; // one can also use '\t' or a different number of spaces

	// optional settings
	options = options || {};
	// remove newline where '{' or '[' follows ':'
	options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true) ? true
			: false;
	// use a space after a colon
	options.spaceAfterColon = (options.spaceAfterColon === false) ? false : true;

	// begin formatting...
	if (typeof json !== 'string') {
		// make sure we start with the JSON as a string
		json = JSON.stringify(json);
	} else {
		// is already a string, so parse and re-stringify in order to remove extra whitespace
		json = JSON.parse(json);
		json = JSON.stringify(json);
	}

	// add newline before and after curly braces
	reg = /([\{\}])/g;
	json = json.replace(reg, '\r\n$1\r\n');

	// add newline before and after square brackets
	reg = /([\[\]])/g;
	json = json.replace(reg, '\r\n$1\r\n');

	// add newline after comma
	reg = /(\,)/g;
	json = json.replace(reg, '$1\r\n');

	// remove multiple newlines
	reg = /(\r\n\r\n)/g;
	json = json.replace(reg, '\r\n');

	// remove newlines before commas
	reg = /\r\n\,/g;
	json = json.replace(reg, ',');

	// optional formatting...
	if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
		reg = /\:\r\n\{/g;
		json = json.replace(reg, ':{');
		reg = /\:\r\n\[/g;
		json = json.replace(reg, ':[');
	}
	if (options.spaceAfterColon) {
		reg = /\:/g;
		json = json.replace(reg, ': ');
	}

	$.each(json.split('\r\n'), function(index, node) {
		var i = 0, indent = 0, padding = '';

		if (node.match(/\{$/) || node.match(/\[$/)) {
			indent = 1;
		} else if (node.match(/\}/) || node.match(/\]/)) {
			if (pad !== 0) {
				pad -= 1;
			}
		} else {
			indent = 0;
		}

		for (i = 0; i < pad; i++) {
			padding += PADDING;
		}

		formatted += padding + node + '\r\n';
		pad += indent;
	});

	return formatted.trim();
};*/

/* 格式化JSON源码(对象转换为JSON文本) */
function formatJson(txt, compress) {
	var indentChar = '    ';
	if (!txt || /^\s*$/.test(txt)) {
		// alert('数据为空,无法格式化! ');   
		return "{}";
	}
	try {
		txt=txt.replace(/\n/g,"").replace(/\r/g,"");
		var data = eval('(' + txt + ')');
	} catch (e) {
		alert('数据源语法错误,格式化失败! 错误信息: ' + e.description, 'err');
		return;
	}
	;
	var draw = [], last = false, This = this, line = compress ? ''
			: '\n', nodeCount = 0, maxDepth = 0;

	var notify = function(name, value, isLast, indent/*缩进*/, formObj) {
		nodeCount++;/*节点计数*/
		for ( var i = 0, tab = ''; i < indent; i++)
			tab += indentChar;/* 缩进HTML */
		tab = compress ? '' : tab;/*压缩模式忽略缩进*/
		maxDepth = ++indent;/*缩进递增并记录*/
		if (value && value.constructor == Array) {/*处理数组*/
			draw.push(tab + (formObj ? ('"' + name + '":') : '') + '['
					+ line);/*缩进'[' 然后换行*/
			for ( var i = 0; i < value.length; i++)
				notify(i, value[i], i == value.length - 1, indent,
						false);
			draw.push(tab + ']' + (isLast ? line : (',' + line)));/*缩进']'换行,若非尾元素则添加逗号*/
		} else if (value && typeof value == 'object') {/*处理对象*/
			draw.push(tab + (formObj ? ('"' + name + '":') : '') + '{'
					+ line);/*缩进'{' 然后换行*/
			var len = 0, i = 0;
			for ( var key in value)
				len++;
			for ( var key in value)
				notify(key, value[key], ++i == len, indent, true);
			draw.push(tab + '}' + (isLast ? line : (',' + line)));/*缩进'}'换行,若非尾元素则添加逗号*/
		} else {
			if (typeof value == 'string')
				value = '"' + value + '"';
			draw.push(tab + (formObj ? ('"' + name + '":') : '')
					+ value + (isLast ? '' : ',') + line);
		}
		;
	};
	var isLast = true, indent = 0;
	notify('', data, isLast, indent, false);
	return draw.join('');
}