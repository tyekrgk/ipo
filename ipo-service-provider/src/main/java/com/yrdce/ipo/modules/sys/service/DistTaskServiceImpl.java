package com.yrdce.ipo.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrdce.ipo.common.utils.CommodityDistribution;
import com.yrdce.ipo.common.utils.DateUtil;
import com.yrdce.ipo.modules.sys.dao.IpoCommodityMapper;
import com.yrdce.ipo.modules.sys.dao.IpoDistributionMapper;
import com.yrdce.ipo.modules.sys.dao.IpoDistributionRuleMapper;
import com.yrdce.ipo.modules.sys.dao.IpoOrderMapper;
import com.yrdce.ipo.modules.sys.dao.TCustomerholdsumMapper;
import com.yrdce.ipo.modules.sys.entity.FirmDistInfo;
import com.yrdce.ipo.modules.sys.entity.IpoCommodity;
import com.yrdce.ipo.modules.sys.entity.IpoDistribution;
import com.yrdce.ipo.modules.sys.entity.IpoDistributionRule;
import com.yrdce.ipo.modules.sys.entity.IpoOrder;

@Service("distTaskService")
public class DistTaskServiceImpl implements DistTaskService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IpoOrderMapper ipoOrderMapper;
	@Autowired
	private IpoDistributionRuleMapper ipoDistributionRuleMapper;
	@Autowired
	private TCustomerholdsumMapper tcustomerholdsumMapper;
	@Autowired
	private IpoDistributionMapper ipoDistributionMapper;
	@Autowired
	private IpoCommodityMapper commodityMapper;

	private List<FirmDistInfo> firmdistInfoList;
	private List<IpoOrder> orderList;

	@Transactional()
	public void getCommodityInfos() throws Exception {
		logger.info("分配开始，开始获取商品信息");
		String time = DateUtil.getTime(1);
		// 查询发售表
		List<IpoCommodity> ipoCommodities = commodityMapper.selectByEnd(time);
		for (IpoCommodity ipoCommodity : ipoCommodities) {
			if (ipoCommodity.getStatus() == 1) {
				distCommodity(ipoCommodity.getCommodityid());
			}
		}
		logger.info("摇号结束");
	}

	// 摇号过程
	@Override
	@Transactional()
	public void distCommodity(String commid) {
		int st = 1;
		try {
			IpoCommodity commodity = commodityMapper.queryByComid(commid);
			st = commodity.getStatus();
			commodityMapper.updateByStatus(31, commid);// 31表示摇号中
			orderList = ipoOrderMapper.selectByCid(commid);
			IpoDistributionRule distributionRule = ipoDistributionRuleMapper.selectInfoByCommId(commid);
			CommodityDistribution commodityDistribution;
			if (distributionRule == null) {
				commodityDistribution = new CommodityDistribution((int) commodity.getCounts(), 100, 0);
			} else {
				commodityDistribution = new CommodityDistribution((int) commodity.getCounts(),
						distributionRule.getPurchaseRatio().doubleValue(),
						distributionRule.getHoldRatio().doubleValue());
			}
			if (orderList.size() != 0) {
				firmdistInfoList = new ArrayList<FirmDistInfo>();
				for (IpoOrder ipoOrder : orderList) {
					logger.info("创建分配对象");
					FirmDistInfo firmDistInfo = creatFirmObj(commodity, ipoOrder, distributionRule);
					logger.info("创建分配对象成功");
					if (commodityDistribution.getAlldistNum() > 0) {
						firmDistInfo = commodityDistribution.distributionMain(firmDistInfo);
					}
					firmdistInfoList.add(firmDistInfo);
				}
				while (firmdistInfoList.size() == 0 || commodityDistribution.getAlldistNum() == 0) {
					for (int i = 0; i < firmdistInfoList.size(); i++) {
						FirmDistInfo firmDistInfo = firmdistInfoList.get(i);
						if (firmDistInfo.isFull()) {
							if (commodityDistribution.getAlldistNum() > 0 && i + 1 != firmdistInfoList.size()) {
								firmDistInfo = commodityDistribution.disCommodityByRandom(firmDistInfo);
							} else if (i + 1 == firmdistInfoList.size()) {
								int disNum = firmDistInfo.getDistNum() + commodityDistribution.getAlldistNum();
								if (disNum > firmDistInfo.getBuyNum()) {
									int tempNum = firmDistInfo.getBuyNum() - firmDistInfo.getDistNum();
									firmDistInfo.setDistNum(firmDistInfo.getBuyNum());
									commodityDistribution
											.setAlldistNum(commodityDistribution.getAlldistNum() - tempNum);
									firmDistInfo.setFull(false);
								} else {
									firmDistInfo.setDistNum(disNum);
									commodityDistribution.setAlldistNum(0);
								}
							}
						}
						IpoDistribution ipoDistribution = new IpoDistribution();
						IpoDistribution tempDistribution = ipoDistributionMapper
								.selectByPrimaryKey(firmDistInfo.getId());
						ipoDistribution.setZcounts(firmDistInfo.getDistNum());
						ipoDistribution.setId(firmDistInfo.getId());
						ipoDistribution.setCommodityid(commodity.getCommodityid());
						ipoDistribution.setCommodityname(commodity.getCommodityname());
						ipoDistribution.setUserid(firmDistInfo.getFirmId());
						if (tempDistribution == null) {
							ipoDistributionMapper.insert(ipoDistribution);
						} else {
							ipoDistributionMapper.updateByPrimaryKey(ipoDistribution);
						}

						if (!firmDistInfo.isFull()) {
							firmdistInfoList.remove(i);
						}
					}
				}

			}
			commodityMapper.updateByStatus(3, commid);
			logger.info("摇号结束");

		} catch (Exception e) {
			logger.error("摇号异常", e);
			commodityMapper.updateByStatus(st, commid);
		}
	}

	// 实例化一个等待摇号的交易商对象
	private FirmDistInfo creatFirmObj(IpoCommodity ipoCommodity, IpoOrder ipoOrder,
			IpoDistributionRule distributionRule) {
		int countsBuy = ipoOrderMapper.selectbysid(ipoOrder.getCommodityid());
		long holdCounts = tcustomerholdsumMapper.selectByCommId(orderList);
		long firmhoidCounts = tcustomerholdsumMapper.selectFirmHold(ipoOrder.getUserid());
		long tempCountsBuy = countsBuy / ipoCommodity.getUnits();
		long tempCountsOrder = ipoOrder.getCounts() / ipoCommodity.getUnits();
		double firmCapitalRatio = tempCountsOrder / tempCountsBuy;
		double firmPositionRatio;
		if (holdCounts != 0) {
			firmPositionRatio = firmhoidCounts / holdCounts;
		} else {
			firmPositionRatio = 0;
		}
		FirmDistInfo distInfo = new FirmDistInfo();
		distInfo.setFirmCapitalRatio(firmCapitalRatio);
		distInfo.setFirmId(ipoOrder.getUserid());
		distInfo.setId(ipoOrder.getOrderid());
		distInfo.setBuyNum(ipoOrder.getCounts());
		if (distributionRule != null) {
			if (distributionRule.getMaxqty().intValue() > ipoOrder.getCounts()) {
				distInfo.setMaxdistNum(ipoOrder.getCounts());
			} else {
				distInfo.setMaxdistNum(distributionRule.getMaxqty().intValue());
			}

		}
		distInfo.setFirmPositionRatio(firmPositionRatio);
		return distInfo;
	}

}