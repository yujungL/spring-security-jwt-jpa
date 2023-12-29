package com.web.my.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {

    private final RedisTemplate redisTemplate;

    public String getValues(String key){
        //opsForValue : Strings를 쉽게 Serialize / Deserialize 해주는 Interface
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void setValues(String key, String value){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key,value, 3600, TimeUnit.SECONDS);
    }

    public void setValues(String key, String value, long expiration, TimeUnit timeUnit){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key,value, expiration, timeUnit);
    }

    public void setSets(String key,String... values){
        redisTemplate.opsForSet().add(key,values);
    }

    public Set getSets(String key){
        return redisTemplate.opsForSet().members(key);
    }

    public String deleteKey(String key){
        return (String) redisTemplate.opsForValue().getAndDelete(key);
    }

}
