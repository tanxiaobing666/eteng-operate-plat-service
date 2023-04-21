var TEST_FLAG = false;
var TEST_SPEED = 2000;

function runAutoTest(){
	TEST_FLAG = true;
	autoTest();
}

var TRANS_CODES = [ "00/CP001Op",
                    "00/CP002Op",
                    //"00/CP011Op","00/CP017Op","00/CP018Op", 
        "PD01001Op",  "PD01012Op", "PP03012Op",
		"PP03010Op", "PP03014Op", "PP03026Op", "PD03042Op", "PD03047Op",
		"PD03055Op", "PD04012Op", "PD04022Op", "PD04032Op",
		"PD04042Op", "PD04013Op", "PD04023Op", "PD04033Op", "PD04043Op",
		"PD04061Op", "PD04062Op", "PD04065Op", "PD04066Op", "PD04073Op",
		"PP05001Op", "PP05001Op", "PP05002Op", "PP05002Op", "PP05003Op",
		"PP05003Op", "PP05004Op", "PP05004Op", "PP05006Op", "PP05006Op",
		"PP05006Op", "PP05006Op", "PP05007Op", "PP05008Op", "PP05009Op",
		"PP05010Op", "PP05011Op", "PP05012Op", "PP05012Op", "PP05013Op",
		"PP05014Op", "PP08015Op", "PP08016Op", "PP08017Op", "PP08019Op",
		"PP08025Op", "PP08026Op", 
                    "00/CP001Op"
		];

var runIndex = 0;
var runHandle = null;
/**
 * 自动测试
 */
function autoTest() {
	if(runHandle != null){
		try{
			clearTimeout(runHandle);
		}catch(e){}
	}
	runHandle = setTimeout(function() {
		if(!TEST_FLAG) {
			YT$.log("autoTest", "------stop handel-----");
			return;
		}
		//YT$.log("autoTest", "------auto run ....----" + runIndex);
		// 测试
		var transCode = TRANS_CODES[runIndex];
		var url = "data/" + transCode + ".do";
		// YT$.log("autoTest", "------auto test-2----");
		var data = null;
		try {
			data = sendsCache[transCode];
		} catch (e) {
		}
		if (data == null) {
			data = sendsCache["DEF"];
		}
		//YT$.log(transCode, "JSON="+JsonToStr(data));
		var ajax = new TransAjax();
		ajax.sendPostData(url, JsonToStr(data), function(rpdata) {
			var transCode = TRANS_CODES[runIndex];
			try{
				if(rpdata && rpdata.STATUS=="1"){
					var succText = $("#SUCC_TRANS_LIST").html();
					succText = succText.replace(transCode, " ").trim();
					succText = succText + " " + transCode;
					$("#SUCC_TRANS_LIST").html(succText);
	
					var failText = $("#FAIL_TRANS_LIST").html();
					failText = failText.replace(transCode, " ").trim();
					$("#FAIL_TRANS_LIST").html(failText);
				} else {
					var succText = $("#SUCC_TRANS_LIST").html();
					succText = succText.replace(transCode, " ").trim();
					$("#SUCC_TRANS_LIST").html(succText);
	
					var failText = $("#FAIL_TRANS_LIST").html();
					failText = failText.replace(transCode, " ").trim();
					failText = failText + " " + transCode;
					$("#FAIL_TRANS_LIST").html(failText);
				}
			}catch(e){
				
			}
			runIndex++;
			runIndex = runIndex % TRANS_CODES.length;
			// 继续处理
			runHandle = null;
			autoTest();	
			
		});
			
	}, TEST_SPEED);
}

/**
 * 显示运行信息
 */
function showTestInfo() {
	
}

function getTransData(transCode){
	var data = null;
	try {
		data = sendsCache[transCode];
	} catch (e) {
	}
	if (data == null) {
		data = sendsCache["DEF"];
	}
	return data;
}

// 测试数据缓存
var sendsCache = [];
/**
 * 加载测试的静态数据
 * 
 * @returns {Array}
 */
function createTestDatas() {
	sendsCache["DEF"] = {
		"STR_DATE" : "2012-05-01",
		"IBS_LGN_ID" : "hjkl1234",
		"ACCT_TYP" : "SA",
		"ACCT_NO" : "00000020511020266",
		"PSW1" : "123123",
		"PSW2" : "123123",
		"PAY_ACCT_NO" : "00000020511020266",
		"PAY_CURR" : "MOP",
		"RECV_ACCT_NO" : "00000020521008259",
		"RECV_CURR" : "MOP",
		"TRANS_MEMO" : "11",
		"MOBILE" : "15363775816",
		"LAGG" : "ZH",
		"AUTH_TYP" : "1",
		"PWD" : "1213232",
		"VERIFY_CODE" : "21211"
	};
	sendsCache["00/CP001Op"] = {
		
	};  
	sendsCache["PD04061Op"] = {
			
	};
	//查询存款交易记录
	sendsCache["PD04073Op"] = {
			"ACCT_NO":"00000020511020266",
			"CARD_NO":"",
			"CARD_SSN":"0",
			"STR_DATE":"2012-01-01",
			"END_DATE":"2013-01-01",
			"NEXT_KEY":"0"
	};
	//缴费（和记，数码通）
	sendsCache["PP05004Op"] = {
			"FUNC_CODE":"BH",
			"BILL_CCY_CODE":"344",
			"CLI_CODE":"24",
			"BILL_NO":"1100507800002",
			"BILL_AMT":"+0000012345600",
			"ACCT_NO":"00000020521008259"
	};
	//查询汇款卡
	sendsCache["CP019Op"] = {
			
	};
	//修改客户信息
	sendCache["CP020"] = {
			"EMAIL":"hl@163.com",
			"MOBILE":"15963327770",
			"PHONE":"10086"
	};
	//报失提款卡
	sendCache["CP021"] = {
			"ACCT_NO":"00000020511000540",
			"REASON":"loss",
			"ISSUE_NEW_CARD":"N",
			"STD_CHRG_IND":"1"
			
	};
	//报失支票簿
	sendCache["CP022"] = {
			"FUNC_CODE":"EC",
			"ACCT_NO":"00010110301105",
			"BEG_NO":"11011",
			"END_NO":"11012"
	};
	//报失定期存折
	sendCache["CP023"] = {
			"FUNC_CODE":"EF",
			"ACCT_NO":"00000060160300829",
			"CERT_NO":"00218"
	};
	//轉帳
	sendCache["CP023"] = {
		"PAY_ACCT_NO":"0000010511005611",
		"PAY_CURR":"156",
		"RECV_ACCT_NO":"0000010511005891",
		"RECV_CURR":"156",
		"PAY_AMT":"2",
		"TRANS_MEMO":"23",
		"PAY_CURR_NME":"HKD",
		"RECV_CURR_NME":"HKD",
		"EXCH_RT":"1"}
	// 附属户解约
	sendCache["PP03012Op"] = {
			"LIST":[{"ACCT_NO":"0000010210500224",
				"ACCT_TYP":"CA","REG_FLAG":"Y","REST_COMM":"N","REST_IN":"N"}]
	};
	// 附属签约
	sendCache["PP03014Op"] = {
			"LIST":[{"ACCT_NO":"0000010210500224",
				"ACCT_TYP":"CA",
				"REG_FLAG":"Y",
				"REST_COMM":"N",
				"REST_IN":"N"}]
	};
	return sendsCache;
}