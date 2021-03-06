package com.yrdce.ipo.modules.sys.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.yrdce.ipo.common.constant.DeliveryConstant;
import com.yrdce.ipo.modules.sys.service.IpoCommConfService;
import com.yrdce.ipo.modules.sys.service.SettlementDeliveryService;
import com.yrdce.ipo.modules.sys.vo.DeliveryCost;
import com.yrdce.ipo.modules.sys.vo.DeliveryOrder;
import com.yrdce.ipo.modules.sys.vo.Express;
import com.yrdce.ipo.modules.sys.vo.Paging;
import com.yrdce.ipo.modules.sys.vo.Pickup;
import com.yrdce.ipo.modules.sys.vo.Position;
import com.yrdce.ipo.modules.sys.vo.ResponseResult;
import com.yrdce.ipo.modules.sys.vo.VIpoCommConf;

/**
 * 
 * @author Bob
 *
 */
@Controller
@RequestMapping("SettlementDeliveryController")
public class SettlementDeliveryController {

	static Logger logger = LoggerFactory
			.getLogger(SettlementDeliveryController.class);
	@Autowired
	private SettlementDeliveryService settlementDeliveryService;
	@Autowired
	private IpoCommConfService ipoCommConfService;

	private final static String NOT_SUFFICIENT_FUNDS = "1003";

	// 提货申请视图
	@RequestMapping(value = "/deliveryview", method = RequestMethod.POST)
	public String deliveryView(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		return "app/delivery/withdraw";
	}

	// 自提打印视图
	@RequestMapping(value = "/printView")
	public String printView(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		return "app/delivery/customer";
	}

	// 撤销提货视图
	@RequestMapping(value = "/revocationView", method = RequestMethod.POST)
	public String revocationView(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		return "app/delivery/cancel";
	}

	// 在线配送视图
	@RequestMapping(value = "/dispatchingView", method = RequestMethod.POST)
	public String dispatchingView(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		return "app/delivery/dispatching";
	}

	// 提货查询视图
	@RequestMapping(value = "/deliveryQueryView", method = RequestMethod.POST)
	public String deliveryQueryView(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		return "app/delivery/deliver";
	}

	// 费用查询视图
	@RequestMapping(value = "/costQueryView", method = RequestMethod.POST)
	public String costQueryView(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		return "app/delivery/cost";
	}

