package com.evan.delivery.ui.home.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Users
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.auth.LoginActivity
import com.evan.delivery.ui.auth.interfaces.ICustomerOrderCountListener
import com.evan.delivery.ui.auth.interfaces.ILastFiveSalesListener
import com.evan.delivery.ui.auth.interfaces.IProfileListener
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.util.SharedPreferenceUtil
import de.hdodenhof.circleimageview.CircleImageView
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class SettingsFragment : Fragment() , KodeinAware, IProfileListener {
    var token: String? = ""
    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    var users:Users?=null
    var img_icon: CircleImageView?=null
    var text_name: TextView?=null
    var text_phone_number: TextView?=null
    var text_email: TextView?=null
    var linear_profile: LinearLayout?=null
    var linear_change_password: LinearLayout?=null
    var linear_logout: LinearLayout?=null
    var progress_bar: ProgressBar?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_settings, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)

        viewModel.profileListener=this
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)
        viewModel.getUserProfile(token!!)
        linear_change_password=root?.findViewById(R.id.linear_change_password)
        linear_logout=root?.findViewById(R.id.linear_logout)

        text_phone_number=root?.findViewById(R.id.text_phone_number)
        linear_profile=root?.findViewById(R.id.linear_profile)
        progress_bar=root?.findViewById(R.id.progress_bar)
        text_name=root?.findViewById(R.id.text_name)
        img_icon=root?.findViewById(R.id.img_icon)
        text_email=root?.findViewById(R.id.text_email)
        linear_profile?.setOnClickListener {
            if (activity is HomeActivity) {
                (activity as HomeActivity).goToProfileUpdateFragment(users!!)
            }
        }
        linear_change_password?.setOnClickListener {
            if (activity is HomeActivity) {
                (activity as HomeActivity).goToChangePasswordFragment(users!!)
            }
        }
        linear_logout?.setOnClickListener {
            Toast.makeText(context!!,"Successfully Logout", Toast.LENGTH_SHORT).show()
            SharedPreferenceUtil.saveShared(context!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN, "")
            SharedPreferenceUtil.saveShared(context!!, SharedPreferenceUtil.TYPE_FRESH, "")
            val intent= Intent(context!!, LoginActivity::class.java)
            startActivity(intent)
            if (activity is HomeActivity) {
                (activity as HomeActivity).finishs()
            }
        }
        return root
    }

    override fun onProfile(user: Users) {
        users=user
        Glide.with(context!!)
            .load(users?.Picture)
            .into(img_icon!!)
        text_name?.setText(users?.Name)
        text_phone_number?.setText(users?.MobileNumber)
        text_email?.setText(users?.Email)
    }


}