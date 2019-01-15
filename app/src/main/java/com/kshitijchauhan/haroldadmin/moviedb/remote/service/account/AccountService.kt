package com.kshitijchauhan.haroldadmin.moviedb.remote.service.account

import com.kshitijchauhan.haroldadmin.moviedb.remote.Config
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountService {

    @GET("${Config.API_VERSION}/account")
    fun getAccountDetails(): Single<AccountDetailsResponse>

    @GET("${Config.API_VERSION}/account/{accountId}/watchlist/movies")
    fun getMoviesWatchList(@Path("accountId") accountId: Int): Single<MovieWatchlistResponse>

    @GET("${Config.API_VERSION}/account/{accountId}/favorite/movies")
    fun getFavouriteMovies(@Path("accountId") accountId: Int): Single<FavouriteMoviesResponse>

    @POST("${Config.API_VERSION}/account/{accountId}/favorite")
    fun markMediaAsFavorite(@Path("accountId") accountId: Int,
                            @Body request: MarkMediaAsFavoriteRequest): Single<MarkAsFavoriteResponse>

    @POST("${Config.API_VERSION}/account/{accountId}/watchlist")
    fun addMediaToWatchlist(@Path("accountId") accountId: Int,
                            @Body request: AddMediaToWatchlistRequest): Single<AddToWatchlistResponse>
}

