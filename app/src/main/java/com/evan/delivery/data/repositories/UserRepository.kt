package com.evan.delivery.data.repositories

import com.evan.delivery.data.db.AppDatabase

import com.evan.delivery.data.network.MyApi
import com.evan.delivery.data.network.SafeApiRequest
import com.evan.delivery.data.network.post.*
import com.evan.delivery.data.network.responses.*
import okhttp3.MultipartBody
import okhttp3.RequestBody


class UserRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun userLogin(email: String, password: String): AuthResponse {
        return apiRequest { api.userLogin(email, password) }
    }
    suspend fun userLoginFor(auth: AuthPost): LoginResponse {
        return apiRequest { api.userLoginFor(auth) }
    }
    suspend fun userSignUp(auth: SignUpPost): BasicResponses {
        return apiRequest { api.userSignUp(auth) }
    }

    suspend fun getLasFive(header:String): LastFiveSalesCountResponses {
        return apiRequest { api.getLasFive(header) }
    }
    suspend fun getCustomerOrderCount(header:String): CustomerOrderCountResponses {
        return apiRequest { api.getCustomerOrderCount(header) }
    }
    suspend fun createImage(part: MultipartBody.Part, body: RequestBody): ImageResponse {
        return apiRequest { api.createProfileImage(part,body) }
    }
    suspend fun getUserProfile(header:String): ProfileResponses {
        return apiRequest { api.getUserProfile(header) }
    }
    suspend fun createToken(header:String,tokenPost: TokenPost): BasicResponses {
        return apiRequest { api.createToken(header,tokenPost) }
    }
    suspend fun updateDeliveryService(header:String,statusPost: StatusPost): BasicResponses {
        return apiRequest { api.updateDeliveryService(header,statusPost) }
    }

}