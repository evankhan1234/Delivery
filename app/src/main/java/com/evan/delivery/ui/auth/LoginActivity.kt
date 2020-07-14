package com.evan.delivery.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
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

import com.evan.delivery.data.db.entities.Users
import com.evan.delivery.databinding.ActivityLoginBinding
import com.evan.delivery.util.MyPasswordTransformationMethod
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
    var btn_create_account: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel


        btn_create_account=findViewById(R.id.btn_create_account)
        viewModel.authListener = this
        btn_create_account?.setOnClickListener {
            Intent(this, CreateAccountActivity::class.java).also {

                startActivity(it)
            }
        }
        et_password?.transformationMethod = MyPasswordTransformationMethod()

        show_pass?.setOnClickListener {
            onPasswordVisibleOrInvisible()
        }


    }

    override fun onStarted() {
        progress_bar.show()
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

    override fun onSuccess(user: Users) {
        progress_bar.hide()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
//        Intent(this, HomeActivity::class.java).also {
//            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(it)
//        }

    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        root_layout.snackbar(message)
    }

}
