package com.yrdce.ipo.modules.sys.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.yrdce.ipo.common.constant.ChargeConstant;
import com.yrdce.ipo.common.utils.PageUtil;
import com.yrdce.ipo.modules.sys.dao.IpoCommodityConfMapper;
import com.yrdce.ipo.modules.sys.dao.IpoPayFlowMapper;
import com.yrdce.ipo.modules.sys.dao.IpoSpoRationMapper;
import com.yrdce.ipo.modules.sys.dao.IpoUnderwriterSubscribeMapper;
import com.yrdce.ipo.modules.sys.entity.IpoUnderWriters;
import com.yrdce.ipo.modules.sys.entity.IpoUnderwriterSubscribe;
import com.yrdce.ipo.modules.sys.vo.DebitFlow;
import com.yrdce.ipo.modules.sys.vo.PayFlow;
import com.yrdce.ipo.modules.sys.vo.UnderWriters;
import com.yrdce.ipo.modules.sys.vo.UnderwriterSubscribe;

@Service
public class UnderwriterSubscribeServiceImpl implements
		UnderwriterSubscribeService, Observer {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IpoUnderwriterSubscribeMapper underwriterSubscribrmapper;

	@Autowired
	private IpoPayFlowMapper payFlowMapper;

	@Autowired
	private IpoCommodityConfMapper commconfmapper;
	@Autowired
	private IpoSpoRationMapper ipoSpoRationMapper;

	@Override
	public List<UnderwriterSubscribe> getInfosByPage(String page, String rows,
			UnderwriterSubscribe example) {
		int startIndex = PageUtil.getStartIndex(page, rows);
		int endIndex = PageUtil.getEndIndex(page, rows);
		if (example != null) {
			IpoUnderwriterSubscribe record = new IpoUnderwriterSubscribe();
			BeanUtils.copyProperties(example, record);
			List<IpoUnderwriterSubscribe> datalist = underwriterSubscribrmapper
					.getInfosByPage(startIndex, endIndex, record);
			List<UnderwriterSubscribe> datalist2 = new ArrayList<UnderwriterSubscribe>();
			for (IpoUnderwriterSubscribe temp : datalist) {
				UnderwriterSubscribe info = new UnderwriterSubscribe();
				BeanUtils.copyProperties(temp, info);
				datalist2.add(info);
			}
			return datalist2;
		}
		return null;
	}

	@Override
	public Integer getQueryNum(UnderwriterSubscribe example) {
		if (example != null) {
			IpoUnderwriterSubscribe record = new IpoUnderwriterSubscribe();
			BeanUtils.copyProperties(example, record);
			return underwriterSubscribrmapper.getQueryNum(record);
		}
		return 0;
	}

	@Override
	public String deleteInfo(String ids) {
		String[] subscribeids = ids.split(",");
		int num = 0;
		for (String id : subscribeids) {
			IpoUnderwriterSubscribe example = underwriterSubscribrmapper
					.selectByPrimaryKey(new BigDecimal(id));
			example.setDeleteFlag((short) 1);
			num = num + underwriterSubscribrmapper.updateByPrimaryKey(example);
		}
		if (num == subscribeids.length) {
			return "true";
		}
		return "false";
	}

	@Override
	@Transactional
	public Integer insertInfo(UnderwriterSubscribe example) {
		if (example != null) {
			IpoUnderwriterSubscribe record = new IpoUnderwriterSubscribe();
			BeanUtils.copyProperties(example, record);
			return underwriterSubscribrmapper.insert(record);
		}
		return 0;
	}

	@Override
	public List<UnderWriters> findUnderwriters(String page, String rows,
			UnderWriters example) {
		int startIndex = PageUtil.getStartIndex(page, rows);
		int endIndex = PageUtil.getEndIndex(page, rows);
		if (example != null) {
			IpoUnderWriters record = new IpoUnderWriters();
			BeanUtils.copyProperties(example, record);
			List<IpoUnderWriters> datalist = underwriterSubscribrmapper
					.getUnderwritersByPub(startIndex, endIndex, record);
			List<UnderWriters> datalist2 = new ArrayList<UnderWriters>();
			for (IpoUnderWriters temp : datalist) {
				UnderWriters info = new UnderWriters();
				BeanUtils.copyProperties(temp, info);
				datalist2.add(info);
			}
			return datalist2;
		}
		return null;
	}

	@Override
	public Integer getUnderwritersNum(UnderWriters example) {
		if (example != null) {
			IpoUnderWriters record = new IpoUnderWriters();
			BeanUtils.copyProperties(example, record);
			return underwriterSubscribrmapper.getUnderwritersNum(record);
		}
		return 0;
	}

	public void update(Observable observable, Object obj) {
		logger.info("监听财务结算消息内容为" + obj);
		String json = (String) obj;
		try {
			DebitFlow debitFlow = (DebitFlow) JSON.parse(json, DebitFlow.class);
			if ("001".equals(debitFlow.getBusinessType())
					&& "001".equals(debitFlow.getChargeType())
					&& debitFlow.getDebitState() == 2
					&& debitFlow.getDebitMode() == 1) {
				String positionId = debitFlow.getOrderId();
				/*
				 * IpoPublisherPosition example = publisherPositionmapper
				 * .selectByPrimaryKey(new BigDecimal(positionId));
				 * example.setStatus((short) 3);// 扣款成功
				 * publisherPositionmapper.updateByPrimaryKey(example);
				 * stroragemapper.updateTransferstatusByPrimaryKey(
				 * example.getStorageid(), example.getStatus().intValue());
				 */
			}
		} catch (ParseException e) {
			logger.error("监听财务结算消息内容json转换失败" + obj);
			throw new RuntimeException(e);
		}

	}

	@Override
	@Transactional
	public void insertLoan(UnderwriterSubscribe example, BigDecimal funds) {
		// 货款流水
		PayFlow payFlow = new PayFlow();
		payFlow.setAmount(funds);
		payFlow.setBusinessType(ChargeConstant.BusinessType.PUBLISH.getCode());
		payFlow.setChargeType(ChargeConstant.ChargeType.GOODS.getCode());
		payFlow.setCommodityId(example.getCommodityid());
		payFlow.setOrderId(String.valueOf(example.getSubscribeid()));
		payFlow.setPayState(ChargeConstant.PayState.UNPAY.getCode());

		payFlow.setPayee(ipoSpoRationMapper.firmidBySales(commconfmapper
				.findIpoCommConfByCommid(example.getCommodityid())
				.getPubmemberid()));
		payFlow.setPayMode(ChargeConstant.PayMode.ONLINE.getCode());
		payFlow.setPayChannel(ChargeConstant.PayChannel.DEPOSIT.getCode());
		payFlow.setCreateUser(example.getCreateUser());
		payFlow.setCreateDate(new Date());
		payFlowMapper.insert(payFlow);
	}

	@Override
	public String checkExist(UnderwriterSubscribe example) {
		IpoUnderwriterSubscribe record = underwriterSubscribrmapper
				.selectByExample(example);
		if (record != null) {
			return "true";// 已存在
		}
		return "false";
	}

	@Override
	public String checkTotalCounts(String commodityId) {
		Long subcounts = underwriterSubscribrmapper
				.selectSubscibedCounts(commodityId);// 已认购的数量
		Long totalcounts = underwriterSubscribrmapper
				.selectTotalCounts(commodityId);// 供认购的总数
		if (totalcounts != null) {
			if (subcounts == null) {
				subcounts = (long) 0;
			}
			if (subcounts < totalcounts) {
				return "true";
			}
		}
		return "false";
	}

	@Override
	public Float checkRatioSum(String commodityId) {
		Float ratio = underwriterSubscribrmapper.checkRatioSum(commodityId);
		if (ratio != null) {
			return 100 - ratio;
		}
		return (float) 100;
	}
}
