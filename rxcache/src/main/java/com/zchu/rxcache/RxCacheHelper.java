package com.zchu.rxcache;

import android.annotation.SuppressLint;

import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;
import com.zchu.rxcache.utils.LogUtils;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;
import java.util.ConcurrentModificationException;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zchu
 * date   : 2017/10/9
 * desc   : RxCache的帮助类
 */

public class RxCacheHelper {

    public static <T> Observable<CacheResult<T>> loadCache(final RxCache rxCache,
                                                           final String key,
                                                           Type type,
                                                           final boolean needEmpty) {
        Observable<CacheResult<T>> observable = rxCache.<T>load(key, type).subscribeOn(Schedulers.io());
        if (needEmpty) {
            observable = observable.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                @Override
                public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                    return Observable.empty();
                }
            });
        }
        return observable;
    }

    public static <T> Observable<CacheResult<T>> loadRemote(final RxCache rxCache,
                                                            final String key,
                                                            Observable<T> source,
                                                            final CacheTarget target,
                                                            final boolean needEmpty) {
        Observable<CacheResult<T>> observable = source.map(new Function<T, CacheResult<T>>() {
            @SuppressLint("CheckResult")
            @Override
            public CacheResult<T> apply(@NonNull T t) throws Exception {
                LogUtils.debug("loadRemote result=" + t);
                rxCache.save(key, t, target).subscribeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean status) throws Exception {
                        LogUtils.debug("save status => " + status);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof ConcurrentModificationException) {
                            LogUtils.log("Save failed, please use a synchronized cache strategy :", throwable);
                        }
                        else {
                            LogUtils.log(throwable);
                        }
                    }
                });
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
        if (needEmpty) {
            observable = observable.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                @Override
                public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                    return Observable.empty();
                }
            });
        }
        return observable;
    }

    public static <T> Observable<CacheResult<T>> loadRemoteSync(final RxCache rxCache,
                                                                final String key,
                                                                Observable<T> source,
                                                                final CacheTarget target,
                                                                final boolean needEmpty) {
        Observable<CacheResult<T>> observable = source.flatMap(new Function<T, ObservableSource<CacheResult<T>>>() {
            @Override
            public ObservableSource<CacheResult<T>> apply(@NonNull T t) throws Exception {
                return saveCacheSync(rxCache, key, t, target);
            }
        });
        if (needEmpty) {
            observable = observable.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                @Override
                public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                    return Observable.empty();
                }
            });
        }
        return observable;

    }

    public static <T> Observable<CacheResult<T>> saveCacheSync(RxCache rxCache, final String key, final T t, CacheTarget target) {
        return rxCache.save(key, t, target).map(new Function<Boolean, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull Boolean aBoolean) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        }).onErrorReturn(new Function<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull Throwable throwable) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
    }

    public static <T> Flowable<CacheResult<T>> loadCacheFlowable(final RxCache rxCache,
                                                                 final String key,
                                                                 Type type,
                                                                 final boolean needEmpty) {
        Flowable<CacheResult<T>> flowable = rxCache.load2Flowable(key, type);
        if (needEmpty) {
            flowable = flowable.onErrorResumeNext(new Function<Throwable, Publisher<? extends CacheResult<T>>>() {
                @Override
                public Publisher<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                    return Flowable.empty();
                }
            });
        }
        return flowable;
    }

    public static <T> Flowable<CacheResult<T>> loadRemoteFlowable(final RxCache rxCache,
                                                                  final String key,
                                                                  Flowable<T> source,
                                                                  final CacheTarget target,
                                                                  final boolean needEmpty) {
        Flowable<CacheResult<T>> flowable = source.map(new Function<T, CacheResult<T>>() {
            @SuppressLint("CheckResult")
            @Override
            public CacheResult<T> apply(@NonNull T t) throws Exception {
                LogUtils.debug("loadRemote result=" + t);
                rxCache.save(key, t, target).subscribeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean status) throws Exception {
                        LogUtils.debug("save status => " + status);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof ConcurrentModificationException) {
                            LogUtils.log("Save failed, please use a synchronized cache strategy :", throwable);
                        }
                        else {
                            LogUtils.log(throwable);
                        }
                    }
                });
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
        if (needEmpty) {
            flowable = flowable.onErrorResumeNext(new Function<Throwable, Publisher<? extends CacheResult<T>>>() {
                @Override
                public Publisher<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                    return Flowable.empty();
                }
            });
        }
        return flowable;
    }

    public static <T> Flowable<CacheResult<T>> loadRemoteSyncFlowable(final RxCache rxCache,
                                                                      final String key,
                                                                      final Flowable<T> source,
                                                                      final CacheTarget target,
                                                                      final boolean needEmpty) {
        Flowable<CacheResult<T>> flowable = source.flatMap(new Function<T, Publisher<CacheResult<T>>>() {
            @Override
            public Publisher<CacheResult<T>> apply(@NonNull T t) throws Exception {
                return saveCacheSyncFlowable(rxCache, key, t, target);
            }
        });
        if (needEmpty) {
            flowable = flowable.onErrorResumeNext(new Function<Throwable, Publisher<? extends CacheResult<T>>>() {
                @Override
                public Publisher<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                    return Flowable.empty();
                }
            });
        }
        return flowable;
    }

    public static <T> Flowable<CacheResult<T>> saveCacheSyncFlowable(RxCache rxCache,
                                                                     final String key,
                                                                     final T t,
                                                                     CacheTarget target) {
        return rxCache.save2Flowable(key, t, target).map(new Function<Boolean, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull Boolean aBoolean) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        }).onErrorReturn(new Function<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull Throwable throwable) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
    }


    public static <T> Single<CacheResult<T>> loadCacheSingle(final RxCache rxCache,
                                                           final String key,
                                                           Type type) {
        Single<CacheResult<T>> single = rxCache.<T>load2Single(key, type).subscribeOn(Schedulers.io());
        return single;
    }

    public static <T> Single<CacheResult<T>> loadRemoteSingle(final RxCache rxCache,
                                                            final String key,
                                                              Single<T> source,
                                                            final CacheTarget target) {
        Single<CacheResult<T>> single = source.map(new Function<T, CacheResult<T>>() {
            @SuppressLint("CheckResult")
            @Override
            public CacheResult<T> apply(@NonNull T t) throws Exception {
                LogUtils.debug("loadRemote result=" + t);
                rxCache.save2Single(key, t, target).subscribeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean status) throws Exception {
                        LogUtils.debug("save status => " + status);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof ConcurrentModificationException) {
                            LogUtils.log("Save failed, please use a synchronized cache strategy :", throwable);
                        }
                        else {
                            LogUtils.log(throwable);
                        }
                    }
                });
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
        return single;
    }

    public static <T> Single<CacheResult<T>> loadRemoteSyncSingle(final RxCache rxCache,
                                                                  final String key,
                                                                  Single<T> source,
                                                                  final CacheTarget target) {
        Single<CacheResult<T>> single = source.flatMap(new Function<T, SingleSource<CacheResult<T>>>() {
            @Override
            public SingleSource<CacheResult<T>> apply(@NonNull T t) throws Exception {
                return saveCacheSyncSingle(rxCache, key, t, target);
            }
        });
        return single;

    }

    public static <T> Single<CacheResult<T>> saveCacheSyncSingle(RxCache rxCache, final String key, final T t, CacheTarget target) {
        return rxCache.save2Single(key, t, target).map(new Function<Boolean, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull Boolean aBoolean) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        }).onErrorReturn(new Function<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull Throwable throwable) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
    }

}
