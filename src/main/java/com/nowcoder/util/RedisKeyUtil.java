package com.nowcoder.util;

public class RedisKeyUtil {
    private static String SPILT=":";
    private static String BIZ_LIKE="LIKE";
    private static String BIZ_DISLIKE="DISLIKE";
    private static String BIZ_EVENTQUEUE="EVENT_QUEUE";


    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType,int entityId){
        return BIZ_DISLIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }

    public static String getEventqueueKey(){
        return BIZ_EVENTQUEUE;
    }

}