	// 提货申请
	@RequestMapping(value = "/deliveryApply", method = RequestMethod.POST)
	@ResponseBody
	public String deliveryApply(DeliveryOrder deliveryOrder, HttpSession session) {
		String method = deliveryOrder.getDeliveryMethod();
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			deliveryOrder.setDealerId(firmId);
			if (method.equals("1")) {
				deliveryOrder.setDeliveryMethod("自提");
				String result = settlementDeliveryService
						.applicationByPickup(deliveryOrder);
				if (result.equals("error")) {
					return NOT_SUFFICIENT_FUNDS;
				}
				return "success";
			} else {
				deliveryOrder.setDeliveryMethod("在线配送");
				String result = settlementDeliveryService
						.applicationByexpress(deliveryOrder);
				if (result.equals("error")) {
					return NOT_SUFFICIENT_FUNDS;
				}
				return "success";
			}
		} catch (Exception e) {
			logger.error("error", e);
			return "error";
		}
	}

	// 提货申请(初始化数据)
	@RequestMapping(value = "/deliveryInfo", method = RequestMethod.GET)
	@ResponseBody
	public String deliveryInfo(HttpSession session) {
		logger.info("提货申请(初始化数据)");
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			List<Position> list = settlementDeliveryService
					.getListByPosition(firmId);// firmId
			return JSON.json(list);
		} catch (IOException e) {
			logger.error("error", e);
			return "error";
		}
	}

	// 自提打印
	@RequestMapping(value = "/print", method = RequestMethod.GET)
	@ResponseBody
	public String print(@RequestParam("page") String page,
			@RequestParam("rows") String rows, Paging paging,
			HttpSession session) {
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			paging.setDealerId(firmId);// user.getUserID()
			logger.info("自提打印" + "userid:" + paging.getDealerId() + "单号："
					+ paging.getDeliveryorderId());
			List<DeliveryOrder> clist = settlementDeliveryService.getPrint(
					page, rows, paging);
			int totalnums = settlementDeliveryService.counts(paging, "自提");
			ResponseResult result = new ResponseResult();
			result.setTotal(totalnums);
			result.setRows(clist);
			return JSON.json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 自提打印(操作后弹出的数据)
	@RequestMapping(value = "/getDetail", method = RequestMethod.POST)
	@ResponseBody
	public String getDetail(@RequestParam("methodid") String methodid) {
		logger.info("自提打印(操作后弹出的数据)" + "methodid:" + methodid);
		try {
			Pickup pickup = settlementDeliveryService
					.getDetailByPickup(methodid);
			DeliveryOrder deliveryOrder = settlementDeliveryService.getorder(
					"自提", methodid);
			List<Object> list = new ArrayList<Object>();
			list.add(pickup);
			list.add(deliveryOrder);
			return JSON.json(list);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 撤销提货单列表
	@RequestMapping(value = "/revocation", method = RequestMethod.GET)
	@ResponseBody
	public String revocation(@RequestParam("page") String page,
			@RequestParam("rows") String rows, Paging paging,
			HttpSession session) {
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			paging.setDealerId(firmId);// user.getUserID()
			logger.info("自提打印" + "userid:" + paging.getDealerId() + "单号："
					+ paging.getDeliveryorderId());
			List<DeliveryOrder> clist = settlementDeliveryService
					.getRevocationList(page, rows, paging);
			int totalnums = settlementDeliveryService.counts(paging, "no");
			ResponseResult result = new ResponseResult();
			result.setTotal(totalnums);
			result.setRows(clist);
			return JSON.json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 自提打印
	@RequestMapping(value = "/updatePrinted", method = RequestMethod.POST)
	@ResponseBody
	public String updatePrinted(
			@RequestParam("deliveryorderid") String deliveryorderid,
			HttpSession session) {
		logger.info("提货单状态修改(撤销提货、提货确认)" + "deliveryorderid:" + deliveryorderid);
		try {
			settlementDeliveryService.updateRevocationStatus(deliveryorderid,
					DeliveryConstant.StatusType.PRINTED.getCode());
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// 确认
	@RequestMapping(value = "/updateByConfirm", method = RequestMethod.POST)
	@ResponseBody
	public String updateByConfirm(
			@RequestParam("deliveryorderid") String deliveryorderid,
			HttpSession session) {
		logger.info("提货单状态修改(撤销提货、提货确认)" + "deliveryorderid:" + deliveryorderid);
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			settlementDeliveryService.determine(deliveryorderid, firmId);
			settlementDeliveryService.updateRevocationStatus(deliveryorderid,
					DeliveryConstant.StatusType.CONFIRM.getCode());
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// 废除
	@RequestMapping(value = "/updateByCancel", method = RequestMethod.POST)
	@ResponseBody
	public String updateByCancel(
			@RequestParam("deliveryorderid") String deliveryorderid,
			HttpSession session) {
		logger.info("提货单状态修改(撤销提货、提货确认)" + "deliveryorderid:" + deliveryorderid);
		try {
			settlementDeliveryService.revoke(deliveryorderid,
					DeliveryConstant.StatusType.CANCEL.getCode());
			settlementDeliveryService.updateRevocationStatus(deliveryorderid,
					DeliveryConstant.StatusType.CANCEL.getCode());
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// 在线配送
	@RequestMapping(value = "/getDispatching", method = RequestMethod.GET)
	@ResponseBody
	public String getDispatching(@RequestParam("page") String page,
			@RequestParam("rows") String rows, Paging paging,
			HttpSession session) {
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			paging.setDealerId(firmId);// user.getUserID()
			logger.info("在线配送" + "用户ID:" + paging.getDealerId() + "单号："
					+ paging.getDeliveryorderId());
			List<Express> clist = settlementDeliveryService.getListByExpress(
					page, rows, paging);
			int totalnums = settlementDeliveryService.counts(paging, "在线配送");
			ResponseResult result = new ResponseResult();
			result.setTotal(totalnums);
			result.setRows(clist);
			return JSON.json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 提货查询
	@RequestMapping(value = "/delivery", method = RequestMethod.GET)
	@ResponseBody
	public String delivery(@RequestParam("page") String page,
			@RequestParam("rows") String rows, Paging paging,
			HttpSession session) {
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			paging.setDealerId(firmId);// user.getUserID()
			logger.info("提货查询" + paging.getDealerId() + "单号："
					+ paging.getDeliveryorderId());
			List<DeliveryOrder> clist = settlementDeliveryService
					.getListByOrder(page, rows, paging);
			int totalnums = settlementDeliveryService.countsByAll(paging);
			ResponseResult result = new ResponseResult();
			result.setTotal(totalnums);
			result.setRows(clist);
			return JSON.json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 提货查询详细
	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@ResponseBody
	public String detail(@RequestParam("methodid") String methodid,
			@RequestParam("deliveryMethod") String deliveryMethod) {
		logger.info("提货查询详细" + "methodid:" + methodid);
		try {
			if (deliveryMethod.equals("1")) {
				Pickup pickup = settlementDeliveryService
						.getDetailByPickup(methodid);
				DeliveryOrder deliveryOrder = settlementDeliveryService
						.getorder("自提", methodid);
				List<Object> list = new ArrayList<Object>();
				list.add(pickup);
				list.add(deliveryOrder);
				return JSON.json(list);
			} else {
				Express express = settlementDeliveryService
						.getDetailByExpress(methodid);
				DeliveryOrder deliveryOrder = settlementDeliveryService
						.getorder("在线配送", methodid);
				List<Object> list = new ArrayList<Object>();
				list.add(express);
				list.add(deliveryOrder);
				return JSON.json(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 提货查询(自提详细)<暂不用>
	@RequestMapping(value = "/expressDetail", method = RequestMethod.GET)
	@ResponseBody
	public String expressDetail(@RequestParam("methodid") String methodid,
			@RequestParam("deliveryMethod") String deliveryMethod) {
		logger.info("提货查询(自提详细)" + "methodid:" + methodid);
		try {
			Express express = settlementDeliveryService
					.getDetailByExpress(methodid);
			return JSON.json(express);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 费用查询
	@RequestMapping(value = "/costQuery", method = RequestMethod.GET)
	@ResponseBody
	public String costQuery(@RequestParam("page") String page,
			@RequestParam("rows") String rows, Paging paging,
			HttpSession session) {
		try {
			String firmId = (String) session.getAttribute("currentFirmId");
			paging.setDealerId(firmId);// user.getUserID()
			logger.info("费用查询" + "用户ID:" + paging.getDealerId() + "单号："
					+ paging.getDeliveryorderId());
			List<DeliveryCost> clist = settlementDeliveryService
					.getListByDeliveryCost(page, rows, paging);
			int totalnums = settlementDeliveryService.countsByCost(paging);
			ResponseResult result = new ResponseResult();
			result.setTotal(totalnums);
			result.setRows(clist);
			return JSON.json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 注册注销费用提示
	@RequestMapping(value = "/getcost", method = RequestMethod.GET)
	@ResponseBody
	public String getCost(@RequestParam("commid") String commid,
			@RequestParam("quatity") String quatity,
			@RequestParam("genre") String genre) {
		Long quatityParam = Long.parseLong(quatity);
		BigDecimal fee = settlementDeliveryService.costQuery(commid,
				quatityParam, genre);
		DecimalFormat df = new DecimalFormat("0.00");
		String feeParam = df.format(fee);
		return feeParam;
	}

	// 验证交割数量是否超出持仓量
	@RequestMapping(value = "/checkDeliveryQuantity", method = RequestMethod.POST)
	@ResponseBody
	public String checkDeliveryQuantity(@RequestParam("commid") String commid,
			@RequestParam("pcount") String pcount,
			@RequestParam("position") String position) {
		VIpoCommConf comm = ipoCommConfService.getVIpoCommConfByCommid(commid);
		BigDecimal unit = comm.getDeliunittocontract();
		BigDecimal deliveryCounts = new BigDecimal(pcount).multiply(unit);
		BigDecimal positionCounts = new BigDecimal(position);
		if (deliveryCounts.compareTo(positionCounts) <= 0) {
			return "true";
		}
		return "false";
	}

}
