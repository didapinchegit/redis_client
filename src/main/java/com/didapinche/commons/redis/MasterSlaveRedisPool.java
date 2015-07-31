package com.didapinche.commons.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单主多从的资源池
 *
 * 注意：如果要设置密码 master slave 的密码必须要设置成一样，否则sentinel调度会混乱
 * 默认db index：0
 *
 * File Created at 2015-7-28 by fengbin
 *
 * Copyright 2015 didapinche.com
 *
 * modified at 15/7/30 by 罗立东 rod
 */
public class MasterSlaveRedisPool implements RedisPool<Jedis>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MasterSlaveRedisPool.class);

    /**
     * 连接池配置
     */
    private JedisPoolConfig jedisPoolConfig;

    /**
     * master池
     */
    private JedisPool masterJedisPool;

    /**
     * slave 池映射表
     */
    private Map<HostAndPort, JedisPool> slaveJedisPoolMap;

    private Random randomSlaveIndex = new Random();

    /**
     * auth
     */
    private String passWord;

    /**
     * 超时 ms
     */
    private int timeout = Protocol.DEFAULT_TIMEOUT;

    /**
     * master conf
     */
    private HostAndPort masterHap;

    /**
     * slave conf
     */
    private List<HostAndPort> slaveHaps;

    @Override
    public void initPool() {
        initMasterPool();
        initSlavePool();
    }

    @Override
    public void initMasterPool() {
        masterJedisPool = new JedisPool(jedisPoolConfig, masterHap.getHost(), masterHap.getPort(), timeout, passWord);
    }

    @Override
    public void initSlavePool() {
        if (CollectionUtils.isEmpty(slaveHaps))
            return;

        if (CollectionUtils.isEmpty(slaveJedisPoolMap))
            slaveJedisPoolMap = new ConcurrentHashMap<>();
        else {
            for (JedisPool slaveJedisPool : slaveJedisPoolMap.values()) {
                slaveJedisPool.close();
            }
            slaveJedisPoolMap.clear();
        }

        for (HostAndPort slaveHap : slaveHaps) {
            slaveJedisPoolMap.put(slaveHap, new JedisPool(jedisPoolConfig, slaveHap.getHost(), slaveHap.getPort(), timeout, passWord));
        }

    }

    @Override
    public boolean hasSlave() {
        return !CollectionUtils.isEmpty(slaveJedisPoolMap);
    }

    @Override
    public void switchMaster(String masterName, HostAndPort newMasterHostAndPort) {
        //新上的主机一定是以前的从机，所以先下线从机
        sdownSlave(masterName, newMasterHostAndPort);

        this.masterHap = newMasterHostAndPort;
        initMasterPool();
    }


    @Override
    public void sdownSlave(String masterName, HostAndPort hostAndPort) {
        slaveHaps.remove(hostAndPort);
        removeSlavePoolPartly(hostAndPort);
    }

    /**
     * 无需重新初始化，移除下线的slave pool
     * @param hostAndPort
     */
    private void removeSlavePoolPartly(HostAndPort hostAndPort){
        slaveJedisPoolMap.remove(hostAndPort);
    }

    @Override
    public void nsdownSlave(String masterName, HostAndPort hostAndPort) {
        slaveHaps.add(hostAndPort);
        addSlavePoolPartly(hostAndPort);
    }

    /**
     * 无需重新初始化，增加上线的slave pool
     * @param hostAndPort
     */
    private void addSlavePoolPartly(HostAndPort hostAndPort) {
        slaveJedisPoolMap.put(hostAndPort, new JedisPool(jedisPoolConfig, hostAndPort.getHost(), hostAndPort.getPort(), timeout, passWord));
    }

    @Override
    public void buildMasterSlaveInfo(String masterName, HostAndPort masterInfo, List<HostAndPort> slaveHaps) {
        this.masterHap = masterInfo;
        this.slaveHaps = slaveHaps;
        initPool();
    }

    @Override
    public Jedis getMasterResource() {
        return masterJedisPool.getResource();
    }

    @Override
    public void returnMasterResourceObject(Jedis jedis) {
        jedis.close();
    }

    @Override
    public Jedis getSlaveResource() {
        int index = randomSlaveIndex.nextInt(slaveHaps.size());
        JedisPool jedisPool = slaveJedisPoolMap.get(slaveHaps.get(index));
        return jedisPool.getResource();
    }

    @Override
    public void returnSlaveResourceObject(Jedis jedis) {
        jedis.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (masterHap == null)
            return;
        initPool();
    }


    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        this.jedisPoolConfig = jedisPoolConfig;
    }


    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public HostAndPort getMasterHap() {
        return masterHap;
    }

    public void setMasterHap(HostAndPort masterHap) {
        this.masterHap = masterHap;
    }

    public List<HostAndPort> getSlaveHaps() {
        return slaveHaps;
    }

    public void setSlaveHaps(List<HostAndPort> slaveHaps) {
        this.slaveHaps = slaveHaps;
    }
}
