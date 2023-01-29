package com.sebasfortierdev.booklist.googleBookApi.response

import com.google.gson.annotations.SerializedName
import com.sebasfortierdev.booklist.googleBookApi.model.GoogleBook

class GoogleBookResponse {
    @SerializedName("kind")
    lateinit var kind: String

    @SerializedName("items")
    lateinit var googleBooks: List<GoogleBook>

}