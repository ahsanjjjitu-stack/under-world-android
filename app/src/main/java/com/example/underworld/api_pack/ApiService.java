package com.example.underworld.api_pack;

import com.example.underworld.auth_pack.LoginRequest;
import com.example.underworld.auth_pack.LoginResponse;
import com.example.underworld.cart_pack.AddToCartRequest;
import com.example.underworld.cart_pack.CartCountResponse;
import com.example.underworld.cart_pack.CartResponse;
import com.example.underworld.cart_pack.RemoveCartItemRequest;
import com.example.underworld.cart_pack.UpdateQuantityRequest;
import com.example.underworld.checkout_pack.OrderRequest;
import com.example.underworld.checkout_pack.OrderResponse;
import com.example.underworld.home_pack.HomeDataResponse;
import com.example.underworld.home_pack.ProductResponse;
import com.example.underworld.notification_pack.NotificationResponse;
import com.example.underworld.notification_pack.UnreadCountResponse;
import com.example.underworld.order_details_pack.OrderDetailsResponse;
import com.example.underworld.order_pack.OrderListResponse;
import com.example.underworld.profile_pack.NotificationSettingRequest;
import com.example.underworld.profile_pack.NotificationSettingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // login
    @POST("api/google-login")
    Call<LoginResponse> googleLogin(@Body LoginRequest request);



    // home data
    @GET("api/home-data")
    Call<HomeDataResponse> getHomeData();


    // home product
    @GET("api/home-product")
    Call<ProductResponse> getProducts(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("category") String category
    );



    // add cart
    @POST("api/cart-add")
    Call<CartResponse> addToCart(
            @Header("Authorization") String token,
            @Body AddToCartRequest request
    );



    // cart count get
    @GET("api/cart-count")
    Call<CartCountResponse> getCartCount(
            @Header("Authorization") String token
    );


    // cart item get
    @GET("api/cart")
    Call<CartResponse> getCart(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("limit") int limit
    );


    // cart quantity update
    @PUT("api/update-quantity")
    Call<CartResponse> updateCartQuantity(
            @Header("Authorization") String token,
            @Body UpdateQuantityRequest request
    );


    // delete cart
    @HTTP(method = "DELETE", path = "api/remove-item", hasBody = true)
    Call<CartResponse> removeCartItem(
            @Header("Authorization") String token,
            @Body RemoveCartItemRequest request
    );



    // add order
    @POST("api/place-order")
    Call<OrderResponse> placeOrder(
            @Header("Authorization") String token,
            @Body OrderRequest orderRequest
    );


    // unread notification count
    @GET("api/notifications/unread-count")
    Call<UnreadCountResponse> getUnreadCount(
            @Header("Authorization") String token
    );



    // get all notification
    @GET("api/notifications")
    Call<NotificationResponse> getNotifications(
            @Header("Authorization") String token,
            @Query("limit") int limit,
            @Query("lastCreatedAt") String lastCreatedAt
    );



    // notification read
    @PATCH("api/notifications/{id}/read")
    Call<okhttp3.ResponseBody> markNotificationAsRead(
            @Header("Authorization") String token,
            @Path("id") String notificationId
    );


    // notification delete
    @DELETE("api/notifications/{id}")
    Call<okhttp3.ResponseBody> deleteNotification(
            @Header("Authorization") String token,
            @Path("id") String notificationId
    );



    // get order details
    @GET("api/orders/{id}")
    Call<OrderDetailsResponse> getOrderDetails(
            @Header("Authorization") String token,
            @Path("id") String orderId
    );


    // get user order
    @GET("api/orders/my-orders")
    Call<OrderListResponse> getUserOrders(
            @Header("Authorization") String token,
            @Query("limit") int limit,
            @Query("lastCreatedAt") String lastCreatedAt
    );




    // notification switch on/off
    @PUT("api/update-notifications")
    Call<NotificationSettingResponse> updateNotificationSettings(
            @Header("Authorization") String token,
            @Body NotificationSettingRequest request
    );


}
