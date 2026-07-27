package com.ikrai.ikraipicturebackend.manager.cache;

import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 多级缓存管理器模板
 * <p>
 * 采用模板方法模式，统一封装「本地缓存 -> Redis 分布式缓存 -> 数据源加载 -> 回写缓存」的流程。
 * 子类只需提供本地缓存实例、缓存 key 前缀、过期策略以及反序列化方式。
 */
public abstract class CacheManager<T> {

    @Resource
    protected StringRedisTemplate stringRedisTemplate;

    /**
     * 本地缓存，由子类构造（容量、过期时间等可不同）
     */
    protected abstract Cache<String, String> getLocalCache();

    /**
     * 缓存 key 前缀，用于隔离不同业务
     */
    protected abstract String getCachePrefix();

    /**
     * Redis 缓存过期时间（秒），可随机化以防止缓存雪崩
     */
    protected abstract int getExpireSeconds();

    /**
     * 反序列化缓存值为目标类型
     */
    protected abstract T deserialize(String json);

    /**
     * 序列化目标对象为缓存字符串，默认使用 JSON
     */
    protected String serialize(T data) {
        return JSONUtil.toJsonStr(data);
    }

    /**
     * 模板方法：按 keyParams 走多级缓存获取，未命中则通过 loader 加载并回写两级缓存
     *
     * @param keyParams 用于生成缓存 key 的参数（一般是请求对象的 JSON）
     * @param loader   缓存未命中时的数据加载逻辑
     * @return 命中或加载到的数据
     */
    public final T getOrLoad(String keyParams, Supplier<T> loader) {
        String cacheKey = buildCacheKey(keyParams);
        Cache<String, String> localCache = getLocalCache();

        // 1. 先查本地缓存
        String cached = localCache.getIfPresent(cacheKey);
        if (cached != null) {
            return deserialize(cached);
        }

        // 2. 本地缓存未命中，查询 Redis 分布式缓存
        cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            // 命中 Redis，回写本地缓存并返回
            localCache.put(cacheKey, cached);
            return deserialize(cached);
        }

        // 3. 两级缓存均未命中，加载数据源
        T data = loader.get();
        if (data != null) {
            String value = serialize(data);
            // 4. 回写 Redis（随机过期防雪崩）与本地缓存
            stringRedisTemplate.opsForValue().set(cacheKey, value, getExpireSeconds(), TimeUnit.SECONDS);
            localCache.put(cacheKey, value);
        }
        return data;
    }

    /**
     * 构建缓存 key：前缀 + 参数摘要
     */
    private String buildCacheKey(String keyParams) {
        String hashKey = DigestUtils.md5DigestAsHex(keyParams.getBytes());
        return String.format("%s:%s", getCachePrefix(), hashKey);
    }
}
