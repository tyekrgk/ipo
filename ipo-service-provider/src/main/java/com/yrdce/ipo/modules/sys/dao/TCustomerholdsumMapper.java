package com.yrdce.ipo.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yrdce.ipo.common.dao.MyBatisDao;
import com.yrdce.ipo.modules.sys.entity.TCustomerholdsum;

/**
 * @ClassName: TCustomerholdsumMapper
 * @Description: 经纪会员持仓汇总
 * @author bob
 */
@MyBatisDao
public interface TCustomerholdsumMapper {
	int deleteByPrimaryKey(@Param("customerid") String customerid, @Param("commodityid") String commodityid,
			@Param("bsFlag") Short bsFlag);

	int insert(TCustomerholdsum record);

	TCustomerholdsum selectByPrimaryKey(@Param("customerid") String customerid,
			@Param("commodityid") String commodityid, @Param("bsFlag") Short bsFlag);

	List<TCustomerholdsum> selectAll();

	int updateByPrimaryKey(TCustomerholdsum record);

	/**
	 * @Title: selectByFirmId
	 * @Description: 根据交易商id查询
	 * @param firmid
	 * @return 参数说明
	 */
	List<TCustomerholdsum> selectByFirmId(String firmid);

	/**
	 * @Title: selectHQT
	 * @Description: 聚合查询（商品id，持仓量）
	 * @param firmid
	 * @return 参数说明
	 */
	List<TCustomerholdsum> selectHQT(String firmid);

}