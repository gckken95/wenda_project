package com.nowcoder.util;

public class RedisKeyUtil {
    private static final String SPILT=":";
    private static final String BIZ_LIKE="LIKE";
    private static final String BIZ_DISLIKE="DISLIKE";

    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType,int entityId){
        return BIZ_DISLIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }


}
