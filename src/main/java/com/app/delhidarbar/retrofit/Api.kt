package com.app.delhidarbar.retrofit

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import com.app.delhidarbar.model.dashboard.home.Parent_Home
import com.app.delhidarbar.model.get_all_restaurant.AllRestaurantResponse
import com.app.delhidarbar.model.login.ParentLogin
import com.app.delhidarbar.model.my_static_view_cart1.PrepareCartResponse
import com.app.delhidarbar.model.profile_model.ProfileResponeModel
import com.app.delhidarbar.model.recent_location.RecentLocation
import com.app.delhidarbar.model.register.ParentRegisterResponse
import com.app.delhidarbar.model.restaurent.SelectRestaurentModel
import com.app.delhidarbar.model.select_restaurent_model.SelectRestaurantResponse
import com.app.delhidarbar.model.update_user.UpdateUser
import com.app.delhidarbar.model.update_user_location.UpdateLocationResponseModel
import com.app.delhidarbar.model.view_all_coupons.ViewAllCouponResponse

interface Api {
    @GET("/public/allrestaurants")
    fun allRestaurants(): Call<SelectRestaurentModel>

    @Multipart
    @POST("/public/createuser")
    fun createUser(@PartMap map: Map<String, @JvmSuppressWildcards RequestBody>): Call<ParentRegisterResponse>


    @Multipart
    @POST("/public/updateuser/{user_id}")
    fun updateUser(@PartMap map: Map<String, @JvmSuppressWildcards RequestBody>, @Path("user_id") user_id: String): Call<UpdateUser>

    /**
     * LoginUser login Api
     *
     * @param email
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("/public/userlogin")
    fun getUserLoginDetails(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("login_type") login_type: String,
            @Field("name") name: String,
            @Field("fgid") fgid: String,
            @Field("language") language: String
    ): Call<ParentLogin>

    /**
     * Forget password api
     *
     * @param email
     * @return
     */
    @FormUrlEncoded
    @POST("/public/forgotpassword")
    fun sendForgertpassword(
            @Field("email") email: String): Call<ParentLogin>

    /**
     * This is the api  to show all items
     */
    @GET("/public/allitems/{user_id}")
    fun dashboard(@Path("user_id") user_id: String,
                  @Query("id") id: String,
                  @Query("type") type: String,
                  @Query("offer_id") offer_id: String): Call<Parent_Home>

    @FormUrlEncoded
    @POST("/public/search")
    fun search(@Field("keyword") keyword: String): Call<Parent_Home>

    @FormUrlEncoded
    @POST("/public/submit-rating")
    fun submitRating(
            @Field("description") description: String,
            @Field("rating") rating: String,
            @Field("price") price: String,
            @Field("rest_address") rest_address: String,
            @Field("rest_name") rest_name: String,
            @Field("user_id") user_id: String
    ): Call<SelectRestaurantResponse>

    @FormUrlEncoded
    @POST("/public/add-review")
    fun addReview(
            @Field("user_id") user_id: String,
            @Field("order_id") order_id: String,
            @Field("rating") rating: String,
            @Field("description") description: String
    ): Call<SelectRestaurantResponse>

    /**
     * This is the api for recents location  of users
     */

   /* @GET("/public/recent-locations")
    fun locationApi(): Call<RecentLocation>*/

    @GET("/public/recent-locations/{user_id}")
    fun locationApi(@Path("user_id") user_id: String
    ): Call<RecentLocation>

    /**
     * This  is the api for show all  coupons
     */
    @GET("/public/all-coupons")
    fun viewAllCoupons(): Call<ViewAllCouponResponse>

    /**
     * this is  the api for add to cart
     */

    @FormUrlEncoded
    @POST("/public/add-cart")
    fun addUserCart(@Field("items") items: String): Call<PrepareCartResponse>

    @FormUrlEncoded
    @POST("/public/re-order")
    fun reOrder(@Field("user_id") user_id: String,@Field("order_id") order_id: String ): Call<PrepareCartResponse>

