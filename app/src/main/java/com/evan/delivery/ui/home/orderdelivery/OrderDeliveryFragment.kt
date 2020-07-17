package com.evan.delivery.ui.home.orderdelivery

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager


import com.evan.delivery.R
import com.google.android.material.tabs.TabLayout


class OrderDeliveryFragment : Fragment() {
    private var mContext: Context? = null
    private var viewPager: ViewPager? = null
    private var tabs: TabLayout? = null
    private var indicator: View? = null
    val SELECTED_PAGE:String="page"
    var root:View?=null
    var selectedPage:Int?=-1
    var fragmentAdapter: OrderDeliveryPagerAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_order_delivery, container, false)
        mContext = activity
        viewPager = root?.findViewById(R.id.viewPager)
        tabs = root?.findViewById(R.id.tabs)
        indicator = root?.findViewById(R.id.indicator)
        setTabView()
        return root
    }
    fun setTabView() {
        fragmentAdapter= OrderDeliveryPagerAdapter(childFragmentManager, mContext!!)
        viewPager?.apply {
            this.adapter = fragmentAdapter
            this.offscreenPageLimit=1
            tabs?.setupWithViewPager(viewPager)

        }

        viewPager?.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, positionOffset: Float, positionOffsetPx: Int) {
            }
            override fun onPageSelected(i: Int) {
                refreshData(i)
            }
            override fun onPageScrollStateChanged(i: Int) {}
        })
    }
    fun refreshData(index:Int?)
    {
        viewPager?.setCurrentItem(index!!)
        if(index==0)
        {
          //  fragmentAdapter?.firstTab?.getDataReload()
        }
        else  if(index==1)
        {
            //  fragmentAdapter?.secondtTab?.getDataFromAPI()
        }else  if(index==2)
        {
            //  fragmentAdapter?.thirdTab?.getDataFromAPI()
        }
        else  if(index==3)
        {
            //  fragmentAdapter?.thirdTab?.getDataFromAPI()
        }
    }

}
