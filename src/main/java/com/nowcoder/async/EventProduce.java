package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProduce {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){
        try {
            String json= JSONObject.toJSONString(model);
            String key= RedisKeyUtil.getEventqueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
