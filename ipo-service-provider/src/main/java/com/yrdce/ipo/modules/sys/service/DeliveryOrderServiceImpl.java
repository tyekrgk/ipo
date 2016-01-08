package com.yrdce.ipo.modules.sys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esotericsoftware.minlog.Log;
import com.yrdce.ipo.modules.sys.dao.IpoDeliveryorderMapper;
import com.yrdce.ipo.modules.sys.dao.IpoExpressMapper;
import com.yrdce.ipo.modules.sys.dao.IpoOutboundMapper;
import com.yrdce.ipo.modules.sys.dao.IpoPickupMapper;
import com.yrdce.ipo.modules.sys.dao.IpoPositionMapper;
import com.yrdce.ipo.modules.sys.entity.IpoDeliveryorder;
import com.yrdce.ipo.modules.sys.entity.IpoExpress;
import com.yrdce.ipo.modules.sys.entity.IpoPickup;
import com.yrdce.ipo.modules.sys.entity.IpoPosition;
import com.yrdce.ipo.modules.sys.vo.DeliveryOrder;
import com.yrdce.ipo.modules.sys.vo.Express;
import com.yrdce.ipo.modules.sys.vo.Pickup;
import com.yrdce.ipo.modules.warehouse.dao.IpoWarehouseStockMapper;
import com.yrdce.ipo.modules.warehouse.entity.IpoWarehouseStock;

