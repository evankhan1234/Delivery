package com.evan.delivery.ui.home.quotes

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.evan.delivery.data.db.entities.Quote
import com.evan.delivery.data.repositories.QuotesRepository
import com.evan.delivery.util.Coroutines
import com.evan.delivery.util.lazyDeferred


class QuotesViewModel(
   private val repository: QuotesRepository
) : ViewModel() {

    val quotes by lazyDeferred {
        repository.getQuotes()
    }
    fun saveUser(quote: Quote){
        Coroutines.main {
            Log.e("sdfds","Sds"+quote.created_at)
            repository.saveUser(quote)
        }

    }
}
