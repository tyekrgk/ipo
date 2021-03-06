$(document).ready(function() {
	 $('#dg').datagrid({  
         title:'托管仓库管理',  
         iconCls:'icon-ok', 
         method:"post",
         height:400,
         pageSize:10,  
         pageList:[5,10,15],  
         singleSelect:true,
         toolbar:"#tb", 
         nowrap:true,  
         striped:true,  
         collapsible:false,  
         url:  getRootPath () + "/trusteeshipWarehouseController/trusteeWarehouseManage" ,  
         loadMsg:'数据加载中......',  
         fitColumns:true,//允许表格自动缩放,以适应父容器   
         columns : [ [ {
        	 field : 'commodityId',  
             width : 200, 
             align: "center",
             title : '商品代码',
             formatter:function(value,row){
         	    return "<a href=\"#\" onclick=\"updateTrusteeWareHouse('"+value+"','"+row.commodityName+"')\">"+value+"</a>";
         	    
         	    
         }
         }, {
        	 field : 'commodityName',  
             width : 200,  
             align: "center",
             title : '商品名称'
         }
         ]],  
         pagination : true 
     });  
	 var p = $('#dg').datagrid('getPager'); 
	    $(p).pagination({ 
	        beforePageText: '第',
	        afterPageText: '页    共 {pages} 页', 
	        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
	    }); 
});


	
 function doSearch(){
	$('#dg').datagrid('load',{
		commodityId: $('#commodityId').val(),
		commodityName: $('#commodityName').val()
	});
}


function clearInfo(){
	$("#commodityId").val("");
	$("#commodityName").val("");
}

function addTrusteeWareHouse(){
	document.location.href = getRootPath () + "/trusteeshipWarehouseController/updateTrusteeWarehouse?flag=create";//encodeURI(encodeURI(commName))

}
function updateTrusteeWareHouse(commId,commName){
	document.location.href = getRootPath () + "/trusteeshipWarehouseController/updateTrusteeWarehouse?commId="+commId+"&&commName="+commName +"&&flag=update";
}

function deleteTrusteeWareHouse(){
	 var row = $("#dg").datagrid("getSelected"); 
	  if(row){
	if(confirm("确定删除该记录吗？")){
		  $.post(getRootPath ()+"/trusteeshipWarehouseController/deleteTrusteeWarehouse",{"commId":row.commodityId},function(data,status){
			  if(data=='true'){
				  alert("删除成功！")
				  $('#dg').datagrid('reload');
			  }
			  if(data=='false'){
				  alert("删除失败！");
			  }
				  });}
	}
		  else{
			  alert("请先选中一行再进行删除！");
		  }
}

