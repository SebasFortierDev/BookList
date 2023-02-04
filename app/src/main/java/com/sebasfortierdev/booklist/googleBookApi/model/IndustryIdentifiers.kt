package com.sebasfortierdev.booklist.googleBookApi.model

import com.google.gson.annotations.SerializedName

data class IndustryIdentifiers(
    @SerializedName("type")
    var type: String,

    @SerializedName("identifier")
    var identifier: String,
)