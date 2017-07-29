package com.myjo.ordercat

import com.lambdaworks.redis.RedisClient
import com.lambdaworks.redis.api.StatefulRedisConnection
import com.lambdaworks.redis.api.sync.RedisCommands
import com.myjo.ordercat.exception.OCException
import com.myjo.ordercat.redis.SetnxLock
import com.myjo.ordercat.utils.OcLcUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class RedisLettuceSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(RedisLettuceSpec.class);


    def "redis-test"() {
        when:



        long ltime = SetnxLock.opLock(0001,0)

        //SetnxLock.unOpLock(0001,ltime);
        Logger.info(ltime)

        then:
        dd == "87"
        dd1 == "0"

    }



}
