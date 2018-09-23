package com.secskill.demo.redis;

/**
 * 接口限流 key
 */
public class AccessKey extends BasePrefix{

	private AccessKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
	
}
