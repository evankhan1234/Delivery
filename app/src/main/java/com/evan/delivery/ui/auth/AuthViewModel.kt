package com.evan.delivery.ui.auth

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.evan.delivery.data.network.post.AuthPost
import com.evan.delivery.data.network.post.SignUpPost
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.ui.auth.interfaces.ISignUpListener
import com.evan.delivery.ui.auth.interfaces.Listener
import com.evan.delivery.util.ApiException
import com.evan.delivery.util.Coroutines
import com.evan.delivery.util.NoInternetException
import com.evan.delivery.util.SharedPreferenceUtil
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody


class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var authPost: AuthPost? = null
    var signUpPost: SignUpPost? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var passwordconfirm: String? = null
    var AddListener: Listener? = null
    var authListener: AuthListener? = null
    var signUpListener: ISignUpListener? = null

    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()
        if ( email.isNullOrEmpty()) {
            authListener?.onFailure("Email is Empty")
            return
        }
        else if ( password.isNullOrEmpty()) {
            authListener?.onFailure("Password is Empty")
            return
        }

        Coroutines.main {
            try {
                authPost = AuthPost(email!!, password!!)
                val authResponse = repository.userLoginFor(authPost!!)
                Log.e("response", "response" + authResponse.message)
                if(authResponse.success!!)
                {

                    SharedPreferenceUtil.saveShared(
                        view.context,
                        SharedPreferenceUtil.TYPE_AUTH_TOKEN,
                        authResponse.jwt!!
                    )
                    authListener?.onSuccess(authResponse.data!!)

                }
                else{
                    authListener?.onFailure(authResponse.message!!)
                }

            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
                authListener?.onFailure(e?.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e?.message!!)

            }
        }

    }



    fun uploadImage(part: MultipartBody.Part, body: RequestBody) {
        //else success
        Coroutines.main {
            try {
                val uploadImageResponse = repository.createImage(part, body)
                if (uploadImageResponse.success!!) {
                    Log.e("imageUpload", "" + Gson().toJson(uploadImageResponse));
                    AddListener?.Success(uploadImageResponse.img_address!!)
                } else {
                    // val alerts = repository.getDeliveryistAPI(1)
                    /**Save in local db*/
                    //   repository.saveAllAlert(alerts)
                    //listOfDelivery.value = alerts
                    AddListener?.Failure(uploadImageResponse.message!!)
                    Log.e("imageUpload", "" + Gson().toJson(uploadImageResponse));

                }
            } catch (e: ApiException) {
                AddListener?.Success(e.message!!)
//                deliveryAddListener!!.onFailure(e.message!!)
                Log.e("imageUpload", "" + (e.message!!));
            } catch (e: NoInternetException) {
                AddListener?.Success(e.message!!)
//                deliveryAddListener!!.onFailure(e.message!!)
                Log.e("imageUpload", "" + (e.message!!));
            }

        }

    }



    fun signUp(email:String,password:String,name:String,picture:String,created:String,nid:String,mobileNumber:String,status:Int,gender:Int,deliveryStatus:Int){
        signUpListener?.onStarted()
        Coroutines.main {
            try {
                signUpPost = SignUpPost(email, password,name,picture,created,nid,mobileNumber,status,gender,deliveryStatus)
                Log.e("response", "response" + Gson().toJson(signUpPost))
                val authResponse = repository.userSignUp(signUpPost!!)

                signUpListener?.onSuccess(authResponse?.message!!)
                signUpListener?.onEnd()

            } catch (e: ApiException) {
                signUpListener?.onFailure(e?.message!!)
                signUpListener?.onEndError()
                Log.e("response", "response" + e?.message!!)

            } catch (e: NoInternetException) {
                signUpListener?.onFailure(e?.message!!)
                signUpListener?.onEndError()

            }
        }
    }

}