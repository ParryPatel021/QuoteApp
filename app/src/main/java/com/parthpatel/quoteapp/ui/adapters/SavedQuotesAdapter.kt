package com.parthpatel.quoteapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.parthpatel.quoteapp.R
import com.parthpatel.quoteapp.databinding.QuoteItemBinding
import com.parthpatel.quoteapp.model.QuoteModel

class SavedQuotesAdapter(
    private val context: Context,
    private val listener: OnBookmarkToggleListener
) :
    RecyclerView.Adapter<SavedQuotesAdapter.QuoteViewHolder>() {

    interface OnBookmarkToggleListener {
        fun onBookmarkClick(position: Int)
    }

    inner class QuoteViewHolder(
        private val binding: QuoteItemBinding,
        private val adapter: SavedQuotesAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.fabAddBookmark.setOnClickListener {
                adapter.listener.onBookmarkClick(adapterPosition)
            }
        }

        fun setDataIntoView(data: QuoteModel) {
            data.let {
                binding.tvQuoteText.text = it.quote
                binding.tvAuthor.text = it.author
                binding.fabAddBookmark.setImageDrawable(
                    ContextCompat.getDrawable(
                        adapter.context,
                        R.drawable.ic_bookmarked
                    )
                )
            }
        }
    }

    // differ callback that checks if elements are same of not
    private val differCallback = object : DiffUtil.ItemCallback<QuoteModel>() {
        override fun areItemsTheSame(oldItem: QuoteModel, newItem: QuoteModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuoteModel, newItem: QuoteModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuoteItemBinding.inflate(inflater, parent, false)
        return QuoteViewHolder(binding, this)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.setDataIntoView(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

}