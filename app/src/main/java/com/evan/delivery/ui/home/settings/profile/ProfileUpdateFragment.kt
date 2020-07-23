package com.evan.delivery.ui.home.settings.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Users
import com.evan.delivery.ui.auth.AuthViewModel
import com.evan.delivery.ui.auth.AuthViewModelFactory
import com.evan.delivery.ui.home.HomeActivity
import com.evan.delivery.util.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_create_account.*

import kotlinx.android.synthetic.main.fragment_profile_update.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*

class ProfileUpdateFragment : Fragment(),KodeinAware ,IProfileUpdateListener{
    override val kodein by kodein()
    var progress_bar: ProgressBar?=null
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    var token:String?=""

    var img_user_profile:CircleImageView?=null
    var root_layout:RelativeLayout?=null
    var et_name:EditText?=null
    var et_email:EditText?=null
    var et_phone:EditText?=null
    var et_address:EditText?=null
    var radio_male: RadioButton?=null
    var radio_female: RadioButton?=null
    var img_user_add: AppCompatImageButton?=null
    var btn_ok: Button?=null
    var image_address: String?=""
    var name: String=""
    var address: String=""
    var genderId: Int=0
    var ids: Int=0
    var users: Users?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_profile_update, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.profileUpdateListener=this
        img_user_profile=root?.findViewById(R.id.img_user_profile)
        root_layout=root?.findViewById(R.id.root_layout)
        btn_ok=root?.findViewById(R.id.btn_ok)
        et_name=root?.findViewById(R.id.et_name)
        et_email=root?.findViewById(R.id.et_email)
        et_phone=root?.findViewById(R.id.et_phone)
        et_address=root?.findViewById(R.id.et_address)
        radio_male=root?.findViewById(R.id.radio_male)
        radio_female=root?.findViewById(R.id.radio_female)
        img_user_add=root?.findViewById(R.id.img_user_add)
        token = SharedPreferenceUtil.getShared(activity!!, SharedPreferenceUtil.TYPE_AUTH_TOKEN)
        val args: Bundle? = arguments
        if (args != null) {
            if (args?.containsKey(Users::class.java.getSimpleName()) != null) {
                users = args?.getParcelable(Users::class.java.getSimpleName())
                image_address=users?.Picture
                name=users?.Name!!
                address=users?.NID!!
                genderId=users?.Gender!!
                ids=users?.Id!!
                Glide.with(context!!)
                    .load(users?.Picture)
                    .into(img_user_profile!!)
                et_name?.setText(users?.Name)
                et_email?.setText(users?.Email)
                et_phone?.setText(users?.MobileNumber)
                et_address?.setText(users?.NID)
                if(users?.Gender==1){
                    radio_male?.isChecked=true
                    radio_female?.isChecked=false
                }
                else{
                    radio_male?.isChecked=false
                    radio_female?.isChecked=true
                }
            }

        }
        img_user_add=root?.findViewById(R.id.img_user_add)
        img_user_add?.setOnClickListener{
            image_update="profile"
            (activity as HomeActivity?)!!.openImageChooser()
        }
        btn_ok?.setOnClickListener {
            name=et_name?.text.toString()
            address=et_address?.text.toString()
            if(radio_male?.isChecked!!){
                genderId=1
            }
            else if(radio_female?.isChecked!!){
                genderId=2
            }
            if(name.isNullOrEmpty()  && address.isNullOrEmpty()&& image_address.isNullOrEmpty()){
                root_layout?.snackbar("All Field is Empty")
            }
            else if(name.isNullOrEmpty()){
                root_layout?.snackbar("Name is Empty")
            }

            else if(address.isNullOrEmpty()){
                root_layout?.snackbar("Address is Empty")
            }
            else if(image_address.isNullOrEmpty()){
                root_layout?.snackbar("Image is Empty")
            }
            else{
                Log.e("id","id"+ids)
                Log.e("address","address"+address)
                Log.e("image_address","image_address"+image_address)
                Log.e("name","name"+name)
                Log.e("genderId","genderId"+genderId)

             viewModel.updateUserDetails(token!!,name,address,image_address!!,genderId)

            }
        }
        return root
    }
    fun showImage(temp:String?){
        image_address="http://192.168.0.105/"+temp
        Log.e("for","Image"+temp)
        Glide.with(this)
            .load("http://192.168.0.105/"+temp)
            .into(img_user_profile!!)

//        image_address="http://hathbazzar.com/"+temp
//        Log.e("for","Image"+temp)
//        Glide.with(this)
//            .load("http://hathbazzar.com/"+temp)
//            .into(img_user_profile!!)
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
     //   status(image_address!!)
    }

    override fun onFailure(message: String) {
        Toast.makeText(context!!,message,Toast.LENGTH_SHORT).show()
    }
}