var menus = [
{"clazz":"PP01","desc":"","name":"查询模板接口","path":"in/flo/tmplList","plus":"","url":"flo/tmplList"},
{"clazz":"PP01","desc":"","name":"修改模板接口","path":"in/flo/tmplSave","plus":"","url":"flo/tmplSave"},
{"clazz":"PP01","desc":"","name":"查询模板接口","path":"in/flo/tmplInfo","plus":"","url":"flo/tmplInfo"},
{"clazz":"PP01","desc":"","name":"保存组件","path":"in/flo/tmplCompLibAdd","plus":"","url":"flo/tmplCompLibAdd"},
{"clazz":"PP01","desc":"","name":"修改组件","path":"in/flo/tmplCompLibUpdate","plus":"","url":"flo/tmplCompLibUpdate"},
{"clazz":"PP01","desc":"","name":"删除组件","path":"in/flo/tmplCompLibDel","plus":"","url":"flo/tmplCompLibDel"},
{"clazz":"PP01","desc":"","name":"单查组件","path":"in/flo/tmplCompLibQuery","plus":"","url":"flo/tmplCompLibQuery"},
{"clazz":"PP01","desc":"","name":"列表查询组件","path":"in/flo/tmplCompLibList","plus":"","url":"flo/tmplCompLibList"},
{"clazz":"PP01","desc":"","name":"获取配置信息","path":"in/flo/TmplCompConf","plus":"","url":"flo/TmplCompConf"}	
,{"clazz":"PPXX","desc":"","name":"日思录分页查询列表","path":"in/daily/recordsPageQueryList","plus":"","url":"daily/recordsPageQueryList"}
,{"clazz":"PPXX","desc":"","name":"日思录删除","path":"in/daily/recordsDel","plus":"","url":"daily/recordsDel"}
,{"clazz":"PPXX","desc":"","name":"新增日思录","path":"in/daily/recordsAdd","plus":"","url":"daily/recordsAdd"}
,{"clazz":"PPXX","desc":"","name":"日思录详情","path":"in/daily/recordsDetails","plus":"","url":"daily/recordsDetails"}
,{"clazz":"PPXX","desc":"","name":"日思录修改","path":"in/daily/recordsUpdate","plus":"","url":"daily/recordsUpdate"}
,{"clazz":"PPXX","desc":"","name":"日思录上架/下架","path":"in/daily/recordsStatusMod","plus":"","url":"daily/recordsStatusMod"}
,{"clazz":"PPXX","desc":"","name":"日思录状态查询列表","path":"in/daily/recordsStatus","plus":"","url":"daily/recordsStatus"}
,{"clazz":"PPXX","desc":"","name":"评论分页查询列表","path":"in/comment/comPageQueryList","plus":"","url":"comment/comPageQueryList"}
,{"clazz":"PPXX","desc":"","name":"审核通过/撤回","path":"in/comment/comStatusMod","plus":"","url":"comment/comStatusMod"}
,{"clazz":"PPXX","desc":"","name":"评论状态查询列表","path":"in/comment/comStatus","plus":"","url":"comment/comStatus"}
,{"clazz":"PPXX","desc":"","name":"浏览统计","path":"in/information/browseCount","plus":"","url":"information/browseCount"}
,{"clazz":"PPXX","desc":"","name":"企业查询列表","path":"in/information/corpList","plus":"","url":"information/corpList"}
,{"clazz":"PPXX","desc":"","name":"标签统计排行","path":"in/information/rankTagList","plus":"","url":"information/rankTagList"}
,{"clazz":"PPXX","desc":"","name":"资讯统计","path":"in/information/messageCount","plus":"","url":"/information/messageCount"}



,{"clazz":"PPXX","desc":"","name":"新增菜单","path":"in/system/menuAdd","plus":"","url":"system/menuAdd"}
,{"clazz":"PPXX","desc":"","name":"修改菜单","path":"in/system/menuModi","plus":"","url":"system/menuModi"}
,{"clazz":"PPXX","desc":"","name":"查询单条菜单","path":"in/system/menuLoad","plus":"","url":"system/menuLoad"}
,{"clazz":"PPXX","desc":"","name":"删除菜单","path":"in/system/menuDel","plus":"","url":"system/menuDel"}
,{"clazz":"PPXX","desc":"","name":"社群通-机构数据同步","path":"in/dataSync/organSynch","plus":"","url":"dataSync/organSynch"}
,{"clazz":"PPXX","desc":"","name":"社群通-产品经理数据同步","path":"in/dataSync/productSynch","plus":"","url":"dataSync/productSynch"}
,{"clazz":"PPXX","desc":"","name":"SAAS资讯-潜在客户数据回流","path":"in/dataSync/latentReflux","plus":"","url":"dataSync/latentReflux"}
];

var tplMenus = [ '{@each menus as item}',
		'<button class="PP ${item.clazz}" data-url="${item.url}" ',
		' data-path="${item.path}" ',
		' onclick="loadForm(this)">${item.name}</button>', '{@/each}' ]
		.join("");
var tplMenusLi = 
	'{@each menus as item}'+
	'<li class="${item.clazz}" data-cn="${item.name}" data-url="${item.url}" data-path="${item.path}" ><a><i class="ui-fa-cog"></i><span>${item.name}</span</a></li>'+
	'{@/each}';


function loadMenus(ftClass, ftCode, ftName) {
	try {
		var datas = [];
		if (ftClass || ftCode || ftName) {
			for (var i = 0, j = menus.length; i < j; i++) {
				var menu = menus[i];
				if (ftName && ftName.length > 0 && menu.name.indexOf(ftName) >= 0) {
					datas.push(menu);
				} else if (ftCode && ftCode.length > 0 && menu.url.indexOf(ftCode) >= 0) {
					datas.push(menu);
				} else if (ftClass && ftClass.length > 0 && menu.clazz.indexOf(ftClass) >= 0) {
					datas.push(menu);
				}
			}
		} else {
			datas = menus;
		}
		var html = juicer(tplMenus, {
			menus : datas
		});
		$("#TRAN_AREA").html(html);
	} catch (e) {
		alert(e);
	}
}
function loadMenusLi(ftName) {
	try {
		var datas = [];
		if (ftName) {
			for (var i = 0, j = menus.length; i < j; i++) {
				var menu = menus[i];
				if (menu.name.indexOf(ftName) >= 0 || menu.url.indexOf(ftName) >= 0 || menu.clazz.indexOf(ftName) >= 0) {
					datas.push(menu);
				} else{
					var reg = new RegExp('[a-zA-Z0-9\- ]');
					if(reg.test(ftName)){ //字母或数字
						var uc = ftName.toUpperCase();
						var pinyin = toPinyin(menu.name);
						var firstpinyin = firstPinyin(menu.name);
						if(pinyin.indexOf(uc) >= 0 || firstpinyin.indexOf(uc) >= 0){
							datas.push(menu);
						}
					}
				}
					
			}
		} else {
			datas = menus;
		}
		juicer.register("toPinyin", toPinyin);
		juicer.register("firstPinyin", firstPinyin);
		var html = juicer(tplMenusLi, {
			menus : datas
		});
		return html;
	} catch (e) {
		alert(e);
	}
}