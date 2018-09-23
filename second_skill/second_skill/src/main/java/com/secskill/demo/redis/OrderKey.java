package com.secskill.demo.redis;

/**
 * 订单 key
 */
public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
	}
	public static final OrderKey  SECOND_SKILL_ORDER = new OrderKey("order");
}
