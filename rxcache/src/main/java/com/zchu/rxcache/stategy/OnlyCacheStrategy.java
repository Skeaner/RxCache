package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.RxCacheHelper;
import com.zchu.rxcache.data.CacheResult;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * 仅加载缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public final class OnlyCacheStrategy implements IStrategy  {

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        return RxCacheHelper.loadCache(rxCache, key, type,false);
    }

    @Override
    public <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type) {
        return RxCacheHelper.loadCacheFlowable(rxCache, key, type,false);
    }

    @Override
    public <T> Single<CacheResult<T>> single(RxCache rxCache, String key, Single<T> source, Type type) {
        return RxCacheHelper.loadCacheSingle(rxCache, key, type);
    }
}
