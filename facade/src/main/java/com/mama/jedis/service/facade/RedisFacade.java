package com.mama.jedis.service.facade;

import javax.jws.WebService;

import com.mama.jedis.service.facade.request.RedisHashRequest;
import com.mama.jedis.service.facade.request.RedisRequest;
import com.mama.jedis.service.facade.response.ListResponse;


@WebService(targetNamespace = "http://cache.redis.88mmmoney.com")
public interface RedisFacade {
	
	
	/**
	 * chenmeiyang
	 * 根据索引和列表名称，从列表中获取元素值
	 */
	public Object getValueByIndexFromList(RedisRequest request);
	
	
	/**
	 * chenmeiyang
	 * 根据key删除hash类型所有字段
	 */
	public Object removeHashByKey(RedisHashRequest request);
	
	/**
	 * chenmeiyang
	 * 放入key-value
	 */
	public Object putKeyAndValue(RedisRequest request);
	
	/**
	 * @param request
	 * @return
	 * 根据key删除value
	 */
	public ListResponse removeByKey(RedisRequest request);
	
	/**
	 * @param request
	 * @return
	 * 根据key获取list集合
	 */
	public ListResponse getListByKey(RedisRequest request);
	
     /**
     * @param request
     * @return
     * 根据key-field获取value
     */
    public Object getValueByKeyAndField(RedisRequest request);
    
    /**
     * @param request
     * @return
     * 根据key获取value
     */
    public String getValueByKey(RedisRequest request);
    
    /**
     * @param request
     * @return
     * 存放散列类型数据
     */
    public Object putKeyAndFieldAndValue(RedisRequest request);
	
}
