package com.sebasfortierdev.booklist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sebasfortierdev.booklist.fragments.AddBookFragment
import com.sebasfortierdev.booklist.fragments.BookListFragment
import com.sebasfortierdev.booklist.fragments.ListsFragment
import com.sebasfortierdev.booklist.fragments.StatisticsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listsFragment = ListsFragment()
        val bookListFragment = BookListFragment()
        val statisticsFragment = StatisticsFragment()

        makeCurrentFragment(bookListFragment)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.ic_favorite -> makeCurrentFragment(listsFragment)
                R.id.bottom_menu_book_list -> makeCurrentFragment(bookListFragment)
                R.id.ic_settings -> makeCurrentFragment(statisticsFragment)
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_book -> {
                val fragment = AddBookFragment()
                makeCurrentFragment(fragment)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }


}