package com.evan.delivery.data.network.responses

import com.evan.delivery.data.db.entities.Quote


data class QuotesResponse (
    val isSuccessful: Boolean,
    val quotes: List<Quote>
)