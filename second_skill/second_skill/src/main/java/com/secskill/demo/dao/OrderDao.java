package com.secskill.demo.dao;

import com.secskill.demo.domain.OrderInfo;
import com.secskill.demo.domain.SecondSkillOrder;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;


@Mapper
public interface OrderDao {
	
	@Select("select * from sec_skill_order where user_id=#{userId} and goods_id=#{goodsId}")
	public SecondSkillOrder getSecondSkillOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

	@Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
			+ "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
	@SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
	public long insert(OrderInfo orderInfo);
	
	@Insert("insert into sec_skill_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
	public int insertSecondSkillOrder(SecondSkillOrder secondSkillOrder);

	@Select("select * from order_info where id = #{orderId}")
	public OrderInfo getOrderById(@Param("orderId") long orderId);

	@Delete("delete from order_info")
	public boolean deleteOrders();

	@Delete("delete from sec_skill_order")
	public boolean deleteSecondSkillOrders();

	
}
