package com.evan.delivery.ui.auth

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.evan.delivery.data.network.post.*
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.ui.auth.interfaces.*
import com.evan.delivery.ui.home.customerorder.ICancelOrderListener
import com.evan.delivery.ui.home.order.IOrdersListListener
import com.evan.delivery.ui.home.settings.profile.IProfileUpdateListener
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
    var lastFiveSalesListener: ILastFiveSalesListener?=null
    var profileUpdateListener: IProfileUpdateListener? = null
    var authPost: AuthPost? = null
    var signUpPost: SignUpPost? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var passwordconfirm: String? = null
    var AddListener: Listener? = null
    var authListener: AuthListener? = null
    var tokenPost: TokenPost? = null
    var userUpdatePost: UserUpdatePost? = null
    var passwordPost: PasswordPost? = null
    var customerOrderPost: CustomerOrderPost? = null
    var customerOrderInformationListener: ICustomerOrderListener? = null
    var shopPost: ShopPost? = null
    var statusPost: StatusPost? = null
    var orderReasonStatusPost: OrderReasonStatusPost? = null
    var orderStatusPost: OrderStatusPost? = null
    var ordersTotalPost: OrdersTotalPost? = null
    var signUpListener: ISignUpListener? = null
    var customerOrderCountListener: ICustomerOrderCountListener?=null
    var profileListener: IProfileListener?=null
    var orderListListener: IOrdersListListener?=null
    var shopListener: IShopListener?=null
    var customerOrderListener: ICustomerOrderListListener? = null
    var cancelOrderListener: ICancelOrderListener? = null
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
    fun getLasFive(header:String) {

        Coroutines.main {
            try {

                val response = repository.getLasFive(header)
                Log.e("Search", "Search" + Gson().toJson(response))
                lastFiveSalesListener?.onLast(response.data!!)

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }
    fun getCustomerOrderCount(header:String) {

        Coroutines.main {
            try {

                val response = repository.getCustomerOrderCount(header)
                Log.e("Search", "Search" + Gson().toJson(response))
                customerOrderCountListener?.onCount(response.data!!)

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }
    fun getUserProfile(header:String) {

        Coroutines.main {
            try {

                val response = repository.getUserProfile(header)
                Log.e("Search", "Search" + Gson().toJson(response))
                profileListener?.onProfile(response.data!!)

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }
    fun createToken(header:String,type:Int,data:String) {

        Coroutines.main {
            try {
                tokenPost= TokenPost(type,data)
                Log.e("createToken", "createToken" + Gson().toJson(tokenPost))
                val response = repository.createToken(header,tokenPost!!)
                Log.e("createToken", "createToken" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }
    fun updateDeliveryService(header:String,status:Int) {

        Coroutines.main {
            try {
                statusPost= StatusPost(status)
                Log.e("createToken", "createToken" + Gson().toJson(statusPost))
                val response = repository.updateDeliveryService(header,statusPost!!)
                Log.e("createToken", "createToken" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }
    fun getOrders(token:String) {
        orderListListener?.onStarted()
        Coroutines.main {
            try {
                val authResponse = repository.getOrders(token)
                orderListListener?.order(authResponse?.data!!)
                Log.e("response", "response" + Gson().toJson(authResponse))
                orderListListener?.onEnd()
            } catch (e: ApiException) {
                orderListListener?.onEnd()
            } catch (e: NoInternetException) {
                orderListListener?.onEnd()
            }
        }

    }

    fun getShopBy(header:String,shopId:Int) {

        Coroutines.main {
            try {
                shopPost= ShopPost(shopId)
                Log.e("createToken", "createToken" + Gson().toJson(shopPost))
                val response = repository.getShopBy(header,shopPost!!)
                Log.e("createToken", "createToken" + Gson().toJson(response))
                shopListener?.onShow(response.data!!)
            } catch (e: ApiException) {
                Log.e("createToken", "createToken" +e?.message)
            } catch (e: NoInternetException) {

            }
        }

    }
    fun getCustomerOrders(token:String,id: Int) {
        customerOrderListener?.onStarted()
        Coroutines.main {
            try {
                customerOrderPost= CustomerOrderPost(id)
                val authResponse = repository.getCustomerOrders(token,customerOrderPost!!)
                customerOrderListener?.order(authResponse?.data!!)
                Log.e("getCustomerOrders", "getCustomerOrders" + Gson().toJson(authResponse))
                customerOrderListener?.onEnd()
            } catch (e: ApiException) {
                customerOrderListener?.onEnd()
            } catch (e: NoInternetException) {
                customerOrderListener?.onEnd()
            }
        }

    }

    fun getCustomerOrderInformation(header: String, orderId: Int) {

        Coroutines.main {
            try {
                customerOrderPost = CustomerOrderPost(orderId)
                Log.e("Search", "Search" + Gson().toJson(customerOrderPost))
                val response = repository.getCustomerOrderInformation(header, customerOrderPost!!)
                Log.e("OrderInformation", "OrderInformation" + Gson().toJson(response))
                if (response.data != null) {
                    customerOrderInformationListener?.onShow(response?.data!!)
                } else {
                    customerOrderInformationListener?.onEmpty()
                }
                //   customerOrderListener?.onShow(response?.data!!)
                Log.e("Search", "Search" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }

    fun updateReturnOrderStatus(header:String,orderId:Int,status:Int,reason:String) {

        Coroutines.main {
            try {
                orderReasonStatusPost= OrderReasonStatusPost(orderId,status,reason)
                Log.e("createToken", "createToken" + Gson().toJson(orderReasonStatusPost))
                val response = repository.updateReturnOrderStatus(header,orderReasonStatusPost!!)
                Log.e("createToken", "createToken" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }

    fun updateOrderDeliveryStatusAmount(header:String,orderId:Int,total:Double) {

        Coroutines.main {
            try {
                ordersTotalPost= OrdersTotalPost(orderId,total)
                Log.e("createToken", "createToken" + Gson().toJson(ordersTotalPost))
                val response = repository.updateOrderDeliveryStatusAmount(header,ordersTotalPost!!)
                Log.e("createToken", "createToken" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }

    fun cancelOrderStatus(header:String,orderId:Int,status:Int) {

        Coroutines.main {
            try {
                orderStatusPost= OrderStatusPost(orderId,status)
                Log.e("createToken", "createToken" + Gson().toJson(orderStatusPost))
                val response = repository.cancelOrderStatus(header,orderStatusPost!!)
                Log.e("createToken", "createToken" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }

    fun cancelOrderDeliveryStatus(header:String,orderId:Int,status:Int) {

        Coroutines.main {
            try {
                orderStatusPost= OrderStatusPost(orderId,status)
                Log.e("createToken", "createToken" + Gson().toJson(orderStatusPost))
                val response = repository.cancelOrderDeliveryStatus(header,orderStatusPost!!)
                if(response.success!!){
                    cancelOrderListener?.onCancel()
                }
                Log.e("createToken", "createToken" + Gson().toJson(response))

            } catch (e: ApiException) {

            } catch (e: NoInternetException) {

            }
        }

    }
    fun updateUserDetails(
        header: String,
        name: String,
        address: String,
        picture: String,
        gender: Int
    ) {
        profileUpdateListener?.onStarted()
        Coroutines.main {
            try {
                userUpdatePost = UserUpdatePost(name!!, address!!, picture!!, gender)
                Log.e("Search", "Search" + Gson().toJson(userUpdatePost))
                val response = repository.updateUserDetails(header, userUpdatePost!!)
                Log.e("response", "response" + Gson().toJson(response))
                profileUpdateListener?.onUser(response?.message!!)
                profileUpdateListener?.onEnd()

            } catch (e: ApiException) {
                profileUpdateListener?.onEnd()
                profileUpdateListener?.onFailure(e?.message!!)
            } catch (e: NoInternetException) {
                profileUpdateListener?.onEnd()
                profileUpdateListener?.onFailure(e?.message!!)
            }
        }

    }
    fun updatePassword(header: String, password: String) {
        profileUpdateListener?.onStarted()
        Coroutines.main {
            try {
                passwordPost = PasswordPost(password!!)
                Log.e("Search", "Search" + Gson().toJson(passwordPost))
                val response = repository.updatePassword(header, passwordPost!!)
                Log.e("response", "response" + Gson().toJson(response))
                profileUpdateListener?.onUser(response?.message!!)
                profileUpdateListener?.onEnd()

            } catch (e: ApiException) {
                profileUpdateListener?.onEnd()
                profileUpdateListener?.onFailure(e?.message!!)
            } catch (e: NoInternetException) {
                profileUpdateListener?.onEnd()
                profileUpdateListener?.onFailure(e?.message!!)
            }
        }

    }
}