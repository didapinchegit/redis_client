package com.didapinche.commons.redis;

import com.didapinche.commons.redis.exceptions.MultiKeyRedisClientException;
import com.didapinche.commons.redis.exceptions.RedisClientException;
import redis.clients.jedis.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Project: redis client
 *
 * File Created at 2015-7-28 by fengbin
 *
 * Copyright 2015 didapinche.com
 */
public abstract class AbstractRedisClient implements RedisClient{

    /**
     * 是否读写分离
     */
    protected boolean autoReadFromSlave = true;


    protected static interface CallBack<T>{
        T execute(JedisCommands jedis);
    }

    protected  <T> T execute(CallBack<T> callBack) {
        return execute(callBack,false);
    }

    protected  <T> T execute(CallBack<T> callBack,boolean readonly) {
        return execute(callBack,readonly,0);
    }


    protected abstract <T> T execute(CallBack<T> callBack, boolean readonly,int retryTimes);


    protected static interface MultiKeyCallBack<T>{
        T execute(MultiKeyCommands jedis);
    }

    protected <T> T execute(MultiKeyCallBack<T> callBack) {
        return execute(callBack,false);
    }

    protected <T> T execute(MultiKeyCallBack<T> callBack,boolean readonly) {
        return execute(callBack,readonly,0);
    }


    protected abstract <T> T execute(MultiKeyCallBack<T> callBack, boolean readonly,int retryTimes) throws MultiKeyRedisClientException;

    /* multi jedis Commands*/

