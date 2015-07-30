package com.didapinche.commons.redis.sentinel;

import com.didapinche.commons.redis.*;
import com.didapinche.commons.redis.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

/**
 * Created by fengbin on 15/7/30.
 */
public class SentinelsManager {
    private static final Logger logger = LoggerFactory.getLogger(SentinelsManager.class);

    //监听redisSentinel
    private Set<MasterListener> masterListeners = new HashSet<MasterListener>();

    private SentinelInfo sentinelInfo;

    private ReidsPool reidsPool;
    /**
     * 初始化一组sentinel监听服务，之后初始化master连接池和slave连接池
     * @return
     */
    private void initSentinels() {

        Set<String> sentinels = sentinelInfo.getSentinels();
        List<String> masterNames = sentinelInfo.getMasterNames();

        HostAndPort master = null;
        List<Map<String,String>>  slaveInfo = null;
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
                    slaveInfo = jedis.sentinelSlaves(masterName);

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
                    throw new JedisException("Can connect to sentinel, but " + masterName
                            + " seems to be not monitored...");
                } else {
                    throw new JedisConnectionException("All sentinels down, cannot determine where is "
                            + masterName + " master is running...");
                }
            }

            reidsPool.buildMasterSlaveInfo(masterName, master, slaveInfo);
        }




        logger.info("Redis master running at " + master + ", starting Sentinel listeners...");

        for (String sentinel : sentinels) {
            final HostAndPort hap = Utils.toHostAndPort(Arrays.asList(sentinel.split(":")));
            MasterListener masterListener = new MasterListener(masterNames, hap.getHost(), hap.getPort(),reidsPool);
            masterListeners.add(masterListener);
            new Thread(masterListener).start();
        }

        reidsPool.initPool();
    }

    public void shutdownSentinels(){
        for (MasterListener masterListener : masterListeners) {
            masterListener.shutdown();
        }
    }


    public ReidsPool getReidsPool() {
        return reidsPool;
    }

    public void setReidsPool(ReidsPool reidsPool) {
        this.reidsPool = reidsPool;
    }

    public SentinelInfo getSentinelInfo() {
        return sentinelInfo;
    }

    public void setSentinelInfo(SentinelInfo sentinelInfo) {
        this.sentinelInfo = sentinelInfo;
    }
}
