package com.parthpatel.quoteapp.room_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.parthpatel.quoteapp.model.QuoteModel

@Database(entities = [QuoteModel::class], version = 1)
abstract class QuoteDataBase : RoomDatabase() {

    //DAO Object
    abstract fun getQuoteDao(): QuoteDAO

    companion object {

        @Volatile
        private var instance: QuoteDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        //Setup Room Database
        private fun createDatabase(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        QuoteDataBase::class.java,
                        "quotes_db.db"
                ).build()
    }

}