    public String set(final String key, final String value) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.set(key,value);
            }
        },false);
    }

    public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.set(key,value,nxxx,expx,time);
            }
        },false);
    }

    public String get(final String key) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.get(key);
            }
        },true);
    }

    public Boolean exists(final String key) {
        return execute(new CallBack<Boolean>() {
            public Boolean execute(JedisCommands jedis) {
                return jedis.exists(key);
            }
        },true);
    }

    public Long persist(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.persist(key);
            }
        },false);
    }

    public String type(final String key) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.type(key);
            }
        },true);
    }

    public Long expire(final String key, final int seconds) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.expire(key, seconds);
            }
        },false);
    }

    public Long pexpire(final String key, final long milliseconds) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.pexpire(key, milliseconds);
            }
        });
    }

    public Long expireAt(final String key, final long unixTime) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.expireAt(key, unixTime);
            }
        });

    }

    public Long pexpireAt(final String key, final long millisecondsTimestamp) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.expireAt(key, millisecondsTimestamp);
            }
        });
    }

    public Long ttl(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.ttl(key);
            }
        },true);
    }

    public Boolean setbit(final String key, final long offset, final boolean value) {
        return execute(new CallBack<Boolean>() {
            public Boolean execute(JedisCommands jedis) {
                return jedis.setbit(key, offset,value);
            }
        });
    }

    public Boolean setbit(final String key, final long offset, final String value) {
        return execute(new CallBack<Boolean>() {
            public Boolean execute(JedisCommands jedis) {
                return jedis.setbit(key, offset,value);
            }
        });
    }

    public Boolean getbit(final String key, final long offset) {
        return execute(new CallBack<Boolean>() {
            public Boolean execute(JedisCommands jedis) {
                return jedis.getbit(key, offset);
            }
        },true);
    }

    public Long setrange(final String key, final long offset, final String value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.setrange(key, offset, value);
            }
        });
    }

    public String getrange(final String key, final long startOffset, final long endOffset) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.getrange(key, startOffset, endOffset);
            }
        },true);
    }

    public String getSet(final String key, final String value) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.getSet(key, value);
            }
        });
    }

    public Long setnx(final String key, final String value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.setnx(key, value);
            }
        });
    }

    public String setex(final String key, final int seconds, final String value) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    public Long decrBy(final String key, final long integer) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.decrBy(key, integer);
            }
        });
    }

    public Long decr(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.decr(key);
            }
        });
    }

    public Long incrBy(final String key, final long integer) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.incrBy(key, integer);
            }
        });
    }

    public Double incrByFloat(final String key, final double value) {
        return execute(new CallBack<Double>() {
            public Double execute(JedisCommands jedis) {
                return jedis.incrByFloat(key, value);
            }
        });
    }

    public Long incr(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.incr(key);
            }
        });
    }

    public Long append(final String key, final String value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.append(key, value);
            }
        });
    }

    public String substr(final String key, final int start, final int end) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.substr(key, start, end);
            }
        });
    }

    public Long hset(final String key, final String field, final String value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    public String hget(final String key, final String field) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.hget(key, field);
            }
        },true);
    }

    public Long hsetnx(final String key, final String field, final String value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.hsetnx(key, field,value);
            }
        });
    }

    public String hmset(final String key, final Map<String, String> hash) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.hmset(key, hash);
            }
        });
    }

    public List<String> hmget(final String key, final String... fields) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.hmget(key, fields);
            }
        },true);
    }

    public Long hincrBy(final String key, final String field, final long value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.hincrBy(key, field, value);
            }
        });
    }


    public Double hincrByFloat(final String key, final String field, final double value){
        return execute(new CallBack<Double>() {
            public Double execute(JedisCommands jedis) {
                if(jedis instanceof  Jedis) {
                   return ((Jedis) jedis).hincrByFloat(key, field, value);
                } else if (jedis instanceof ShardedJedis){
                   return ((ShardedJedis) jedis).hincrByFloat(key, field, value);
                } else{
                    throw new RedisClientException("error jedis type");
                }
            }
        });
    }

    public Boolean hexists(final String key, final String field) {
        return execute(new CallBack<Boolean>() {
            public Boolean execute(JedisCommands jedis) {
                return jedis.hexists(key, field);
            }
        },true);
    }

    public Long hdel(final String key, final String... field) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    public Long hlen(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.hlen(key);
            }
        },true);
    }

    public Set<String> hkeys(final String key) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.hkeys(key);
            }
        },true);
    }

    public List<String> hvals(final String key) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.hvals(key);
            }
        },true);
    }

    public Map<String, String> hgetAll(final String key) {
        return execute(new CallBack<Map<String, String>>() {
            public Map<String, String> execute(JedisCommands jedis) {
                return jedis.hgetAll(key);
            }
        },true);
    }

    public Long rpush(final String key, final String... string) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.rpush(key, string);
            }
        });
    }

    public Long lpush(final String key, final String... string) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.lpush(key, string);
            }
        });
    }

    public Long llen(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.llen(key);
            }
        },true);
    }

    public List<String> lrange(final String key, final long start, final long end) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.lrange(key, start, end);
            }
        },true);
    }

    public String ltrim(final String key, final long start, final long end) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.ltrim(key, start, end);
            }
        });
    }

    public String lindex(final String key, final long index) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.lindex(key, index);
            }
        },true);
    }

    public String lset(final String key, final long index, final String value) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.lset(key, index,value);
            }
        });
    }

    public Long lrem(final String key, final long count, final String value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.lrem(key, count, value);
            }
        });
    }

    public String lpop(final String key) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.lpop(key);
            }
        });
    }

    public String rpop(final String key) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.rpop(key);
            }
        });
    }

    public Long sadd(final String key, final String... member) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.sadd(key,member);
            }
        });
    }

    public Set<String> smembers(final String key) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.smembers(key);
            }
        },true);
    }

    public Long srem(final String key, final String... member) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.srem(key, member);
            }
        });
    }

    public String spop(final String key) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.spop(key);
            }
        });
    }

    public Set<String> spop(final String key, final long count) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.spop(key, count);
            }
        });
    }

    public Long scard(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.scard(key);
            }
        },true);
    }

    public Boolean sismember(final String key, final String member) {
        return execute(new CallBack<Boolean>() {
            public Boolean execute(JedisCommands jedis) {
                return jedis.sismember(key, member);
            }
        },true);
    }

    public String srandmember(final String key) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.srandmember(key);
            }
        });
    }

    public List<String> srandmember(final String key, final int count) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.srandmember(key,count);
            }
        });
    }

    public Long strlen(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.strlen(key);
            }
        },true);
    }

    public Long zadd(final String key, final double score, final String member) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zadd(key, score, member);
            }
        });
    }

    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zadd(key,scoreMembers);
            }
        });
    }

    public Set<String> zrange(final String key, final long start, final long end) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrange(key, start, end);
            }
        },true);
    }

    public Long zrem(final String key, final String... member) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zrem(key, member);
            }
        });
    }

    public Double zincrby(final String key, final double score, final String member) {
        return execute(new CallBack<Double>() {
            public Double execute(JedisCommands jedis) {
                return jedis.zincrby(key, score, member);
            }
        });
    }

    public Long zrank(final String key, final String member) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zrank(key, member);
            }
        },true);
    }

    public Long zrevrank(final String key, final String member) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zrevrank(key, member);
            }
        },true);
    }

    public Set<String> zrevrange(final String key, final long start, final long end) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrevrange(key, start, end);
            }
        },true);
    }

    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrangeWithScores(key, start, end);
            }
        },true);
    }

    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrevrangeWithScores(key, start, end);
            }
        },true);
    }

    public Long zcard(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zcard(key);
            }
        },true);
    }

    public Double zscore(final String key, final String member) {
        return execute(new CallBack<Double>() {
            public Double execute(JedisCommands jedis) {
                return jedis.zscore(key, member);
            }
        },true);
    }

    public List<String> sort(final String key) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.sort(key);
            }
        },true);
    }

    public List<String> sort(final String key, final SortingParams sortingParameters) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.sort(key, sortingParameters);
            }
        },true);
    }

    public Long zcount(final String key, final double min, final double max) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zcount(key, min, max);
            }
        },true);
    }

    public Long zcount(final String key, final String min, final String max) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zcount(key, min, max);
            }
        },true);
    }

    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrangeByScore(key, min, max);
            }
        },true);
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrangeByScore(key, min, max);
            }
        },true);
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScore(key, min, max);
            }
        });
    }

    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, final int count) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrangeByScore(key, min, max, offset,count);
            }
        },true);
    }

    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScore(key, min, max);
            }
        },true);
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max, final int offset, final int count) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrangeByScore(key, min, max,offset,count);
            }
        },true);
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScore(key, min, max, offset, count);
            }
        },true);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max);
            }
        },true);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScoreWithScores(key, min, max);
            }
        },true);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset, final int count) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max,offset,count);
            }
        },true);
    }

    public Set<String> zrevrangeByScore(final String key, final String max, final String min, final int offset, final int count) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScore(key, min, max, offset, count);
            }
        },true);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max);
            }
        },true);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScoreWithScores(key, min, max);
            }
        },true);
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max, int offset, int count) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max);
            }
        });
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset, final int count) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScoreWithScores(key, min, max,offset,count);
            }
        },true);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min, final int offset, final int count) {
        return execute(new CallBack<Set<Tuple>>() {
            public Set<Tuple> execute(JedisCommands jedis) {
                return jedis.zrevrangeByScoreWithScores(key, min, max,offset,count);
            }
        },true);
    }

    public Long zremrangeByRank(final String key, final long start, final long end) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zremrangeByRank(key, start, end);
            }
        });
    }

    public Long zremrangeByScore(final String key, final double start, final double end) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zremrangeByScore(key, start, end);
            }
        });
    }

    public Long zremrangeByScore(final String key, final String start, final String end) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zremrangeByScore(key, start, end);
            }
        });
    }

    public Long zlexcount(final String key, final String min, final String max) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zlexcount(key, min, max);
            }
        },true);
    }

    public Set<String> zrangeByLex(final String key, final String min, final String max) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrangeByLex(key,min,max);
            }
        },true);
    }

    public Set<String> zrangeByLex(final String key, final String min, final String max, final int offset, final int count) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrangeByLex(key, min, max, offset,count);
            }
        },true);
    }

    public Set<String> zrevrangeByLex(final String key, final String max, final String min) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrangeByLex(key, min, max);
            }
        },true);
    }

    public Set<String> zrevrangeByLex(final String key, final String max, final String min, final int offset, final int count) {
        return execute(new CallBack<Set<String>>() {
            public Set<String> execute(JedisCommands jedis) {
                return jedis.zrevrangeByLex(key, min, max, offset, count);
            }
        },true);
    }

    public Long zremrangeByLex(final String key, final String min, final String max) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.zremrangeByLex(key, min, max);
            }
        });
    }

    public Long linsert(final String key, final BinaryClient.LIST_POSITION where, final String pivot, final String value) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.linsert(key, where, pivot, value);
            }
        });
    }

    public Long lpushx(final String key, final String... string) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.lpushx(key, string);
            }
        });
    }

    public Long rpushx(final String key, final String... string) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.rpushx(key, string);
            }
        });
    }

    @Deprecated
    public List<String> blpop(final String arg) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.blpop(arg);
            }
        });
    }


    public List<String> blpop(final int timeout, final String key) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.blpop(timeout, key);
            }
        });
    }

    @Deprecated
    public List<String> brpop(final String arg) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.brpop(arg);
            }
        });
    }

    public List<String> brpop(final int timeout, final String key) {
        return execute(new CallBack<List<String>>() {
            public List<String> execute(JedisCommands jedis) {
                return jedis.brpop(timeout, key);
            }
        });
    }

    public Long del(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.del(key);
            }
        });
    }

    public String echo(final String string) {
        return execute(new CallBack<String>() {
            public String execute(JedisCommands jedis) {
                return jedis.echo(string);
            }
        },true);
    }

    public Long move(final String key, final int dbIndex) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.move(key, dbIndex);
            }
        });
    }

    public Long bitcount(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.bitcount(key);
            }
        },true);
    }

    public Long bitcount(final String key, final long start, final long end) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.bitcount(key, start, end);
            }
        },true);
    }

    @Deprecated
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final int cursor) {
        return execute(new CallBack<ScanResult<Map.Entry<String, String>>>() {
            public ScanResult<Map.Entry<String, String>> execute(JedisCommands jedis) {
                return jedis.hscan(key, cursor);
            }
        },true);
    }

    public ScanResult<String> sscan(final String key, final int cursor) {
        return execute(new CallBack<ScanResult<String>>() {
            public ScanResult<String> execute(JedisCommands jedis) {
                return jedis.sscan(key, cursor);
            }
        },true);
    }

    public ScanResult<Tuple> zscan(final String key, final int cursor) {
        return execute(new CallBack<ScanResult<Tuple>>() {
            public ScanResult<Tuple> execute(JedisCommands jedis) {
                return jedis.zscan(key, cursor);
            }
        },true);
    }

    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor) {
        return execute(new CallBack<ScanResult<Map.Entry<String, String>>>() {
            public ScanResult<Map.Entry<String, String>> execute(JedisCommands jedis) {
                return jedis.hscan(key, cursor);
            }
        },true);
    }

    public ScanResult<String> sscan(final String key, final String cursor) {
        return execute(new CallBack<ScanResult<String>>() {
            public ScanResult<String> execute(JedisCommands jedis) {
                return jedis.sscan(key, cursor);
            }
        },true);
    }

    public ScanResult<Tuple> zscan(final String key, final String cursor) {
        return execute(new CallBack<ScanResult<Tuple>>() {
            public ScanResult<Tuple> execute(JedisCommands jedis) {
                return jedis.zscan(key, cursor);
            }
        },true);
    }

    public Long pfadd(final String key, final String... elements) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.pfadd(key, elements);
            }
        });
    }

    public long pfcount(final String key) {
        return execute(new CallBack<Long>() {
            public Long execute(JedisCommands jedis) {
                return jedis.pfadd(key);
            }
        },true);
    }



    /* multi jedis Commands*/


    @Override
    public Long del(final String... keys)  {

        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.del(keys);
            }
        });
    }

    @Override
    public List<String> blpop(final int timeout, final String... keys) {

        return execute(new MultiKeyCallBack<List<String>>(){

            @Override
            public List<String> execute(MultiKeyCommands jedis) {
                return jedis.blpop(timeout,keys);
            }
        });
    }

    @Override
    public List<String> brpop(final int timeout, final String... keys) {
        return execute(new MultiKeyCallBack<List<String>>(){

            @Override
            public List<String> execute(MultiKeyCommands jedis) {
                return jedis.brpop(timeout, keys);
            }
        });
    }

    @Override
    public List<String> blpop(final String... args) {
        return execute(new MultiKeyCallBack<List<String>>(){

            @Override
            public List<String> execute(MultiKeyCommands jedis) {
                return jedis.blpop(args);
            }
        });
    }

    @Override
    public List<String> brpop(final String... args) {
        return execute(new MultiKeyCallBack<List<String>>(){

            @Override
            public List<String> execute(MultiKeyCommands jedis) {
                return jedis.brpop(args);
            }
        });
    }

    @Override
    public Set<String> keys(final String pattern) {
        return execute(new MultiKeyCallBack<Set<String>>(){

            @Override
            public Set<String> execute(MultiKeyCommands jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    @Override
    public List<String> mget(final String... keys) {
        return execute(new MultiKeyCallBack<List<String>>(){

            @Override
            public List<String> execute(MultiKeyCommands jedis) {
                return jedis.mget(keys);
            }
        });
    }

    @Override
    public String mset(final String... keysvalues) {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.mset(keysvalues);
            }
        });
    }

    @Override
    public Long msetnx(final String... keysvalues) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.msetnx(keysvalues);
            }
        });
    }

    @Override
    public String rename(final String oldkey, final String newkey) {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.rename(oldkey,newkey);
            }
        });
    }

    @Override
    public Long renamenx(final String oldkey, final String newkey) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.renamenx(oldkey,newkey);
            }
        });
    }

    @Override
    public String rpoplpush(final String srckey, final String dstkey) {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.rpoplpush(srckey, dstkey);
            }
        });
    }

    @Override
    public Set<String> sdiff(final String... keys) {
        return execute(new MultiKeyCallBack<Set<String>>(){

            @Override
            public Set<String> execute(MultiKeyCommands jedis) {
                return jedis.sdiff(keys);
            }
        });
    }

    @Override
    public Long sdiffstore(final String dstkey, final String... keys) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.sdiffstore(dstkey,keys);
            }
        });
    }

    @Override
    public Set<String> sinter(final String... keys) {
        return execute(new MultiKeyCallBack<Set<String>>(){

            @Override
            public Set<String> execute(MultiKeyCommands jedis) {
                return jedis.sinter(keys);
            }
        });
    }

    @Override
    public Long sinterstore(final String dstkey, final String... keys) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.sinterstore(dstkey, keys);
            }
        });
    }

    @Override
    public Long smove(final String srckey, final String dstkey, final String member) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.smove(srckey, dstkey, member);
            }
        });
    }

    @Override
    public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.sort(key, sortingParameters, dstkey);
            }
        });
    }

    @Override
    public Long sort(final String key, final String dstkey) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.sort(key, dstkey);
            }
        });
    }

    @Override
    public Set<String> sunion(final String... keys) {
        return execute(new MultiKeyCallBack<Set<String>>(){

            @Override
            public Set<String> execute(MultiKeyCommands jedis) {
                return jedis.sunion(keys);
            }
        });
    }

    @Override
    public Long sunionstore(final String dstkey, final String... keys) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.sunionstore(dstkey, keys);
            }
        });
    }

    @Override
    public String watch(final String... keys) {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.watch(keys);
            }
        });
    }

    @Override
    public String unwatch() {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.unwatch();
            }
        });
    }

    @Override
    public Long zinterstore(final String dstkey, final String... sets) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.zinterstore(dstkey,sets);
            }
        });
    }

    @Override
    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.zinterstore(dstkey,params,sets);
            }
        });
    }

    @Override
    public Long zunionstore(final String dstkey, final String... sets) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.zunionstore(dstkey, sets);
            }
        });
    }

    @Override
    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.zunionstore(dstkey,params, sets);
            }
        });
    }

    @Override
    public String brpoplpush(final String source, final String destination, final int timeout) {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.brpoplpush(source, destination, timeout);
            }
        });
    }

    @Override
    public Long publish(final String channel, final String message) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.publish(channel, message);
            }
        });
    }

    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
         execute(new MultiKeyCallBack<Integer>(){

            @Override
            public Integer execute(MultiKeyCommands jedis) {
                 jedis.subscribe(jedisPubSub, channels);
                return 0;
            }
        });
    }

    @Override
    public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        execute(new MultiKeyCallBack<Integer>(){

            @Override
            public Integer execute(MultiKeyCommands jedis) {
                jedis.psubscribe(jedisPubSub, patterns);
                return 0;
            }
        });
    }

    @Override
    public String randomKey() {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.randomKey();
            }
        });
    }

    @Override
    public Long bitop(final BitOP op, final String destKey, final String... srcKeys) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.bitop(op, destKey, srcKeys);
            }
        });
    }

    @Override
    public ScanResult<String> scan(final int cursor) {
        return execute(new MultiKeyCallBack<ScanResult<String>>(){

            @Override
            public ScanResult<String> execute(MultiKeyCommands jedis) {
                return jedis.scan(cursor);
            }
        });
    }

    @Override
    public ScanResult<String> scan(final String cursor) {
        return execute(new MultiKeyCallBack<ScanResult<String>>(){

            @Override
            public ScanResult<String> execute(MultiKeyCommands jedis) {
                return jedis.scan(cursor);
            }
        });
    }

    @Override
    public String pfmerge(final String destkey, final String... sourcekeys) {
        return execute(new MultiKeyCallBack<String>(){

            @Override
            public String execute(MultiKeyCommands jedis) {
                return jedis.pfmerge(destkey,sourcekeys);
            }
        });
    }

    @Override
    public long pfcount(final String... keys) {
        return execute(new MultiKeyCallBack<Long>(){

            @Override
            public Long execute(MultiKeyCommands jedis) {
                return jedis.pfcount(keys);
            }
        });
    }

    @Override
    public Pipeline pipelined() {
        return execute(new CallBack<Pipeline>(){
            @Override
            public Pipeline execute(JedisCommands jedis) {
                if(jedis instanceof Jedis) {
                    Pipeline pipeline = new Pipeline();
                    pipeline.setClient(((Jedis)jedis).getClient());
                    return pipeline;
                }else {
                    return null;
                }

            }
        });
    }


}
