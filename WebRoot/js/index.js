//用于更新用户本地浏览器的缓存配置信息
var platformVersion = "v2.1";

//批量操作工具html
var utilPageHtml = "";

//jvm监控 设置页面
var jvmMonitorStartHtml = "";
//linux命令执行页面
var linuxExecCommandHtml = "";

//预警信息查看表格模板
var alertTableTpl;

//userKey设置表格模板
var userKeysTpl;

//当前广播消息
var alertInfo = {};

//预警设置
var alertSettingValue = {
	linux:{
		freeCpu:{
			value:10,
			sign:"%",
			mode:"<"
		},
		freeMem:{
			value:10,
			sign:"%",
			mode:"<"
		},
		ioWait:{
			value:20,
			sign:"%",
			mode:">"
		},
		TIME_WAIT:{
			value:40000,
			sign:"",
			mode:">"
		},
		CLOSE_WAIT:{
			value:40000,
			sign:"",
			mode:">"
		}
	},
	weblogic:{
		freePercent:{
			value:10,
			sign:"%",
			mode:"<"
		},
		pendingCount:{
			value:1,
			sign:"",
			mode:">"
		},
		waitingForConnectionCurrentCount:{
			value:1,
			sign:"",
			mode:">"
		}
	},
	jvm:{
		edenSpacePercent:{
			value:100,
			sign:"%",
			mode:">"
		},
		oldSpacePercent:{
			value:100,
			sign:"%",
			mode:">"
		},
		permSpacePercent:{
			value:100,
			sign:"%",
			mode:">"
		}		
	}
};

var serverList = {};

//视图显示设置
var viewVisable = {
	linux:true,
	weblogic:true,
	tomcat:true,
	jvm:true
};

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
			activeConnectionsHighCount:"历史最大活动连接数"
			
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
		},
		diskInfo:{
			rootDisk:[],
			userDisk:[]
		}
	},
	"weblogic":{
		commonInfo:{
		},
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
		},
		jdbcInfo:{
			jdbcState:{},
			activeConnectionsCurrentCount:{},
			availableConnectionCount:{},
			waitingForConnectionCurrentCount:{},
			activeConnectionsHighCount:{}
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
		}		
	}
	
};

//Datatable等对象
var returnObject = {};

/**
 * 其他设置
 */
/********************************/
var otherSetting = {
		intervalTime:6000, //表格刷新间隔时间
		playNoticIntervalTime:5000, //预警公告刷新时间间隔
		maxInfoDataCount:20000,  //最大缓存数据的条数
		autoClearDataFlag:false,  //自动清理本地缓存数据
		autoExportDataFlag:false, //自动导出数据
		infoCount:0,//缓存数据条数计数
		alertMonitorIntervalTime:8000,//预警监控循环时间
		cacheDataDetection:30000 //缓存数据检测循环时间
};

/********************************/
//广播信息是否更新过
var alertInfoUpdate = false;

//是否有正在执行的命令
var hasExecCommand = false;
var execWaittingCount = 1;
var execTipIntervalId = 0;
//资源动态数据本地储存   
var serverInfos = {
		linux:{},
		weblogic:{},
		tomcat:{},
		jvm:{}
};
//时间
var dates = {};


