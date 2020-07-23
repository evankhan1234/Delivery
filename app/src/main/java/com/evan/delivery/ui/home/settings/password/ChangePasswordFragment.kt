package com.evan.delivery.ui.home.settings.password

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Users
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.ui.home.settings.profile.IProfileUpdateListener
import com.evan.delivery.util.*

import kotlinx.android.synthetic.main.activity_create_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ChangePasswordFragment : Fragment(),KodeinAware, IProfileUpdateListener {

    override val kodein by kodein()
    var progress_bar: ProgressBar?=null
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    var token:String?=""
    var et_current_password:EditText?=null
    var et_password:EditText?=null
    var et_confirm_password:EditText?=null
    var show_current_pass:ImageView?=null
    var show_pass:ImageView?=null
    var show_confirm_pass:ImageView?=null
    var mdPassword:String?=""
    var current_password: String=""
    var password: String=""
    var confirm_password: String=""
    var address: String=""
    var btn_ok: Button?=null
    var users: Users?=null
    var root_layout: RelativeLayout?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_change_password, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.profileUpdateListener=this
        root_layout=root?.findViewById(R.id.root_layout)
        et_current_password=root?.findViewById(R.id.et_current_password)
        progress_bar=root?.findViewById(R.id.progress_bar)
        show_current_pass=root?.findViewById(R.id.show_current_pass)
        et_password=root?.findViewById(R.id.et_password)
        show_pass=root?.findViewById(R.id.show_pass)
        et_confirm_password=root?.findViewById(R.id.et_confirm_password)
        show_confirm_pass=root?.findViewById(R.id.show_confirm_pass)
        btn_ok=root?.findViewById(R.id.btn_ok)
        et_current_password?.transformationMethod = MyPasswordTransformationMethod()
        et_password?.transformationMethod = MyPasswordTransformationMethod()
        et_confirm_password?.transformationMethod = MyPasswordTransformationMethod()
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)
        show_pass?.setOnClickListener {
            onPasswordVisibleOrInvisible()
        }
        show_current_pass?.setOnClickListener {
            onCurrentPasswordVisibleOrInvisible()
        }
        show_confirm_pass?.setOnClickListener {
            onConfirmPasswordVisibleOrInvisible()
        }
        val args: Bundle? = arguments
        if (args != null) {
            if (args?.containsKey(Users::class.java.getSimpleName()) != null) {
                users = args?.getParcelable(Users::class.java.getSimpleName())

            }

        }

        Log.e("mdPassword","mdPassword"+mdPassword)
        btn_ok?.setOnClickListener {
            current_password=et_current_password?.text.toString()
            password=et_password?.text.toString()
            confirm_password=et_confirm_password?.text.toString()
            if(current_password.isNullOrEmpty()  && password.isNullOrEmpty()&& confirm_password.isNullOrEmpty()){
                root_layout?.snackbar("All Field is Empty")
            }
            else if(current_password.isNullOrEmpty()){
                root_layout?.snackbar("Current Password is Empty")
            }

            else if(password.isNullOrEmpty()){
                root_layout?.snackbar("Password is Empty")
            }
            else if(confirm_password.isNullOrEmpty()){
                root_layout?.snackbar("Confirm Password is Empty")
            }
            else{
                mdPassword=getHashPassWordMD5(et_current_password?.text.toString())
                if(users?.Password.equals(mdPassword)){
                    if(password!!.equals(confirm_password)){

                      var pass=getHashPassWordMD5(password)
                        Log.e("pass","pass"+pass)
                        viewModel?.updatePassword(token!!,pass!!)

                    }
                    else{
                        root_layout?.snackbar("Password and Confirm password not same")
                    }
                }
                else{
                    root_layout?.snackbar("Current password not correct")
                }
//                Log.e("id","id"+ids)
//                Log.e("address","address"+address)
//                Log.e("image_address","image_address"+image_address)
//                Log.e("name","name"+name)
//                Log.e("genderId","genderId"+genderId)

               // viewModel.updateUserDetails(token!!,ids,name,address,image_address!!,genderId)

            }

        }
        return root
    }

    fun onPasswordVisibleOrInvisible() {
        val cursorPosition = et_password?.selectionStart

        if (et_password?.transformationMethod == null) {
            et_password?.transformationMethod = MyPasswordTransformationMethod()
            show_pass?.isSelected = false
        } else {

            et_password?.transformationMethod = null
            show_pass?.isSelected = true
        }
        et_password?.setSelection(cursorPosition!!)
    }
    fun onCurrentPasswordVisibleOrInvisible() {
        val cursorPosition = et_current_password?.selectionStart

        if (et_current_password?.transformationMethod == null) {
            et_current_password?.transformationMethod = MyPasswordTransformationMethod()
            show_current_pass?.isSelected = false
        } else {

            et_current_password?.transformationMethod = null
            show_current_pass?.isSelected = true
        }
        et_current_password?.setSelection(cursorPosition!!)
    }
    fun onConfirmPasswordVisibleOrInvisible() {
        val cursorPosition = et_confirm_password?.selectionStart

        if (et_confirm_password?.transformationMethod == null) {
            et_confirm_password?.transformationMethod = MyPasswordTransformationMethod()
            show_confirm_pass?.isSelected = false
        } else {

            et_confirm_password?.transformationMethod = null
            show_confirm_pass?.isSelected = true
        }
        et_confirm_password?.setSelection(cursorPosition!!)
    }
    override fun onStarted() {

        progress_bar?.show()
//
    }

    override fun onEnd() {
        progress_bar?.hide()
    }

    override fun onUser(message: String) {
        Toast.makeText(context!!,message,Toast.LENGTH_SHORT).show()
        (activity as HomeActivity?)!!.onBackPressed()
    }

    override fun onFailure(message: String) {
        Toast.makeText(context!!,message,Toast.LENGTH_SHORT).show()
    }
}