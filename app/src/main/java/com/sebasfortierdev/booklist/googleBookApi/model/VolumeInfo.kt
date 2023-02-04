package com.sebasfortierdev.booklist.googleBookApi.model

data class VolumeInfo(
    var title: String,
    var authors: List<String>?,
    var publisher: String?,
    var publishedDate: String?,
    var industryIdentifiers: List<IndustryIdentifiers>,
    var description: String?,
    var pageCount: Int?,
    var language: String?,
    var imageLinks: ImageLinks?,
)