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

var userKey = '';
layui.use(['element', 'layer'], function () {
	var $ = layui.jquery;
	var element = layui.element();
	
	var thisDomNav = $(".layui-this");
	
	if (!getUserKey()) {		
		layer.alert('当前浏览器未设置userKey,请在控制台页面点击选择已有或者新建.<br>[设置userKey是为了区分不同用户的工作空间，多个用户可以共用一个userKey]', 
				{icon:3, title:'提示'}, function(index) {
			layer.close(index);
		});
	}	
	
	
	$.fn.dataTable.ext.errMode = 'throw';
	
	//layui.util.fixbar();
	 //侧边导航开关点击事件
    $('.blog-navicon').click(function () {
        var sear = new RegExp('layui-hide');
        if (sear.test($('.blog-nav-left').attr('class'))) {
            leftIn();
        } else {
            leftOut();
        }
    });
    //侧边导航遮罩点击事件
    $('.blog-mask').click(function () {
        leftOut();
    });   
    
    
  //显示侧边导航
    function leftIn() {
        $('.blog-mask').unbind('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend');
        $('.blog-nav-left').unbind('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend');

        $('.blog-mask').removeClass('maskOut');
        $('.blog-mask').addClass('maskIn');
        $('.blog-mask').removeClass('layui-hide');
        $('.blog-mask').addClass('layui-show');

        $('.blog-nav-left').removeClass('leftOut');
        $('.blog-nav-left').addClass('leftIn');
        $('.blog-nav-left').removeClass('layui-hide');
        $('.blog-nav-left').addClass('layui-show');
    }
    //隐藏侧边导航
    function leftOut() {
        /*$('.blog-mask').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
            $('.blog-mask').addClass('layui-hide');
        });
        $('.blog-nav-left').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
            $('.blog-nav-left').addClass('layui-hide');
        });*/
    	
    	$('.blog-mask').addClass('layui-hide');
    	$('.blog-nav-left').addClass('layui-hide');

        $('.blog-mask').removeClass('maskIn');
        $('.blog-mask').addClass('maskOut');
        $('.blog-mask').removeClass('layui-show');

        $('.blog-nav-left').removeClass('leftIn');
        $('.blog-nav-left').addClass('leftOut');
        $('.blog-nav-left').removeClass('layui-show');
    }
});

/**
 * 自定义扩展的jquery方法
 * 已配置的方式代理事件
 */
$.fn.delegates = function(configs) {
     el = $(this[0]);
     for (var name in configs) {
          var value = configs[name];
          if (typeof value == 'function') {
               var obj = {};
               obj.click = value;
               value = obj;
          };
          for (var type in value) {
               el.delegate(name, type, value[type]);
          }
     }
     return this;
};

/**
 * 获取json长度
 * @param jsonData
 * @returns {Number}
 */
function getJsonLength(jsonData) {
	var jsonLength = 0;
	for(var item in jsonData){
		jsonLength ++;
	}
	return jsonLength;
}

function getUserKey() {
	var d = layui.data("rmp");
	userKey = d.userKey;
	if (userKey == null) {
		return false;
	}
	
	$.post('server/setUserKey', {userKey:userKey}, function(json) {
		  json = JSON.parse(json);
		  if (json.returnCode != 0) {					  
			  layer.alert('无法申请用户空间：' + json.msg, {icon:5});
		  }
	  });
	return true;
}