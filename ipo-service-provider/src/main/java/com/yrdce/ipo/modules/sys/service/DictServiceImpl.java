/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.yrdce.ipo.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrdce.ipo.common.service.CrudService;
import com.yrdce.ipo.modules.sys.dao.DictDao;
import com.yrdce.ipo.modules.sys.entity.Dict;

/**
 * 字典Service
 * 
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class DictServiceImpl extends CrudService<DictDao, Dict>implements DictService {

	/**
	 * 查询字段类型列表
	 * 
	 * @return
	 */
	public List<String> findTypeList() {
		return dao.findTypeList(new Dict());
	}

	@Transactional(readOnly = false)
	public void save(Dict dict) {
		super.save(dict);
		// CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	@Transactional(readOnly = false)
	public void delete(Dict dict) {
		super.delete(dict);
		// CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

}
