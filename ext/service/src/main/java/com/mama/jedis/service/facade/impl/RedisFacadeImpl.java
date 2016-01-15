package com.mama.jedis.service.facade.impl;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;

import com.mama.jedis.service.facade.RedisFacade;
import com.mama.jedis.service.facade.redis.RedisFactory;
import com.mama.jedis.service.facade.request.RedisDBEnum;
import com.mama.jedis.service.facade.request.RedisHashRequest;
import com.mama.jedis.service.facade.request.RedisRequest;
import com.mama.jedis.service.facade.response.ListResponse;
import com.meidusa.toolkit.common.util.StringUtil;
import com.mmzb.house.jedis.common.AppProperties;
import com.mmzb.house.jedis.common.Constant;


@WebService(endpointInterface = "com.mama.jedis.service.facade.RedisFacade", targetNamespace ="http://cache.redis.88mmmoney.com")
public class RedisFacadeImpl implements RedisFacade {
	
	private Logger logger = LoggerFactory.getLogger(RedisFacadeImpl.class);
	
	@Resource(name="redisFactory")
	private RedisFactory redisFactory;
	
	@Resource(name="appProperties")
	private AppProperties appProperties;
	
	/**
	 * chenmeiyang
	 * 根据索引和列表名称，从列表中获取元素值
	 */
	@Override
	public Object getValueByIndexFromList(RedisRequest request) {
		logger.info("==========【根据索引和列表名称，从列表中获取元素值】，request为："+request);
		ShardedJedis jedis = null;
		boolean isBroken = false;
		
		try{
			int getIndex = 0;
			jedis = redisFactory.getJedis();
			Jedis j = jedis.getShard(request.getKey());
			j.select(this.getDB(request.getDb()));
			Pipeline pipeline = j.pipelined();
			
//	        ShardedJedisPipeline pipeline = jedis.pipelined();
	        
	        Response<Long> lenResp = pipeline.llen(request.getKey());
	        pipeline.sync();
	        
	        long len = lenResp.get();
	        int  index = request.getIndex();
	        if(index < 0 || index >= len){//输入不合法，随机生成一个索引
	        	Random r = new Random();
	        	getIndex = r.nextInt((int)len);
	        }else{
	        	getIndex = index;
	        }
	        
	        pipeline = j.pipelined();
	        Response<String> strResp = pipeline.lindex(request.getKey(), getIndex);
	        pipeline.sync();
	        
	        return strResp.get();
	        
		}catch(Exception e){
			logger.error("【根据索引和列表名称，从列表中获取元素值】，出现异常",e);
			isBroken = true;
		}finally{
			redisFactory.release(jedis, isBroken);
		}
		return null;
	}
	
	/**
	 * chenmeiyang
	 * 根据key删除hash类型所有字段
	 */
	@Override
	public Object removeHashByKey(RedisHashRequest request) {
		logger.info("==========【根据key删除hash类型所有字段】，请求参数RedisRequest对象为："+request);
		ShardedJedis jedis  = null;
		boolean isBroken = false;
		
		try{
			if(StringUtil.isEmpty(request.getKey()) || request.getFiled().length == 0){
				return null;
			}
			jedis = redisFactory.getJedis();
			Jedis j = jedis.getShard(request.getKey());
			j.select(this.getDB(request.getDb()));
			Pipeline pipeline = j.pipelined();
			
//	        ShardedJedisPipeline pipeline = jedis.pipelined();
	        
	        Response<Long> response = pipeline.hdel(request.getKey(), request.getFiled());
	        pipeline.sync();
	        
	        return response.get();
		}catch(Exception e){
			logger.error("【根据key删除hash类型所有字段】，出现异常",e);
			isBroken = true;
		}finally{
			redisFactory.release(jedis, isBroken);
		}
		return null;
	}
	


