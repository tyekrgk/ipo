package com.yrdce.ipo.modules.sys.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.yrdce.ipo.modules.sys.service.IpoCommConfService;
import com.yrdce.ipo.modules.sys.service.TradetimeService;
import com.yrdce.ipo.modules.sys.vo.Nottradeday;
import com.yrdce.ipo.modules.sys.vo.ResponseResult;
import com.yrdce.ipo.modules.sys.vo.Tradetime;
import com.yrdce.ipo.modules.sys.vo.TradetimeComm;
import com.yrdce.ipo.modules.sys.vo.VIpoCommConf;

/**
 * 交易节增删改查
 * 
 * @author Bob
 *
 */

@Controller
@RequestMapping("TradetimeController")
public class TradetimeController {

	static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TradetimeController.class);

	@Autowired
	private TradetimeService tradetimeService;

	@Autowired
	private IpoCommConfService ipoCommConfService;

	// 交易节信息展示
	@RequestMapping(value = "/getTradetimeList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getTradetimeList(@RequestParam("page") String page, @RequestParam("rows") String rows)
			throws IOException {
		logger.info("交易节信息展示" + "page:" + page + "rows:" + rows);
		try {
			List<Tradetime> clist = tradetimeService.selectByPage(page, rows);
			int totalnums = tradetimeService.selectByCounts();
			ResponseResult result = new ResponseResult();
			result.setTotal(totalnums);
			result.setRows(clist);
			// System.out.println(JSON.json(result));
			return JSON.json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 修改交易节
	@RequestMapping(value = "/updateTradetime", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String updateTradetime(HttpServletRequest request, HttpServletResponse response, Model model,
			Tradetime tradetime, @RequestParam(value = "comms", required = false) String comms) {
		logger.info("修改交易节" + "tradetime:" + tradetime + comms);
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>tradetime:" + tradetime.getName());
		try {
			String name = new String(tradetime.getName().getBytes("gbk"), "utf-8");
			tradetime.setName(name);
			if (comms != null) {
				tradetimeService.upDate(tradetime, comms);
				return "app/tradetime/close";
			} else {
				comms = "no";
				tradetimeService.upDate(tradetime, comms);
				return "app/tradetime/close";
			}
		} catch (Exception e) {
			return "error/500";
		}

	}

	// 添加交易节

	@RequestMapping(value = "/addTradetime", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String addTradetime(Tradetime tradetime,
			@RequestParam(value = "comms", required = false) String comms) {
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>tradetime:" + tradetime.getName());
		try {
			String name = new String(tradetime.getName().getBytes("gbk"), "utf-8");
			tradetime.setName(name);
			if (comms != null) {
				tradetimeService.insert(tradetime, comms);
				return "app/tradetime/close";
			} else {
				comms = "no";
				tradetimeService.insert(tradetime, comms);
				return "app/tradetime/close";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}

	}

	// 删除交易节
	@RequestMapping(value = "/deleteTradetime", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String deleteTradetime(String ids) {
		logger.debug("进入删除交易节" + "ids:" + ids);
		// 根据交易节id查询与商品的关联
		try {
			Boolean falg = tradetimeService.tradeTimeAndCom(ids);
			logger.debug(falg + "");
			// 判断是否有关联
			if (falg) {

				tradetimeService.delete(ids);

				return "success";
			} else {
				return "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	// 交易节视图
	@RequestMapping(value = "/getTradeTimeForward", method = RequestMethod.GET)
	public String getTradeTimeForward(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "app/tradetime/tradeTime_list";

	}

	// 修改交易节视图
	@RequestMapping(value = "/updateTradetimeforward", method = RequestMethod.GET)
	public String updateTradetimeforward(HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam("sectionid") String sectionid) throws IOException {
		logger.info("进入修改视图");
		short id = Short.parseShort(sectionid);
		List<TradetimeComm> list;
		try {
			list = tradetimeService.getTradetimeByComm(id);

			request.setAttribute("comm", list);
			List<VIpoCommConf> comlist = ipoCommConfService.findIpoCommConfs();
			request.setAttribute("commlist", comlist);
			return "app/tradetime/update_tradetime";
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}
	}

	// 添加交易节视图
	@RequestMapping(value = "/addTradetimeforward", method = RequestMethod.GET)
	public String addTradetimeforward(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("进入新增视图");
		List<VIpoCommConf> comlist = ipoCommConfService.findIpoCommConfs();
		logger.debug("" + comlist.size());
		request.setAttribute("commlist", comlist);
		return "app/tradetime/add_tradeTime";
	}

	// 非交易节视图
	@RequestMapping(value = "/getNottradedayforward", method = RequestMethod.GET)
	public String getNottradedayforward(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		logger.info("进入非交易日视图");
		Nottradeday nottradeday;
		try {
			nottradeday = tradetimeService.select();

			logger.debug("nottradeday:" + nottradeday);
			if (nottradeday != null) {
				String week = nottradeday.getWeek();
				String day = nottradeday.getDay();
				if (week == null) {
					week = "";
				}
				if (day == null) {
					day = "";
				}
				request.setAttribute("week", week);
				request.setAttribute("day", day);
				request.setAttribute("id", 1);
				return "app/tradetime/notTradeDay";
			} else {
				request.setAttribute("week", null);
				request.setAttribute("day", null);
				request.setAttribute("id", 0);
				return "app/tradetime/notTradeDay";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error/500";
		}
	}

	// 非交易日查询
	@RequestMapping(value = "/getNottradeday", method = RequestMethod.GET)
	@ResponseBody
	public Nottradeday getNottradeday() {
		try {
			Nottradeday nottradeday = tradetimeService.select();
			return nottradeday;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 更新、修改
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public String update(Nottradeday notTradeDay) {
		logger.debug("week:" + notTradeDay.getWeek() + "day:" + notTradeDay.getDay() + "id:"
				+ notTradeDay.getId());
		try {
			tradetimeService.insertByNottradeday(notTradeDay);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
}
