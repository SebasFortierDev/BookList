package com.sebasfortierdev.booklist.fragments.addBook

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sebasfortierdev.booklist.R

class AddBookFragment : Fragment() {

    private val addBookViewModel: AddBookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBookViewModel.googleBooksLiveData.observe(
            viewLifecycleOwner,
            { googleBooks ->
                Log.d("BookSearch", "Réponse : ${googleBooks.size}")
//                mPhotoRecyclerView.adapter = PhotoAdapter(galleryItems)
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_book_top_menu, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)

        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d("BookSearch", "onQueryTextSubmit : $queryText")
                    addBookViewModel.fetchGoogleBooks(queryText)
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d("BookSearch", "onQueryTextChange : $queryText")
                    return false
                }
            })

            setOnSearchClickListener {
                Log.d("BookSearch", "setOnSearchClickListener")
//                searchView.setQuery(mPhotoGalleryViewModel.searchTerm, false)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.add_book)
        item.isVisible = false
    }
}