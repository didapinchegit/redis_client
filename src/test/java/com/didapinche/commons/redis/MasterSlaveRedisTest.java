package com.didapinche.commons.redis;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * @author 罗立东 rod
 * @time 15/7/30
 */
@ContextConfiguration(locations = {"classpath*:spring_MasterSlave.xml"})
public class MasterSlaveRedisTest extends RedisTestBase {

    @Autowired
    private MasterSlaveRedisClient client;

    @Autowired
    private MasterSlaveRedisClient clientWithSentinel;


    /**
     * 1master 2slave
     */
    @Test
    public void testBasicReadWrite() throws InterruptedException {
        String key = "key";
        String value = "value";
        client.set(key, value);
        Assert.assertEquals(value, client.get(key));

        client.pexpire(key, 100);
        Assert.assertTrue(client.exists(key));

        //主从复制有延时
        Thread.sleep(200);

        Assert.assertFalse(client.exists(key));

    }

    /**
     * HA主动发现 初始化
     */
    @Test
    public void testHAInit() throws InterruptedException {
        //初始化sentinelsManager
        applicationContext.getBean("sentinelsManager");

        Jedis sentinelJedis = (Jedis) applicationContext.getBean("sentinelJedis");

        //wait to conf
        Thread.sleep(1000);

        MasterSlaveRedisPool masterSlaveRedisPool = clientWithSentinel.getPool();


        //获取master info
        List<Map<String, String>> mastersInfo = sentinelJedis.sentinelMasters();

        //验证master数量
        Assert.assertEquals(1, mastersInfo.size());

        Map<String, String> masterInfo = mastersInfo.get(0);

        String masterName = masterInfo.get("name");
        String masterHost = masterInfo.get("ip");
        int masterPort = Integer.parseInt(masterInfo.get("port"));

        //比对master
        HostAndPort masterHap = masterSlaveRedisPool.getMasterHap();
        Assert.assertTrue(masterHap.equals(new HostAndPort(masterHost,masterPort)));

        //获取slave info
        List<Map<String, String>> slavesInfo = sentinelJedis.sentinelSlaves(masterName);

        List<HostAndPort> slavesHap = masterSlaveRedisPool.getSlaveHaps();

        //验证slave数量
        Assert.assertEquals(slavesHap.size(),slavesInfo.size());

        for (Map<String, String> slaveInfo : slavesInfo) {
            String slaveHost = slaveInfo.get("ip");
            int slavePort = Integer.parseInt(slaveInfo.get("port"));
            HostAndPort slaveHap = new HostAndPort(slaveHost, slavePort);
            slavesHap.remove(slaveHap);
        }

        Assert.assertEquals(0,slavesHap.size());
    }


}
