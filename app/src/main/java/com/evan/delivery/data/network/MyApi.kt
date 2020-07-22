package com.evan.delivery.data.network


import com.evan.delivery.data.network.post.*
import com.evan.delivery.data.network.responses.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {

    @POST("update-return-order-status.php")
    suspend fun updateReturnOrderStatus(
        @Header("Authorization") Authorization:String,
        @Body post: OrderReasonStatusPost
    ): Response<BasicResponses>

    @POST("update-return-order-delivery-status.php")
    suspend fun updateOrderDeliveryStatusAmount(
        @Header("Authorization") Authorization:String,
        @Body post: OrdersTotalPost
    ): Response<BasicResponses>

    @POST("update-cancel-custmer-order-status.php")
    suspend fun cancelOrderStatus(
        @Header("Authorization") Authorization:String,
        @Body post: OrderStatusPost
    ): Response<BasicResponses>

    @POST("update-cancel-customer-order-delivery-status.php")
    suspend fun cancelOrderDeliveryStatus(
        @Header("Authorization") Authorization:String,
        @Body post: OrderStatusPost
    ): Response<BasicResponses>

    @POST("get-delivery-customer-order-details.php")
    suspend fun getCustomerOrderInformation(
        @Header("Authorization") Authorization:String,
        @Body post: CustomerOrderPost
    ): Response<CustomerOrderResponses>

    @POST("get-delivery-own-deliveries-pagination.php")
    suspend fun getOwnDeliveryPagination(
        @Header("Authorization") Authorization:String,
        @Body post: LimitPost
    ): Response<OwnDeliveryResponses>
    @POST("update-delivery-user-details.php")
    suspend fun  updateUserDetails(
        @Header("Authorization") Authorization:String,
        @Body userUpdatePost: UserUpdatePost
    ): Response<BasicResponses>
    @POST("customer-order-products-delivery.php")
    suspend fun getCustomerOrder(
        @Header("Authorization") Authorization:String,
        @Body customerOrderPost: CustomerOrderPost
    ): Response<CustomerOrderListResponses>
    @POST("update-delivery-password.php")
    suspend fun  updatePassword(
        @Header("Authorization") Authorization:String,
        @Body passwordPost: PasswordPost
    ): Response<BasicResponses>
    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<AuthResponse>
    @GET("get-delivery-last-five-sales.php")
    suspend fun getLasFive(
        @Header("Authorization") Authorization:String
    ): Response<LastFiveSalesCountResponses>
    @GET("get-delivery-order-count.php")
    suspend fun getCustomerOrderCount(
        @Header("Authorization") Authorization:String
    ): Response<CustomerOrderCountResponses>

    @GET("get-delivery-user-profile.php")
    suspend fun getUserProfile(
        @Header("Authorization") Authorization:String
    ): Response<ProfileResponses>

    @POST("get-shop-by-latitude.php")
    suspend fun getShopBy(
        @Header("Authorization") Authorization:String,
        @Body shopPost: ShopPost
    ): Response<ShopResponses>
    @POST("user-token.php")
    suspend fun createToken(
        @Header("Authorization") Authorization:String,
        @Body tokenPost: TokenPost
    ): Response<BasicResponses>
    @POST("login-delivery-api.php")
    suspend fun userLoginFor(
        @Body authPost: AuthPost
    ) : Response<LoginResponse>
    @FormUrlEncoded
    @POST("signup")
    suspend fun userSignup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<AuthResponse>
    @GET("get-delivery-pending-order.php")
    suspend fun getOrders(
        @Header("Authorization") Authorization:String
    ): Response<OrderListResponses>
    @POST("get-delivery-processing-pagination.php")
    suspend fun getDeliveryList(
        @Header("Authorization") Authorization:String,
        @Body limitPost: OrderPost
    ): Response<DeliveryResponses>

    @POST("get-delivery-pending-order-pagination.php")
    suspend fun getOrdersPagination(
        @Header("Authorization") Authorization:String,
        @Body limitPost: OrderPost
    ): Response<OrderListResponses>

    @POST("update-delivery-api-delivery-user.php")
    suspend fun updateDeliveryStatus(
        @Header("Authorization") Authorization:String,
        @Body deliveryStatusPost: DeliveryStatusPost
    ): Response<BasicResponses>
    @POST("get-token.php")
    suspend fun getToken(
        @Header("Authorization") Authorization:String,
        @Body tokenPost: TokenPost
    ): Response<TokenResponses>
    @POST("update-delivery-status.php")
    suspend fun updateDeliveryService(
        @Header("Authorization") Authorization:String,
        @Body statusPost: StatusPost
    ) : Response<BasicResponses>
    @Multipart
    @POST("create-sign-up-image.php")
    suspend fun createProfileImage(
        @Part file: MultipartBody.Part?, @Part("uploaded_file") requestBody: RequestBody?
    ): Response<ImageResponse>

    @POST("create-delivery-user-api.php")
    suspend fun userSignUp(
        @Body post: SignUpPost
    ) : Response<BasicResponses>
    @GET("quotes")
    suspend fun getQuotes() : Response<QuotesResponse>

    companion object{
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : MyApi{

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl("http://192.168.0.105/stationary/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}

