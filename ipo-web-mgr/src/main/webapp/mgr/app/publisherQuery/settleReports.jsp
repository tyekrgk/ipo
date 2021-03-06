<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="../ipoInclude.jsp"%>
<html>
<head>
<title>发行会员结算表</title>
<script type="text/javascript">

$(function () {
    $("#queryDate").datebox({
   	    editable: false,
        required: true,
        missingMessage: "必填项",
        formatter: function (date) {
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        var d = date.getDate();
        return y + "-" + (m < 10 ? ("0" + m) : m) + "-" + (d < 10 ? ("0" + d) : d);
      }
     });
});

function setPublisher(value){
	$("#publisherid").val(value);
} 

 function query(){
	 var flag= $('#frm').form('validate');
	 if(flag==true)
	{
	document.location.href= '<%=request.getContextPath()%>/PublisherController/showSettleLists?publisherid='+$("#publisherid").val()+'&&queryDate='+$("#queryDate").datebox("getValue");
	}} 

</script>
</head>
<body>
	<form method="POST" action="" name="frm" id="frm">
        <table border="0" height="40%" width="60%" align="center">
			<tr>
				<td>
              <fieldset class="pickList" >
	                 <legend class="common"><b>发行会员结算报表</b></legend>
		<table border="0" align="center" cellpadding="5" cellspacing="5" class="common" width="100%">
			<tr>
	           	<td align="center" colspan="2" style="color:red"></td>
	        </tr>  
	         <tr>
	        	<td align="right" style="font-size:15px" width="50%"></td>
	            <td align="left" width="60%">
	            <span class="required">（填空为查询全部）</span>  
	            </td>
	        </tr>   
	        <tr>
	        	<td style="font-size:15px" align="right" width="20%">发行会员：</td>
	        	<td align="left" width="50%">
	        	<input type="text" id="publisherid" name="publisherid" style="width:100px"/>
	        	<select id="publisher" name="publisher" style="width:100px" onchange="setPublisher(value)">
						<option value="">请选择</option>
                         <c:forEach var="broker" items="${brokerlist}">
                         <option value="${broker.brokerid}">${broker.brokerid}(${broker.name})</option>
                          </c:forEach>
				</select><span class="required">*</span>
	        	</td>
	        </tr>  
	         <tr>
	        	<td style="font-size:15px" align="right" width="20%">查询日期：</td>
	        	<td align="left" width="50%">
	        	 <input style="width:100px;" type="text" id="queryDate" name="queryDate"/><span class="required">*</span>
	        	</td>
	        </tr> 
		  	<tr>
				<td align="right">
					<input type="button" value="查看" onclick="query()"/>
		    	</td>
		    	<td align="left"><input type="button" value="保存为excel" onclick="saveExcel()"/></td>
		 	</tr>
	    </table>
	</fieldset>
	</td>
	</tr>
</table>
</form>
</body>
</html>