	/* chenmeiyang
	 * 放入key-value
	 */
	@Override
	public Object putKeyAndValue(RedisRequest request) {
		
		logger.info("==========【放入key-value】，请求参数RedisRequest对象为："+request);
		ShardedJedis jedis = null;
		boolean isBroken = false;
		try{
			if(StringUtil.isEmpty(request.getKey()) || StringUtil.isEmpty(request.getValue())){
				logger.info("【放入key-value】，请求参数RedisRequest对象参数不全");
				return null;
			}
			
			jedis = redisFactory.getJedis();
			Jedis j = jedis.getShard(request.getKey());
			j.select(this.getDB(request.getDb()));
			Pipeline pipeline = j.pipelined();
//	        ShardedJedisPipeline pipeline = jedis.pipelined();
	        
	        Response<String> response = pipeline.set(request.getKey(), request.getValue());
	        pipeline.sync();
	        
	        return response.get();
			
		}catch(Exception e){
			logger.error("【放入key-value】，出现异常",e);
			isBroken = true;
		}finally{
			redisFactory.release(jedis, isBroken);
		}
		return null;
	}

	
	
	/**
	 * @param request
	 * @return
	 * 根据key删除value
	 */
	@Override
	public ListResponse removeByKey(RedisRequest request) {
		ListResponse response = new ListResponse();
		ShardedJedis jedis = null;
		boolean isBroken = false;
		try{
			logger.info("==========【根据key删除value】，请求参数RedisRequest对象为："+request);
			this.validateRequest(request);
			
			jedis = redisFactory.getJedis();
			Jedis j = jedis.getShard(request.getKey());
			j.select(this.getDB(request.getDb()));
			Pipeline pipeline = j.pipelined();
//	        ShardedJedisPipeline pipeline = jedis.pipelined();
	        
	        //删除
	        Response<Long> delLogo = pipeline.del(request.getKey());
	        pipeline.sync();
	        
            //组装响应参数
	        response.setSuccess(true);
	        response.setMessage("删除成功");
	        
		}catch(Exception e){
			logger.error("【根据key删除value】，出现异常",e);
			response.setSuccess(false);
			response.setMessage("【根据key删除value】，出现异常");
			isBroken = true;
		}finally{
			redisFactory.release(jedis, isBroken);
		}
		return response;
	}
	

	/**
	 * @param request
	 * @return
	 * 根据key获取list集合
	 */
	@Override
	public ListResponse getListByKey(RedisRequest request) {
		ListResponse response = new ListResponse();
		ShardedJedis jedis = null;
		boolean isBroken = false;
		try{
			logger.info("==========【根据key获取list集合】，请求参数RedisRequest对象为："+request);
			this.validateRequest(request);
			
			jedis = redisFactory.getJedis();
			Jedis j = jedis.getShard(request.getKey());
			j.select(this.getDB(request.getDb()));
			Pipeline pipeline = j.pipelined();
//	        ShardedJedisPipeline pipeline = jedis.pipelined();
	        
	        //获取该列表的总长度
	        Response<Long> resLength = pipeline.llen(request.getKey());
	        pipeline.sync();
	        
	        Long listLen = resLength.get();
	        
	        
	        pipeline = j.pipelined();
	        Response<List<String>> listCoin = pipeline.lrange(request.getKey(), 0, listLen);
	        pipeline.sync();
	        
	        //组装响应参数
	        response.setSuccess(true);
	        response.setMessage("调用成功");
	        response.setResult(listCoin.get());
			
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("【根据key获取list集合】，出现异常",e);
			response.setSuccess(false);
			response.setMessage("【根据key获取list集合】，出现异常");
			isBroken = true;
		}finally{
			redisFactory.release(jedis, isBroken);
		}
		return response;
	}
	

