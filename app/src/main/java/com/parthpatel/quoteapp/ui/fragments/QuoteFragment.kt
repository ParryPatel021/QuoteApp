package com.parthpatel.quoteapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.parthpatel.quoteapp.MainActivity
import com.parthpatel.quoteapp.R
import com.parthpatel.quoteapp.databinding.FragmentQuoteBinding
import com.parthpatel.quoteapp.model.QuoteModel
import com.parthpatel.quoteapp.utility.Resource
import com.parthpatel.quoteapp.utility.makeGone
import com.parthpatel.quoteapp.utility.makeInvisible
import com.parthpatel.quoteapp.utility.makeVisible
import com.parthpatel.quoteapp.view_model.QuoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class QuoteFragment : Fragment(R.layout.fragment_quote) {

    private lateinit var quoteBinding: FragmentQuoteBinding
    private lateinit var viewModel: QuoteViewModel
    private var quoteModel: QuoteModel? = null
    private lateinit var activity: MainActivity
    private var isBookMarked = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quoteBinding = FragmentQuoteBinding.bind(view)

        initialize()

        handleViewVisibility(isShow = false)

        quoteBinding.btnNextQuote.setOnClickListener {
            viewModel.getRandomQuote()
        }

        quoteBinding.fabAddBookmark.setOnClickListener {
            // save quote if not already saved
            if (quoteModel != null) {

                if (isBookMarked) {

                    // delete quote if it is already bookmarked.
                    viewModel.deleteQuote(quoteModel!!)
                    if (activity.atHome) {
                        makeSnackBar(
                            activity.mainBinding.contentMain.navHostFragment,
                            "Removed Bookmark!"
                        )
                    }
                } else {
                    viewModel.saveQuote(quoteModel!!)
                    makeSnackBar(it, "Quote has been added")
                }
            }
        }
    }

    private fun initialize() {
        viewModel = activity.viewModel

        setupObservable()
    }

    private fun setupObservable() {
        viewModel.quote.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Loading -> {
                    handleViewVisibility(isShow = false)
                    quoteModel = null
                }
                is Resource.Success -> {
                    quoteBinding.pbLoader.makeGone()
                    quoteBinding.grpContents.makeVisible()
                    response.data.let {
                        quoteModel = it
                        quoteBinding.tvQuoteText.text =
                            resources.getString(R.string.quote, quoteModel?.quote)
                        quoteBinding.tvAuthor.text =
                            resources.getString(R.string.author, quoteModel?.author)
                    }
                }
                is Resource.Error -> {
                    quoteBinding.pbLoader.makeGone()
                    quoteBinding.grpContents.makeGone()
                    quoteModel = null
                }
            }
        })

        viewModel.bookmarked.observe(viewLifecycleOwner, {
            isBookMarked = it
            quoteBinding.fabAddBookmark.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (isBookMarked) R.drawable.ic_bookmarked
                    else R.drawable.ic_un_bookmark
                )
            )
        })
    }

    private fun handleViewVisibility(isShow: Boolean) {
        if (isShow) {
            quoteBinding.grpContents.makeVisible()
            quoteBinding.pbLoader.makeGone()
        } else {
            quoteBinding.grpContents.makeInvisible()
            quoteBinding.pbLoader.makeVisible()
        }
    }

    private fun makeSnackBar(view: View, message: String) {
        if (activity.atHome) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()

            // workaround to disable floating action button when a snackbar is made
            // to prevent double clicks while task is executing/snackbar is visible
            if (quoteBinding.fabAddBookmark.visibility == View.VISIBLE) {
                quoteBinding.fabAddBookmark.makeInvisible()
                lifecycleScope.launch(context = Dispatchers.Default) {
                    delay(2000)
                    withContext(Dispatchers.Main) {
                        quoteBinding.fabAddBookmark.makeVisible()
                    }
                }
            }
        }
    }

}