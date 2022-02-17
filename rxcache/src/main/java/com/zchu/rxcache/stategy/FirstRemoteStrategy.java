package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.RxCacheHelper;
import com.zchu.rxcache.data.CacheResult;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;
import java.util.Arrays;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

/**
 * 优先网络
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public final class FirstRemoteStrategy implements IStrategy {
    private boolean isSync;

    public FirstRemoteStrategy() {
        isSync = false;
    }

    public FirstRemoteStrategy(boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = RxCacheHelper.loadCache(rxCache, key, type, true);
        Observable<CacheResult<T>> remote;
        if (isSync) {
            remote = RxCacheHelper.loadRemoteSync(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        else {
            remote = RxCacheHelper.loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        return Observable.concatDelayError(Arrays.asList(remote, cache)).take(1);
    }

    @Override
    public <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type) {
        Flowable<CacheResult<T>> cache = RxCacheHelper.loadCacheFlowable(rxCache, key, type, true);
        Flowable<CacheResult<T>> remote;
        if (isSync) {
            remote = RxCacheHelper.loadRemoteSyncFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        else {
            remote = RxCacheHelper.loadRemoteFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        return Flowable.concatDelayError(Arrays.asList(remote, cache)).take(1);
    }

    @Override
    public <T> Single<CacheResult<T>> single(RxCache rxCache, String key, Single<T> source, Type type) {
        final Single<CacheResult<T>> cache = RxCacheHelper.loadCacheSingle(rxCache, key, type);
        final Single<CacheResult<T>> remote;
        if (isSync) {
            remote = RxCacheHelper.loadRemoteSyncSingle(rxCache, key, source, CacheTarget.MemoryAndDisk);
        }
        else {
            remote = RxCacheHelper.loadRemoteSingle(rxCache, key, source, CacheTarget.MemoryAndDisk);
        }
        return remote.flatMap(new Function<CacheResult<T>, Single<CacheResult<T>>>() {
            @Override
            public Single<CacheResult<T>> apply(CacheResult<T> result) throws Exception {
                if (result.getData() == null) {
                    return cache;
                }
                else return Single.just(result);
            }
        }).onErrorResumeNext(cache);
    }
}
