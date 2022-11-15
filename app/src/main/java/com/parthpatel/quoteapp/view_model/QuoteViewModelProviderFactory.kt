package com.parthpatel.quoteapp.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.parthpatel.quoteapp.repository.QuoteRepository
import com.parthpatel.quoteapp.utility.QuoteApp


// View Model Provider Factory Class
class QuoteViewModelProviderFactory(
        private val app: QuoteApp,
        private val quoteRepository: QuoteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return QuoteViewModel(app, quoteRepository) as T
    }

}