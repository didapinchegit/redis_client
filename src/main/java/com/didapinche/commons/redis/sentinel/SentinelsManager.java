package com.didapinche.commons.redis.sentinel;

import com.didapinche.commons.redis.*;
import com.didapinche.commons.redis.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

/**
 * RedisClientException.java
 * Project: redis client
 *
 * File Created at 2015-7-30 by fengbin
 *
 * Copyright 2015 didapinche.com
 */
public class SentinelsManager implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SentinelsManager.class);

    /**
     * 监听redisSentinel
     */
    private Set<MasterListener> masterListeners = new HashSet<MasterListener>();
    /**
     * sentinel 配置信息
     */
    private SentinelInfo sentinelInfo;
    /**
     * 连接池
     */
    private RedisPool reidsPool;
    /**
     * 初始化一组sentinel监听服务，之后初始化master连接池和slave连接池
     * @return
     */
    private void initSentinels() {

        Set<String> sentinels = sentinelInfo.getSentinels();
        List<String> masterNames = sentinelInfo.getMasterNames();

        HostAndPort master = null;
        List<Map<String,String>>  slaveInfos = null;
        List<HostAndPort> slaveHaps = null;
        boolean sentinelAvailable = false;

        logger.info("Trying to find master from available Sentinels...");

        for(String masterName : masterNames ){
            for (String sentinel : sentinels) {
                final HostAndPort hap = Utils.toHostAndPort(Arrays.asList(sentinel.split(":")));

                logger.info("Connecting to Sentinel " + hap);

                Jedis jedis = null;
                try {
                    jedis = new Jedis(hap.getHost(), hap.getPort());


                    List<String> masterAddr = jedis.sentinelGetMasterAddrByName(masterName);
                    slaveInfos = jedis.sentinelSlaves(masterName);

                    slaveHaps = new ArrayList<>();
                    for(Map<String,String>slaveInfo : slaveInfos) {

                        //下线状态的slave也会查出来，跳过下线状态的
                        String sdownTime = slaveInfo.get("s-down-time");
                        if(!(sdownTime == null || "0".equals(sdownTime))){
                            continue;
                        }
                        String host = slaveInfo.get("ip");
                        String port = slaveInfo.get("port");
                        HostAndPort hostAndPort = new HostAndPort(host, Integer.parseInt(port));

                        slaveHaps.add(hostAndPort);
                    }

                    // connected to sentinel...
                    sentinelAvailable = true;

                    if (masterAddr == null || masterAddr.size() != 2) {
                        logger.warn("Can not get master addr, master name: " + masterName + ". Sentinel: " + hap
                                + ".");
                        continue;
                    }

                    master = Utils.toHostAndPort(masterAddr);

                    logger.info("Found Redis master at " + master);
                    break;

                } catch (JedisConnectionException e) {
                    logger.warn("Cannot connect to sentinel running @ " + hap + ". Trying next one.");
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }


            if (master == null) {
                if (sentinelAvailable) {
                    // can connect to sentinel, but master name seems to not
                    // monitored
                    this.shutdownSentinels();
                    throw new JedisException("Can connect to sentinel, but " + masterName
                            + " seems to be not monitored...");
                } else {
                    this.shutdownSentinels();
                    throw new JedisConnectionException("All sentinels down, cannot determine where is "
                            + masterName + " master is running...");
                }
            }

            //构建一个masterName
            reidsPool.buildMasterSlaveInfo(masterName, master, slaveHaps);
        }

        //初始化线程池
        reidsPool.initPool();



        logger.info("Redis master running at " + master + ", starting Sentinel listeners...");

        for (String sentinel : sentinels) {
            final HostAndPort hap = Utils.toHostAndPort(Arrays.asList(sentinel.split(":")));
            MasterListener masterListener = new MasterListener(masterNames, hap.getHost(), hap.getPort(),reidsPool);
            masterListeners.add(masterListener);
            new Thread(masterListener).start();
        }

    }

    public void shutdownSentinels(){
        for (MasterListener masterListener : masterListeners) {
            masterListener.shutdown();
        }
    }


    public RedisPool getReidsPool() {
        return reidsPool;
    }

    public void setReidsPool(RedisPool reidsPool) {
        this.reidsPool = reidsPool;
    }

    public SentinelInfo getSentinelInfo() {
        return sentinelInfo;
    }

    public void setSentinelInfo(SentinelInfo sentinelInfo) {
        this.sentinelInfo = sentinelInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initSentinels();
    }
}
