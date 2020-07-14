package com.evan.delivery.ui.home.profile

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Quote
import com.evan.delivery.databinding.ProfileFragmentBinding
import com.evan.delivery.ui.home.quotes.QuotesViewModel
import com.evan.delivery.ui.home.quotes.QuotesViewModelFactory
import com.evan.delivery.util.Coroutines
import com.google.gson.Gson


import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class ProfileFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: QuotesViewModel
    private val factory: QuotesViewModelFactory by instance()

    var text: TextView?=null
    var quote: Quote? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ProfileFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(QuotesViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        Coroutines.main {
            viewModel.quotes.await().observe(this, Observer { user ->
                if(user != null){
                    initRecyclerView(user!!)
                }
            })
        }
        binding.tvWelcome.setOnClickListener {
            quote= Quote(1,"Fdf","df","Dfd","Fdf","df")
            // your code to perform when the user clicks on the button
            viewModel.saveUser(quote!!)
        }
        return binding.root
    }

    private fun initRecyclerView(list: List<Quote>) {
        var gson = Gson()
        var jsonString = gson.toJson(list)
        Log.e("evan","evan"+jsonString)
    }



}
