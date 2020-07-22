package com.evan.delivery.ui.home.notice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Notice
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.util.NetworkState

import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class NoticeFragment : Fragment(),KodeinAware,INoticeUpdateListener {
    override val kodein by kodein()

    private val factory : NoticeModelFactory by instance()

    var noticeAdapter:NoticeAdapter?=null


    private lateinit var viewModel: NoticeViewModel

    var rcv_notice: RecyclerView?=null

    var progress_bar: ProgressBar?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_notice, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(NoticeViewModel::class.java)
        rcv_notice=root?.findViewById(R.id.rcv_notice)
        initAdapter()
        initState()
        return root
    }

    private fun initAdapter() {
        noticeAdapter = NoticeAdapter(context!!,this)
        rcv_notice?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        rcv_notice?.adapter = noticeAdapter
        startListening()

    }

    private fun startListening() {


        viewModel.listOfAlerts?.observe(this, Observer {
            noticeAdapter?.submitList(it)
        })

    }


    private fun initState() {
        viewModel.getNetworkState().observe(this, Observer { state ->
            when (state.status) {
                NetworkState.Status.LOADIND -> {
                    progress_bar?.visibility=View.VISIBLE
                }
                NetworkState.Status.SUCCESS -> {
                    progress_bar?.visibility=View.GONE
                }
                NetworkState.Status.FAILED -> {
                    progress_bar?.visibility=View.GONE
                }
            }
        })
    }

    override fun onUpdate(notice: Notice) {
        if (activity is HomeActivity) {
            (activity as HomeActivity).goToNoticeDetailsFragment(notice)
        }
    }
}