@Service("deliveryorderservice")
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

	static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(DeliveryOrderServiceImpl.class);

	@Autowired
	private IpoDeliveryorderMapper deliveryordermapper;

	@Autowired
	private IpoExpressMapper ipoexpressmapper;

	@Autowired
	private IpoPickupMapper ipopickupmapper;
	@Autowired
	private IpoWarehouseStockMapper ipoWarehouseStockMapper;
	@Autowired
	private IpoOutboundMapper ipoOutboundMapper;
	@Autowired
	private IpoPositionMapper ipopositionmapper;

	public IpoDeliveryorderMapper getDeliveryordermapper() {
		return deliveryordermapper;
	}

	public void setDeliveryordermapper(
			IpoDeliveryorderMapper deliveryordermapper) {
		this.deliveryordermapper = deliveryordermapper;
	}

	public IpoExpressMapper getIpoexpressmapper() {
		return ipoexpressmapper;
	}

	public void setIpoexpressmapper(IpoExpressMapper ipoexpressmapper) {
		this.ipoexpressmapper = ipoexpressmapper;
	}

	public IpoPickupMapper getIpopickupmapper() {
		return ipopickupmapper;
	}

	public void setIpopickupmapper(IpoPickupMapper ipopickupmapper) {
		this.ipopickupmapper = ipopickupmapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DeliveryOrder> queryAllDeliOrdersByPage(String page,
			String rows, DeliveryOrder deorder) {
		Log.info("分页模糊查询提货单服务");
		page = (page == null ? "1" : page);
		rows = (rows == null ? "5" : rows);
		int curpage = Integer.parseInt(page);
		int pagesize = Integer.parseInt(rows);
		IpoDeliveryorder record = new IpoDeliveryorder();
		if (deorder != null) {
			BeanUtils.copyProperties(deorder, record);
			List<IpoDeliveryorder> dorderslist = deliveryordermapper
					.queryAllDeliOrdersByPage((curpage - 1) * pagesize + 1,
							curpage * pagesize, record);
			List<DeliveryOrder> dorderslist2 = new ArrayList<DeliveryOrder>();
			for (int i = 0; i < dorderslist.size(); i++) {
				DeliveryOrder temp = new DeliveryOrder();
				BeanUtils.copyProperties(dorderslist.get(i), temp);
				dorderslist2.add(temp);
				Log.info(temp.toString());
			}
			return dorderslist2;
		}
		return null;

	}

	@Override
	public Integer getQueryNum(DeliveryOrder deorder) {
		IpoDeliveryorder order = new IpoDeliveryorder();
		if (deorder != null) {
			BeanUtils.copyProperties(deorder, order);
			return deliveryordermapper.getQueryNum(order);
		}
		return 0;
	}

	@Override
	public DeliveryOrder getDeliveryOrderByDeliOrderID(String deliOrderID) {
		Log.info("根据提货单号查询提货单");
		IpoDeliveryorder deorder = deliveryordermapper
				.selectByPrimaryKey(deliOrderID);
		if (deorder != null) {
			DeliveryOrder order = new DeliveryOrder();
			BeanUtils.copyProperties(deorder, order);
			return order;
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public String updateDeliveryOrder(DeliveryOrder order, Pickup pickup,
			String managerId) {
		Log.info("审核自提提货单服务");
		IpoDeliveryorder deorder = new IpoDeliveryorder();
		IpoPickup ipopickup = new IpoPickup();
		if (order != null) {
			if (pickup != null) {
				BeanUtils.copyProperties(order, deorder);
				BeanUtils.copyProperties(pickup, ipopickup);
				deorder.setApproveDate(new Date());
				deorder.setApprovers(managerId);
				int onum = deliveryordermapper.updateByPrimaryKey(deorder);
				if (order.getApprovalStatus() == 2) {
					ipopickupmapper.updateByPrimaryKey(ipopickup);
					Long quantity = deorder.getDeliveryQuatity();// 冻结仓库库存
					String commid = deorder.getCommodityId();
					IpoWarehouseStock stock = ipoWarehouseStockMapper
							.selectByCommoId(commid,
									Long.parseLong(deorder.getWarehouseId()));
					if (stock != null) {
						Long frozennum = stock.getForzennum();
						Long available = stock.getAvailablenum();
						Long newfrozen = frozennum + quantity;
						Long newavailble = available - quantity;
						stock.setForzennum(newfrozen);
						stock.setAvailablenum(newavailble);
						ipoWarehouseStockMapper.updateInfo(stock);
					}
				}
				if (order.getApprovalStatus() == 3) {
					// 驳回更新持仓量
					long quatity = order.getDeliveryQuatity();
					String firmid = order.getDealerId();
					String commid = order.getCommodityId();
					IpoPosition ipoPosition = ipopositionmapper.selectPosition(
							firmid, commid);
					long position = ipoPosition.getPosition();
					long num = position + quatity;
					ipopositionmapper.updatePosition(firmid, commid, num);
				}
				if (onum != 0) {
					return "已审核";
				}
			}
		}
		return "审核失败";
	}

	@Override
	@Transactional
	public String updateDeliveryOrder(DeliveryOrder order, Express express,
			String managerId) {
		Log.info("审核配送提货单服务");
		IpoDeliveryorder deorder = new IpoDeliveryorder();
		IpoExpress ipoexpress = new IpoExpress();
		if (order != null) {
			if (express != null) {
				BeanUtils.copyProperties(order, deorder);
				BeanUtils.copyProperties(express, ipoexpress);
				deorder.setApproveDate(new Date());
				deorder.setApprovers(managerId);
				int onum = deliveryordermapper.updateByPrimaryKey(deorder);
				if (order.getApprovalStatus() == 2) {
					ipoexpressmapper.updateByPrimaryKey(ipoexpress);
					Long quantity = deorder.getDeliveryQuatity();// 冻结仓库库存
					String commid = deorder.getCommodityId();
					IpoWarehouseStock stock = ipoWarehouseStockMapper
							.selectByCommoId(commid,
									Long.parseLong(deorder.getWarehouseId()));
					Long frozennum = stock.getForzennum();
					Long available = stock.getAvailablenum();
					Long newfrozen = frozennum + quantity;
					Long newavailble = available - quantity;
					stock.setForzennum(newfrozen);
					stock.setAvailablenum(newavailble);
					ipoWarehouseStockMapper.updateInfo(stock);
				}
				if (order.getApprovalStatus() == 3) {
					// 驳回更新持仓量
					long quatity = order.getDeliveryQuatity();
					String firmid = order.getDealerId();
					String commid = order.getCommodityId();
					IpoPosition ipoPosition = ipopositionmapper.selectPosition(
							firmid, commid);
					long position = ipoPosition.getPosition();
					long num = position + quatity;
					ipopositionmapper.updatePosition(firmid, commid, num);
				}
				if (onum != 0) {
					return "已审核";
				}
			}
		}
		return "审核失败";
	}

	@Override
	public Pickup getPickUpDetail(String pickUpId) {
		Pickup pickup = new Pickup();
		IpoPickup detail = ipopickupmapper.selectByPrimaryKey(pickUpId);
		if (detail != null) {
			BeanUtils.copyProperties(detail, pickup);
			return pickup;
		}
		return null;
	}

	@Override
	public Express getExpressDetail(String expressId) {
		Express express = new Express();
		IpoExpress detail = ipoexpressmapper.selectByPrimaryKey(expressId);
		if (detail != null) {
			BeanUtils.copyProperties(detail, express);
			return express;
		}
		return null;
	}

	@Override
	public List<DeliveryOrder> cancelDeliOrdersByPage(String page, String rows) {
		Log.info("分页查询可撤销提货单服务");
		page = (page == null ? "1" : page);
		rows = (rows == null ? "5" : rows);
		int curpage = Integer.parseInt(page);
		int pagesize = Integer.parseInt(rows);
		List<IpoDeliveryorder> dorderslist = deliveryordermapper
				.cancelDeliOrdersByPage((curpage - 1) * pagesize + 1, curpage
						* pagesize);
		List<DeliveryOrder> dorderslist2 = new ArrayList<DeliveryOrder>();
		for (int i = 0; i < dorderslist.size(); i++) {
			DeliveryOrder temp = new DeliveryOrder();
			BeanUtils.copyProperties(dorderslist.get(i), temp);
			dorderslist2.add(temp);
			Log.info(temp.toString());
		}
		return dorderslist2;
	}

	@Override
	public Integer getCancelNum() {
		return deliveryordermapper.getCancelNum();
	}

	@Override
	@Transactional
	public String cancelDeorder(String deOrderId, String cancellId) {
		deliveryordermapper.cancelDeorder(deOrderId, cancellId);
		IpoDeliveryorder deorder = deliveryordermapper
				.selectByPrimaryKey(deOrderId);
		if (deorder != null) {
			Integer status = deorder.getApprovalStatus();
			long quatity = deorder.getDeliveryQuatity();// 撤销返还持仓量
			String firmid = deorder.getDealerId();
			String commid = deorder.getCommodityId();
			IpoPosition ipoPosition = ipopositionmapper.selectPosition(firmid,
					commid);
			long position = ipoPosition.getPosition();
			long num = position + quatity;
			ipopositionmapper.updatePosition(firmid, commid, num);
			if (status == 10) {
				return "撤销成功";
			}
		}
		return "撤销失败";
	}

	@Override
	public List<DeliveryOrder> queryCancelDeliOrdersByPage(String page,
			String rows, DeliveryOrder deorder) {
		Log.info("分页模糊查询提货单服务");
		page = (page == null ? "1" : page);
		rows = (rows == null ? "5" : rows);
		int curpage = Integer.parseInt(page);
		int pagesize = Integer.parseInt(rows);
		IpoDeliveryorder record = new IpoDeliveryorder();
		if (deorder != null) {
			BeanUtils.copyProperties(deorder, record);
			List<IpoDeliveryorder> dorderslist = deliveryordermapper
					.queryCancelDeliOrdersByPage((curpage - 1) * pagesize + 1,
							curpage * pagesize, record);
			List<DeliveryOrder> dorderslist2 = new ArrayList<DeliveryOrder>();
			for (int i = 0; i < dorderslist.size(); i++) {
				DeliveryOrder temp = new DeliveryOrder();
				BeanUtils.copyProperties(dorderslist.get(i), temp);
				dorderslist2.add(temp);
				Log.info(temp.toString());
			}
			return dorderslist2;
		}
		return null;
	}

	@Override
	public Integer getQueryCancelNum(DeliveryOrder deorder) {
		IpoDeliveryorder order = new IpoDeliveryorder();
		if (deorder != null) {
			BeanUtils.copyProperties(deorder, order);
			return deliveryordermapper.getQueryCancelNum(order);
		}
		return null;
	}

	public String genRandomNum() {
		final int maxNum = 36;
		int i;
		int count = 0;
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < 9) {
			i = Math.abs(r.nextInt(maxNum));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	/**
	 * li
	 * 
	 */
	@Override
	public DeliveryOrder getPickupDeliveryInfo(DeliveryOrder order) {
		IpoDeliveryorder delivery = new IpoDeliveryorder();
		DeliveryOrder deliveryOrder = new DeliveryOrder();
		BeanUtils.copyProperties(order, delivery);
		IpoDeliveryorder temporder = deliveryordermapper
				.getPickupDeliveryInfo(delivery);
		if (temporder != null) {
			BeanUtils.copyProperties(temporder, deliveryOrder);
		}
		return deliveryOrder;
	}

	@Override
	public DeliveryOrder getExpressDeliveryInfo(DeliveryOrder order) {
		IpoDeliveryorder delivery = new IpoDeliveryorder();
		DeliveryOrder deliveryOrder = new DeliveryOrder();

		BeanUtils.copyProperties(order, delivery);
		IpoDeliveryorder temporder = deliveryordermapper
				.getExpressDeliveryInfo(delivery);
		if (temporder != null) {
			BeanUtils.copyProperties(temporder, deliveryOrder);
		}
		return deliveryOrder;
	}

	/**
	 * li
	 * */
	@Override
	@Transactional
	public int updateStatus(DeliveryOrder deliveryOrder, String outboundorderid) {
		int result = 0;
		int result1 = 0;
		int result3 = 0;
		IpoDeliveryorder deliveryorder2 = new IpoDeliveryorder();
		if (deliveryorder2 != null) {
			IpoDeliveryorder deliveryorder3 = deliveryordermapper
					.selectByPrimaryKey(deliveryOrder.getDeliveryorderId());
			String tempCommId = deliveryorder3.getCommodityId();
			String wareHouseId = deliveryorder3.getWarehouseId();
			IpoWarehouseStock ipoWarehouseStock = ipoWarehouseStockMapper
					.selectByCommoId(tempCommId, Long.parseLong(wareHouseId));
			long num = ipoWarehouseStock.getAvailablenum()
					- deliveryorder3.getDeliveryQuatity();
			long num2 = ipoWarehouseStock.getOutboundnum()
					+ deliveryorder3.getDeliveryQuatity();
			ipoWarehouseStock.setAvailablenum(num);
			ipoWarehouseStock.setAvailablenum(num2);
			BeanUtils.copyProperties(deliveryOrder, deliveryorder2);
			result = deliveryordermapper.updateStatus(deliveryorder2);
			result1 = ipoWarehouseStockMapper.updateInfo(ipoWarehouseStock);
			result3 = ipoOutboundMapper.updateOutBoundState(4, outboundorderid);
		}
		if (result > 0 && result1 > 0 && result3 > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	@Transactional
	public Integer transferDeliveryOrder(String deliveryId) {
		IpoDeliveryorder example = deliveryordermapper
				.selectByPrimaryKey(deliveryId);
		example.setApprovalStatus(5);// 已过户
		return deliveryordermapper.updateByPrimaryKey(example);
	}

}
