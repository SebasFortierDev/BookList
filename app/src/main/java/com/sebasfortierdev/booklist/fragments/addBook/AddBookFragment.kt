package com.sebasfortierdev.booklist.fragments.addBook

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sebasfortierdev.booklist.R
import com.sebasfortierdev.booklist.googleBookApi.model.GoogleBook

class AddBookFragment : Fragment() {
    private lateinit var googleBookRecyclerView: RecyclerView

    private val addBookViewModel: AddBookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_book, container, false)

        googleBookRecyclerView = view.findViewById(R.id.google_book_recycler_view)
        googleBookRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBookViewModel.googleBooksLiveData.observe(
            viewLifecycleOwner
        ) { googleBooks ->
            googleBookRecyclerView.adapter = GoogleBookAdapter(googleBooks)
        }
    }

    private inner class GoogleBookHolder(view: View) : RecyclerView.ViewHolder(view),
        OnClickListener {
        private lateinit var googleBook: GoogleBook

        val googleBookTitle: TextView = itemView.findViewById(R.id.google_book_title)
        val googleBookAuthor: TextView = itemView.findViewById(R.id.google_book_author)
        val googleBookDescription: TextView = itemView.findViewById(R.id.google_book_description)
        val bookImageView: ImageView = itemView.findViewById(R.id.google_book_image)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(googleBook: GoogleBook) {
            this.googleBook = googleBook
            googleBookTitle.text = googleBook.volumeInfo.title
            googleBookAuthor.text = googleBook.firstAuthor() ?: getString(R.string.unknown_author)
            googleBookDescription.text = googleBook.shortDescription()

            Glide.with(this@AddBookFragment)
//                .load(makeGoogleBookImageUrlHttps(googleBook.volumeInfo.imageLinks?.imageLink))
                .load(googleBook.makeImageUrlHttps())
                .placeholder(ColorDrawable(Color.GRAY))
                .into(bookImageView)
        }

        override fun onClick(view: View?) {
            Toast.makeText(context, googleBook.volumeInfo.title, Toast.LENGTH_SHORT).show()
            // TODO("Open dialog")
        }
    }

    private inner class GoogleBookAdapter(private val googleBooks: List<GoogleBook>) :
        RecyclerView.Adapter<GoogleBookHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoogleBookHolder {
            val view = layoutInflater.inflate(R.layout.google_book_list_item, parent, false)

            return GoogleBookHolder(view)
        }

        override fun getItemCount(): Int = googleBooks.size

        override fun onBindViewHolder(holder: GoogleBookHolder, position: Int) {
            val googleBook = googleBooks[position]

            holder.bind(googleBook)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_book_top_menu, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)

        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    addBookViewModel.fetchGoogleBooks(queryText)

                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    return false
                }
            })

            setOnSearchClickListener {
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.add_book)
        item.isVisible = false
    }
}