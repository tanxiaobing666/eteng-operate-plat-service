var modules = [// 
   {id : ""	,name : "全部"}, 
{id : "00"	,name : "基础服务"}, 
{id : "01"	,name : "我的账户"}, 
{id : "02"	,name : "金融服务"}, 
{id : "07"	,name : "穿透交易"},
{id : "98"	,name : "客户端"},
{id : "99"	,name : "交易示例"}
];

var tplModules = "{@each modules as item}"// 
		+ "<li data-value='PP${item.id}'>${item.id} ${item.name}</li> "// 
		+ "{@/each}";

function generyModules(elem) {
	var html = juicer(tplModules, {
		modules : modules
	});
	$(elem).html(html);
}