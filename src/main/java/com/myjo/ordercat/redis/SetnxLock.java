package com.myjo.ordercat.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.myjo.ordercat.config.OrderCatConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by lee5hx on 2017/7/26.
 */
public class SetnxLock {
    private static final Logger Logger = LogManager.getLogger(SetnxLock.class);
    private static final int OP_LOCK_TIME_OUT = 1000 * 60 * 5; //自动下单锁超时时间-5分钟




    //redis://password@localhost:6379/0
    private static long lock(final String lockKey, final int lockTimeOut) {
        String uri = String.format("redis://%s@%s:%s",
                OrderCatConfig.getRedisPassword(),
                OrderCatConfig.getRedisHost(),
                OrderCatConfig.getRedisPort());
        Logger.info(uri);
        RedisClient redisClient = RedisClient.create(uri);


        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        long timeout;
        long now;
        boolean lock;
        try {
            while (true) {
                now = System.currentTimeMillis();
                timeout = now + lockTimeOut + 1;
                lock = syncCommands.setnx(lockKey, String.valueOf(timeout));

                if (lock || ((now > getLockTimeOut(syncCommands, lockKey) && now > getSet(syncCommands, lockKey, timeout)))) {
                    break;
                } else {
                    timeout = -1;
                    break;
                }
            }
        } finally {
            connection.close();
            redisClient.shutdown();
        }
        return timeout;

    }

    private static void unLock(final String lockKey, final Long timeout) {

        String uri = String.format("redis://%s@%s:%s",
                OrderCatConfig.getRedisPassword(),
                OrderCatConfig.getRedisHost(),
                OrderCatConfig.getRedisPort());
        Logger.info(uri);
        RedisClient redisClient = RedisClient.create(uri);

        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        try {
            long now = System.currentTimeMillis();
            if (now < timeout) {
                syncCommands.del(lockKey);
            }
        } finally {
            connection.close();
            redisClient.shutdown();
        }
        Logger.info(String.format("Setnx-LockKey: %s unlock .", lockKey));
    }

    public static Long opLock(final Long tid, final int lockTimeOut) {
        Logger.info(String.format("订单[%d]锁定-lockTimeOut:[%d]", tid, lockTimeOut));
        return lock(String.format("OP_LOCK_TID_%d", tid), OP_LOCK_TIME_OUT);
    }

    public static void unOpLock(final Long tid, final Long timeout) {
        Logger.info(String.format("订单[%d]解锁-timeout:[%d]", tid, timeout));
        unLock(String.format("OP_LOCK_TID_%d", tid), timeout);
    }


    private static long getSet(RedisCommands<String, String> syncCommands, String lockKey, long timeout) {
        return Long.valueOf(syncCommands.getset(lockKey, String.valueOf(timeout)));
    }

    private static long getLockTimeOut(RedisCommands<String, String> syncCommands, String lockKey) {
        return Long.valueOf(syncCommands.get(lockKey));
    }

}
