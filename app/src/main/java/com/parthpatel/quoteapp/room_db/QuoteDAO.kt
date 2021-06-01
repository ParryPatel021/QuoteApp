package com.parthpatel.quoteapp.room_db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.parthpatel.quoteapp.model.QuoteModel

@Dao
interface QuoteDAO {

    // upsert-> insert or update
    // insert the value to database if not present
    // update if already present
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuote(quoteModel: QuoteModel): Long

    //Get all saved Quotes
    @Query("SELECT * FROM QUOTEMODEL")
    fun getSavedQuotes(): LiveData<List<QuoteModel>>

    //Delete the specified Quote
    @Delete
    suspend fun deleteQuote(quoteModel: QuoteModel)

}