package com.zchu.sample;


import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface ServerAPI {
    String BASE_URL = "https://api.douban.com";
    String BASE_URL2 = "https://api.m.jd.com/";

    @GET("/v2/movie/in_theaters?city=上海")
    Observable<Movie> getInTheatersMovies();

    @GET("/v2/movie/in_theaters?city=上海")
    Single<Movie> getInTheatersMoviesSingle();

    @GET("client.action")
    Single<JDRes> testSingle();
}
