<%@ page trimDirectiveWhitespaces="true" contentType="text/html;charset=GBK"%>
<%@ include file="/front/public/includefiles/taglib.jsp" %>
<%@ include file="/front/public/includefiles/path.jsp" %>
<script>
	var jastOpenMenu="";<%/* 记录最后一次点击左菜单的ID号 */%>
	function selectMenu(menuID){
		if(menuID){
			$("#"+menuID).click();
		}else if(jastOpenMenu){
			$("#"+jastOpenMenu).click();
		}
	}
	jQuery(document).ready(function(){
		$(".menu_1").click(function(){<%/* 一级菜单点击事件 */%>
			if($(this).attr("class")=='menu_1'){
				$(this).attr("class","menu_2");
			}else{
				$(this).attr("class","menu_1");
			}
			$("#"+$(this).attr("id")+"Div").toggle("fast");
			return false;
		});
		$(".menu_3").click(function(){<%/* 二级菜单点击事件 */%>
			jastOpenMenu=$(this).attr("id");
			mymenu.close("openUpdateMenuDiv");
			$(this).parent().show();<%/* 打开本二级菜单的父菜单 */%>
			$(".menu_4").attr("class","menu_3");<%/*将以前选中的二级菜单样式还原*/%>
			$(this).attr("class","menu_4");<%/*修改当前点击二级菜单的样式*/%>
			$("#mainfrm").attr("action",$(this).attr("action"));
	     	$("#mainfrm").submit();
			return false;
		});
		$("#openUpdateMenuDiv").click(function(){<%/* 打开设置我的菜单div */%>
			var html = $("#updateMenuDiv").html();
			mymenu.show($(this).attr("id"), html);
			return false;
		});
		$(".menu_1")[0].click();<%/* 设置首个一级菜单打开 */%>
	});
</script>
<form id="mainfrm" name="mainfrm" target="main" method="post" action=""></form>
<%/* 展示信息 */%>
<div class="left_titlebor<c:if test="${empty modelContextMap[selfModuleID]['homepageAction']}">2</c:if>" style="height: 100%;width:100%;z-index:10;">
	<div class="title">管理中心</div>
	<!-- <div class="titler">
	<c:if test="${not empty modelContextMap[selfModuleID]['welcomePage']}">
	<a href="${basePath}${modelContextMap[selfModuleID]['welcomePage']}" target="_top">进入首页</a>
	</c:if>
	</div> -->
	<!-- <div class="main">
	<c:if test="${not empty modelContextMap[selfModuleID]['homepageAction']}">
	<a href="${basePath}${modelContextMap[selfModuleID]['homepageAction']}}" target="main">平台主页</a>
	</c:if>
	</div> -->
	<!--  <div class="main"></div> -->
	<div class="clear"></div>
	<%/* 我的菜单信息展示 */%>
	<%-- <c:if test="${not empty mymenu}">
	<div class="menu_1" id="mymenu">我的菜单</div>
	<div id="mymenuDiv" style="display: none">
		<c:forEach var="menu" items="${mymenu}">
		<div id="f${menu.value.id}" class="menu_3" action="${basePath}${menu.value.visiturl}">${menu.value.name}</div>
		</c:forEach>
	<div id="f4001001000" class="menu_3">IPO申购</div>
	</div>
	</c:if> --%>
	<%/* 遍历循环菜单展示 */%>
	<div class="menu_1" id="4001001000">商品发售</div>
	<div id="4001001000Div" style="display: none">
		<div class="menu_3" id="4001001010" action="<%=request.getContextPath()%>/CommodityController/ipoapply">商品申购</div>
		<div class="menu_3" id="4001001020" action="<%=request.getContextPath()%>/CommodityController/OrderQuery">申购记录</div>
		<div class="menu_3" id="4001001030" action="<%=request.getContextPath()%>/CommodityController/DistribQuery">配号记录</div>
		<div class="menu_3" id="4001001040" action="<%=request.getContextPath()%>/CommodityController/SelectedQuery">中签记录</div>
	</div>
	<div class="menu_1" id="3001001000">交收提货</div>
	<div id="3001001000Div" style="display: none;">
		<div class="menu_3" id="3001001010" action="<%=request.getContextPath()%>/front/app/withdraw.html">提货申请</div>
		<div class="menu_3" id="3001001010" action="<%=request.getContextPath()%>/front/app/customer.html">自提打印</div>
		<div class="menu_3" id="3001001020" action="<%=request.getContextPath()%>/front/app/cancel.html">撤销提货</div>
		<div class="menu_3" id="3001001030" action="<%=request.getContextPath()%>/front/app/deliver.html">提货查询</div>
		<div class="menu_3" id="3001001040" action="<%=request.getContextPath()%>/front/app/cost.html">费用查询</div>
	</div>
	<%-- <a href="mailto: <%=Global.getMarketInfoMap().get("marketEmail") %>"><div class="left_ad"></div></a> --%>
</div>
<%/* 隐藏层信息 */%>
<%-- <div style="display: none;">
	<iframe name="hiddenframe" width=0 height=0 application='yes'></iframe>
	<div id="updateMenuDiv">
		<form id="changeMymenuFrm" action="${basePath}/menu/changemymenu.action" target="hiddenframe" method="post">
			<table width="600" border="0" cellspacing="10" cellpadding="0" bgcolor="#FFF">
				<tr>
					<td height="30" colspan="5">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td colspan="3" height="30" class="font_14B borderB">设置常用菜单</td>
								<td class="borderB">&nbsp;</td>
							</tr>
							<tr>
								<td height="30" colspan="4" bgcolor="#F2F2F2">
									&nbsp;&nbsp;&nbsp;&nbsp;适当展示您的菜单有助于您的操作方便
								</td>
							</tr>
							<tr valign="top">
						<c:set var="status" value="0"></c:set>
						<c:forEach var="mainMenu" items="${HaveRightMenu.childMenuSet}">
							<c:if test="${status != 0 && status%4==0}">
							</tr><tr valign="top">
							</c:if>
								<c:set var="status" value="${status+1}"></c:set>
								<td width="25%" height="30" valign="top">
									<table width="100%" border="0" align="center" cellpadding="0"
										cellspacing="0">
										<tr>
											<td>
												<strong>${mainMenu.name}</strong>
											</td>
										</tr>
										<c:forEach var="childMenu" items="${mainMenu.childMenuSet}">
											<tr>
												<td>
													<c:set var="menuid" value="${childMenu.id}"></c:set>
													<c:if test="${not empty mymenu[childMenu.id]}">
														<input type="checkbox" checked="checked" name="mymenu"
															value="${childMenu.id}" />${childMenu.name}
									</c:if>
													<c:if test="${empty mymenu[childMenu.id]}">
														<input type="checkbox" name="mymenu"
															value="${childMenu.id}" />${childMenu.name}
									</c:if>
												</td>
											</tr>
										</c:forEach>
									</table>
								</td>
								</c:forEach>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="4" class="borderT">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="submit" id="buttonMenu" value="提交" class="button"/>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="reset"  value="重置" class="button"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div> --%>