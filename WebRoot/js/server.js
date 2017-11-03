var additionItemParameters = {
		//linux
		"0":{
			javaHome:""
		},
		//weblogic
		"1":{
			linuxLoginUsername:"",
			linuxLoginPassword:"",
			javaHome:"",
			startScriptPath:""
		}
};

layui.use(['element', 'layer', 'form', 'util'], function () {
	var layer = layui.layer;
    var form = layui.form();   
    var util = layui.util;  
    var element = layui.element(); 
    
    var table;
    
    var singleSaveHtml = '';
    var batchSaveHtml = '';
    
    util.fixbar();
    
    $(function() {
    	
    	if (singleSaveHtml == '') {
			singleSaveHtml = $("#single-save-info").html();
			$("#single-save-info").remove();
		}
    	
    	if (batchSaveHtml == '') {
    		batchSaveHtml = $("#batch-save-info").html();
			$("#batch-save-info").remove();
		}
    	
    	table = $(".layui-table").DataTable(
    			$.extend(true, {}, CONSTANT.DATA_TABLES.DEFAULT_OPTION
    					, {
    				 	"columnDefs": [{"orderable":false, "aTargets":[0, 2, 4, 8, 9]}],
         	        	"ajax":"server/list",
         	        	"columns":[
         	        	           {
         	        	        	  "data": null,
         	        	        	  "render": function(data) {
         	        	         		return '<input type="checkbox">';
         	        	        	  }
         	        	           },
         	        	           {
         	        	        	   "data":"serverId",         	        	        	   
         	        	           },
         	        	           {
         	        	        	   "data":null,
         	        	        	   "render":function(data) {
         	        	        		   return data.host + ":" + data.port;
         	        	        	   }
         	        	           
         	        	           },
         	        	           {
         	        	        	   "data":"username"
         	        	           },
         	        	           {
         	        	        	   "data":"password",
         	        	        	   "render":function(data) {
         	        	        		   return '<a class="layui-btn layui-btn-small" href="javascript:;" onclick="layer.alert(\'' + data + '\', {title:\'密码查看\', icon:4});">查看</a>';
         	        	        	   }
         	        	           },
         	        	           {
         	        	        	   "data":"type",
         	        	        	   "render":function(data) {
         	        	        		   var typeName = "";
         	        	        		   switch (data) {
											case "0":
												typeName = "linux";
												break;
											case "1":
												typeName = "weblogic";
												break;
											case "2":
												typeName = "tomcat";
												break;
											case "3":
												typeName = "JVM";
												break;
											default:
												break;
											}
         	        	        		   return '<img src="imgs/' + typeName + '.png" alt="' + typeName + '" title="' + typeName + '" style="height:30px;width:34px;"/>' + typeName;       	        	        		   
         	        	        	   }
         	        	           },
         	        	           {
         	        	        	   "data":"createTime",
         	        	        	  "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
         	        	           },
         	        	           {
         	        	        	   "data":"time",
         	        	        	  "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
         	        	           },
         	        	          {
         	        	        	   "data":"tags",
         	        	        	  "render":function(data){
         	        	        		  var arrs = data.split(" ");
         	        	        		  var html = '';
         	        	        		  $.each(arrs, function(i, n) {
         	        	        			  html += '<span class="label label-success server-tags-label">' + n + '</span>&nbsp;';
         	        	        		  });
         	        	        		  return html;
         	        	        	  }
         	        	           },
         	        	           {
         	        	        	   "data":"mark",
         	        	        	   "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
         	        	           },
         	        	           {
         	        	        	   "data":null,
         	        	        	   "render":function(data) {
         	        	        		  var html = '<button type="button" class="layui-btn layui-btn-danger layui-btn-small del-server">删除</button>'
		                         		   	+ '<button type="button" class="layui-btn layui-btn-warm layui-btn-small update-server">修改</button>'
		                         		    + '<button type="button" class="layui-btn layui-btn-normal layui-btn-small start-server">监控</button>'
		                         		    + '<button type="button" class="layui-btn layui-btn-small copy-server">复制</button>';
		                         		   return html;
         	        	        	   }
         	        	           }],
         	        	"initComplete":function() { 
         	        		$("input[type='checkbox']:eq(0)").click(function() {
         	           		if ($(this).is(":checked")) {
         	           			$("input[type='checkbox']").prop("checked",true);   
         	           		} else {
         	           			$("input[type='checkbox']").prop("checked",false);  
         	           		}
         	           	});
         	            }
    			}));
    	
    		//批量操作 			
 			$(document).delegate('.server-btn-util > .right-btn-tool > button:eq(1)', 'click', function() {
        		layer.confirm('请点击下方按钮选择操作:', {title:'批量操作工具', btn: ['批量监控', '批量添加', '批量删除'] //可以无限个按钮
	        		,btn3: function(index, layero){
	        				layer.close(index);
	        				var checkboxList = $("td > input:checked"); 
	        				
	        				if (checkboxList.length < 1) {
	        					return;
	        				}
	        				layer.confirm('确认删除选择的 ' + checkboxList.length + '条记录吗？', {}, function(index) {
	        					layer.close(index);
	        					var loadIndex = layer.msg('正在批量删除信息...', {icon:16, time:9999999, shade:0.35});
		        				var successCount = 0;
		        				var totalCount = 0;
		        				$.each(checkboxList, function(i, obj) {
		        					var data = table.row( $(obj).parents('tr') ).data();
		        					
		        					$.post('server/del', {serverId:data.serverId}, function(json) {
		         	     	   			json = JSON.parse(json);
		         	     	   			if (json.returnCode == 0) {
		         	     	   				table.row($(obj).parents('tr')).remove().draw();
		         	     	   				successCount++;		         	     	   						         	     	   			
		         	     	   			}
		         	     	   			totalCount++;
			         	     	   		if (totalCount == checkboxList.length) {
	         	     	   					layer.close(loadIndex);
	         	     	   					layer.alert('操作完毕！成功删除：' + successCount + '条信息，失败：' + (totalCount - successCount) + '条!', {icon:1});
	         	     	   				}
		         	     	   			
		         		        	   });
		        				});
		        				
	        				});	        				
	        			  }}        		
	        		, function(index, layero) {
	        			layer.close(index);
	        			var checkboxList = $("td > input:checked"); 
        				
        				if (checkboxList.length < 1) {
        					return;
        				}
        				var loadIndex = layer.msg('正在批量启动监控...', {icon:16, time:9999999, shade:0.35});
        				var successCount = 0;
        				var totalCount = 0;
        				$.each(checkboxList, function(i, obj) {
        					var data = table.row( $(obj).parents('tr') ).data();
        					var typeName = "";
        	 	     	   	switch (data.type) {
        	 				case "0":
        	 					typeName = "linux";
        	 					break;
        	 				case "1":
        	 					typeName = "weblogic";
        	 					break;
        	 				case "2":
        	 					typeName = "tomcat";
        	 					break;
        	 				case "3":
        	 					typeName = "JVM";
        	 					break;
        	 				default:
        	 					break;
        	 				}
        	 	     	  getUserKey();
        	 	     	  $.post(typeName.toLowerCase() + '/useHistory', {serverId:data.serverId, userKey:userKey}, function(json) {
        	 	     		   json = JSON.parse(json);
        	 	     		   if (json.returnCode == 0) {	      
        	 	     			 successCount++;       	 	     			         	 	     			       	 	     			
        	 	     		   }
        	 	     		   totalCount++;
        	 	     		   if (totalCount == checkboxList.length) {
        	 	     			   layer.close(loadIndex);
        	 	     			   layer.alert('启动完毕!正常启动' + successCount + '个,失败：' + (totalCount - successCount) + '个！', {icon:1});
        	 	     		   }
        	 	     	   });
        	 	     	   	
        					
        				});       				       				
	        		}
	        		, function(index, layero) {
	        			layer.close(index);
	        			layer.open({
	            			type:1,
	            			title:'批量添加服务器信息',
	            			content:batchSaveHtml,
	            			area: ['700px', '585px'],
	            			success:function() {
	            				form.render();
	            			}
	            		});
        		});
        	});
 			
 			//刷新表格
 			$(document).delegate('.server-btn-util > .right-btn-tool > button:eq(3)', 'click', function() {
        		table.ajax.reload(function(data) {
        			layer.msg('重新加载成功!', {icon:1, time:1500});
        		} ,false);
        	});
 			
 			//筛选
 			$('.layui-table').delegate('.server-tags-label', 'click', function() {
 				var that = this;
 				table.columns(8).search($(that).text()).draw();
 			});
 			
 			//复制
 			$('.layui-table').delegate('.copy-server', 'click', function() {
 				var data = table.row( $(this).parents('tr') ).data();
 				
 				layer.open({
	        			type:1,
	        			title:'复制服务器信息',
	        			content:singleSaveHtml,
	        			area: ['680px', '580px'],
	        			success:function() {
	        				$.each(data, function(key, value) {
	        					if ($("#" + key)) {
	        						$("#" + key).val(value);
	        					}
	        					$("#serverId").val("");
	        				});
	        				form.render();
	        			}
	        		});
 				
/* 				layer.confirm('请点击下方按钮选择复制模式[ip/端口号/用户名相同的信息将不会被保存]', {title:'服务器复制工具', btn: ['批量复制', '单条复制'], icon:3},
 						function(index, layero) {
 							layer.close(index);
 							layer.msg("这个功能不存在的!", {time:1500, icon:0});							
 						}, function(index, layero) {
 							layer.close(index);
 							
 						}); */				
 			});
        	
        	//编辑
 			$('.layui-table').delegate('.update-server', 'click', function() {
        		var data = table.row( $(this).parents('tr') ).data();
        		layer.open({
        			type:1,
        			title:'编辑服务器信息',
        			content:singleSaveHtml,
        			area: ['680px', '580px'],
        			success:function() {
        				$.each(data, function(key, value) {
        					if ($("#" + key)) {
        						$("#" + key).val(value);
        					}
        				});
        				form.render();
        			}
        		});
        	});      	
        	
        	//监听更新/添加表单提交事件
 			form.on('submit(singleEdit)', function(data){
 				getUserKey();
 				data.field.userKey = userKey;
	 			$.post('server/edit', data.field, function(json) {
	     		  json = JSON.parse(json);
	     		  if (json.returnCode == 0) {
	     			  table.ajax.reload(null, false);
	     			  layer.closeAll('page');
	     			  layer.msg('更新/添加成功!', {icon:1, time:1500});
	     		  } else {
	     			  layer.alert(json.msg, {icon:5});
	     		  }	         	        		  
	 			});
	 			return false; 
 			});	 
 			
 			//批量添加信息表单提交监听
 			form.on('submit(batchSave)', function(data){
 				getUserKey();
 				data.field.userKey = userKey;
 				var loadIndex = layer.msg('正在添加信息...', {icon:16, time:9999999, shade:0.35});
 				$.post('server/batchSave', data.field, function(json) {
 		     		  json = JSON.parse(json);
 		     		  layer.close(loadIndex);
 		     		  if (json.returnCode == 0) {
 		     			  table.ajax.reload(null, false);
 		     			  layer.closeAll('page');
 		     			  layer.alert(json.msg, {icon:1});
 		     		  } else {
 		     			  layer.alert(json.msg, {icon:5});
 		     		  }
 				});
 				return false;
 			});
 			
 			
        	//删除
 			$('.layui-table').delegate('.del-server', 'click', function() {
 				var data = table.row( $(this).parents('tr') ).data();
 	     	   	var that = this;
 	     	   	layer.confirm('确认删除此条信息?(删除不会影响正在监控的任务!)', {title:'警告', icon:3}, function(index) {
 	     	   		layer.close(index);
 	     	   		$.post('server/del', {serverId:data.serverId}, function(json) {
 	     	   			json = JSON.parse(json);
 	     	   			if (json.returnCode == 0) {
 	     	   				table.row($(that).parents('tr')).remove().draw();
 	     	   				layer.msg('已删除', {icon:1, time:1500});
 	     	   			} else {
 	     	   				layer.alert('删除失败:' + json.msg, {icon:5});
 	     	   			}		         	        		   
 		        	   });
 		           });
        	});
 			
 			
        	         	           	
 			//监控
 			$('.layui-table').delegate('.start-server', 'click', function() {
 				var data = table.row( $(this).parents('tr') ).data();
 	     	   	var typeName = "";
 	     	   	switch (data.type) {
 				case "0":
 					typeName = "linux";
 					break;
 				case "1":
 					typeName = "weblogic";
 					break;
 				case "2":
 					typeName = "tomcat";
 					break;
 				case "3":
 					typeName = "JVM";
 					break;
 				default:
 					break;
 				}
 			   //var loadIndex = layer.load(2, {shade:0.35});
 	     	   var loadIndex = layer.msg('尝试连接中...', {icon:16, time:9999999, shade:0.35});
 	     	   getUserKey();
 	     	   $.post(typeName.toLowerCase() + '/useHistory', {serverId:data.serverId, userKey:userKey}, function(json) {
 	     		   layer.close(loadIndex);
 	     		   json = JSON.parse(json);
 	     		   if (json.returnCode == 0) {	      
 	     			  layer.msg('开启监控成功,你可以返回控制台查看!', {icon:1, time:1500});
 	     		   } else {
 	     			  layer.alert('开启监控失败:' + json.msg, {icon:5});
 	     		   }	         	        		   
 	     	   });
        	});        
    	
			$(".server-btn-util > .layui-btn-group > button").click(function() {
				var type = $(this).attr("data-type");
				var ajaxUrl = "server/list";
				if (type != "" && type != null) {
					ajaxUrl += ajaxUrl + "?type=" + type;
				}
				var that = this;
				table.ajax.url(ajaxUrl).load(function(json) {
					table.columns(8).search('').draw();
					$(that).removeClass("layui-btn-primary").addClass("layui-btn-normal").siblings("button").removeClass("layui-btn-normal").addClass("layui-btn-primary");  			
				}, false);
				
			});
    	
	    	//添加服务器
	    	$('.server-btn-util > .right-btn-tool > button:eq(0)').click(function() {    		    		
	    		layer.open({
	    			type:1,
	    			title:'添加新的服务器',
	    			content:singleSaveHtml,
	    			area: ['680px', '580px'],
	    			success:function() {
	    				form.render();
	    			}
	    		});
	    	});
	    	
	    	//命令执行
	    	$('.server-btn-util > .right-btn-tool > button:eq(2)').click(function() {    		    		
	    		
	    	});
	    	
	    	//添加或者编辑页面切换类型时改变parameters
	    	form.on('select(type-single)', function(data){
	    		$("#parameters").val(JSON.stringify(additionItemParameters[$("#type").val()])); 	    			    		
	    	});      
	    	//添加或者编辑页面切换类型时改变parameters
	    	form.on('select(type-batch)', function(data){
	    		 var type = $("#type").val();
	    		 var tips = ",";
	    		 $.each(additionItemParameters[type], function(key, value) {
	    			 tips += key + ",";
	    		 });
	    		 tips = tips.substring(0, (tips.length - 1));
	    		 
	    		 $("#batch-add-server-tip").text("添加格式：主机IP,端口,用户名,密码,备注" + tips);
	    	}); 
	    	
	    	
	    	//打开附加项编辑页面
	    	$(document).delegate('#setting-parameters', 'click', function() {
	    		var settingParameters = $("#parameters").val();
	    		if (settingParameters == null || settingParameters == "") {
	    			settingParameters = JSON.stringify(additionItemParameters[$("#type").val()]);
	    		}
	    		settingParameters = JSON.parse(settingParameters);
	    		
	    		var html = '<form class="layui-form layui-form-pane setting-parameters" action="">';
	    		$.each(settingParameters, function(key, value) {
	    			html += '<div class="layui-form-item"><label class="layui-form-label">' + key + '</label>'
	    				+ '<div class="layui-input-block">'
	    				+ '<input type="text" name="' + key + '" id="' + key + '" autocomplete="off" class="layui-input" value="' + value + '">'
	    				+ '</div></div>';
	    		});    		
	    		html += '</form>';
	    		
	    		layer.open({
	    			type:1,
	    			title:'附加项填写',
	    			content:html,
	    			area:['600px', '360px'],
	    			cancel:function(index, layero) {
	    				$.each(settingParameters, function(key, value) {
	    					if ($(".setting-parameters #" + key)) {
	    						settingParameters[key] = $(".setting-parameters #" + key).val();
	    					}
	    				});
	    				$("#parameters").val(JSON.stringify(settingParameters));
	    				console.log(JSON.stringify(settingParameters));
	    			}	    			
	    		});
	    	});  	
    }); 	
});