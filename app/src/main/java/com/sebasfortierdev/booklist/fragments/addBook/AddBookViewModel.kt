package com.sebasfortierdev.booklist.fragments.addBook

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.sebasfortierdev.booklist.googleBookApi.GoogleBookFetchr
import com.sebasfortierdev.booklist.googleBookApi.model.GoogleBook
import java.util.Calendar

class AddBookViewModel(app: Application) : AndroidViewModel(app) {
    val googleBooksLiveData: LiveData<List<GoogleBook>>
    var googleBookToAddLiveData: MutableLiveData<GoogleBook?>

    var bookStartDate: Calendar? = null
    var bookEndDate: Calendar? = null

    private val googleBookFetchr = GoogleBookFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = ""
        googleBookToAddLiveData = MutableLiveData(null)

        googleBooksLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            googleBookFetchr.fetchSearch(searchTerm)
        }
    }

    fun fetchGoogleBooks(query: String = "") {
        mutableSearchTerm.value = query
    }

    fun setGoogleBookToAdd(isbn: String) {
        googleBookToAddLiveData = googleBookFetchr.findGoogleBookByIsbn(isbn)
    }

    fun addBook() {

    }
}