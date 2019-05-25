package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {


    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId,int entityType,int entityId){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);

        Date date=new Date();

        Jedis jedis=jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> result=jedisAdapter.exec(tx,jedis);

        return result.size()==2 && (Long)result.get(0)>0 && (Long)result.get(1)>0;

    }

    public boolean unfollow(int userId,int entityType,int entityId){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);

        Date date=new Date();

        Jedis jedis=jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> result=jedisAdapter.exec(tx,jedis);

        return result.size()==2 && (Long)result.get(0)>0 && (Long)result.get(1)>0;
    }

    public List<Integer> getIdFromSet(Set<String> idset){
        List<Integer> list=new ArrayList<>();
        for(String str:idset){
            list.add(Integer.parseInt(str));
        }
        return list;
    }

    public List<Integer> getFollowers(int entityType,int entityId,int count){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }

    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdFromSet(jedisAdapter.zrevrange(followerKey,offset,count));
    }

    public List<Integer> getFollowees(int userId,int entityType,int count){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    public List<Integer> getFollowees(int userId,int entityType,int offset,int count){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdFromSet(jedisAdapter.zrevrange(followeeKey,offset,count));
    }

    public Long getFollowerCount(int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public Long getFolloweeCount(int userId,int entityType){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;

    }
}
