<%@ page contentType="text/html;charset=GBK"%>
<%@ include file="/mgr/public/includefiles/allincludefiles.jsp"%>
<%--������Ʒ�Ƿ�˰ ������������ֱ��ʹ�� 1Ϊ����˰ 0Ϊ��˰ --%>
<c:set value="${entity.taxIncluesive}" var="WT" scope="page"/>
<html>
  <head>
    <title>�򷽱�֤��ת������Ϣ</title>
    
    <link rel="stylesheet" href="${skinPath }/css/validationengine/validationEngine.jquery.css" type="text/css" />
	<link rel="stylesheet" href="${skinPath }/css/validationengine/template.css" type="text/css" />
	<script src="${publicPath }/js/jquery-1.6.min.js" type="text/javascript"></script>
	<script src="${publicPath }/js/validationengine/languages/jquery.validationEngine-zh_CN.js" type="text/javascript" charset="GBK"></script>
	<script src="${publicPath }/js/validationengine/jquery.validationEngine.js" type="text/javascript" charset="GBK"></script>
    <script type="text/javascript">

	    function checkNum()
		{
			if(frm.percent.value!='')
			{
				if(Number(frm.percent.value)){
					var percentMoney = (frm.totalMoney.value)*0.01*(frm.percent.value);
					frm.thisPayMent.value = percentMoney.toFixed(2);
				}
			}else{
				var val= frm.thisPayMent.value
				if(val.indexOf(".") != -1 && val.substring(val.indexOf(".")+1,val.length).length>2){
					return "* С�������Ϊ2λ";
				}
			}
		}
	
	  jQuery(document).ready(function() {
			
		  $("#frm").validationEngine('attach');
			
			$("#update").click(function(check) {
				if ($("#frm").validationEngine('validate')) {
					if(Number(frm.thisPayMent.value)==0){
						alert("����������");
					}else{
						var vaild = affirm("��ȷ��Ҫ������");
						if (vaild == true) {
							$("#frm").validationEngine('attach');
							//$('#frm').attr('action', 'action');
						    $("#frm").submit();
						}
					}
			}})
	});
    </script>
    
  </head>
  <body>
  <form id="frm" enctype="multipart/form-data" method="post" targetType="hidden" action="${basePath}/timebargain/settle/matchDispose/marginToPayout.action?entity.matchID=${entity.matchID}" >
    <div class="div_cx">
	  <table border="0" width="100%" align="center">
	    <tr>
		  <td>
		    <div class="warning">
			  <div class="content">
			          ��ܰ��ʾ :������ԡ�${entity.matchID}���򷽱�֤��ת������Ϣ
			  </div>
			</div>
		  </td>
		</tr>
		<tr>
		  <td>
		    <table border="0" width="100%" align="center">
			  <tr>
			  
			    <td>
				  <div class="div_cxtj">
				    <div class="div_cxtjL"></div>
					<div class="div_cxtjC">
					     �򷽱�֤��ת������Ϣ
					</div>
					<div class="div_cxtjR"></div>
				  </div>
				  <div style="clear: both;"></div>
				  <div>
				    <table border="0" cellspacing="0" cellpadding="8" width="100%" align="center" class="table2_style">
					  <tr>
			            <td align="right">�򷽽����̴��룺</td>
						<td >
						  ${entity.firmID_B }&nbsp;
						</td>
					  </tr>
					  <tr>
			             <td align="right">��Ʒ���룺</td>
						<td>
						  ${entity.commodityID}&nbsp;
						</td>
					  </tr>
					  <tr>
					  <td align="right">����������</td>
						<td>
							${entity.quantity }&nbsp;
						</td>
					  </tr>
					  <tr>
			           <td align="right">�����򷽱�֤��</td>
						<td>
						  <fmt:formatNumber value="${entity.buyMargin }" pattern="#,##0.00"/>&nbsp;
						</td>
					  </tr>
					  <tr>
					    <td align="right">�����򷽻��</td>
						<td >
						  <fmt:formatNumber value="${entity.buyPayout}" pattern="#,##0.00"/>
						&nbsp;
						</td>
					  </tr>
					  <tr>
			            <td align="right">�����򷽱�֤��ת���ٷֱȣ�</td>
						<td>
						 <input class="validate[custom[number],funcCall[checkNum]]" id="percent" name="percent" size="12" /><font color="red">%</font>
						 <input type="hidden" name="totalMoney" value="${entity.buyMargin }">
						</td>
					  </tr>
					  <tr>
			            <td align="right"><span class='required'>*</span>���λ�ת��</td>
						<td>
						 <input class="validate[required,custom[number],funcCall[checkNum]]" id="thisPayMent" name="thisPayMent" size="12" />
						</td>
					  </tr>
					</table>
				  </div>
				</td>
				</tr>
			</table>
		  </td>
		</tr>
		
		
	  </table>
	</div>
	
	<div class="tab_pad">
	  <table border="0" cellspacing="0" cellpadding="10" width="100%" align="center">
	    <tr align="center">   
		  <td>
			
						<rightButton:rightButton name="����" onclick="" className="btn_sec"
							action="/timebargain/settle/matchDispose/marginToPayout.action" id="update" ></rightButton:rightButton>
		  </td>
	    </tr>
	  </table>
    </div>
	</form>	
  </body>
</html>
<%@ include file="/mgr/public/jsp/footinc.jsp"%>