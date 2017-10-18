layui.use(['element', 'jquery', 'form', 'layedit', 'laytpl'], function () {
    var element = layui.element();
    var form = layui.form();
    var $ = layui.jquery;
    var layedit = layui.layedit;
    var laytpl = layui.laytpl;
    
    var msgTpl = laytpl($("#leave-message-comment").html());
    
    
    
    $.get("server/listMsg", function(json) {
    	json = JSON.parse(json);
    	if (json.returnCode == 0) {
    		$(".blog-comment").html(msgTpl.render(json.data));
    	} else {
    		layer.alert(json.msg, {icon:5});
    	}
    });
    
    //评论和留言的编辑器
    var editIndex = layedit.build('remarkEditor', {
        height: 150,
        tool: ['face', '|', 'left', 'center', 'right', '|', 'link'],
    });
    //评论和留言的编辑器的验证
    layui.form().verify({
        content: function (value) {
            value = $.trim(layedit.getText(editIndex));
            if (value == "") return "至少得有一个字吧";
            layedit.sync(editIndex);
        }
    });

    //Hash地址的定位
    var layid = location.hash.replace(/^#tabIndex=/, '');
    if (layid == "") {
        element.tabChange('tabAbout', 1);
    }
    element.tabChange('tabAbout', layid);

    element.on('tab(tabAbout)', function (elem) {
        location.hash = 'tabIndex=' + $(this).attr('lay-id');
    });

    //监听留言提交
    form.on('submit(formLeaveMessage)', function (data) {   
    	var index = layer.load(1);
    	$.post("server/saveMsg", {userKey:userKey, content:data.field.editorContent}, function(json) {
    		layer.close(index);
    		json = JSON.parse(json);
    		if (json.returnCode == 0) {
    			var html = '<li><div class="comment-parent"><img src="./images/user/' + Math.floor(Math.random()*6 + 1) + '.png"alt="' + json.message.username + '"/><div class="info"><span class="username">' + json.message.username + '</span></div><div class="content">' + json.message.content + '</div><p class="info info-footer"><span class="time">' + json.message.createTime + '</span></p></div></li>';
    			$('.blog-comment').append(html);
                $('#remarkEditor').val('');
                editIndex = layui.layedit.build('remarkEditor', {
                    height: 150,
                    tool: ['face', '|', 'left', 'center', 'right', '|', 'link'],
                });
                layer.msg("留言成功", { icon: 1 });                
    		} else {
    			layer.alert(json.msg, {icon:5});
    		}   		
    	});   	
    	return false;
       /* //模拟留言提交
        setTimeout(function () {
            layer.close(index);
            var content = data.field.editorContent;
            var html = '<li><div class="comment-parent"><img src="./images/Logo_40.png"alt="模拟留言"/><div class="info"><span class="username">模拟留言</span></div><div class="content">' + content + '</div><p class="info info-footer"><span class="time">2017-03-18 18:09</span><a class="btn-reply"href="javascript:;" onclick="btnReplyClick(this)">回复</a></p></div><!--回复表单默认隐藏--><div class="replycontainer layui-hide"><form class="layui-form"action=""><div class="layui-form-item"><textarea name="replyContent"lay-verify="replyContent"placeholder="请输入回复内容"class="layui-textarea"style="min-height:80px;"></textarea></div><div class="layui-form-item"><button class="layui-btn layui-btn-mini"lay-submit="formReply"lay-filter="formReply">提交</button></div></form></div></li>';
            $('.blog-comment').append(html);
            $('#remarkEditor').val('');
            editIndex = layui.layedit.build('remarkEditor', {
                height: 150,
                tool: ['face', '|', 'left', 'center', 'right', '|', 'link'],
            });
            layer.msg("留言成功", { icon: 1 });
        }, 500);
        return false;*/
    });
});
systemTime();
console.log("调用server/delAllMsg来删除所有留言,慎用!");
function systemTime() {
    //获取系统时间。
    var dateTime = new Date();
    var year = dateTime.getFullYear();
    var month = dateTime.getMonth() + 1;
    var day = dateTime.getDate();
    var hh = dateTime.getHours();
    var mm = dateTime.getMinutes();
    var ss = dateTime.getSeconds();

    //分秒时间是一位数字，在数字前补0。
    mm = extra(mm);
    ss = extra(ss);

    //将时间显示到ID为time的位置，时间格式形如：19:18:02
    document.getElementById("time").innerHTML = year + "-" + month + "-" + day + " " + hh + ":" + mm + ":" + ss;
    //每隔1000ms执行方法systemTime()。
    setTimeout("systemTime()", 1000);
}

//补位函数。
function extra(x) {
    //如果传入数字小于10，数字前补一位0。
    if (x < 10) {
        return "0" + x;
    }
    else {
        return x;
    }
}