package com.parthpatel.quoteapp.view_model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.parthpatel.quoteapp.model.QuoteModel
import com.parthpatel.quoteapp.repository.QuoteRepository
import com.parthpatel.quoteapp.utility.QuoteApp
import com.parthpatel.quoteapp.utility.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class QuoteViewModel(
    app: QuoteApp,
    private val quoteRepository: QuoteRepository
) : AndroidViewModel(app) {

    //Observable variables
    val quote: MutableLiveData<Resource<QuoteModel>> = MutableLiveData()
    val bookmarked: MutableLiveData<Boolean> = MutableLiveData()

    init {
        //get Quote when the viewModel is called for the first time
        getRandomQuote()
    }

    //Function that get quote using background Thread
    fun getRandomQuote() = viewModelScope.launch {
        bookmarked.postValue(false)
        safeQuotesCall()
    }

    //Function that checks for possible phone states before making API requests
    //For Example: Internet connectivity issue
    private suspend fun safeQuotesCall() {
        try {
            //if (hasInternetConnection()) {
                quote.postValue(Resource.Loading())
                val response = quoteRepository.getRandomQuote()
                quote.postValue(handleQuoteResponse(response))
            /*} else {
                quote.postValue(Resource.Error("No internet connection"))
            }*/
        } catch (t: Throwable) {
            when (t) {
                is IOException -> quote.postValue(Resource.Error("Network Error"))
                else -> quote.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    // convert API response to resource type that can be used to fetch status of response
    private fun handleQuoteResponse(response: Response<List<QuoteModel>>): Resource<QuoteModel> {
        if (response.isSuccessful) {
            return Resource.Success(response.body()!![0])
        }
        return Resource.Error(response.message())
    }

    // save a particular quote to the database
    fun saveQuote(quote: QuoteModel) = viewModelScope.launch {
        quoteRepository.upsert(quote)
        bookmarked.postValue(true)
    }

    // get all the saved quotes from the database
    fun getSavedQuotes() = quoteRepository.getSavedQuotes()

    // delete a particular quote
    fun deleteQuote(quote: QuoteModel) = viewModelScope.launch {
        quoteRepository.deleteQuote(quote)
        bookmarked.postValue(false)
    }

    // function to check all the possible conditons when the device can have
    // an active internet connection or not
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<QuoteApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    return when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return false
    }

}