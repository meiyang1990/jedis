package com.mama.jedis.service.facade.redis;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class RedisFactory {
	
	private static Logger logger = LoggerFactory.getLogger(RedisFactory.class);
	
	private List<String> host;
	private long maxWaitMillis;
	private int maxIdle;
	private int minIdle;
	private boolean testOnBorrow;
	private int timeOut;
	private int minEvictableIdleTimeMillis;
	private int maxTotal;
	private static List<JedisShardInfo> shards;
	private static ShardedJedisPool pool;

	public RedisFactory(List<String> host, long maxWaitMillis, int maxIdle,
			boolean testOnBorrow, String pass, int timeOut, int minIdle,
			int minEvictableIdleTimeMillis, int maxTotal) {
		this.host = host;
		this.maxWaitMillis = maxWaitMillis;
		this.maxIdle = maxIdle;
		this.minIdle = minIdle;
		this.testOnBorrow = testOnBorrow;
		this.timeOut = timeOut;
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
		this.maxTotal = maxTotal;
		if (host.size() != 0)
			for (int i = 0; i < host.size(); i++) {
				String[] h = ((String) this.host.get(i)).split(":");
				shards = Arrays
						.asList(new JedisShardInfo[] { new JedisShardInfo(h[0]
								.trim(), Integer.parseInt(h[1].trim())) });
			}
		else {
			logger.info("请检查Redis配置，host项为必填项！格式[IP:PORT]");
		}

		for (int i = 0; i < shards.size(); i++) {
			JedisShardInfo info = (JedisShardInfo) shards.get(i);
			info.setPassword(pass);
			info.setConnectionTimeout(timeOut);
		}
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setMaxTotal(maxTotal);
		poolConfig.setTestOnBorrow(testOnBorrow);
		poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		pool = new ShardedJedisPool(poolConfig, shards);
	}

	/**
	 * chenmeiyang
	 * 获取Jedis实例
	 */
	@SuppressWarnings("deprecation")
	public ShardedJedis getJedis() throws JedisException {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
		} catch (JedisException e) {
			logger.error("=======获取Jedis实例异常", e);
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
			throw e;
		}
		return jedis;
	}

	/**
	 * chenmeiyang
	 * 释放Jedis实例连接
	 */
	@SuppressWarnings("deprecation")
	public void release(ShardedJedis jedis, boolean isBroken) {
		if (jedis != null)
			if (isBroken)
				pool.returnBrokenResource(jedis);
			else
				pool.returnResource(jedis);
	}
}