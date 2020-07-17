package com.evan.delivery.ui.home.orderdelivery

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.evan.delivery.R
import com.evan.delivery.ui.home.delivery.DeliveryFragment
import com.evan.delivery.ui.home.order.OrderFragment


class OrderDeliveryPagerAdapter (fm: FragmentManager, val context: Context) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var firstTab: OrderFragment?= OrderFragment()
    var secondtTab: DeliveryFragment?= DeliveryFragment()


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                firstTab!!
            }
            1 ->   secondtTab!!
            else -> {
                return secondtTab!!
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.resources.getString(R.string.order)
            1 -> context.resources.getString(R.string.delivery)
            else -> {
                return context.resources.getString(R.string.delivery)
            }
        }
    }
}