//DT默认初始化参数
var CONSTANT = {
	DATA_TABLES : {
		DEFAULT_OPTION:{
			"aaSorting": [[ 1, "asc" ]],//默认第几个排序
			"bStateSave": true,//状态保存
			"processing": false,   //显示处理状态
			"serverSide": false,  //服务器处理
			"autoWidth": false,   //自动宽度
			"scrollX": true,
			"lengthChange": false,
			"paging": false,
			"language": {
				"url": "libs/zh_CN.json"
			},
			"lengthMenu": [[100], ['100']],  //显示数量设置
		},
		//常用的COLUMN
		COLUMNFUN:{
			//过长内容省略号替代
			ELLIPSIS:function (data, type, row, meta) {
				data = data||"";
				return '<span title="' + data + '" class="layui-elip">' + data + '</span>';
			}
		}
	}
};

//视图显示设置
var viewVisable = {
	linux:true,
	weblogic:true,
	tomcat:true,
	jvm:true
};

//选择分析数据Tpl模板
var analyzeDataTpl;
//分析结果展示模板
var analyzeResultTpl;

var analyzeResultData;

//常量信息
var consts = {
		"linux":{
			ESTABLISHED:"Tcp连接-ESTABLISHED",
			CLOSE_WAIT:"Tcp连接-CLOSE_WAIT",
			LISTEN:"Tcp连接-LISTEN",
			TIME_WAIT:"Tcp连接-TIME_WAIT",
			rx:"入网总流量[kb/s]",
			tx:"出网总流量[kb/s]",
			rootDisk:"根目录磁盘已使用百分比[%]",
			userDisk:"用户目录磁盘已使用百分比[%]",
			freeCpu:"空闲CPU百分比[%]",
			freeMem:"空闲内存百分比[%]",
			ioWait:"io等待CPU执行时间百分比[%]"
		},
		"weblogic":{
			currentSize:"JVM堆当前使用大小[MB]",
			freeSize:"JVM堆当前空闲大小[MB]",
			freePercent:"JVM堆当前空闲百分比[%]",
			maxThreadCount:"活动线程总数",
			pendingCount:"暂挂用户请求数",
			idleCount:"空闲线程数",
			activeConnectionsCurrentCount:"JDBC当前活动连接数",
			availableConnectionCount:"JDBC当前可用连接数",
			waitingForConnectionCurrentCount:"JDBC当前等待连接数",
			hoggingThreadCount:"独占线程数",
			throughput:"吞吐量",
			activeConnectionsHighCount:"JDBC历史最大连接数"
		},
		"jvm":{
			survivorSpacePercent_0:"幸存0区容量使用百分比[%]",
			survivorSpacePercent_1:"幸存1区容量使用百分比[%]",
			edenSpacePercent:"伊甸园区容量使用百分比[%]",
			oldSpacePercent:"老生代容量使用百分比[%]",
			permSpacePercent:"持久代容量使用百分比[%]",
			youngGCTotalCount:"Young GC总次数",
			youngGCTime:"Young GC花费总时间[s]",
			fullGCTotalCount:"Full GC总次数",
			fullGCTime:"Full GC花费总时间[s]",
			GCTotalTime:"GC花费的总时间[s]"
		}
};

//属性名称
//要用$.extend而不是直接赋值。注意js中同样也有对象空间
var propertyObject = {
	"linux":{
		commonInfo:{
			freeCpu:[],
			freeMem:[],
			ioWait:[]
		},
		tcpInfo:{
			ESTABLISHED:[],
			LISTEN:[],
			CLOSE_WAIT:[],
			TIME_WAIT:[]
		},
		networkInfo:{
			rx:[],
			tx:[]
		}
	},
	"weblogic":{
		jvmInfo:{
			currentSize:[],
			freeSize:[],
			freePercent:[]
		},
		queueInfo:{
			maxThreadCount:[],
			pendingCount:[],
			idleCount:[],
			throughput:[],
			hoggingThreadCount:[]
		}
	},
	"jvm":{
		commonInfo:{
			survivorSpacePercent_0:[],
			survivorSpacePercent_1:[],
			edenSpacePercent:[],
			oldSpacePercent:[],
			permSpacePercent:[],
			youngGCTotalCount:[],
			youngGCTime:[],
			fullGCTotalCount:[],
			fullGCTime:[],
			GCTotalTime:[]
		},		
	}
	
};


