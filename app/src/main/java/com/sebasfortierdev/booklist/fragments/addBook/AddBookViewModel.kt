package com.sebasfortierdev.booklist.fragments.addBook

import android.app.Application
import androidx.lifecycle.*
import com.sebasfortierdev.booklist.googleBookApi.GoogleBookFetchr
import com.sebasfortierdev.booklist.googleBookApi.model.GoogleBook

class AddBookViewModel(app: Application) : AndroidViewModel(app) {
    val googleBooksLiveData: LiveData<List<GoogleBook>>

    private val googleBookFetchr = GoogleBookFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = ""

        googleBooksLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            googleBookFetchr.fetchSearch(searchTerm)
        }
    }

    fun fetchGoogleBooks(query: String = "") {
        mutableSearchTerm.value = query
    }
}