package com.secskill.demo.vo;

import com.secskill.demo.domain.OrderInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrderDetailVo {
	private GoodsVo goods;
	private OrderInfo order;

}
