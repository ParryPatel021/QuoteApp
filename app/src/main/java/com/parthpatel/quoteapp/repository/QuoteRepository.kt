package com.parthpatel.quoteapp.repository

import com.parthpatel.quoteapp.api.RetrofitInstance
import com.parthpatel.quoteapp.model.QuoteModel
import com.parthpatel.quoteapp.room_db.QuoteDataBase

//Bridge between ViewModel, API and Local Database
class QuoteRepository(
        private val db: QuoteDataBase
) {

    suspend fun getRandomQuote() = RetrofitInstance.api.getRandomQuote()

    suspend fun upsert(quoteModel: QuoteModel) = db.getQuoteDao().upsertQuote(quoteModel)

    suspend fun deleteQuote(quoteModel: QuoteModel) = db.getQuoteDao().deleteQuote(quoteModel)

    fun getSavedQuotes() = db.getQuoteDao().getSavedQuotes()
}