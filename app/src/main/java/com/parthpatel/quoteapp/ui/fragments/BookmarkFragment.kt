package com.parthpatel.quoteapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.parthpatel.quoteapp.MainActivity
import com.parthpatel.quoteapp.R
import com.parthpatel.quoteapp.databinding.FragmentBookmarkBinding
import com.parthpatel.quoteapp.ui.adapters.SavedQuotesAdapter
import com.parthpatel.quoteapp.utility.makeGone
import com.parthpatel.quoteapp.utility.makeVisible
import com.parthpatel.quoteapp.view_model.QuoteViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {

    private lateinit var bookmarkBinding: FragmentBookmarkBinding
    private lateinit var activity: MainActivity
    lateinit var viewModel: QuoteViewModel
    private lateinit var savedQuotesAdapter: SavedQuotesAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookmarkBinding = FragmentBookmarkBinding.bind(view)

        viewModel = activity.viewModel
        prepareRecyclerView()
        prepareObserver()
        //findNavController().navigate(R.id.action_BookmarkFragment_to_QuoteFragment)

    }

    private fun prepareObserver() {
        // observe data changes and apply them to the recycler view
        viewModel.getSavedQuotes().observe(viewLifecycleOwner, { articles ->
            savedQuotesAdapter.differ.submitList(articles)

            // if no quotes present, then show textview and hide recyclerview
            if (articles.isEmpty()) {
                bookmarkBinding.rvQuoteList.makeGone()
                bookmarkBinding.tvNotFound.makeVisible()
            } else {
                bookmarkBinding.rvQuoteList.makeVisible()
                bookmarkBinding.tvNotFound.makeGone()
            }
        })
    }

    private fun prepareRecyclerView() {
        savedQuotesAdapter =
            SavedQuotesAdapter(activity, object : SavedQuotesAdapter.OnBookmarkToggleListener {
                override fun onBookmarkClick(position: Int) {
                    val quote = savedQuotesAdapter.differ.currentList[position]
                    viewModel.deleteQuote(quote)
                    Snackbar.make(
                        bookmarkBinding.rvQuoteList,
                        "Removed Bookmark!",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            })
        bookmarkBinding.rvQuoteList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = savedQuotesAdapter
        }
    }
}