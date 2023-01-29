package com.sebasfortierdev.booklist.googleBookApi.model

data class GoogleBook(
    var id: String,
    var volumeInfo: VolumeInfo
) {
    fun shortDescription(maxCharacter: Int = 300): String {
        val description = this.volumeInfo.description

        if (description == null || description.isBlank()) {
            return ""
        }

        if (description.length > maxCharacter) {
            return description.substring(0, maxCharacter) + "..."
        }

        return description
    }

    fun firstAuthor(): String? {
        return this.volumeInfo.authors?.first()
    }

    /**
     * Necessary to add a 's' after 'http' because
     * the URL we obtain from the GoogleAPI return an http url
     */
    fun makeImageUrlHttps(): String {
        val imageUrl = this.volumeInfo.imageLinks?.imageLink ?: return ""

        var httpsUrl = ""

        imageUrl.forEachIndexed { index, element ->
            httpsUrl += element

            if (index == 3 && element == 'p') {
                httpsUrl += 's'
            }
        }

        return httpsUrl
    }
}

