package com.sebasfortierdev.booklist.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.sebasfortierdev.booklist.R

class AddBookFragment : Fragment() {
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.add_book)
        item.isVisible = false
    }
}