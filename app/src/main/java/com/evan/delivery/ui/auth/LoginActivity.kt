package com.evan.delivery.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.evan.delivery.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.User
import com.evan.delivery.databinding.ActivityLoginBinding
import com.evan.delivery.util.hide
import com.evan.delivery.util.show
import com.evan.delivery.util.snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class LoginActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    var text_building_name: String? = ""
    var tv_sign_in: TextView? = null
    var et_email: EditText? = null
    var et_mobile: EditText? = null
    var radio_email: RadioButton? = null
    var radio_mobile: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel
        et_email=findViewById(R.id.et_email)
        et_mobile=findViewById(R.id.et_mobile)
        radio_email=findViewById(R.id.radio_email)
        tv_sign_in=findViewById(R.id.tv_sign_in)
        radio_mobile=findViewById(R.id.radio_mobile)
        radio_email?.setOnClickListener{
            et_mobile?.visibility=View.GONE
            et_email?.visibility=View.VISIBLE
        }
        radio_mobile?.setOnClickListener{
            et_email?.visibility=View.GONE
            et_mobile?.visibility=View.VISIBLE
        }
        viewModel.authListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if(user != null){
                Intent(this, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        })
        text_building_name =
            resources!!.getString(R.string.account) + "<font color=#DDC915> Sign up</font>"
        tv_sign_in?.text = Html.fromHtml(text_building_name)

    }

    override fun onStarted() {
        progress_bar.show()
    }

    override fun onSuccess(user: User) {
        progress_bar.hide()
        Intent(this, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        root_layout.snackbar(message)
    }

}
