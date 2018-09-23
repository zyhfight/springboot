package com.secskill.demo.service;


import com.secskill.demo.exception.GlobalException;
import com.secskill.demo.vo.GoodsVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GoodsService {

	/**
	 * 获取商品list
	 * @return List<GoodsVo>
	 */
	List<GoodsVo> listGoodsVo();

	/**
	 * 根据商品id获取商品
	 * @param goodsId
	 * @return GoodsVo
	 */
	GoodsVo getGoodsVoByGoodsId(long goodsId);

	/**
	 * 减库存
	 * @param goods
	 * @return boolean
	 */
	boolean reduceStock(GoodsVo goods) ;

	/**
	 * 重置库存
	 * @param goodsList
	 */
	@Transactional(rollbackFor = GlobalException.class)
	void resetStock(List<GoodsVo> goodsList) ;

	
	
}
