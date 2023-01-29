package com.sebasfortierdev.booklist.googleBookApi

import com.sebasfortierdev.booklist.googleBookApi.response.GoogleBookResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBookApi {
    // https://www.googleapis.com/books/v1/volumes?q=isbn:9791028106836
    /**
     * Routes qui permet de changer l'état du colis pour 'récupérer'
     *
     * @return Un colis response contenant les informations des colis
     */
    @GET("books/v1/volumes")
    fun fetchGoogleBook(@Query("q") isbn: String): Call<GoogleBookResponse>
}