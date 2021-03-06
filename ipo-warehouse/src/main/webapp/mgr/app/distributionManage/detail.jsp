<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/mgr/public/includefiles/allincludefiles.jsp"%>
<html>
	<head>
		<title>提货单详情</title>
		<script type="text/javascript" src="<%=request.getContextPath()%>/static/ipo/delivery/detail.js"> 
		</script>
		<style type="text/css">
		tr{margin-top:5px;}
		</style>
</head>
<body leftmargin="14" topmargin="0">
<div class="warning">
		<div class="title font_orange_14b">温馨提示 : 提货单审核 </div>
		<div class="content" style="color: red">此页展示提货单的详细信息，后台管理人员根据详细信息进行审核。  </div>
	</div>
	<table border="0"  width="100%"  >
		<tr>
			<td valign="top">
				<form id="frm" name="frm" method="POST" >
				<fieldset>
				<legend class="common"><b>提货单详情</b></legend>
				<span id="baseinfo9">
				<table width="850" border="0" align="center"  class="common" cellpadding="0" cellspacing="2">
					<!-- 基本信息 -->
        				<tr class="common">
							<td colspan="4">
					      		<fieldset>
					          	<legend>
					           	<table cellspacing="0" cellpadding="0" border="0" width="800" class="common" >
					            	<col width="55"></col><col></col><col width="6"></col>
					               	<tr>
					                 	<td><b style="font-size: 11px;">基本信息</b></td>
					                    <td><hr width="100%" class="pickList"/></td>
					            	</tr>
					         	</table>
					         	</legend>
								<span id="baseinfo">
								<table cellSpacing="0" cellPadding="0" width="800" border="0" align="left" class="common">
        							<tr>
        			 					<td align="right" width="108">提货单号：</td>
	      								<td>
	      								    <input id="deliveryorderId" type="text" name="deliveryorderId" value="${entity.deliveryorderId }" readonly="readonly"/>
		          						</td>
										<td align="right" width="90">商品名称：</td>     
            							<td> 
            								 <input id="commodityName" type="text" name="commodityName" value="${entity.commodityName }" readonly="readonly"/>
										</td>
							        </tr>  
							        <tr>
										<td align="right" width="110">交易商名称：</td>     
            							<td> 
            								 <input id="dealerName" type="text" name="dealerName" value="${entity.dealerName }" readonly="readonly"/>
										</td>
										<td align="right" width="90">提货方式：</td>     
            							<td> 

            							<input id="deliveryMethod" type="text" name="deliveryMethod" value="在线配送" readonly="readonly"/>
            							<input id="methodId" type="hidden" name="methodId" value="${entity.methodId }"/>
            							<input id="deliveryCounts" type="hidden" name="deliveryCounts" value="${entity.deliveryCounts }"/>
            							<input id="position" type="hidden" name="position" value="${entity.position }"/>
										</td>
							        </tr> 
							         <tr>
        			 					<td align="right" width="108">仓库代码：</td>
	      								<td>
	      								    <input id="warehouseId" type="text" name="warehouseId" value="${entity.warehouseId }" readonly="readonly"/>
		          						</td>
										<td align="right" width="110">仓库名称：</td>     
            							<td> 
            								 <input id="warehouseName" type="text" name="warehouseName" value="${entity.warehouseName }" readonly="readonly"/>
										</td>
										<td align="right" width="90">交割数量：</td>     
            							<td> 
            								 <input id="deliveryQuatity" type="text" name="deliveryQuatity" value="${entity.deliveryQuatity }" readonly="readonly"/>
										</td>
							        </tr> 
							         <tr>
        			 					<td align="right" width="108">单位：</td>
	      								<td>
	      								    <input id="unit" type="text" name="unit" value="${entity.unit }" readonly="readonly"/>
		          						</td>
		          						<td align="right" width="90">申请日期：</td>     
            							<td><input type="hidden" value="${applyDate }" id="applytime"/>
            								 <input id="applyDate" type="text" name="applyDate" value="" disabled="disabled"/>
										</td>
										<td align="right" width="110">提货日期：</td>     
            							<td> <input type="hidden" value="${deliveryDate }" id="picktime"/>
            								 <input id="deliveryDate" type="text" name="deliveryDate" value="" disabled="disabled"/>
										</td>
										
							        </tr> 
							        <tr>
							                			 					
										<td align="right" width="110">收货人：</td>     
            							<td> 
            								 <input id="receiver" type="text" name="receiver" value="${detail.receiver }" readonly="readonly"/>
										</td>
        			 					<td align="right" width="108">身份证号：</td>
	      								<td>
	      								    <input id="idcardNum" type="text" name="idcardNum" value="${detail.idcardNum }" readonly="readonly"/>
		          						</td>
										<td align="right" width="90">联系电话：</td>     
            							<td> 
            								 <input id="tel" type="text" name="tel" value="${detail.tel }" readonly="readonly"/>
										</td>
							        </tr> 
							         <tr>
							            <td align="right" width="108">审核意见：</td>
	      								<td>
	      								 <select id="approvalStatus" name="approvalStatus" style="width:142">
												<option value=""></option>
											    <option value="2">通过</option>
												<option value="3">驳回</option>
										   </select> <span class="required">*</span> 
										</td>
        			 					<td align="right" width="110">配送费：</td>
	      								<td>
	      								    <input id="cost" type="text" name="cost"
	      								    class="easyui-numberbox" data-options="required:true,min:0,precision:2,missingMessage:'精度为2的正数(驳回可不填)'"/>
		          						</td>
							        </tr> 
							        <tr>
							        	   <td align="right" width="108">配送地址：</td>
	      								<td colspan="5">
	      								 <input id="address" type="text" name="address" style="width:675px;" value="" readonly="readonly"/>
		          						</td>
							        </tr>
							         <tr>
							      
        			 					<td align="right" width="108">备注：</td>
	      								<td colspan="5">
	      								 <input id="remarks" type="text" name="remarks" style="width: 675px;">
										</td>
							        </tr> 
								</table >
								</span>
						    	</fieldset>
							</td>
						</tr>

						<tr>
							<td colspan="4" align="center">
								<div class="div_gn">
 
								    	<input type="button" value="提交" onclick="" class="anniu_btn"/>
									&nbsp;&nbsp;
									<input type="button" value="返回" onclick="" class="anniu_btn"/>
								</div>
							</td>
						</tr>
					</table>
				</span>
			</fieldset>
			</form>
		</td>
	</tr>
</table>
</body>
</html>
