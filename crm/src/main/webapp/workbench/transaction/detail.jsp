<%@ page import="com.bjpowernode.crm.settings.domain.DicValue" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.bjpowernode.crm.workbench.domain.Tran" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

	//在服务器缓存中取得最新的数据字典，因为项目之后的维护可能向数据库的数据字典添加新的字段
	List<DicValue> dvList = (List<DicValue>) application.getAttribute("stage");
	//因为要根据可能性区判断正常的阶段还是不正常的阶段的分界点，所以需要在服务区缓存中取出阶段和可能性的对应关系
	Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
	Set<String> set = pMap.keySet();
	int point = 0;
	for(int i=0;i<dvList.size();i++){
		DicValue dv = dvList.get(i);
		String stage = dv.getValue();
		String possibility = pMap.get(stage);
		if("0".equals(possibility)){
			point = i;
			break;
		}
	}


%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

<style type="text/css">
.mystage{
	font-size: 20px;
	vertical-align: middle;
	cursor: pointer;
}
.closingDate{
	font-size : 15px;
	cursor: pointer;
	vertical-align: middle;
}
</style>
	
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});
		
		
		//阶段提示框
		$(".mystage").popover({
            trigger:'manual',
            placement : 'bottom',
            html: 'true',
            animation: false
        }).on("mouseenter", function () {
                    var _this = this;
                    $(this).popover("show");
                    $(this).siblings(".popover").on("mouseleave", function () {
                        $(_this).popover('hide');
                    });
                }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });

		//当交易详情页面加载完毕之后发送请求查询历史
		showTranHistoryList();

		//为阶段图标绑定事件
		changeStage(stage,index);
	});
	function showTranHistoryList() {
		$.ajax({
			url:"workbench/transaction/getTranHistoryListById.do",
			data:{
				"id":"${t.id}"
			},
			type:"get",
			dataType:"json",
			success:function (data) {
				var html = "";
				$.each(data,function (i,v) {
					html += '<tr>';
					html += '<td>'+v.stage+'</td>';
					html += '<td>'+v.money+'</td>';
					html += '<td>'+v.possibility+'</td>';
					html += '<td>'+v.expectedDate+'</td>';
					html += '<td>'+v.createTime+'</td>';
					html += '<td>'+v.createBy+'</td>';
					html += '</tr>';
				})
				$("#tranHistoryBody").html(html);
			}
		});
	}

	function changeStage(stage,index) {
		$.ajax({
			url:"workbench/transaction/changeStage.do",
			data:{
				"id":"${t.id}",
				"stage":stage,
				"money":"${t.money}",
				"expectedDate":"${t.expectedDate}"
			},
			type:"post",
			dataType:"json",
			success:function (data) {
				if(data.success){
					$("#stage").html(data.t.stage);
					$("#possibility").html(data.t.possibility);
					$("#editBy").html(data.t.editBy);
					$("#editTime").html(data.t.editTime);
					showTranHistoryList();
					changeStageClsss(stage,index);
				}else{
					alert("修改阶段失败");
				}
			}
		});
	}

	function changeStageClsss(stage,index) {
		//当前阶段
		var currentStage = stage;
		//当前阶段的可能性
		var currentPossibility = $("#possibility").html();
		//当前阶段的下标
		var currentIndex = index;
		//正常阶段与不正常阶段的分界点
		var point = "<%=point%>";
		//如果当前阶段的可能性是0，前面7个一定是黑圈，后面两个一个是红叉，一个是黑叉
		if(currentPossibility == "0"){
			//遍历前面七个
			for (var i = 0; i < point; i++) {
				//黑圈
				$("#"+i).removeClass();
				$("#"+i).addClass("glyphicon glyphicon-record mystage");
				$("#"+i).css("color","black");
			}

			for (var i = point; i < <%=dvList.size()%>; i++) {
				if(i == currentIndex){
					//红叉
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","red");
				}else {
					//黑叉
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","black");
				}
			}

		//如果当前阶段的可能性不是0，前面7个有可能是绿色标记，绿圈，黑圈，后面两个一定是黑叉
		}else{
			for (var i = 0; i < point; i++) {
				if(i == currentIndex){
					//绿色标记
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-map-marker mystage");
					$("#"+i).css("color","#90F790");
				}else if(i < currentIndex){
					//绿圈
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-ok-circle mystage");
					$("#"+i).css("color","#90F790");
				}else{
					//黑圈
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-record mystage");
					$("#"+i).css("color","black");
				}
			}

			for (var i = point; i < <%=dvList.size()%>; i++) {
				$("#"+i).removeClass();
				$("#"+i).addClass("glyphicon glyphicon-remove mystage");
				$("#"+i).css("color","black");
			}
		}
	}
	
	
