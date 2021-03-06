package com.mama.jedis.service.facade.request;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author chenmeiyang
 *
 */
public class RedisHashRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key; //键
	private String[] filed;//字段
    private RedisDBEnum db;//redis数据库字段
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String[] getFiled() {
		return filed;
	}
	public void setFiled(String[] filed) {
		this.filed = filed;
	}
	
	public RedisDBEnum getDb() {
		return db;
	}
	public void setDb(RedisDBEnum db) {
		this.db = db;
	}
	
	
	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	

}
