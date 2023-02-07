package com.sebasfortierdev.booklist.fragments.addBook

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.sebasfortierdev.booklist.CaptureAct
import com.sebasfortierdev.booklist.LinkedHashMapAdapter
import com.sebasfortierdev.booklist.R
import com.sebasfortierdev.booklist.googleBookApi.model.GoogleBook
import java.text.SimpleDateFormat
import java.util.*

class AddBookFragment : Fragment() {
    private lateinit var googleBookRecyclerView: RecyclerView
    private lateinit var addBookTextView: TextView

    private lateinit var startDatePicker: Button
    private lateinit var startDateValueTextView: TextView

    private lateinit var endDatePicker: Button
    private lateinit var endDateValueTextView: TextView


    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        onScanIntentResult(result)
    }

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

        addBookTextView = view.findViewById(R.id.add_book_empty_list_text)
        googleBookRecyclerView = view.findViewById(R.id.google_book_recycler_view)
        googleBookRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateAddBookTextView()

        addBookViewModel.googleBooksLiveData.observe(
            viewLifecycleOwner
        ) { googleBooks ->
            googleBookRecyclerView.adapter = GoogleBookAdapter(googleBooks)
            updateAddBookTextView()
        }

        addBookViewModel.googleBookToAddLiveData.observe(
            viewLifecycleOwner
        ) { googleBookToAdd ->
            Log.d("TEST", "OBSERVER")
            if (googleBookToAdd != null) {
                openAddBookDialog()
            }
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
                .load(googleBook.makeImageUrlHttps())
                .placeholder(ColorDrawable(Color.GRAY))
                .into(bookImageView)
        }

        override fun onClick(view: View?) {
            addBookViewModel.googleBookToAddLiveData.value = googleBook

//            openAddBookDialog()
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
        val scanButton: MenuItem = menu.findItem(R.id.scan_book)

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
                // ...
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.add_book)
        item.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.scan_book -> {
                scanCode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun scanCode() {
        val options = ScanOptions()
        options.setPrompt(getString(R.string.scan_book_message))
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)
        options.captureActivity = CaptureAct::class.java

        barcodeLauncher.launch(options)
    }

    private fun onScanIntentResult(result: ScanIntentResult) {
        if (result.contents != null) {
            val isbn = result.contents

            addBookViewModel.setGoogleBookToAdd(isbn)
//            openAddBookDialog()

        }
    }

    private fun updateAddBookTextView() {
        if (addBookViewModel.googleBooksLiveData.value?.size == 0 || addBookViewModel.googleBooksLiveData.value?.size == null) {
            addBookTextView.visibility = VISIBLE
        } else {
            addBookTextView.visibility = INVISIBLE
        }
    }

    private fun openAddBookDialog() {

        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_book, null)

        startDatePicker = dialogView.findViewById(R.id.book_start_picker)
        startDateValueTextView = dialogView.findViewById(R.id.book_start_date_value)

        endDatePicker = dialogView.findViewById(R.id.book_end_picker)
        endDateValueTextView = dialogView.findViewById(R.id.book_end_date_value)

        startDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()

            val startDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                updateStartDateValue(calendar)
            }

            DatePickerDialog(
                requireContext(),
                startDatePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        endDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()

            val startDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                updateEndDateValue(calendar)
            }

            DatePickerDialog(
                requireContext(),
                startDatePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val bookStatesSpinner = dialogView.findViewById<Spinner>(R.id.book_state_spinner)
        setupSpinner(bookStatesSpinner)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("ADD", null)
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->
                // On ne fait rien
            }
            .create()

        dialog.setOnShowListener {
            val button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE)

            button.setOnClickListener {
                val actualSpinnerChoice = actualSpinnerChoice(bookStatesSpinner.selectedItem)

                Log.d("TEST", actualSpinnerChoice)
                Snackbar.make(requireView(), "CLOSE $actualSpinnerChoice", Snackbar.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun setupSpinner(spinner: Spinner) {
        val spinnerChoices = LinkedHashMap<String, String>()

        spinnerChoices["toRead"] = "To read"
        spinnerChoices["reading"] = "Reading"
        spinnerChoices["completed"] = "Completed"

        val spinnerAdapter = LinkedHashMapAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            spinnerChoices,
            LinkedHashMapAdapter.FLAG_FILTER_ON_KEY
        )

        spinner.adapter = spinnerAdapter
    }

    private fun actualSpinnerChoice(spinnerSelection: Any): String {
        return spinnerSelection.toString().split('=')[0]
    }

    private fun updateStartDateValue(calendar: Calendar) {
        val dateFormat = "yyyy-MM-dd"

        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

        startDateValueTextView.text = simpleDateFormat.format(calendar.time)
        addBookViewModel.bookStartDate = calendar
    }

    private fun updateEndDateValue(calendar: Calendar) {
        val dateFormat = "yyyy-MM-dd"

        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

        endDateValueTextView.text = simpleDateFormat.format(calendar.time)
        addBookViewModel.bookEndDate = calendar
    }

}