//Datatable等对象
var returnObject = {};

var infos;

var handleFiles;

/********************************/
//时间
var dates = {};
//资源动态数据本地储存
var serverInfos = {};
//服务器列表
var serverList = {};
/*

if (infos != null) {
	dates = infos.dates;
	serverInfos = infos.serverInfos;
	serverList = infos.serverList;
}
*/

//layui模块入口
layui.use(['element', 'layer', 'form', 'util', 'laytpl'], function () {

	//var $ = layui.jquery;
	var layer = layui.layer;
	var form = layui.form();
	var util = layui.util;
	var element = layui.element();
	var laytpl = layui.laytpl;
	
	analyzeDataTpl = laytpl($("#choose-analyze-data").html());
	analyzeResultTpl = laytpl($("#analyze-result-table").html());

	///////////////////////////////////////////////////////////////////////////////

	$(function () {
		getDataFromStorage();

		initSettingValue();
		//Datatables初始化

		//linux
		var linuxTableSetting = {
			typeName: "linux",
			tableDom: "#linux table",
			columnVisibleSettingDom: "#linux-columns",
			dtParams: {
				aTargets: [16],
				data: serverList.linux,
				columns: [
					{
						"data": "id"
					},
					{
						"data": null,
						"render": function (data, type, full, meta) {
							return data.host + ":" + data.port;
						}
					},
					{
						"data": "mark",
						"visible": false
					},
					{
						"data": "username",
						"visible": false
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return data.cpuInfo + "核";
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return data.memInfo + "kb";
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return data.info.diskInfo.userDisk + "%";
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return data.info.diskInfo.rootDisk + "%";
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="freeCpu">&nbsp;' + data.info.freeCpu + '%&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="freeMem">&nbsp;' + data.info.freeMem + '%&nbsp;</span>';
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="ESTABLISHED">&nbsp;' + data.info.tcpInfo.ESTABLISHED + '&nbsp;</span>';
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="CLOSE_WAIT">&nbsp;' + data.info.tcpInfo.CLOSE_WAIT + '&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="TIME_WAIT">&nbsp;' + data.info.tcpInfo.TIME_WAIT + '&nbsp;</span>';
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="LISTEN">&nbsp;' + data.info.tcpInfo.LISTEN + '&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="networkInfo" data-name="rx">&nbsp;' + data.info.networkInfo.rx + 'kb/s&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="networkInfo" data-name="tx">&nbsp;' + data.info.networkInfo.tx + 'kb/s&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="ioWait">&nbsp;' + data.info.ioWait + '%&nbsp;</span>';
						}
					}
				],
				initComplete: null
			}
		};
		returnObject.linux = serverDynamicResourceView(linuxTableSetting);


		//weblogic
		var weblogicTableSetting = {
			typeName: "weblogic",
			tableDom: "#weblogic table",
			columnVisibleSettingDom: "#weblgic-columns",
			dtParams: {
				aTargets: [21],
				data: serverList.weblogic,
				columns: [
					{
						"data": "id"
					},
					{
						"data": null,
						"render": function (data, type, full, meta) {
							return data.host + ":" + data.port;
						}
					}, {
						"data": "mark",
						"visible": false
					}, {
						"data": "username",
						"visible": false
					}, {
						"data": "info.serverName",
						"visible": false
					}, {
						"data": null,
						"render": function (data) {
							var statusCss = "danger";
							if (data.info.status == "RUNNING") {
								statusCss = "success";
							}
							return '<span class="label label-' + statusCss + '">' + data.info.status + '</span>';
						}
					}, {
						"data": null,
						"visible": false,
						"render": function (data) {
							var statusCss = "danger";
							if (data.info.health == "Health") {
								statusCss = "success";
							}
							return '<span class="label label-' + statusCss + '">' + data.info.health + '</span>';
						}
					}, {
						"data": "info.startTime",
						"visible": false
					}, {
						"data": null,
						"render": function (data) {
							return data.info.maxJvm + "MB";
						}
					}, {
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jvmInfo" data-name="currentSize">&nbsp;' + data.info.jvmInfo.currentSize + 'MB&nbsp;</span>';
						}
					}, {
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jvmInfo" data-name="freeSize">&nbsp;' + data.info.jvmInfo.freeSize + 'MB&nbsp;</span>';
						}
					}, {
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jvmInfo" data-name="freePercent">&nbsp;' + data.info.jvmInfo.freePercent + '%&nbsp;</span>';
						}
					}, {
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="maxThreadCount">&nbsp;&nbsp;' + data.info.queueInfo.maxThreadCount + '&nbsp;&nbsp;</span>';
						}

					}, {
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="pendingCount">&nbsp;&nbsp;' + data.info.queueInfo.pendingCount + '&nbsp;&nbsp;</span>';
						}
					}
					, {
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="idleCount">&nbsp;&nbsp;' + data.info.queueInfo.idleCount + '&nbsp;&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="hoggingThreadCount">&nbsp;&nbsp;' + data.info.queueInfo.hoggingThreadCount + '&nbsp;&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="throughput">&nbsp;&nbsp;' + data.info.queueInfo.throughput + '&nbsp;&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							var html = '';
							var count = 0;
							$.each(data.info.jdbcInfo, function (jdbcName, jdbcInfo) {
								if (jdbcInfo.jdbcState == "Running") {
									html += '<span class="label label-success jdbc-connect-status">' + jdbcName + ':Running</span>&nbsp;';
								} else {
									html += '<span class="label label-danger jdbc-connect-status">' + jdbcName + ":" + jdbcInfo.jdbcState + '&nbsp;</span>';
								}
								count++;
								if (count % 2 == 0) {
									html += '<br>';
								}

							});

							return html;
						}
					},
					{
						"data": null,
						"render": function (data) {
							var html = '';
							var count = 0;
							$.each(data.info.jdbcInfo, function (jdbcName, jdbcInfo) {
								html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="activeConnectionsCurrentCount">' + jdbcName + ":&nbsp;" + jdbcInfo.activeConnectionsCurrentCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
								count++;
								if (count % 2 == 0) {
									html += '<br>';
								}
							});
							return html;
						}
					},
					{
						"data": null,
						"render": function (data) {
							var html = '';
							var count = 0;
							$.each(data.info.jdbcInfo, function (jdbcName, jdbcInfo) {
								html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="availableConnectionCount">' + jdbcName + ":&nbsp;" + jdbcInfo.availableConnectionCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
								count++;
								if (count % 2 == 0) {
									html += '<br>';
								}
							});
							return html;
						}
					},
					{
						"data": null,
						"render": function (data) {
							var html = '';
							var count = 0;
							$.each(data.info.jdbcInfo, function (jdbcName, jdbcInfo) {
								html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="waitingForConnectionCurrentCount">' + jdbcName + ":&nbsp;" + jdbcInfo.waitingForConnectionCurrentCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
								count++;
								if (count % 2 == 0) {
									html += '<br>';
								}
							});
							return html;
						}
					},
					{
						"data": null,
						"render": function (data) {
							var html = '';
							var count = 0;
							$.each(data.info.jdbcInfo, function (jdbcName, jdbcInfo) {
								html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="activeConnectionsHighCount">' + jdbcName + ":&nbsp;" + jdbcInfo.activeConnectionsHighCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
								count++;
								if (count % 2 == 0) {
									html += '<br>';
								}
							});
							return html;
						}
					}
				],
				initComplete: null
			}
		};
		returnObject.weblogic = serverDynamicResourceView(weblogicTableSetting);


		//jvm
		var JVMTableSetting = {
			typeName: "jvm",
			tableDom: "#jvm table",
			columnVisibleSettingDom: "#jvm-columns",
			dtParams: {
				aTargets: [15],
				data: serverList.jvm,
				columns: [
					{
						"data": "id"
					},
					{
						"data": null,
						"render": function (data, type, full, meta) {
							return data.host + ":" + data.port;
						}
					}, {
						"data": "mark",
					}, {
						"data": "username",
						"visible": false
					}, {
						"data": "pid"
					}, {
						"data": "processName"
					}, {
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="survivorSpacePercent_0">&nbsp;' + data.jvmInfo.survivorSpacePercent_0 + '%&nbsp;</span>';
						}
					}, {
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="survivorSpacePercent_1">&nbsp;' + data.jvmInfo.survivorSpacePercent_1 + '%&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="edenSpacePercent">&nbsp;' + data.jvmInfo.edenSpacePercent + '%&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="oldSpacePercent">&nbsp;' + data.jvmInfo.oldSpacePercent + '%&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="permSpacePercent">&nbsp;' + data.jvmInfo.permSpacePercent + '%&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="youngGCTotalCount">&nbsp;' + data.jvmInfo.youngGCTotalCount + '&nbsp;</span>';
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="youngGCTime">&nbsp;' + data.jvmInfo.youngGCTime + 's&nbsp;</span>';
						}
					},
					{
						"data": null,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="fullGCTotalCount">&nbsp;' + data.jvmInfo.fullGCTotalCount + '&nbsp;</span>';
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="fullGCTime">&nbsp;' + data.jvmInfo.fullGCTime + 's&nbsp;</span>';
						}
					},
					{
						"data": null,
						"visible": false,
						"render": function (data) {
							return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="GCTotalTime">&nbsp;' + data.jvmInfo.GCTotalTime + 's&nbsp;</span>';
						}
					}
				],
				initComplete: null
			}
		};
		returnObject.jvm = serverDynamicResourceView(JVMTableSetting);


		//绑定echarts图表显示事件
		$(document).delegate(".dynamic-data", "click", function () {
			var serverId = $(this).attr("data-id");	//唯一id
			var serverType = $(this).parents(".layui-colla-item").attr("id");//类型 如linux
			var itemName = $(this).attr("data-item");//信息条目 如 jdbcInfo
			var dataName = $(this).attr("data-name");//详细名称 如 freeCpu

			var mode = 0;

			if (serverType == "weblogic" && itemName == "jdbcInfo") {
				//多类目折线图显示
				mode = 1;
			}

			showEchartView(consts[serverType][dataName], serverInfos[serverType][serverId][itemName][dataName], dates[serverId], mode);
		});

		//视图设置显示按钮
		$('.dcits-table-setting > button:not(:eq(2))').click(function () {
			var type = $(this).attr("show-type");
			$("#" + type).toggleClass("layui-hide");

			if (type != "views") {
				$("#views").addClass("layui-hide");
			}

			if (type != "columns") {
				$("#columns").addClass("layui-hide");
			}

			$(this).toggleClass("layui-btn-danger").siblings("button").removeClass("layui-btn-danger").addClass("layui-btn-normal");
		});

		$('.dcits-table-setting > button:eq(2)').click(function () {
			alert(111);
		});

		//按钮控制显示视图
		form.on('checkbox(view)', function (data) {
			var type = $(data.elem).attr('data-column');
			$("#" + type).toggleClass("layui-hide");
			viewVisable[type] = !(viewVisable[type]);
		});
		
		form.on('checkbox(all-checked)', function(data) {
			$(data.elem).siblings('input').prop("checked",data.elem.checked);
			form.render('checkbox');
		});
		
		//数据分析：统计指定时间段指定服务器指定选项的最大值、最小值、平均值
		$("#to-analyze-data").click(function(){
			if ($.isEmptyObject(serverList)) {
				layer.msg('请先选择文件并导入数据!', {icon:5, time:2000});
				return;
			}
			var html = analyzeDataTpl.render({"serverList":serverList, "propertyObject":propertyObject});
			layer.open({
				type:1,
	    		title:"选择数据",
	    		content:html,
	    		area: ['800px', '600px'],
	    		success:function() {
	    			element.init();
	    			form.render('checkbox');
	    		}
			});
		});

		//发送前分析请求
		$(document).delegate("#send-analyze-request", "click", function() {
			var serverObjs = $(".analyze-data-server:checked");
			var analyzeServerList = [];
			$.each(serverObjs, function(i, n) {
				analyzeServerList.push({id:$(n).attr("data-id"), host:$(n).attr("title"), serverType:$(n).attr("data-type")});
			});
			var typeItems = $(".analyze-data-item:checked");
			var analyzeItems = {linux:[], weblogic:[], jvm:[]};
			$.each(typeItems, function(i, n) {
				analyzeItems[$(n).attr("data-type")].push($(n).attr("data-value"));
			});
			
			if (analyzeServerList.length < 1) {
				layer.msg("请至少选择一个服务器!", {icon:5, time:2000});
				return;
			}
			
			if (analyzeItems['linux'].length < 1 && analyzeItems['weblogic'].length < 1 && analyzeItems['jvm'].length < 1) {
				layer.msg("请至少选择一个服务器类目!", {icon:5, time:2000});
				return;
			}
			
			/*console.log(JSON.stringify(analyzeServerList))
			console.log("=========================")
			console.log(JSON.stringify(analyzeItems))*/
			var loadIndex = layer.msg('正在努力分析数据,请稍等...', {icon:16, time:60000, shade:0.35});
			$.post("server/analyzeData", {analyzeServerList:JSON.stringify(analyzeServerList)
				, analyzeItems:JSON.stringify(analyzeItems), serverInfos:JSON.stringify(serverInfos)
				, dates:JSON.stringify(dates)}, function(json) {
				json = JSON.parse(json);
				layer.close(loadIndex);
				if (json.returnCode == 0) {
					analyzeResultData = json.result;
					var htmlLinux = (json.linuxCount == 0 ? '' : analyzeResultTpl.render({"type":"linux","data":analyzeResultData}));
					var htmlWebloigc = (json.weblogicCount == 0 ? '' : analyzeResultTpl.render({"type":"weblogic","data":analyzeResultData}));
					var htmlJvm = (json.jvmCount == 0 ? '' : analyzeResultTpl.render({"type":"jvm","data":analyzeResultData}));
					layer.open({
						type:1,
			    		title:"查看结果-全部复制可直接粘贴到Excel表格中",
			    		content:'<div id="analyze-result-data-view"></div>',
			    		area: ['900px', '680px'],
			    		success:function(layero, index) {
			    			$(layero).find('#analyze-result-data-view').append(htmlLinux).append(htmlWebloigc).append(htmlJvm);
			    		}
					});
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});
		});
		
/////////////////////////////////公共方法///////////////////////////////////////////////////////////////////////////
		handleFiles = function (files)
		{
			if(files.length)
			{
				var file = files[0];
				var reader = new FileReader();
				reader.onload = function()
				{
					infos = JSON.parse(this.result);
					if (infos != null) {
						dates = infos.dates;
						serverInfos = infos.serverInfos;
						serverList = infos.serverList;

						$.each(returnObject, function(type, obj) {
							(obj.table).destroy();
						});
						linuxTableSetting.dtParams.data = infos.serverList.linux;
						returnObject.linux = serverDynamicResourceView(linuxTableSetting);
						weblogicTableSetting.dtParams.data = infos.serverList.weblogic;
						returnObject.weblogic = serverDynamicResourceView(weblogicTableSetting);
						JVMTableSetting.dtParams.data = infos.serverList.jvm;
						returnObject.jvm = serverDynamicResourceView(JVMTableSetting);
					}
				};
				reader.readAsText(file);
			}
		}


		//单个类型资源的监控处理方法
		/**
		 * setting参数
		 *
		 * typeName 类型名称 如tomcat weblogic linux等 String
		 * tableDom  表格dom  String
		 * columnVisibleSettingDom 列显示设置的父节点 String
		 *
		 * setting.dtParams:
		 * aTargets 不参与排序的列 Array
		 * ajaxUrl 获取数据的ajax地址 String
		 * columns 列渲染设置  Array
		 * initComplete DT渲染完成后的回调
		 *
		 *
		 *
		 */
		function serverDynamicResourceView(setting) {
			var returnInfo = {};
			//初始化
			var tableView = $(setting.tableDom).DataTable(
				$.extend(true, {}, CONSTANT.DATA_TABLES.DEFAULT_OPTION, {
					"columnDefs": [{"orderable": false, "aTargets": setting.dtParams.aTargets}],
					"data": setting.dtParams.data,
					"columns": setting.dtParams.columns,
					"initComplete": function () {
						checkedBox(tableView, setting.columnVisibleSettingDom);
						bindShowColumns(tableView, setting.typeName);
					}
				}));


			returnInfo.table = tableView;
			return returnInfo;
		}

		//绑定列显示
		function bindShowColumns(tableObject, typeName) {
			//显示或者隐藏列
			form.on('checkbox(' + typeName + ')', function (data) {
				var column = tableObject.column($(data.elem).attr('data-column'));
				column.visible(!column.visible());
			});

		}

		//单种类型数据的echarts图表展示
		/**
		 * title:图表标题
		 * seriesData:信息数据
		 * date:对应日期
		 * mode:是否为多类目折线图
		 */
		function showEchartView(title, seriesData, date, mode) {
			var option = {};
			//标题
			if (mode == 0) {
				option.title = {
					text: title,
					x: 'center',
					align: 'right',
					top: 'top',
					textStyle: {
						color: '#EF2203'
					}
				};
			}

			//提示框触发类型
			option.tooltip = {
				trigger: 'axis'
			};

			//工具组件
			option.toolbox = {
				show: true,
				feature: {
					dataView: {readOnly: true}, //数据视图
					saveAsImage: {},// 保存为图片
				}
			};

			//x轴定义
			option.xAxis = {
				type: 'category',
				boundaryGap: false,
				data: date,// X轴的定义
				axisLine: {onZero: false},
			};
			//y轴定义
			option.yAxis = {
				type: 'value',// Y轴的定义
				max: 'dataMax',
				boundaryGap: [0.1, 0.1]
			};

			//图例组件
			if (mode == 1) {
				option.legend = {
					data: []
				};
				$.each(seriesData, function (i, n) {
					option.legend.data.push(i);
				});
			}

			//数据
			option.series = [];

			if (mode == 0) {
				option.series.push({
					name: title,
					type: 'line',
					smooth: true,
					data: seriesData
				})
			}

			if (mode == 1) {
				$.each(seriesData, function (i, n) {
					option.series.push({
						name: i,
						type: 'line',
						smooth: true,
						data: n
					});
				});
			}

			var thisechart;
			var idOfInterval;
			layer.open({
				type: 1,
				title: title,
				content: '<div class="open-echarts" id="thisId"></div>',
				area: ['880px', '460px'],
				success:function(){
					thisechart = echarts.init(document.getElementById("thisId"),'shine');
					thisechart.setOption(option);
				}
			});

		}

		//匹配显示隐藏列的对应的checkbox状态
		function checkedBox(tableObject, dom) {
			var checkList = $(dom + " .toggle-vis");
			var column;
			var that;
			$.each(checkList, function (i, n) {
				that = $(this);
				column = tableObject.column($(this).attr('data-column'));
				if (column.visible()) {
					that.attr("checked", true);
				}

			});
			form.render('checkbox');
		}


		//本地数据储存
		function saveDataToStorage() {
			layui.data('viewSetting', {
				key: 'viewVisable', value: JSON.stringify(viewVisable)
			});
			//return "请不要选择\"禁止弹出对话框\"选项!";
		}

		//本地数据获取
		function getDataFromStorage() {
			var viewSetting = layui.data('viewSetting');
			if (viewSetting.viewVisable != null) {
				viewVisable = JSON.parse(viewSetting.viewVisable);
			}
		}


		//初始化时设置相关表单值如视图显示、预警值设置
		function initSettingValue() {
			$.each(viewVisable, function (type, visable) {
				if (!visable) {
					$("#" + type).addClass("layui-hide");
				}
				$('#views input[data-column="' + type + '"]').prop('checked', visable);
			});
		}
	});

});


