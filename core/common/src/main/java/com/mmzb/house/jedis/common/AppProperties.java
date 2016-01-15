package com.mmzb.house.jedis.common;

import org.springframework.beans.factory.annotation.Value;

/**
 * <p>配置文件中注入的参数</p>
 *
 * @author Zhang Yun
 * @version $Id: AppProperties.java, v 0.1 14-1-15 上午10:30 zhangyun Exp $
 */
public class AppProperties {

    @Value("#{properties['redis.db.qianbao']}")
    private int qianbaoDB;
    
    @Value("#{properties['redis.db.house']}")
    private int houseDB;

	public int getQianbaoDB() {
		return qianbaoDB;
	}

	public void setQianbaoDB(int qianbaoDB) {
		this.qianbaoDB = qianbaoDB;
	}

	public int getHouseDB() {
		return houseDB;
	}

	public void setHouseDB(int houseDB) {
		this.houseDB = houseDB;
	}
	
}
