package com.sebasfortierdev.booklist.googleBookApi

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.sebasfortierdev.booklist.googleBookApi.model.GoogleBook
import com.sebasfortierdev.booklist.googleBookApi.response.GoogleBookResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoogleBookFetchr {
    private val googleBookApi: GoogleBookApi

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(GoogleBookInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        googleBookApi = retrofit.create(GoogleBookApi::class.java)
    }

    fun findGoogleBookByIsbn(isbn: String): MutableLiveData<GoogleBook?> {
        val responseLiveData: MutableLiveData<GoogleBook?> = MutableLiveData()

        val isbnQuery = "isbn:$isbn"

        googleBookApi.search(isbnQuery).enqueue(object : Callback<GoogleBookResponse> {
            override fun onResponse(call: Call<GoogleBookResponse>, response: Response<GoogleBookResponse>) {
                val googleBookResponse: GoogleBookResponse? = response.body()

                if (googleBookResponse != null && googleBookResponse.googleBooks.isNotEmpty()) {
                    responseLiveData.value = googleBookResponse.googleBooks[0]
                }
            }
            override fun onFailure(call: Call<GoogleBookResponse>, t: Throwable) {
                // TODO ManageError
                Log.d("API googleBook", "Une erreur est survenue lors de La requête à l'API $t")
            }
        })

        return responseLiveData
    }

    private fun fetchSearchRequest(query: String): Call<GoogleBookResponse> {
        return googleBookApi.search(query)
    }

    fun fetchSearch(search: String): MutableLiveData<List<GoogleBook>> {
        if (search.isEmpty()) {
            return MutableLiveData()
        }

        return fetchMetadata(fetchSearchRequest(search))
    }

    private fun fetchMetadata(googleBookRequest: Call<GoogleBookResponse>) : MutableLiveData<List<GoogleBook>> {
        val responseLiveData: MutableLiveData<List<GoogleBook>> = MutableLiveData()

        googleBookRequest.enqueue(object : Callback<GoogleBookResponse> {
            override fun onResponse(call: Call<GoogleBookResponse>, response: Response<GoogleBookResponse>) {
                val googleBookResponse: GoogleBookResponse? = response.body()

                if (googleBookResponse != null) {
                    responseLiveData.value = googleBookResponse.googleBooks
                }
            }
            override fun onFailure(call: Call<GoogleBookResponse>, t: Throwable) {
                Log.d("API googleBook", "Une erreur est survenue lors de La requête à l'API $t")
            }
        })

        return responseLiveData
    }
}