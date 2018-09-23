package com.secskill.demo.redis;

/**
 * 商品 key
 */
public class GoodsKey extends BasePrefix{

	private GoodsKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static final GoodsKey  GOOD_LIST = new GoodsKey(600, "gl");
	public static final GoodsKey  GOOD_DETAIL = new GoodsKey(600, "gd");
	public static final GoodsKey  SECOND_SKILL_STOCK = new GoodsKey(0, "gs");
}
