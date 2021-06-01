package com.parthpatel.quoteapp.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "QuoteModel", primaryKeys = ["id","quote"])
data class QuoteModel(
        //Primary key
        var id: Int,
        @SerializedName("q")
        val quote: String = "",
        @SerializedName("a")
        val author: String = "",
        @SerializedName("h")
        val formatted: String = ""
) {
}