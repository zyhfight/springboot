package com.secskill.demo.dao;

import java.util.List;

import com.secskill.demo.domain.SecondSkillGoods;
import com.secskill.demo.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface GoodsDao {
	
	@Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.sec_skill_price from sec_skill_goods mg left join goods g on mg.goods_id = g.id")
	public List<GoodsVo> listGoodsVo();

	@Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.sec_skill_price from sec_skill_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
	public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

	@Update("update sec_skill_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
	public int reduceStock(SecondSkillGoods g);

	@Update("update sec_skill_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
	public int resetStock(SecondSkillGoods g);
	
}