//layui模块入口
layui.use(['element', 'layer', 'form', 'util', 'laytpl'], function () {
	
    //var $ = layui.jquery;
    var layer = layui.layer;
    var form = layui.form();   
    var util = layui.util;  
    var element = layui.element();   
    var laytpl = layui.laytpl;
    
    //批量操作选择类型
    var currentTypeName = "all";
    
    alertTableTpl = laytpl($("#alert-info-table").html());
    userKeysTpl = laytpl($("#show-userKey-table").html());
    
    //右下工具类
    util.fixbar({
    	bar1: '&#xe631;',
    	click: function(type){
		    if(type === 'bar1'){
		    	openWebUtil();
		    }
	  }
    });
     
    ///////////////////////////////////////////////////////////////////////////////
    
    $(function () {
    	
    	window.onbeforeunload = function(){return saveDataToStorage();};
    	
    	//本地获取数据
    	getDataFromStorage();
    	
        //播放广播
        playAnnouncement(otherSetting.playNoticIntervalTime);
        
       //广播点击事件,查看当前所有信息
        $(".home-tips-container").click(function() {
        	var idOfInterval;
        	layer.open({
				type:1,
	    		title:"预警信息查看",
	    		shadeClose:true,
	    		anim:5,
	    		content:alertTableTpl.render(alertInfo),
	    		area: ['800px', '600px']
        	});
        });
        
        initSettingValue();
        
        //Datatables初始化
        
        //linux
        var linuxTableSetting = {
            	typeName:"linux",
            	tableDom:"#linux table",
            	columnVisibleSettingDom:"#linux-columns",
            	dtParams:{
            		aTargets:[18],
            		ajaxUrl:"linux/getList",
            		columns:[
            		         {
								   "data":"id"  
            		         },
            		         {
            		        	   "data":null,
                            	   "render":function(data, type, full, meta) {
                               			return data.host + ":" + data.port;
                            	   }
            		         },
            		         {
	                        	   "data":"mark",
	                          	   "visible": false
                              },
                              {
		                      	   "data":"username",
		                      	   "visible": false
                              },
                              {
                            	  "data":null,
                            	  "visible": false,
                            	  "render":function(data) {
                            		  return data.cpuInfo + "核";
                            	  }                            
                              },
                              {
                            	  "data":null,
                            	  "visible": false,
                            	  "render":function(data) {
                            		  return data.memInfo + "kb";
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "visible": false,
                            	  "render":function(data) {
                            		  return data.info.diskInfo.userDisk + "%";
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "visible": false,
                            	  "render":function(data) {
                            		  return data.info.diskInfo.rootDisk + "%";
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="freeCpu">&nbsp;' + data.info.freeCpu + '%&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="freeMem">&nbsp;' + data.info.freeMem + '%&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "visible": false,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="ESTABLISHED">&nbsp;' + data.info.tcpInfo.ESTABLISHED + '&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "visible": false,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="CLOSE_WAIT">&nbsp;' + data.info.tcpInfo.CLOSE_WAIT + '&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="TIME_WAIT">&nbsp;' + data.info.tcpInfo.TIME_WAIT + '&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "visible": false,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="tcpInfo" data-name="LISTEN">&nbsp;' + data.info.tcpInfo.LISTEN + '&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="networkInfo" data-name="rx">&nbsp;' + data.info.networkInfo.rx + 'kb/s&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="networkInfo" data-name="tx">&nbsp;' + data.info.networkInfo.tx + 'kb/s&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "render":function(data) {
                            		  return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="ioWait">&nbsp;' + data.info.ioWait + '%&nbsp;</span>';
                            	  }
                              },
                              {
                            	  "data":null,
                            	  "render":function(data) {
                            		  if (data.connectStatus != "true") {
                             			   return '<a href="javascript:;" onclick="javascript:layer.alert(\'' + data.connectStatus + '\',{icon:5});"><span class="label label-danger server-connect-status">异常</span></a>';
                             		   }
                             		   
                             		   return '<span class="label label-success server-connect-status">正常</span>';
                            	  }
                              },
                              {
                            	  "data":null,
	                         	   "render":function(data) {
	                         		   var html = '<button type="button" class="layui-btn layui-btn-danger layui-btn-small" onclick="reconnect(' + data.id + ',\'linux\');">重连</button>'
	                         		   	+ '&nbsp;<button type="button" class="layui-btn layui-btn-danger layui-btn-small" onclick="delServer(this,' + data.id + ',\'linux\');">删除</button>'
	                         		   + '&nbsp;<button type="button" class="layui-btn layui-btn-normal layui-btn-small" onclick="getJvm(' + data.id + ',\'' + data.host + '\',\'' + data.mark + '\');">JVM</button>';
	                         		   return html;
                             	   }
                              }                                        		         
            		         ],
            		initComplete:null
            	}
            };
        returnObject.linux = serverDynamicResourceView(linuxTableSetting);
               
        //weblogic
        var weblogicTableSetting = {
            	typeName:"weblogic",
            	tableDom:"#weblogic table",
            	columnVisibleSettingDom:"#weblgic-columns",
            	dtParams:{
            		aTargets:[0, 24],
            		ajaxUrl:"weblogic/getList",
            		columns:[
							{
								  "data": null,
								  "render": function(data) {
									return '<input type="checkbox" class="weblogic-server"  data-id="' + data.id + '">';
								  }
							  },
                             {
                          	   "data":"id"  
                             },
                             {
                          	   "data":null,
                          	   "render":function(data, type, full, meta) {
                             			return data.host + ":" + data.port;
                          	   }
                             },{
                          	   "data":"mark",
                          	   "visible": false
                             },{
                          	   "data":"username",
                          	   "visible": false
                             },{
                          	   "data":"info.serverName",
                          	   "visible": false
                             },{
                          	   "data":null,
                          	   "render":function(data) {
                          		   var statusCss = "danger";
                          		   if (data.info.status == "RUNNING") {
                          			   statusCss = "success";
                          		   } 
                          		   return '<span class="label label-' + statusCss + '">' + data.info.status + '</span>';
                          	   }
                             },{
                          	  "data":null,
                          	  "visible": false,
                          	  "render":function(data) {                		 
                          		   var statusCss = "danger";
          	               		   if (data.info. health == "Health") {
          	               			   statusCss = "success";
          	               		   } 
          	               		   return '<span class="label label-' + statusCss + '">' + data.info. health + '</span>';
                          	  }
                             },{
                          	   "data":"info.startTime",
                          	   "visible": false
                             },{
                          	   "data":null,
                          	   "render":function(data) {
                          		   return data.info.maxJvm + "MB";
                          	   }
                             },{
                      		   "data":null,
                      		   "visible": false,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jvmInfo" data-name="currentSize">&nbsp;'+data.info.jvmInfo.currentSize + 'MB&nbsp;</span>';
                          	   }
                             },{
                      		   "data":null,
                      		   "visible": false,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jvmInfo" data-name="freeSize">&nbsp;' + data.info.jvmInfo.freeSize + 'MB&nbsp;</span>';
                          	   }
                             },{
                      		   "data":null,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jvmInfo" data-name="freePercent">&nbsp;' + data.info.jvmInfo.freePercent + '%&nbsp;</span>';
                          	   }
                             },{
                          	   "data":null,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="maxThreadCount">&nbsp;&nbsp;' + data.info.queueInfo.maxThreadCount + '&nbsp;&nbsp;</span>';
                          	   }
                          	   
                             },{
                          	   "data":null,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="pendingCount">&nbsp;&nbsp;' + data.info.queueInfo.pendingCount + '&nbsp;&nbsp;</span>';
                          	   }
                             }
                             ,{
                          	   "data":null,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="idleCount">&nbsp;&nbsp;' + data.info.queueInfo.idleCount + '&nbsp;&nbsp;</span>';
                          	   }
                             },
                             {
                            	   "data":null,
                            	   "render":function(data) {
                            		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="hoggingThreadCount">&nbsp;&nbsp;' + data.info.queueInfo.hoggingThreadCount + '&nbsp;&nbsp;</span>';
                            	   }
                              },
                              {
                           	   "data":null,
                           	   "render":function(data) {
                           		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="queueInfo" data-name="throughput">&nbsp;&nbsp;' + data.info.queueInfo.throughput + '&nbsp;&nbsp;</span>';
                           	   }
                              },
                              {
                              	   "data":null,
                              	   "render":function(data) {
                              		   var html = '';
                              		   var count = 0;
                              		   $.each(data.info.jdbcInfo, function(jdbcName, jdbcInfo) {
                              			  if (jdbcInfo.jdbcState == "Running") {
                              				  html += '<span class="label label-success jdbc-connect-status">' + jdbcName + ':Running</span>&nbsp;';
                              			  } else {
                              				html += '<span class="label label-danger jdbc-connect-status">'+ jdbcName + ":" + jdbcInfo.jdbcState + '&nbsp;</span>'; 
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
                               	   "data":null,
                               	   "render":function(data) {                               		   
	                               		var html = '';
	                               		var count = 0;
	                               		$.each(data.info.jdbcInfo, function(jdbcName, jdbcInfo) {
	                               			html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="activeConnectionsCurrentCount">'+ jdbcName + ":&nbsp;" + jdbcInfo.activeConnectionsCurrentCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
	                               			count++;
	                               			if (count % 2 == 0) {
	                              				  html += '<br>';
	                              			  }
	                               		});	 
	                               		return html;
                               	   }
                               },
                               {
                               	   "data":null,
                               	   "render":function(data) {
	                               		var html = '';
	                               		var count = 0;
	                               		$.each(data.info.jdbcInfo, function(jdbcName, jdbcInfo) {
	                               			html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="availableConnectionCount">' + jdbcName + ":&nbsp;"  + jdbcInfo.availableConnectionCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
	                               			count++;
	                               			if (count % 2 == 0) {
	                              				  html += '<br>';
	                              			  }
	                               		});
	                               		return html;
                               	   }
                               },
                               {
                               	   "data":null,
                               	   "render":function(data) {
                               		    var html = '';
                               		    var count = 0;
	                               		$.each(data.info.jdbcInfo, function(jdbcName, jdbcInfo) {
	                               			html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="waitingForConnectionCurrentCount">' + jdbcName + ":&nbsp;"  + jdbcInfo.waitingForConnectionCurrentCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
	                               			count++;
	                               			if (count % 2 == 0) {
	                              				  html += '<br>';
	                              			  }
	                               		});	 
	                               		return html;
                               	   }
                               },
                               {
                               	   "data":null,
                               	   "render":function(data) {
                               		    var html = '';
                               		    var count = 0;
	                               		$.each(data.info.jdbcInfo, function(jdbcName, jdbcInfo) {
	                               			html += '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="jdbcInfo" data-name="activeConnectionsHighCount">' + jdbcName + ":&nbsp;"  + jdbcInfo.activeConnectionsHighCount + '&nbsp;&nbsp;</span>&nbsp;&nbsp;';
	                               			count++;
	                               			if (count % 2 == 0) {
	                              				  html += '<br>';
	                              			  }
	                               		});	 
	                               		return html;
                               	   }
                               },
                              {
                          	   "data":null,
                          	   "render":function(data) {
                          		   
                          		   if (data.connectStatus != "true") {
                          			   return '<a href="javascript:;" onclick="javascript:layer.alert(\'' + data.connectStatus + '\',{icon:5});"><span class="label label-danger server-connect-status">异常</span></a>';
                          		   }
                          		   
                          		   return '<span class="label label-success server-connect-status">正常</span>';
                          	   }
                             },{
                          	   "data":null,
                          	   "render":function(data) {
                          		   var html = '<button type="button" class="layui-btn layui-btn-danger layui-btn-small" data-type="weblogic" onclick="reconnect(' + data.id + ',\'weblogic\');">重连</button>'
                          		   	+ '&nbsp;<button type="button" class="layui-btn layui-btn-danger layui-btn-small" data-type="weblogic" onclick="delServer(this,' + data.id + ',\'weblogic\');">删除</button>'
                          		  + '&nbsp;<button type="button" class="layui-btn layui-btn-danger layui-btn-small" onclick="rebootWeblogic(' + data.id + ');">重启</button>'
                          		  + '&nbsp;<button type="button" class="layui-btn layui-btn-normal layui-btn-small" onclick="getWeblogicJvm(' + data.id + ');">JVM</button>';
                          		   return html;
                          	   }
                             }
                  ],
            		initComplete:null
            	}
            };
        returnObject.weblogic = serverDynamicResourceView(weblogicTableSetting);
        
        //jvm
        var JVMTableSetting = {
            	typeName:"jvm",
            	tableDom:"#jvm table",
            	columnVisibleSettingDom:"#jvm-columns",
            	dtParams:{
            		aTargets:[17],
            		ajaxUrl:"jvm/getList",
            		columns:[
                             {
                          	   "data":"id"  
                             },
                             {
                          	   "data":null,
                          	   "render":function(data, type, full, meta) {
                             			return data.host + ":" + data.port;
                          	   }
                             },{
                          	   "data":"mark"
                             },{
                          	   "data":"username",
                          	   "visible": false
                             },{
                          	   "data":"pid"
                             },{
                          	   "data":"processName"
                             },{
                      		   "data":null,
                      		   "visible": false,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="survivorSpacePercent_0">&nbsp;'+data.jvmInfo.survivorSpacePercent_0 + '%&nbsp;</span>';
                          	   }
                             },{
                      		   "data":null,
                      		   "visible": false,
                          	   "render":function(data) {
                          		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="survivorSpacePercent_1">&nbsp;' + data.jvmInfo.survivorSpacePercent_1 + '%&nbsp;</span>';
                          	   }
                             },
                             {
                        		   "data":null,
                            	   "render":function(data) {
                            		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="edenSpacePercent">&nbsp;' + data.jvmInfo.edenSpacePercent + '%&nbsp;</span>';
                            	   }
                               },
                               {
                          		   "data":null,
                              	   "render":function(data) {
                              		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="oldSpacePercent">&nbsp;' + data.jvmInfo.oldSpacePercent + '%&nbsp;</span>';
                              	   }
                                },
                                {
                           		   "data":null,
                               	   "render":function(data) {
                               		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="permSpacePercent">&nbsp;' + data.jvmInfo.permSpacePercent + '%&nbsp;</span>';
                               	   }
                                 },
                                 {
                             		   "data":null,
                                 	   "render":function(data) {
                                 		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="youngGCTotalCount">&nbsp;' + data.jvmInfo.youngGCTotalCount + '&nbsp;</span>';
                                 	   }
                                   },
                                   {
                             		   "data":null,
                             		   "visible": false,
                                 	   "render":function(data) {
                                 		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="youngGCTime">&nbsp;' + data.jvmInfo.youngGCTime + 's&nbsp;</span>';
                                 	   }
                                   },
                                   {
                             		   "data":null,                            		   
                                 	   "render":function(data) {
                                 		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="fullGCTotalCount">&nbsp;' + data.jvmInfo.fullGCTotalCount + '&nbsp;</span>';
                                 	   }
                                   },
                                   {
                             		   "data":null,
                             		  "visible": false,
                                 	   "render":function(data) {
                                 		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="fullGCTime">&nbsp;' + data.jvmInfo.fullGCTime + 's&nbsp;</span>';
                                 	   }
                                   },
                                   {
                             		   "data":null,
                             		  "visible": false,
                                 	   "render":function(data) {
                                 		   return '<span class="dynamic-data bg-primary" data-id="' + data.id + '" data-item="commonInfo" data-name="GCTotalTime">&nbsp;' + data.jvmInfo.GCTotalTime + 's&nbsp;</span>';
                                 	   }
                                   },
                                   {
                                	 "data":null,
                                	 "render":function(data) {                        		   
                                		 if (data.connectStatus != "true") {
                                			   return '<a href="javascript:;" onclick="javascript:layer.alert(\'' + data.connectStatus + '\',{icon:5});"><span class="label label-danger server-connect-status">异常</span></a>';
                                		   }
                                		   
                                		   return '<span class="label label-success server-connect-status">正常</span>';
                                	 }
                                 },{
                                	 "data":null,
                                	 "render":function(data) {
                                		 var html = '<button type="button" class="layui-btn layui-btn-danger layui-btn-small" data-type="jvm" onclick="reconnect(' + data.id + ',\'jvm\');">重连</button>'
                          		   				+ '&nbsp;<button type="button" class="layui-btn layui-btn-danger layui-btn-small" data-type="jvm" onclick="delServer(this,' + data.id + ',\'jvm\');">删除</button>'
                          		   				+ '&nbsp;<button type="button" class="layui-btn layui-btn-noraml layui-btn-small" data-type="jvm" onclick="stackLog(' + data.id + ');">堆栈</button>';
                                		 return html;
                                	 }
                                 }
                  ],
            		initComplete:null
            	}
            };
        returnObject.jvm = serverDynamicResourceView(JVMTableSetting);
        

        //绑定echarts图表显示事件
        $(document).delegate(".dynamic-data","click", function(){
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
        $('.dcits-table-setting > button').click(function() {
        	var type = $(this).attr("show-type");
        	$("#" + type).toggleClass("layui-hide");
        	
        	if (type != "views") {
        		$("#views").addClass("layui-hide"); 
        	}
        	
        	if (type != "columns") {
        		$("#columns").addClass("layui-hide");
        	}
        	
        	if (type != "alert-setting-value") {
        		$("#alert-setting-value").addClass("layui-hide");
        	}
        	
        	if (type != "other-setting") {
        		$("#other-setting").addClass("layui-hide");
        	}
        	
        	$(this).toggleClass("layui-btn-danger").siblings("button").removeClass("layui-btn-danger").addClass("layui-btn-normal");
        });
        
        //显示userKey
        $('.dcits-table-setting #show-user-keys').click(function() {
        	var loadIndex = layer.msg('查询中...', {icon:16, time:60000, shade:0.35});
        	$.get('server/getUserSpaceList', function(json) {       		
        		layer.close(loadIndex);   
        		json = JSON.parse(json);
        		if (json.returnCode == 0) {      			
            		var html = userKeysTpl.render(json.data);
        			layer.open({
        				type:1,
        				anim:5,
        	    		title:"userKey列表",
        	    		shadeClose:true,
        	    		content:html,
        	    		area: ['800px', '500px']
        			});
        		} else {
        			layer.alert(json.msg, {icon:5});
        		}
        	});      	
        });
        
        //按钮控制显示视图
        form.on('checkbox(view)', function(data){
        	var type =$(data.elem).attr('data-column');
        	$("#" + type).toggleClass("layui-hide");
        	viewVisable[type] = !(viewVisable[type]);
        	currentTypeName = type;  	
        	if ($("#" + type).is(":hidden")) {
        		//同时关闭数据刷新
        		stopRefreshTable();      		
        	} else {
        		//同时打开数据刷新
        		startRefreshTable();
        	}             	
        });
        
        
        form.on('select', function(data){
        	currentTypeName = data.value;
        }); 
        
        //更新预警设置值
        form.on('submit(alert-setting-value)', function(data) {
        	var typeName = $(data.elem).attr('data-type');
        	$.each(data.field, function(key, value) {
        		alertSettingValue[typeName][key]["value"] = value;
        	});
        	layer.msg('更新成功!', {icon:1, time:1500});
        	return false;
        });
        
        //更新其他设置
        form.on('submit(other-setting)', function(data) {
        	otherSetting["autoClearDataFlag"] = false;
        	otherSetting["autoExportDataFlag"] = false;
        	$.each(data.field, function(key, value) {
        		otherSetting[key] = value;
        		if (key == "autoClearDataFlag" || key == "autoExportDataFlag") {
        			otherSetting[key] = true;       			
        		}      		
        	});
        	layer.msg('更新成功!', {icon:1, time:1500});
        	return false;
        });
               
        
        //批量操作工具绑定
        $(document.body).delegates({
        	'.util-page-btn > button:eq(0)':delAll, //删除全部
        	'.util-page-btn > button:eq(1)':reclock, //重新计时
        	'.util-page-btn > button:eq(2)':stopRefreshTable, //停止刷新
        	'.util-page-btn > button:eq(3)':startRefreshTable, //开始刷新
        	'.util-page-btn > button:eq(4)':function () {exportData(false)},
        	"#refresh-alert-table":function() {
        		$(this).parents(".layui-layer-content").html(alertTableTpl.render(alertInfo));
        		layer.msg("刷新成功!", {icon:1, time:1500});
        	},
        	'#checkJps':checkJps, //检查java进程
        	'#startJvmMonitor':startJvmMonitor, //开启jvm监控
        	'#exec-command-util input[type="button"]:eq(0)':function() { //开始执行命令
        		if (hasExecCommand) {
        			layer.msg("当前有正在执行的命令,请等待执行完毕或者手动中断再执行新的命令!", {icon:5, time:1500});
        			return false;
        		}
        		$("#returnInfo").val("");
        		
        		var command = $("#command").val();
        		var serverId = $("#command-server-list #serverId").val();
        		
        		if (command != null && command != "" && serverId != null && serverId != "") {
        			hasExecCommand = true;
        			$("#exec-command-tips").text("正在请求执行命令,请稍等/");
        			execCommandTip();
        			execTipIntervalId = setInterval(execCommandTip, 1000);
        			//带上一个标记,防止中断命令时的并发冲突
        			var tag = "" + Date.parse(new Date()) + userKey;
        			$("#exec-command-tag").val(tag);
        			$.post("linux/execCommand", {id:serverId, command:command, userKey:userKey, tag:tag}, function(json) {  
        				clearInterval(execTipIntervalId);
        				
        				json = JSON.parse(json);
        				       				
        				hasExecCommand = false;
            			execWaittingCount = 1;
            			$("#exec-command-tag").val('');
            			if (json.returnCode == 0) {
            				$("#returnInfo").val(json.returnInfo);             				
            				$("#exec-command-tips").text("命令执行成功!  耗时:" + json.useTime + "ms.");
            			} else {           				            				
            				$("#exec-command-tips").text("命令执行失败:" + json.msg);
            			}            			
            			
            			$("#command").focus();
        			});
        		}       		
        	},
        	'#exec-command-util input[type="button"]:eq(1)':function() { //中断执行命令
        		if (!hasExecCommand || $("#exec-command-tag").val() == "") {
        			return false;
        		}
        		$.post("linux/stopExec?userKey=" + userKey, {tag:$("#exec-command-tag").val()}, function(json) {
        			json = JSON.parse(json);
        			if (json.returnCode == 0) {
        				layer.msg("已发送中断命令请求,请稍后!", {icon:1, time:1500});
        			} else {
        				layer.alert(json.msg, {icon:5});
        			}
        		});
        	},
        	'#add-new-userKey':function() { //新增userKey
        		layer.prompt({
  				  formType:0,
  				  value: '',
  				  title: '新增userKey[6-20位字母数字下划线]',
  				  maxlength:20,				  
  				  area: ['500px', '180px']
  				}, function(value, index, elem){
  				  $.post('server/setUserKey', {userKey:value}, function(json) {
  					  json = JSON.parse(json);
  					  if (json.returnCode == 0) {
  						  layui.data('rmp', {key:'userKey', value:value});						  
  						  window.location.reload();
  					  } else {
  						  layer.alert(json.msg, {icon:5});
  					  }
  				  });
  				});
        	},
        	'.change-userKey':function() {//更改userKey
        		var idKey = $(this).attr('id');
        		layer.confirm('确认切换到该用户空间吗？', {title:'提示'}, function(index) {
        			layer.close(index);
        			layui.data('rmp', {key:'userKey', value:idKey});	
        			window.location.reload();
        		});        		
        	},
        	'#weblogic button:eq(0)':function() { //批量重启weblogic
        		var checkboxList = $("#weblogic .weblogic-server:checked");
        		if (checkboxList.length < 1) {
					return;
				}
        		
        		layer.confirm('确认重启选中的' + checkboxList.length + '条记录？', {title:'警告', icon:3}, function(index) {
        			layer.close(index);
        			layer.open({
        				type:1,
        				title:"webogic重启日志",
        				anim:5,
        				shadeClose:true,
        	    		content:'<div style="padding:18px;" id="weblogic-reboot-logs"></div>',
        	    		area: ['800px', '600px'],
        	    		success:function(layero, index) {
        	    			$.each(checkboxList, function(i, n) {
        	    				var data = (returnObject.weblogic.table).row( $(this).parents('tr') ).data();
        	    				$("#weblogic-reboot-logs").append('<p>['+ new Date().Format("yyyy-MM-dd hh:mm:ss") +'][' 
        	    						+ data.host + ':' + data.port + '-' + data.mark + '] ' + '开始重启...</p>');
        	    				
        	    				$.post("weblogic/reboot", {id:data.id, userKey:userKey}, function(json) {
        	    					json = JSON.parse(json);
        	    					var flag = "<span style=\"color:#FF5722;\">重启失败:</span>";
        	    					if (json.returnCode == 0) {
        	    						flag = "<span style=\"color:#009688;\">重启成功:</span>";
        	    					}
        	    					$("#weblogic-reboot-logs").append('<p>['+ new Date().Format("yyyy-MM-dd hh:mm:ss") +'][' 
            	    						+ data.host + ':' + data.port + '-' + data.mark + '] ' + flag + json.msg +'</p>');
        	    				});
        	    			});
        	    		}
        			});
        		});
        	}
        });   
        
        
        //linux命令控制台
        $("#linux button:eq(0)").on('click', function() {
        	$.get("linux/getList?userKey=" + userKey, function(json) {
        		json = JSON.parse(json);
        		if (json.returnCode == 0) {
        			if ($.isEmptyObject(json.data)) {
        				layer.msg("当前无可连接的linux主机!", {icon:5, time:1500});
        				return;
        			}  
        			if (linuxExecCommandHtml == "") {
        				linuxExecCommandHtml = $("#exec-command-container").html();
        				$("#exec-command-container").html("");
        			}
        			
        			layer.open({
        				type:1,
        	    		title:"linux-命令控制台",
        	    		anim:5,
        	    		shadeClose:true,
        	    		content:linuxExecCommandHtml,
        	    		area: ['800px', '600px'],
        	    		success:function(layero, index) {
        	    			var serverListHtml = '';
        					$.each(json.data, function(i,n) {
        						
        						if (i == 0) {
        							$("#command-server-list #serverId").val(n.id);
        						}
        						
        						serverListHtml += '<option value="' + n.id + '">';
        						if (n.realHost != "" && n.realHost != null) {
        							serverListHtml += n.realHost;
        						} else {
        							serverListHtml += n.host + ':' + n.port;
        						}
        						serverListHtml += '[' + n.uanme + ']' + '</option>';							
        					});
        					$("#command-server-list #serverId").html(serverListHtml);
        					form.render('select');  
        					
        					$("#command").focus();
        					
        					form.on('select(select-linux-server)', function(data){
        						  $("#command-server-list #serverId").val(data.value);
        					}); 
        					
        					//回车键执行命令       					
	    					 layero.keyup(function (event) {
								 var keycode = event.which;
								 if(keycode==13){
									 $('#exec-command-util input[type="button"]:eq(0)').click();
								 }
	    					 });
        	    		},
        	    		cancel:function() {
        	    			hasExecCommand = false;
        	    			clearInterval(execTipIntervalId);
        	    		}
        			});
        		} else {
        			layer.alert(json.msg, {icon:5});
        		}
        	});
        });
        
        //每隔30s时间检测当前缓存中的数据数量，超过最大值弹窗提示
        setInterval(function() {
        	if (otherSetting.infoCount >= Number(otherSetting.maxInfoDataCount)) {
        		//需要自动保存
        		if (Boolean(otherSetting.autoExportDataFlag)) {
        			exportData(true);
        		}
        		
        		//需要自动清理
        		if (Boolean(otherSetting.autoClearDataFlag)) {
        			//自动清理
            		currentTypeName = "all";
            		reclock();
            		otherSetting.infoCount = 0;
            		layer.alert('当前缓存中的数据量已超过设置的最大值[' + otherSetting.maxInfoDataCount + '],系统已进行自动清除处理!(如不想进行自动清理请在"其他设置"中关闭此选项)'
            				, {title:'缓存数据过量提示', icon:1});
        		} else {
            		layer.alert('当前缓存中的数据量已超过设置的最大值[' + otherSetting.maxInfoDataCount + '],请在工具栏中选择"重新计时"来清除数据!'
            				, {title:'缓存数据过量提示', icon:5});
            	}
        		
        	} 
        }, otherSetting.cacheDataDetection);
        //定时预警检测
        checkWarningData();
        setInterval(checkWarningData, otherSetting.alertMonitorIntervalTime);
    });
    
/////////////////////////////////公共方法////////////////////////////////////////////////////////////////////////////
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
    	var tableView = $(setting.tableDom).on( 'error.dt', function ( e, settings, techNote, message ){
    	    	//这里可以接管错误处理，也可以不做任何处理
    	    	console.log( 'An error has been reported by DataTables: ', message );
    		}).on( 'xhr.dt', function ( e, settings, json, xhr) {
    		if (xhr.status != 200) {
    			$.each($("#" + setting.typeName + " .server-connect-status"), function(i, n) {
    	    		if ($(n).hasClass("label-success")) {
    	    			$(n).removeClass("label-success").addClass("label-danger");
    	    			$(n).text("中断");
    	    		}
    	    	});
    	        //console.log("ajax获取数据不正常,请检查服务器状态或者网络连接!" + "\nAjax当前状态：" + xhr.status);
    		}		
    		}).DataTable(
        		$.extend(true, {}, CONSTANT.DATA_TABLES.DEFAULT_OPTION, {                                       
        	        "columnDefs": [{"orderable":false, "aTargets":setting.dtParams.aTargets}],
        	        "ajax":setting.dtParams.ajaxUrl + "?userKey=" + userKey,
        	        "columns":setting.dtParams.columns,
        	        "initComplete":function() {    
        	        	checkedBox(tableView, setting.columnVisibleSettingDom);    
        	        	bindShowColumns(tableView, setting.typeName); 
        	        	 	
     	        		$("#"+ setting.typeName + " input[type='checkbox']:eq(0)").click(function() {
     	           		if ($(this).is(":checked")) {
     	           			$("#"+ setting.typeName + " input[type='checkbox']").prop("checked",true);   
     	           		} else {
     	           			$("#"+ setting.typeName + " input[type='checkbox']").prop("checked",false);  
     	           		}
     	        		});
        	        	if ($("#" + setting.typeName).is(":hidden")) {
        	        		returnInfo.intervalId=0;		
        	        	} else {
        	        		returnInfo.intervalId = setInterval(function(){
            	        		refreshTableData(tableView, setting.typeName);
            	            }, otherSetting.intervalTime);
        	        	}       	        	       	        	
        	        }
        	       }));
    	 
    	   	
    	returnInfo.table = tableView;
    	return returnInfo;
    }
    
    //等待执行linux命令
    function execCommandTip() {	
    	if (execWaittingCount == 18) {
    		$("#exec-command-tips").text("正在请求执行命令,请稍等/");
    		execWaittingCount = 1;
    		return;
    	}
    	var tips = $("#exec-command-tips").text();
    	if (execWaittingCount % 2 == 0) {
    		$("#exec-command-tips").text(tips.substring(0, (tips.length -1)) + "." + "/");
    	} else {
    		$("#exec-command-tips").text(tips.substring(0, (tips.length -1)) + "." + "\\");
    	}	
    	execWaittingCount++;
    }
    
    //绑定列显示
    function bindShowColumns(tableObject, typeName) {
    	 //显示或者隐藏列
        form.on('checkbox(' + typeName + ')', function(data){
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
    		        top:'top',
    		        textStyle:{
    		        	color:'#EF2203'
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
	            saveAsImage:{}// 保存为图片				        	
	        }	
    	};
    	
    	//x轴定义
    	option.xAxis = {
	            type : 'category',
	            boundaryGap: false,
	            data : date,// X轴的定义
	            axisLine: {onZero: false}
	        };
    	//y轴定义
    	option.yAxis = {
    		type : 'value',// Y轴的定义	
    		max:'dataMax',
    		boundaryGap: [0.1, 0.1]
    	};
    	
    	//图例组件
    	if (mode == 1) {
    		option.legend = {
    				data:[]
    		};
    		$.each(seriesData, function(i, n) {
    			option.legend.data.push(i);
    		});
    	}
    	    	
    	//数据
    	option.series = [];
    	
    	if (mode == 0) {
    		option.series.push({
    			name:title,
                type:'line',			                 
                smooth:true,
                data: seriesData
    		})
    	}
    	
    	if (mode == 1) {
    		$.each(seriesData, function(i, n) {
    			option.series.push({
    				name:i,
    				type:'line',
    				smooth:true,
    				data:n
    			});
    		});
    	}
    	
    	
    	/*var option = {
    		    title: {
    		        text: title,
    		        x: 'center',
    		        align: 'right',
    		        top:'top',
    		        textStyle:{
    		        	color:'#EF2203'
    		        }
    		    },		    
    		    tooltip: {
    		        trigger: 'axis'
    		    },
    		    toolbox: {
    		    	show: true,
    		        feature: {
    		        	dataView: {readOnly: true}, //数据视图
    		            saveAsImage:{},// 保存为图片				        	
    		        }
    		    },
    		    xAxis : 
				        {
				            type : 'category',
				            boundaryGap: false,
				            data : date,// X轴的定义
				            axisLine: {onZero: false},
				        }
    				    ,
    		    yAxis : [
    		        {
    		            type : 'value',// Y轴的定义
    		            boundaryGap: [0.1, 0.1],
    		            data:seriesData,
    		            max:'dataMax'
    		        }
    		    ],
    		    series: [
    		             {
    		            	 name:title,
    		                 type:'line',			                 
    		                 smooth:true,
    		                 data: seriesData
    		             }
    		         ]
    		};*/
    	
    	var thisechart;
    	var idOfInterval;
    	layer.open({
    		type:1,
    		title:title,
    		anim:5,
    		shadeClose:true,
    		content:'<div class="open-echarts" id="thisId"></div>',
    		area: ['880px', '460px'],
    		success:function(){
    			thisechart = echarts.init(document.getElementById("thisId"),'shine');
    			thisechart.setOption(option);
    			idOfInterval = setInterval(function () {
    				thisechart.setOption(option);
    			}, 5000);
    		},
    		cancel:function(){
    			clearInterval(idOfInterval);
    		}	
    	});

    }
    
    
    //刷新表格获取信息
    function refreshTableData(tableObject, typeName) {
    	tableObject.ajax.reload(function(json){  		
    		$.each(json.data, function(i, n){ 
    			//计数
    			otherSetting.infoCount++;
    			
    			serverList[n.id] = n.host + ":" + n.port;
    			if (n.mark != null && n.mark != "") {
    				serverList[n.id] = n.mark;
    			}
    			
    			if (serverInfos[typeName][n.id] == null) {
    				serverInfos[typeName][n.id] = $.extend(true, {}, propertyObject[typeName]);
    			}
    			   			
    			if (dates[n.id] == null) {
    				dates[n.id] = [];
    			}
    			
    			var realTimeInfo = n.info;
    			if (typeName == "jvm") {
    				realTimeInfo = n.jvmInfo;
    			}
    			
    			dates[n.id].push(realTimeInfo.time); 
    			
    			/*
    			 * 考虑jdbc信息的特殊情况
    			 */
    			$.each(realTimeInfo, function(i1, n1){
    				
    				if (typeName == "weblogic" && i1 == "jdbcInfo") {
    					
    					$.each(n1, function(jdbcName, jdbcInfo) {
    						$.each(jdbcInfo, function(i3, n3) {
    							if (serverInfos[typeName][n.id][i1][i3][jdbcName] == null) {
    								serverInfos[typeName][n.id][i1][i3][jdbcName] = [];
    							}
    							serverInfos[typeName][n.id][i1][i3][jdbcName].push(n3);
    						});
    					});
    					
    					return;
    				}
    				
    				if ((typeof n1) == 'object') {  
    					$.each(n1, function(i2, n2){   					   					  						
    						if (serverInfos[typeName][n.id][i1][i2] != null) {
    							serverInfos[typeName][n.id][i1][i2].push(n2);
    						}
    					});
    				} else {   					
    					if (serverInfos[typeName][n.id]["commonInfo"][i1] != null) {
    						serverInfos[typeName][n.id]["commonInfo"][i1].push(n1);
    					}
    				}
    			});	
    		});
    	},false);
    }
    
    
    //匹配显示隐藏列的对应的checkbox状态
    function checkedBox(tableObject, dom) {
    	var checkList = $(dom + " .toggle-vis");
        var column;
        var that;
        $.each(checkList, function(i, n){
        	that = $(this);
        	column = tableObject.column($(this).attr('data-column'));
        	if (column.visible()) {
        		that.attr("checked",true);
        	}
        	
        });
        form.render('checkbox');
    }
    
    //循环播放广播信息
    function playAnnouncement(interval) {
        var index = 0;
        var $announcement = $('.home-tips-container>span');
        //自动轮换
        setInterval(function () {
        	if (alertInfoUpdate == true) {
        		var html = "";
        		$.each(alertInfo, function(i, n) { 
        			$.each(n, function(valueName, value) {
        				if ((typeof value) == 'object') {
        					html += '<span server_id="' + i + '" style="color:red;">' + n.host + "的" + n.serverType + "服务器" + value.itemMark
        					+ "当前值为" + value.currentValue + ",请关注!" + '</span>';                   		
        				}
        			});            		
            	});
        		if (html == "") {
        			html = '<span style="color: #009688">服务器的各种警告信息在这里哦，点击查看详细信息!</span> ';
        		}
        		$('.home-tips-container').html(html);
        		$announcement = $('.home-tips-container>span');
        		alertInfoUpdate = false;
        	}
        	
            index++;    //下标更新
            if (index >= $announcement.length) {
                index = 0;
            }
            $announcement.eq(index).stop(true, true).fadeIn().siblings('span').fadeOut();  //下标对应的图片显示，同辈元素隐藏
        }, interval);
    }
    
    //本地数据储存
    function saveDataToStorage() {
    	sessionStorage.setItem("serverInfos", JSON.stringify(serverInfos));
    	sessionStorage.setItem("dates", JSON.stringify(dates));
    	layui.data('viewSetting', {
    		key: 'viewVisable',value: JSON.stringify(viewVisable)
    		});
    	layui.data('viewSetting', {
  		  	key: 'alertSettingValue',value: JSON.stringify(alertSettingValue)
  			});
    	layui.data('viewSetting', {
  		  	key: 'otherSetting',value: JSON.stringify(otherSetting)
  			});
    	//return "请不要选择\"禁止弹出对话框\"选项!";
    }
    
    //本地数据获取
    function getDataFromStorage() {
    	if(sessionStorage.getItem("dates")) {
    		serverInfos = JSON.parse(sessionStorage.getItem("serverInfos"));
    		dates = JSON.parse(sessionStorage.getItem("dates"));
    	}
    	
    	var viewSetting = layui.data('viewSetting');
    	
    	if (viewSetting.version != platformVersion) {
    		layui.data('viewSetting', {key: 'version',value: platformVersion});  
    		return false;
    	}
    	
    	if (viewSetting.viewVisable != null) {
    		viewVisable = JSON.parse(viewSetting.viewVisable);
    	}
    	
    	if (viewSetting.alertSettingValue != null) {
    		alertSettingValue = JSON.parse(viewSetting.alertSettingValue);
    	}    	
    	
    	if (viewSetting.otherSetting != null) {
    		otherSetting = JSON.parse(viewSetting.otherSetting);
    	}  	  	
    }
    
    
    
    //打开工具窗口
    function openWebUtil() {
    	
    	if (utilPageHtml == "") {
    		utilPageHtml = $("#utilPage").html();
    		$("#utilPage").html('');
    	}
    	
    	layer.open({
    			type:1,
    			title:"批量操作工具",
    			anim:5,
    			shadeClose:true,
    			content:utilPageHtml,
    			area: ['600px', '400px'],
    			//shadeClose:true,
    			success:function() {
    				$(".util-page-tips").html('');
    				currentTypeName = "all";
    				form.render('select');
    			}
    	});
    }
    
    //导出数据
    function exportData(autoFlag) {
    	var loadIndex;
    	if (!autoFlag) {
    		loadIndex = layer.msg('正在导出当前时间段的数据...', {icon:16, time:60000, shade:0.35});
    	}    	
    	$.post("server/exportData", {
    		"dates":JSON.stringify(dates),
    		"serverInfos":JSON.stringify(serverInfos),
    		"recordCount":otherSetting.infoCount,
    		"userKey":userKey
    	}, function(json) {
    		if (!autoFlag) {
    			layer.close(loadIndex);
	    		json = JSON.parse(json);
	    		if (json.returnCode == 0) {
	    			$(".util-page-tips").append('<p>导出成功,请右键另存为保存到本地：<a href="' + json.filePath + '" target="_blank">infos.json</a></p>');
	    		} else {
	    			layer.alert(json.msg, {icon:5});
	    		}
    		}   		
    	});
    }
    
    
    //delAll删除全部
    function delAll() {
    	layer.confirm('确认删除' + currentTypeName + '类型的所有监控条目吗?删除同时会清除所有缓存数据!', {title:'提示'}, function(index) {
    		layer.close(index);
    		$.each(returnObject, function(i, n) {
        		if (currentTypeName == i || currentTypeName == "all") {
        			$.get(i + "/delAll?userKey=" + userKey, function(json) {
        				json = JSON.parse(json);
        				var tip = i + "表格数据已全部删除!";
        				if (json.returnCode != 0) {
        					tip = i + "表格数据删除失败,原因:" + json.msg;
        					return false;
        				}
        				(n.table).clear().draw();
        				$.each(serverInfos[i], function(i1, n1) {      					
            				if (dates[i1] != null) {
            					otherSetting.infoCount = (otherSetting.infoCount - (dates[i1]).length);
            					delete dates[i1];
            				}
            			});
            			if (serverInfos[i] != null) {
            				delete serverInfos[i];
                			serverInfos[i] = {};
            			}
            			$.each(alertInfo, function(id, infos){
            				if (infos.serverType == i) {
            					delete alertInfo[id]
            				}
            			});
        				$(".util-page-tips").append('<p>' + tip + '</p>');
        			});
        		}
        	});
    	});
    }
    
    //reclock重新计时
    function reclock() {
    	$.each(serverInfos, function(i, n) {
    		if (currentTypeName == i || currentTypeName == "all") {    			
    			$.each(n, function(i1, n1) {
    				if (dates[i1] != null) {
    					otherSetting.infoCount = (otherSetting.infoCount - (dates[i1]).length);
    					delete dates[i1];
    				}
    			});
    			if (serverInfos[i] != null) {
    				delete serverInfos[i];
        			serverInfos[i] = {};
    			}   			
    			$(".util-page-tips").append('<p>当前表格' + i + '数据重置成功已重新计时!</p>');
    		}
    	});
    }
    
    //stopRefreshTable停止刷新表格
    function stopRefreshTable() {
    	$.each(returnObject, function(i, n) {
    		if(currentTypeName == i || currentTypeName == "all") {
    			if (n.intervalId == 0) {
    				$(".util-page-tips").append('<p>当前表格' + i + '已停止刷新,请勿重复停止!</p>');
    				return true;
    			} 
    			clearInterval(n.intervalId); 
    			n.intervalId = 0;
    			$("#" + i + " .layui-anim").removeClass("layui-anim-rotate");
    			$(".util-page-tips").append('<p>停止刷新 '+ i + '表格成功！</p>');
    		}
    	});    	   	
    }
    
    //startRefreshTable开始刷新表格
    function startRefreshTable() {
    	$.each(returnObject, function(i, n) {
    		if(currentTypeName == i || currentTypeName == "all") {
    			if (n.intervalId != 0) {
    				$(".util-page-tips").append('<p>当前表格' + i + '已在刷新状态!</p>');
    				return true;
    			} 
    			
    			if ($("#" + i).is(":hidden")) {
    				$(".util-page-tips").append('<p>当前表格' + i + '已被隐藏,若需刷新请先打开视图!</p>');
    				return true;
    			}
    			
    			n.intervalId = setInterval(function(){
	        		refreshTableData(n.table, i);
	            }, otherSetting.intervalTime);
    			$("#" + i + " .layui-anim").addClass("layui-anim-rotate");
    			$(".util-page-tips").append('<p>开始刷新 '+ i + '表格！</p>');
    		}
    	}); 
    }  
    
    
  //初始化时设置相关表单值如视图显示、预警值设置
    function initSettingValue() {
    	$.each(viewVisable, function(type, visable) {
    		if (!visable) {
    			$("#" + type).addClass("layui-hide"); 			
    		}
    		$('#views input[data-column="' + type + '"]').prop('checked', visable);
    	});
    	$.each(alertSettingValue, function(type, obj) {
    		$.each(obj, function(item, value) {
    			if ($("#alert-setting-value input[name='" + item + "']")) {
    				$("#alert-setting-value input[name='" + item + "']").val(value["value"]);
    			}
    		});
    	});
    	$.each(otherSetting, function(option, value) {
    		if ($("#other-setting input[name='" + option + "']")) {
				$("#other-setting input[name='" + option + "']").val(value);
			}
    	});
    	
    	if ($("#other-setting input[name='autoClearDataFlag']")) {
    		if (otherSetting.autoClearDataFlag) {
    			$("#other-setting input[name='autoClearDataFlag']").prop('checked', true);
    		}
    	}
    	
    	if ($("#other-setting input[name='autoExportDataFlag']")) {
    		if (otherSetting.autoExportDataFlag) {
    			$("#other-setting input[name='autoExportDataFlag']").prop('checked', true);
    		}
    	}
    }

    
});

//定时预警检查
function checkWarningData () {
	$.each(serverInfos, function(typeName, value1) {
		$.each(value1, function(serverId, item1) {			
			$.each(item1, function(itemName1, item2) {
				$.each(item2, function(itemName, valueArray) {
					if (alertSettingValue[typeName][itemName] != null) {
						var currentValue = valueArray[valueArray.length - 1];
						var resultFlag = true;
						if (alertSettingValue[typeName][itemName]["mode"] == ">") {
							resultFlag = (Number(alertSettingValue[typeName][itemName]["value"]) <=  Number(currentValue));
						}
						
						if (alertSettingValue[typeName][itemName]["mode"] == "<") {
							resultFlag = (Number(alertSettingValue[typeName][itemName]["value"]) >=  Number(currentValue));
						}
						if (resultFlag) {
							if (alertInfo[serverId] == null) {
								alertInfo[serverId] = {
										host:serverList[serverId],
										serverType:typeName
								};
							}
							
							if (alertInfo[serverId]["host"] == null) {
								alertInfo[serverId]["host"] = serverList[serverId];
							}
							
							alertInfo[serverId][itemName] = {
									alertValue:alertSettingValue[typeName][itemName]["value"] + alertSettingValue[typeName][itemName]["sign"],
									currentValue:currentValue + alertSettingValue[typeName][itemName]["sign"],
									itemMark:consts[typeName][itemName]								
							};
							alertInfoUpdate = true;
						} else {
							if (alertInfo[serverId] != null && alertInfo[serverId][itemName] != null) {
								delete alertInfo[serverId][itemName];
								alertInfoUpdate = true;
							}
						}
					}
				});
			});
		});
	});
}

//重新连接,不同类型的服务器或者应用连接方式不同，主要在后端实现
function reconnect(id, typeName) {  	
	layer.confirm("确定需要进行重新连接操作吗？", {icon:3, title:"警告"}, function(index) {
		var loadIndex = layer.msg('正在重新连接...', {icon:16, time:60000, shade:0.35});
		layer.close(index);
		$.get(typeName + "/reconnect?id=" + id + "&userKey=" + userKey, function(json) {
    		json = JSON.parse(json);
    		layer.close(loadIndex);
    		if (json.returnCode == 0) {
    			layer.msg('重新连接成功!', {icon:1, time:1500});
    		} else {
    			layer.alert('重新连接失败:' + json.msg, {icon:5});
    		}
    	});
	});   	
}

//删除server
function delServer(dom, id, typeName) {
	layer.confirm("确定进行删除操作吗？", {icon:3, title:"警告"}, function(index) {
		layer.close(index);
		$.get(typeName + "/del?id=" + id + "&userKey=" + userKey, function(json) {
    		json = JSON.parse(json);
    		if (json.returnCode == 0) {        			
    			returnObject[typeName]["table"].row($(dom).parents('tr')).remove().draw();
    			delete serverInfos[typeName][id];
    			delete dates[id];
    			delete alertInfo[id]
    			layer.msg('删除成功!', {icon:1, time:1500});
    		} else {
    			layer.alert('删除失败:' + json.msg, {icon:5});
    		}
    	});    		
	});	
}
//监控weblogic对应的jvm GC情况
function getWeblogicJvm(id) {
	var loadIndex = layer.msg('正在启动监控...', {icon:16, time:60000, shade:0.35});
	$.post("jvm/addWeblogicJvm", {id:id, userKey:userKey}, function(json) {
		json = JSON.parse(json);
		layer.close(loadIndex);
		if (json.returnCode == 0) {
			layer.msg("启动jvm监控成功!", {icon:1, time:1500});
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});
}


//重启weblogic
function rebootWeblogic(id) {
	return false;
	var loadIndex = layer.msg('正在重启该weblogic...', {icon:16, time:60000, shade:0.35});
	$.post("weblogic/reboot", {id:id, userKey:userKey}, function(json) {
		layer.close(loadIndex);
		json = JSON.parse(json);
		if (json.returnCode == 0) {
			layer.alert("该weblogic重启成功", {icon:1});
		} else {
			layer.alert("该weblogic重启失败", {icon:5});
		}
	});	
}


//准备监控linux上的java进程的jvm信息
function getJvm(id,host,mark) {
	
	if (jvmMonitorStartHtml == '') {
		jvmMonitorStartHtml = $("#jvmMonitorStartPage").html();
		 $("#jvmMonitorStartPage").html('');
	}
	
	if (mark != '' && mark != null) {
		host = mark;
	}
	
	layer.open(
			{
    			type:1,
    			shadeClose:true,
    			anim:5,
    			title:"启动jvm监控[" + host + "]",
    			content:jvmMonitorStartHtml,
    			area: ['600px', '460px'],
    			success:function() {
    				var loadIndex = layer.msg('正在获取进程信息...', {icon:16, time:60000, shade:0.35});
    				$.post("jvm/check", {id:id, userKey:userKey}, function(json) {
    					json = JSON.parse(json);
    					layer.close(loadIndex);
    		    		if (json.returnCode == 0) {
    		    			 $("#processNames").val(json.processNames);  		    			
    		    			 $("#javaHome").val(json.javaHome);
    		    			 $("#jvm-serverId").val(id);
    		    		} else {
    		    			layer.alert(json.msg, {icon:5});
    		    		}
    				});
    			}
    	});
}

//获取当前主机上java进程列表
function checkJps() {
	var loadIndex = layer.msg('正在获取进程信息...', {icon:16, time:60000, shade:0.35});
	$.post("jvm/check", {id:$("#jvm-serverId").val(), javaHome:$("#javaHome").val(), userKey:userKey}, function(json) {
		json = JSON.parse(json);
		layer.close(loadIndex);
		if (json.returnCode == 0) {
			 $("#processNames").val(json.processNames);
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});	
}

//启动jvm监控
function startJvmMonitor() {
	var loadIndex = layer.msg('正在启动JVM监控...', {icon:16, time:60000, shade:0.35});
	$.post("jvm/add", {id:$("#jvm-serverId").val(), pid:$("#pid").val(), processName:$("#processName").val(), javaHome:$("#javaHome").val(), userKey:userKey}, function(json) {
		json = JSON.parse(json);
		layer.close(loadIndex);
		if (json.returnCode == 0) {
			 layer.msg("启动jvm监控成功!", {icon:1, time:1500});
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});	
}

//获取堆栈信息
function stackLog(id) {
	var loadIndex = layer.msg('正在获取该进程的实时堆栈信息...', {icon:16, time:60000, shade:0.35});
	$.post("jvm/stack", {id:id, userKey:userKey}, function(json) {
		json = JSON.parse(json);
		layer.close(loadIndex);
		if (json.returnCode == 0) {
			layer.prompt({
				  formType: 2,
				  value: json.log,
				  title: '实时堆栈信息查看',
				  maxlength:999999,
				  area: ['800px', '350px'] //自定义文本域宽高
				}, function(value, index, elem){
				  layer.close(index);
				});
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});
}