package com.evan.delivery.ui.home.quotes

import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Quote
import com.evan.delivery.databinding.ItemQuoteBinding
import com.xwray.groupie.databinding.BindableItem


class QuoteItem(
    private val quote: Quote
) : BindableItem<ItemQuoteBinding>(){

    override fun getLayout() = R.layout.item_quote

    override fun bind(viewBinding: ItemQuoteBinding, position: Int) {
        viewBinding.setQuote(quote)
    }
}