package com.parthpatel.quoteapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.parthpatel.quoteapp.databinding.ActivityMainBinding
import com.parthpatel.quoteapp.repository.QuoteRepository
import com.parthpatel.quoteapp.room_db.QuoteDataBase
import com.parthpatel.quoteapp.ui.fragments.BookmarkFragmentDirections
import com.parthpatel.quoteapp.ui.fragments.QuoteFragmentDirections
import com.parthpatel.quoteapp.utility.QuoteApp
import com.parthpatel.quoteapp.utility.makeGone
import com.parthpatel.quoteapp.utility.makeInvisible
import com.parthpatel.quoteapp.utility.makeVisible
import com.parthpatel.quoteapp.view_model.QuoteViewModel
import com.parthpatel.quoteapp.view_model.QuoteViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding
    private val currentContext = this@MainActivity
    lateinit var viewModel: QuoteViewModel
    private lateinit var navController: NavController
    var atHome = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initialize()
        handleListeners()
        setTheme(R.style.Theme_QuoteApp)
    }

    private fun initialize() {
        val quoteRepository = QuoteRepository(QuoteDataBase(currentContext))
        val viewModelProviderFactory =
            QuoteViewModelProviderFactory(app = QuoteApp(), quoteRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(QuoteViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun handleListeners() {
        mainBinding.ivBookmarkList.setOnClickListener {
            val action = QuoteFragmentDirections.actionQuoteFragmentToBookmarkFragment()
            navController.navigate(action)
            it.makeInvisible()
            mainBinding.ivBack.makeVisible()
            mainBinding.tvAppTitle.text = resources.getText(R.string.myBookMarks)
            atHome = false
        }

        mainBinding.ivBack.setOnClickListener {
            handleBack(it)
        }

    }

    private fun handleBack(it: View) {
        val action = BookmarkFragmentDirections.actionBookmarkFragmentToQuoteFragment()
        navController.navigate(action)
        it.makeInvisible()
        mainBinding.ivBookmarkList.makeVisible()
        mainBinding.tvAppTitle.text = resources.getText(R.string.app_name)
        atHome = true
    }

    override fun onBackPressed() {
        handleBack(mainBinding.ivBack)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}