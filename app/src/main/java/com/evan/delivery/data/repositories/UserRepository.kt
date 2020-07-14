package com.evan.delivery.data.repositories

import com.evan.delivery.data.db.AppDatabase

import com.evan.delivery.data.network.MyApi
import com.evan.delivery.data.network.SafeApiRequest
import com.evan.delivery.data.network.post.AuthPost
import com.evan.delivery.data.network.post.LoginResponse
import com.evan.delivery.data.network.post.SignUpPost
import com.evan.delivery.data.network.responses.AuthResponse
import com.evan.delivery.data.network.responses.BasicResponses
import com.evan.delivery.data.network.responses.ImageResponse
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



    suspend fun createImage(part: MultipartBody.Part, body: RequestBody): ImageResponse {
        return apiRequest { api.createProfileImage(part,body) }
    }

}