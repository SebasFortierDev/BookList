package com.sebasfortierdev.booklist.googleBookApi

import com.sebasfortierdev.booklist.googleBookApi.response.GoogleBookResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBookApi {
    /**
     * Routes qui permet de changer l'état du colis pour 'récupérer'
     *
     * https://www.googleapis.com/books/v1/volumes?q=isbn:9791028106836
     *
     * @return Un colis response contenant les informations des colis
     */
    @GET("books/v1/volumes")
    fun search(@Query("q") search: String): Call<GoogleBookResponse>
}