    /**
     * @param request
     * @return
     * 存放散列类型数据
     */
	@Override
	public Object putKeyAndFieldAndValue(RedisRequest request) {
		
		ShardedJedis jedis = null;
		boolean isBroken = false;
		try{
			
		logger.info("=================redis开始存放数据===request==="+request);
		jedis = redisFactory.getJedis();
		Jedis j = jedis.getShard(request.getKey());
		j.select(this.getDB(request.getDb()));
		Pipeline p = j.pipelined();
		
		Response<Long> response = p.hset(request.getKey(), request.getFiled(), request.getValue());
//        ShardedJedisPipeline pipeline = jedis.pipelined();
//
//        Response<Long> response = null;
//        if(!StringUtils.isEmpty(request.getValue())){
//        	response = pipeline.hset(request.getKey(), request.getFiled(), request.getValue());
//        }
//        pipeline.sync();
		p.sync();
        
        return response.get();
        
		}catch(Exception e){
			logger.error("=======redis存放数据出现异常=======", e);
			isBroken = true;
		}finally{
			redisFactory.release(jedis, isBroken);
		}
		
		return null;
	}

    /**
    * @param request
    * @return
    * 根据key-field获取value
    */
	@Override
	public Object getValueByKeyAndField(RedisRequest request) {
		
		ShardedJedis jedis = null;
		boolean isBroken = false;
		try{
			logger.info("=====【getValueByKeyAndField请求对象为】==="+request);
			validateRequest(request);
			
			jedis = redisFactory.getJedis();
			Jedis j = jedis.getShard(request.getKey());
			j.select(this.getDB(request.getDb()));
			
			Pipeline p = j.pipelined();
			Response<String> response = p.hget(request.getKey(), request.getFiled());
			
			p.sync();
			
			Object responseStr = response.get();
			
//	        ShardedJedisPipeline pipeline = jedis.pipelined();
//	        
//	        Response<String> response  = pipeline.hget(request.getKey(), request.getFiled());
//	        
//	        pipeline.sync();
//	        
//	        Object responseStr = response.get();
	        
	        logger.info("======================Redis获取数据完毕,响应response为===="+ responseStr);
	        
	        return responseStr;
	        
			}catch(Exception e){
				logger.error("=====【getValueByKeyAndField获取value】出现异常===", e);
				isBroken = true;
			}finally{
				redisFactory.release(jedis, isBroken);
			}
			return null;
	}


	 /**
     * @param request
     * @return
     * 根据key获取value
     */
	@Override
	public String getValueByKey(RedisRequest request) {
		
		ShardedJedis jedis = null;
		boolean isBroken = false;
		
		try{
			logger.info("=====【getValueByKey请求对象为】==="+request);
			validateRequest(request);
			
			jedis = redisFactory.getJedis();
			Jedis j = jedis.getShard(request.getKey());
			j.select(this.getDB(request.getDb()));
			Pipeline p = j.pipelined();
			Response<String> response = p.get(request.getKey());
			p.sync();
//	        ShardedJedisPipeline pipeline = jedis.pipelined();
//	       
//	        Response<String> response  = pipeline.get(request.getKey());
//	        pipeline.sync();
	        
	        String responseStr        = response.get();
	        
	        logger.info("======================Redis获取数据完毕===="+responseStr);
	        
	        return responseStr;
	        
			}catch(Exception e){
				logger.error("=====【getValueByKey获取value】出现异常===", e);
				isBroken = true;
			}finally{
				redisFactory.release(jedis, isBroken);
			}
			
			return null;
	}
	
	
	private void validateRequest(RedisRequest request){
		
		Assert.notNull(request,"请求对象不能为空");
		Assert.notNull(request.getKey(), "请求对象的key字段不能为空");
//		Assert.notNull(request.getDb(), "请求对象的数据库DB字段不能为空");
		
	}
	
	/**
	 * chenmeiyang
	 * 根据用户请求，选择指定的DB
	 */
	private int getDB(RedisDBEnum db){
		if(null == db){
			return appProperties.getQianbaoDB();
		}else if(Constant.DB_QIANBAO.equals(db.getCode())){
			return appProperties.getQianbaoDB();
		}else if(Constant.DB_HOUSE.equals(db.getCode())){
			return appProperties.getHouseDB();
		}else{//默认选择 钱包DB
			return appProperties.getQianbaoDB();
		}
	}


}
