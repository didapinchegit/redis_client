package com.didapinche.commons.redis;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;

import java.util.*;

/**
 * Created by fengbin on 15/7/20.
 */

@ContextConfiguration(locations = {"classpath*:spring_Matrix.xml"})
public class MatrixRedisTest extends RedisTestBase {

    @Autowired
    protected MatrixRedisClient matrixRedisClient;

    /**
     * master1 集群3个，master2 集群3个
     */
    @Test
    public void testInitPool_3_3(){
        MatrixRedisPool pool =  (MatrixRedisPool)matrixRedisClient.getRedisPool();
        Assert.assertNotNull(pool.getMasterShardedJedisPool());

        Assert.assertTrue(pool.getSlaveShardedJedisPools().size() == 2);
    }
    /**
     * master1 集群2个，master2 集群3个
     */
    @Test
    public void testInitPool_2_3(){
        MatrixRedisPool pool =  (MatrixRedisPool)matrixRedisClient.getRedisPool();
        Assert.assertNotNull(pool.getMasterShardedJedisPool());

        Assert.assertTrue(pool.getSlaveShardedJedisPools().size() == 1);
    }

    /**
     * master1 集群1个，master2 集群3个
     */
    @Test
    public void testInitPool_1_3(){
        MatrixRedisPool pool =  (MatrixRedisPool)matrixRedisClient.getRedisPool();
        Assert.assertNotNull(pool.getMasterShardedJedisPool());

        Assert.assertTrue(pool.getSlaveShardedJedisPools().size() == 0);
    }

    @Test
    public void testSwitchMaster(){
//        MatrixRedisPool pool =  (MatrixRedisPool)matrixRedisClient.getRedisPool();
//        ShardedJedis masterShardedJedis = pool.getMasterResource();
//        ShardedJedis slaveShardedJedis = pool.getSlaveResource();
//        Map<String, JedisShardInfo> masterShards =  pool.getMasterShards();
//        Map<String, List<JedisShardInfo>> slaveShards =  pool.getMultiSlaveShards();
//
//        Map<String, String> masterSearchMap =  new HashMap<>();
//        Map<String, String> slaveSearchMap =  new HashMap<>();
//
//        for(String masterName:masterShards.keySet()){
//            JedisShardInfo shardInfo = masterShards.get(masterName);
//            masterSearchMap.put(shardInfo.getHost()+":"+shardInfo.getPort(),masterName);
//        }
//
//        for(String masterName:slaveShards.keySet()){
//            List<JedisShardInfo> slaveshardInfo = slaveShards.get(masterName);
//            for(JedisShardInfo shardInfo:slaveshardInfo){
//                slaveSearchMap.put(shardInfo.getHost()+":"+shardInfo.getPort(),masterName);
//            }
//        }
//
//
//        for(JedisShardInfo masterShardInfo:pool.getMasterShardedJedisPool().getResource().getAllShardInfo()){
//            String masterMasterName = masterSearchMap.get(masterShardInfo .getHost() + ":" + masterShardInfo.getPort());
//
//            System.out.println(masterMasterName);
//        }
//
//
//        for(JedisShardInfo slaveShardInfo:pool.getSlaveShardedJedisPools().get(0).getResource().getAllShardInfo()){
//            String slaveMasterName = slaveSearchMap.get(slaveShardInfo.getHost() + ":" + slaveShardInfo.getPort());
//
//            System.out.println(slaveMasterName);
//        }
//


    }


    @Test
    public void testSdown() throws InterruptedException {
        MatrixRedisPool pool =  (MatrixRedisPool)matrixRedisClient.getRedisPool();
        int slaveSize = pool.getSlaveShardedJedisPools().size();
        System.out.println("请下线一台slave......");

        Thread.currentThread().sleep(30000);



        int newSlaveSize = pool.getSlaveShardedJedisPools().size();
        Assert.assertTrue(newSlaveSize == (slaveSize -1));

    }


    @Test
    public void testnSdown() throws InterruptedException {
        MatrixRedisPool pool =  (MatrixRedisPool)matrixRedisClient.getRedisPool();
        int slaveSize = pool.getSlaveShardedJedisPools().size();
        System.out.println("请补齐一台slave......");

        Thread.currentThread().sleep(30000);



        int newSlaveSize = pool.getSlaveShardedJedisPools().size();
        Assert.assertTrue(newSlaveSize == (slaveSize + 1));


    }



    @Test
    public void testGetMasterResource(){

        ShardedJedis shardedJedis = matrixRedisClient.getRedisPool().getMasterResource();
        Collection<JedisShardInfo> shardInfoList = shardedJedis.getAllShardInfo();


        for(JedisShardInfo shardInfo:shardInfoList){
            Assert.assertTrue(Constants.DEFAULT_MASTER1_MASTER.equals(shardInfo.getHost() + ":" + shardInfo.getPort())
                    || Constants.DEFAULT_MASTER2_MASTER.equals(shardInfo.getHost() + ":" + shardInfo.getPort()));
        }
    }



    @Test
    public void testGetSlaveResource(){
        ShardedJedis shardedJedis = matrixRedisClient.getRedisPool().getSlaveResource();
        Collection<JedisShardInfo> shardInfoList = shardedJedis.getAllShardInfo();

        for(JedisShardInfo shardInfo:shardInfoList){
            Assert.assertTrue(Arrays.asList(Constants.DEFAULT_MASTER1_SLAVES).contains(shardInfo.getHost() + ":" + shardInfo.getPort())
                    || Arrays.asList(Constants.DEFAULT_MASTER2_SLAVES).contains(shardInfo.getHost() + ":" + shardInfo.getPort()));
        }
    }


    @Test
    public void testHashDiff() {
        MatrixRedisPool pool =  (MatrixRedisPool)matrixRedisClient.getRedisPool();
        ShardedJedis masterShardedJedis = pool.getMasterResource();
        ShardedJedis slaveShardedJedis = pool.getSlaveResource();
        Map<String, JedisShardInfo> masterShards =  pool.getMasterShards();
        Map<String, List<JedisShardInfo>> slaveShards =  pool.getMultiSlaveShards();

        Map<String, String> masterSearchMap =  new HashMap<>();
        Map<String, String> slaveSearchMap =  new HashMap<>();

        for(String masterName:masterShards.keySet()){
            JedisShardInfo shardInfo = masterShards.get(masterName);
            masterSearchMap.put(shardInfo.getHost()+":"+shardInfo.getHost(),masterName);
        }

        for(String masterName:slaveShards.keySet()){
            List<JedisShardInfo> slaveshardInfo = slaveShards.get(masterName);
            for(JedisShardInfo shardInfo:slaveshardInfo){
                slaveSearchMap.put(shardInfo.getHost()+":"+shardInfo.getHost(),masterName);
            }
        }




        for (int i =0 ;i < 10 ;i++){
            String randomKey = String.valueOf(new Random().nextInt(100000));

            JedisShardInfo masterShardInfo = masterShardedJedis.getShardInfo(randomKey);
            JedisShardInfo slaveShardInfo = slaveShardedJedis.getShardInfo(randomKey);

            String masterMasterName = masterSearchMap.get(masterShardInfo .getHost() + ":" + masterShardInfo.getPort());
            String slaveMasterName = slaveSearchMap.get(slaveShardInfo .getHost() + ":" + slaveShardInfo.getPort());

            Assert.assertEquals(masterMasterName, slaveMasterName);

        }


    }


}
