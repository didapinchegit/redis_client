package com.didapinche.commons.redis.exceptions;

/**
 * RedisClientException.java
 * Project: redis client
 *
 * File Created at 2015-7-30 by fengbin
 *
 * Copyright 2015 didapinche.com
 */
public class RedisClientException extends RuntimeException {
    public RedisClientException(String msg){
         super(msg);
    }

    public RedisClientException(String msg,Throwable e){
        super(msg,e);
    }
}
