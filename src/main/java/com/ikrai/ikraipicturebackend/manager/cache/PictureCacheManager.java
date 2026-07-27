package com.ikrai.ikraipicturebackend.manager.cache;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ikrai.ikraipicturebackend.model.vo.PictureVO;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 图片分页查询的多级缓存管理器
 * <p>
 * 本地缓存使用 Caffeine，5 分钟过期；Redis 缓存 5~10 分钟随机过期防雪崩。
 */
@Component
public class PictureCacheManager extends CacheManager<Page<PictureVO>> {

    /**
     * 本地缓存：初始容量 1024，最大 1 万条，写入后 5 分钟过期
     */
    private final Cache<String, String> LOCAL_CACHE =
            Caffeine.newBuilder()
                    .initialCapacity(1024)
                    .maximumSize(10000L)
                    .expireAfterWrite(5L, TimeUnit.MINUTES)
                    .build();

    @Override
    protected Cache<String, String> getLocalCache() {
        return LOCAL_CACHE;
    }

    @Override
    protected String getCachePrefix() {
        return "ikrai:listPictureVOByPage";
    }

    @Override
    protected int getExpireSeconds() {
        // 5~10 分钟随机过期，防止缓存雪崩
        return 300 + RandomUtil.randomInt(0, 300);
    }

    @Override
    protected Page<PictureVO> deserialize(String json) {
        return JSONUtil.toBean(json, Page.class);
    }
}