    /**
     * This the api for select coupon
     */
    @FormUrlEncoded
    @POST("/public/select-coupon")
    fun selectCoupon(
            @Field("coupon_id") coupon_id: String,
            @Field("percentage") percentage: String,
            @Field("amount") amount: String,
            @Field("order_number") order_number: String,
            @Field("user_id") user_id: String
    ): Call<PrepareCartResponse>

    /**
     * This is the api for view cart
     */
    @FormUrlEncoded
    @POST("/public/view-cart")
    fun viewUserCart(
            @Field("quantity") quantity: String,
            @Field("item_id") item_id: String,
            @Field("order_number") order_number: String,
            @Field("user_id") user_id: String,
            @Field("order_id") order_id: String
    ): Call<PrepareCartResponse>

    /**
     * This is the api for selct restaurent and send data to the server
     */
    @FormUrlEncoded
    @POST("/public/select-restaurant")
    fun selectRestaurentApi(
            @Field("user_id") user_id: String,
            @Field("restaurant_id") restaurant_id: Int): Call<SelectRestaurantResponse>

    /**
     * This is the api for get user details for api
     */
    @GET("/public/profile/{user_id}")
    fun getProfileDataApi(@Path("user_id") user_id: String
    ): Call<ProfileResponeModel>

    /**
     * This is the api for  choose location and update user
     */

    @FormUrlEncoded
    @POST("/public/choose-location")
    fun updateUserLocation(
            @Field("user_id") user_id: String,
            @Field("address") address: String,
            @Field("contact") contact: String,
            @Field("note") note: String,
            @Field("name") name:String,
            @Field("email") email:String): Call<UpdateLocationResponseModel>

    @FormUrlEncoded
    @POST("/public/book-table")
    fun bookAnTable(
            @Field("user_id") user_id: String,
            @Field("restaurant_id") restaurant_id: String,
            @Field("person_no") party_id: String,
            @Field("date") date: String,
            @Field("time") time: String,
            @Field("name") name: String,
            @Field("phone") phone: String,
            @Field("email") email: String,
            @Field("description") description:String,
            @Field("lang") language:String): Call<SelectRestaurantResponse>

    @FormUrlEncoded
    @POST("/public/payment-order")
    fun afterPaymentSuccess(
            @Field("user_id") user_id: String,
            @Field("order_id") order_id: String,
            @Field("token") token: String,
            @Field("amount") amount: String): Call<SelectRestaurantResponse>

    //user_id, order_id, product_id, coupon_id, quantity, delivery_address, instructions, payment_status

    @FormUrlEncoded
    @POST("public/place-order")
    fun placeAnOrder(
            @Field("user_id") user_id: String,
            @Field("order_id") order_id: String,
            @Field("product_id") order_number: String,
            @Field("coupon_id") coupon_name: String,
            @Field("quantity") amount: String,
            @Field("delivery_address") delivery_address: String,
            @Field("instructions") instructins: String,
            @Field("payment_status") payment_status: String,
            @Field("order_from") order_from: String
            ): Call<PrepareCartResponse>

/*
@FormUrlEncoded
    @POST("public/place-order")
    fun placeAnOrder(
            @Field("user_id") user_id: String,
            @Field("order_id") order_id: String,
            @Field("order_number") order_number: String,
            @Field("coupon_name") coupon_name: String,
            @Field("amount") amount: String,
            @Field("delivery_address") delivery_address: String,
            @Field("instructins") instructins: String,
            @Field("coupon_percentage") coupon_percentage: String): Call<PrepareCartResponse>
*/


    @GET("/public/all-parties")
    fun allParties(): Call<AllRestaurantResponse>

    @FormUrlEncoded
    @POST("public/update-address")
    fun updateAddress(@Field("user_id") user_id: String,
                      @Field("address_id") address_id: String,
                      @Field("name") name: String,
                      @Field("address") address: String): Call<ProfileResponeModel>

    //    http://delhi.uniquecaryantra.com/public/address-delete/1
//    http://delhi.uniquecaryantra.com/public/profile/2
    @DELETE("public/address-delete/{user_id}")
    fun deleteAddress(@Path("user_id") id: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("public/add-address")
    fun getAddAdressApi(@Field("user_id") user_id: String,
                        @Field("name") name: String,
                        @Field("address") address: String): Call<ProfileResponeModel>

}
