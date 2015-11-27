package com.yrdce.ipo.modules.sys.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.yrdce.ipo.modules.sys.service.TradetimeService;
import com.yrdce.ipo.modules.sys.vo.ResponseResult;
import com.yrdce.ipo.modules.sys.vo.Tradetime;

/**
 * @author hxx
 * 
 */
@RequestMapping("tradeController")
public class TradeController {

	private static Logger log = org.slf4j.LoggerFactory.getLogger(TradeController.class);

	// @Autowired
	private TradetimeService tradetimeService;

	// 交易节信息展示
	@RequestMapping(value = "/getTradeStatus", method = RequestMethod.GET)
	@ResponseBody
	public String getTradeStatus() throws IOException {
		try {
			List<Tradetime> clist = new ArrayList<Tradetime>();

			int totalnums = tradetimeService.selectByCounts();
			ResponseResult result = new ResponseResult();
			result.setTotal(totalnums);
			result.setRows(clist);
			System.out.println(JSON.json(result));
			return JSON.json(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// 跳出修改页面时的一次查询
	public String getTradetime(short id) {
		log.info("根据主键查询交易节信息" + "id:" + id);
		try {
			Tradetime tradetime = tradetimeService.selectByKey(id);
			return JSON.json(tradetime);
		} catch (Exception e) {
			log.info("修改交易节信息转换json失败");
			e.printStackTrace();
			return "";
		}
	}

	// 修改交易节
	public int updateTradetime(Tradetime tradetime) {
		log.info("修改交易节" + "tradetime:" + tradetime);
		try {
			int i = tradetimeService.upDate(tradetime);
			return i;
		} catch (Exception e) {
			return 0;
		}

	}

	// 添加交易节
	@RequestMapping(value = "/addTradetime", method = RequestMethod.POST)
	@ResponseBody
	public int addTradetime(Tradetime tradetime) {
		log.info("进入添加交易节" + "tradetime:" + tradetime);
		try {
			int i = tradetimeService.insert(tradetime);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	// 删除交易节
	@RequestMapping(value = "/deleteTradetime", method = RequestMethod.POST)
	@ResponseBody
	public int deleteTradetime(Object[] ids) {
		log.info("进入删除交易节" + "ids" + ids);
		try {
			int i = tradetimeService.delete(ids);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