</script>

</head>
<body>
	
	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${requestScope.t.customerId}-${t.name} <small>${t.money}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" onclick="window.location.href='edit.jsp';"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>

	<!-- 阶段状态 -->
	<div style="position: relative; left: 40px; top: -50px;">
		阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

		<%
			//取得当前阶段
			Tran t = (Tran) request.getAttribute("t");
			//取得当前阶段的可能性
			String currentStage = t.getStage();
			String currentPossibility = pMap.get(currentStage);
			//如果当前阶段的可能性是0，那么前7个一定是黑圈，后面两个要么是红叉，要么是黑叉
			if("0".equals(currentPossibility)){
				//取得每一个遍历的阶段，取得每个遍历阶段的可能性
				for(int i=0;i<dvList.size();i++){
						DicValue dv = dvList.get(i);
						String listStage = dv.getValue();
						String listPossibility = pMap.get(listStage);
						//如果遍历阶段可能性是0，说明后面两个一个红叉一个是黑叉
						if("0".equals(listPossibility)){
							//如果遍历阶段等于当前阶段
							if(listStage.equals(currentStage)){
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dv.getText()%>" style="color: red;"></span>
		-----------

		<%
							}else{
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dv.getText()%>" style="color: black;"></span>
		-----------

		<%
							}

							//如果遍历阶段可能性不是0，那么前面七个一定是黑圈
						}else {
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dv.getText()%>" style="color: black;"></span>
		-----------

		<%
						}

				}


			//如果当前阶段的可能性不是0，前7个可能是绿圈，绿色标记，黑圈，后面两个一定是黑叉
			}else{
				//取得当前阶段的下标
				int index = 0;
				for (int i = 0; i < dvList.size(); i++) {
					DicValue dv = dvList.get(i);
					String stage = dv.getValue();
					if(stage.equals(currentStage)){
						index = i;
						break;
					}
				}


				for(int i=0;i<dvList.size();i++){
					DicValue dv = dvList.get(i);
					String listStage = dv.getValue();
					String listPossibility = pMap.get(listStage);
					if("0".equals(listPossibility)){
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dv.getText()%>" style="color: black;"></span>
		-----------

		<%
					}else{
						if(i==index){
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-map-marker mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dv.getText()%>" style="color: #90F790;"></span>
		-----------

		<%
						}else if(i<index){
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-ok-circle mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dv.getText()%>" style="color: #90F790;"></span>
		-----------

		<%
						}else{
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dv.getText()%>" style="color: black;"></span>
		-----------

		<%
						}
					}
				}
			}


		%>

		<%--<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="资质审查" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="价值建议" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="确定决策者" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom" data-content="提案/报价" style="color: #90F790;"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="谈判/复审"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="成交"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="丢失的线索"></span>
		-----------
		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="因竞争丢失关闭"></span>
		-------------%>
		<span class="closingDate">${t.expectedDate}</span>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: 0px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.money}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.t.customerId}-${t.name}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">客户名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.t.customerId}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${t.stage}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">类型</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.type}&nbsp;</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="possibility">${t.possibility}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">来源</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.source}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.activityId}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">联系人名称</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${t.contactsId}</b></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${t.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${t.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${t.editBy}&nbsp;&nbsp;</b><small id="editTime" style="font-size: 10px; color: gray;">${t.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${t.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${t.contactSummary}&nbsp;
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 100px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${t.nextContactTime}&nbsp;</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 100px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
		<!-- 备注1 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<!-- 备注2 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 阶段历史 -->
	<div>
		<div style="position: relative; top: 100px; left: 40px;">
			<div class="page-header">
				<h4>阶段历史</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table id="activityTable" class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>阶段</td>
							<td>金额</td>
							<td>可能性</td>
							<td>预计成交日期</td>
							<td>创建时间</td>
							<td>创建人</td>
						</tr>
					</thead>
					<tbody id="tranHistoryBody">
						<%--<tr>
							<td>资质审查</td>
							<td>5,000</td>
							<td>10</td>
							<td>2017-02-07</td>
							<td>2016-10-10 10:10:10</td>
							<td>zhangsan</td>
						</tr>
						<tr>
							<td>需求分析</td>
							<td>5,000</td>
							<td>20</td>
							<td>2017-02-07</td>
							<td>2016-10-20 10:10:10</td>
							<td>zhangsan</td>
						</tr>
						<tr>
							<td>谈判/复审</td>
							<td>5,000</td>
							<td>90</td>
							<td>2017-02-07</td>
							<td>2017-02-09 10:10:10</td>
							<td>zhangsan</td>
						</tr>--%>
					</tbody>
				</table>
			</div>
			
		</div>
	</div>
	
	<div style="height: 200px;"></div>
	
</body>
</html>