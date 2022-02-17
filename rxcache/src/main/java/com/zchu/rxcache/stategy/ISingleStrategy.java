package com.zchu.rxcache.stategy;


import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * author : zchu
 * date   : 2017/10/11
 * desc   :
 */
public interface ISingleStrategy {

    <T> Single<CacheResult<T>> single(RxCache rxCache, String key, Single<T> source, Type type);
}
