package com.example.underworld.main_pack;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;
import com.example.underworld.SocketManager;
import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;
import com.example.underworld.auth_pack.LoginActivity;
import com.example.underworld.cart_pack.AddToCartRequest;
import com.example.underworld.cart_pack.CartAdapter;
import com.example.underworld.cart_pack.CartColourModel;
import com.example.underworld.cart_pack.CartCountResponse;
import com.example.underworld.cart_pack.CartItemModel;
import com.example.underworld.cart_pack.CartResponse;
import com.example.underworld.cart_pack.CartViewModel;
import com.example.underworld.cart_pack.RemoveCartItemRequest;
import com.example.underworld.cart_pack.UpdateQuantityRequest;
import com.example.underworld.home_pack.BannerAdapter;
import com.example.underworld.home_pack.BannerModel;
import com.example.underworld.home_pack.CategoryAdapter;
import com.example.underworld.home_pack.CategoryModel;
import com.example.underworld.home_pack.HomeViewModel;
import com.example.underworld.home_pack.ProductAdapter;
import com.example.underworld.home_pack.ProductModel;
import com.example.underworld.home_pack.ProductViewModel;
import com.example.underworld.notification_pack.UnreadCountResponse;
import com.example.underworld.profile_pack.NotificationSettingRequest;
import com.example.underworld.profile_pack.NotificationSettingResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import io.socket.client.Socket;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    // *********************** bottom nav variable **************************
    private static final int COLOR_ACTIVE = Color.parseColor("#FF6B3D");
    private static final int COLOR_INACTIVE = Color.parseColor("#8A90A0");

    private View cartLayout, profileLayout;
    private View navHome, navCart, navProfile;
    private ImageView icHome, icCart, icProfile;
    private TextView tvHome, tvCart, tvProfile;





    // ********************************** home all variable *************************
    private androidx.viewpager2.widget.ViewPager2 bannerPager;
    private LinearLayout dotIndicator;
    private View[] dots;
    private HomeViewModel homeViewModel;
    private BannerAdapter bannerAdapter;
    private RecyclerView rvTypes, rvPopular;
    private CategoryAdapter categoryAdapter;
    private ProgressBar paginationProgress;
    private NestedScrollView homeLayout;

    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;
    private String selectedCategory = "ALL";
    private TextView tvItemCount;









    // ******************* cart all variable *******************************
    private TextView tvNavCartBadge;
    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private com.example.underworld.cart_pack.CartViewModel cartViewModel;

    private TextView tvCartCount, tvSubtotal, tvDelivery, tvTotal;
    private View layoutEmptyCart, layoutSummary;
    private NestedScrollView cartNested;

    private int cartCurrentPage = 1;
    private int cartTotalPages = 1;
    private boolean isCartLoading = false;
    private List<CartItemModel> fullCartItemList = new ArrayList<>();

    private final android.os.Handler quantityHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable quantityRunnable;









    // notification variable

    private Socket mSocket;
    private TextView tvTopNotificationBadge;
    private ImageView btnTopNotification;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);




        // ********************************************** bottom nav all code ***************************************

        homeLayout = findViewById(R.id.homeLayout);
        cartLayout = findViewById(R.id.cartLayout);
        profileLayout = findViewById(R.id.profileLayout);

        navHome = findViewById(R.id.navHome);
        navCart = findViewById(R.id.navCart);
        navProfile = findViewById(R.id.navProfile);

        icHome = findViewById(R.id.icHome);
        icCart = findViewById(R.id.icCart);
        icProfile = findViewById(R.id.icProfile);

        tvHome = findViewById(R.id.tvHome);
        tvCart = findViewById(R.id.tvCart);
        tvProfile = findViewById(R.id.tvProfile);
        tvNavCartBadge = findViewById(R.id.tvNavCartBadge);

















        // ********************************************* notification socket code *********************************

        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", "");



        SocketManager.connectSocket(userId);
        mSocket = SocketManager.getSocket();




        if (mSocket != null) {
            mSocket.on("new_notification", onNewNotification);
        }



        btnTopNotification = findViewById(R.id.btnTopNotification);
        tvTopNotificationBadge = findViewById(R.id.tvTopNotificationBadge);

        unreadNotificationCount();

        btnTopNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });














        // ************************************************* bottom nav code ***************************************************

        navHome.setOnClickListener(v -> showTab(0));
        navCart.setOnClickListener(v -> showTab(1));
        navProfile.setOnClickListener(v -> showTab(2));

























        // ********************************************** home all code ************************************


        bannerPager = findViewById(R.id.bannerPager);
        dotIndicator = findViewById(R.id.dotIndicator);
        rvTypes = findViewById(R.id.rvTypes);
        rvPopular = findViewById(R.id.rvPopular);
        tvItemCount = findViewById(R.id.tvItemCount);
        paginationProgress = findViewById(R.id.paginationProgress);
        homeLayout = findViewById(R.id.homeLayout);



        bannerAdapter = new BannerAdapter(this, new ArrayList<>(), banner ->  {
            // banner click
        });
        bannerPager.setAdapter(bannerAdapter);




        homeViewModel = new androidx.lifecycle.ViewModelProvider(this).get(HomeViewModel.class);


        homeViewModel.getHomeData().observe(this, response -> {
            if (response != null && response.isSuccess() && response.getData() != null) {
                java.util.List<BannerModel> banners = response.getData().getBanners();
                if (banners != null && !banners.isEmpty()) {
                    bannerAdapter.updateData(banners);
                    setupDotIndicators(banners.size());
                }

                List<CategoryModel> categories = response.getData().getCategories();
                if (categories != null) {
                    android.util.Log.d("CategoryDebug", "Server Categories Count: " + categories.size());
                    categoryAdapter.setCategories(categories);
                } else {
                    android.util.Log.d("CategoryDebug", "Categories is NULL from server!");
                }

            }
        });




        bannerPager.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });
















        // product get ************************************************

        rvPopular.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        rvPopular.setItemAnimator(null);



        productAdapter = new ProductAdapter(this, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onItemClick(ProductModel product, int position) {
                String productJson = new Gson().toJson(product);
                android.content.Intent intent = new android.content.Intent(MainActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product_data", productJson);
                startActivity(intent);
            }

            @Override
            public void onAddClick(ProductModel product, int position) {
               addToCart(product, position);
            }
        });



        rvPopular.setAdapter(productAdapter);

        productViewModel = new androidx.lifecycle.ViewModelProvider(this).get(ProductViewModel.class);

        loadProducts(currentPage, selectedCategory, false);

        categoryAdapter = new CategoryAdapter(this, (category, position) -> {
            selectedCategory = category.getId().equals("ALL") ? "ALL" : category.getTitle();
            currentPage = 1;

            loadProducts(currentPage, selectedCategory, false);
        });
        rvTypes.setAdapter(categoryAdapter);





        homeLayout.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                if (!isLoading && currentPage < totalPages) {
                    currentPage++;
                    isLoading = true;
                    loadProducts(currentPage, selectedCategory, true);
                }
            }
        });




























        // ****************************************************** cart all code ************************************





        rvCart = findViewById(R.id.rvCart);
        tvCartCount = findViewById(R.id.tvCartCount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvTotal = findViewById(R.id.tvTotal);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutSummary = findViewById(R.id.layoutSummary);
        cartNested = findViewById(R.id.cartNested);

        rvCart.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        rvCart.setItemAnimator(null);




        cartAdapter = new CartAdapter(this, new CartAdapter.OnCartActionListener() {
            @Override
            public void onIncreaseClick(CartItemModel item, int position) {
                updateItemQuantity(item, item.getQuantity() + 1);
            }

            @Override
            public void onDecreaseClick(CartItemModel item, int position) {
                if (item.getQuantity() > 1) {
                    updateItemQuantity(item, item.getQuantity() - 1);
                } else {
                    removeItemFromCart(item);
                }
            }

            @Override
            public void onRemoveClick(CartItemModel item, int position) {
                removeItemFromCart(item);
            }
        });





        rvCart.setAdapter(cartAdapter);

        cartViewModel = new androidx.lifecycle.ViewModelProvider(this).get(CartViewModel.class);


        // NestedScrollView Pagination Scroll Listener
        cartNested.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                if (!isCartLoading && cartCurrentPage < cartTotalPages) {
                    cartCurrentPage++;
                    isCartLoading = true;
                    loadUserCart(cartCurrentPage, true);
                }
            }
        });



        findViewById(R.id.btnStartShopping).setOnClickListener(v -> showTab(0));
        TextView btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> {
            double subtotal = 0;
            for (CartItemModel item : fullCartItemList) {
                subtotal += item.getPrice() * item.getQuantity();
            }
            double delivery = 60.0;
            double total = subtotal + delivery;

            Intent intent = new Intent(MainActivity.this, CheckOutActivity.class);
            intent.putExtra("cart_items", new ArrayList<>(fullCartItemList));
            intent.putExtra("subtotal", subtotal);
            intent.putExtra("delivery", delivery);
            intent.putExtra("total", total);
            startActivity(intent);
        });






























        // ********************************************** profile all code **********************************************


        TextView tvProfileName = findViewById(R.id.tvProfileName);
        TextView btnLogout = findViewById(R.id.btnLogout);
        LinearLayout rowOrders = findViewById(R.id.rowOrders);

        LinearLayout rowNotifications = findViewById(R.id.rowNotifications);
        SwitchCompat switchNotifications = findViewById(R.id.switchNotifications);




        String user_name = prefs.getString("USER_NAME", "");

        if (!"".equals(user_name)){
            tvProfileName.setText(user_name);
        }
        else {
            tvProfileName.setText("Guest");
        }


        rowOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
            }
        });







        switchNotifications.setClickable(false);
        switchNotifications.setFocusable(false);



        String token = prefs.getString("JWT_TOKEN", "");
        boolean isLoggedIn = (token != null && !token.isEmpty());

        if (!isLoggedIn) {
            switchNotifications.setChecked(false);
            btnLogout.setVisibility(View.GONE);
        } else {
            boolean isNotifEnabled = prefs.getBoolean("PUSH_NOTIF_ENABLED", true);
            switchNotifications.setChecked(isNotifEnabled);
            btnLogout.setVisibility(View.VISIBLE);
        }





        rowNotifications.setOnClickListener(v -> {

            String currentToken = prefs.getString("JWT_TOKEN", "");
            if (currentToken == null || currentToken.isEmpty()) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return;
            }

            boolean targetState = !switchNotifications.isChecked();

            updateNotificationSettingOnServer(currentToken, targetState, switchNotifications, prefs);
        });






        btnLogout.setOnClickListener(v -> showLogoutDialog());






    }



    // ******************************************* status bar code *************************************
    private void setSystemBarColors() {
        getWindow().setStatusBarColor(Color.parseColor("#0D1017"));
        getWindow().setNavigationBarColor(Color.parseColor("#0D1017"));

        getWindow().getDecorView().setSystemUiVisibility(0);
    }



    @Override
    protected void onResume() {
        super.onResume();
        setSystemBarColors();
        fetchCartCount();
        unreadNotificationCount();
    }













    // ***************************** notification method ********************************************



    private final io.socket.emitter.Emitter.Listener onNewNotification = args -> {
        if (args.length > 0) {
            try {
                JSONObject notificationObj = (JSONObject) args[0];
                String title = notificationObj.getString("title");
                String body = notificationObj.getString("body");

                runOnUiThread(() -> {
                    unreadNotificationCount();
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };








    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("new_notification", onNewNotification);
        }
    }












    // unread notification count

    private void unreadNotificationCount() {

        android.content.SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = preferences.getString("JWT_TOKEN", "");


        if (token.isEmpty()) {
            tvTopNotificationBadge.setVisibility(View.GONE);
            return;
        }


        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;


        ApiService apiService = RetrofitClient.getInstance().getApi();

        apiService.getUnreadCount(authToken).enqueue(new Callback<UnreadCountResponse>() {
            @Override
            public void onResponse(Call<UnreadCountResponse> call, Response<UnreadCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().getUnreadCount();
                    if (count > 9) {
                        tvTopNotificationBadge.setVisibility(View.VISIBLE);
                        tvTopNotificationBadge.setText("9+");
                    }
                    else if (count <= 0) {
                        tvTopNotificationBadge.setVisibility(View.GONE);
                    }

                    else {
                        tvTopNotificationBadge.setVisibility(View.VISIBLE);
                        tvTopNotificationBadge.setText(String.valueOf(count));
                    }

                } else {
                    tvTopNotificationBadge.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<UnreadCountResponse> call, Throwable t) {
                tvTopNotificationBadge.setVisibility(View.GONE);

            }
        });



    }











    private void fetchCartCount() {
        android.content.SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = preferences.getString("JWT_TOKEN", "");


        if (token.isEmpty()) {
            tvNavCartBadge.setVisibility(View.GONE);
            return;
        }

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        ApiService apiService = RetrofitClient.getInstance().getApi();

        apiService.getCartCount(authToken).enqueue(new Callback<CartCountResponse>() {
            @Override
            public void onResponse(Call<CartCountResponse> call, Response<CartCountResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int totalCount = response.body().getCount();

                    if (totalCount > 0) {
                        tvNavCartBadge.setVisibility(View.VISIBLE);
                        tvNavCartBadge.setText(String.valueOf(totalCount));
                    } else {
                        tvNavCartBadge.setVisibility(View.GONE);
                    }
                } else {
                    tvNavCartBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CartCountResponse> call, Throwable t) {
                tvNavCartBadge.setVisibility(View.GONE);
            }
        });
    }














    // ***************************************************** bottom nav all method *************************************


    private void showTab(int index) {
        homeLayout.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        cartLayout.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        profileLayout.setVisibility(index == 2 ? View.VISIBLE : View.GONE);

        setNavState(icHome, tvHome, index == 0);
        setNavState(icCart, tvCart, index == 1);
        setNavState(icProfile, tvProfile, index == 2);

        // 🚀 User cart tab-e gele automatic first page load hobe
        if (index == 1) {
            cartCurrentPage = 1;
            loadUserCart(1, false);
        }

    }






    private void setNavState(ImageView icon, TextView label, boolean active) {
        int color = active ? COLOR_ACTIVE : COLOR_INACTIVE;
        ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(color));
        label.setTextColor(color);
    }
















    // ******************************************** home all method **************************************


    private void setupDotIndicators(int count) {
        dotIndicator.removeAllViews();
        dots = new View[count];

        for (int i = 0; i < count; i++) {
            dots[i] = new View(this);
            LinearLayout.LayoutParams params;
            if (i == 0) {
                params = new LinearLayout.LayoutParams(dpToPx(22), dpToPx(6));
                dots[i].setBackgroundResource(R.drawable.dot_active);
            } else {
                params = new LinearLayout.LayoutParams(dpToPx(6), dpToPx(6));
                params.setMargins(dpToPx(8), 0, 0, 0);
                dots[i].setBackgroundResource(R.drawable.dot_inactive);
            }
            dots[i].setLayoutParams(params);
            dotIndicator.addView(dots[i]);
        }
    }






    private void updateDots(int activePosition) {
        if (dots == null) return;
        for (int i = 0; i < dots.length; i++) {
            LinearLayout.LayoutParams params;
            if (i == activePosition) {
                params = new LinearLayout.LayoutParams(dpToPx(22), dpToPx(6));
                if (i > 0) params.setMargins(dpToPx(8), 0, 0, 0);
                dots[i].setLayoutParams(params);
                dots[i].setBackgroundResource(R.drawable.dot_active);
            } else {
                params = new LinearLayout.LayoutParams(dpToPx(6), dpToPx(6));
                if (i > 0) params.setMargins(dpToPx(8), 0, 0, 0);
                dots[i].setLayoutParams(params);
                dots[i].setBackgroundResource(R.drawable.dot_inactive);
            }
        }
    }




    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }










    private void loadProducts(int page, String category, boolean showProgress) {
        if (showProgress) {
            paginationProgress.setVisibility(View.VISIBLE);
        } else {
            paginationProgress.setVisibility(View.GONE);
        }

        String catQuery = category.equals("ALL") ? null : category;

        productViewModel.fetchProducts(page, 10, catQuery).observe(this, response -> {
            isLoading = false;
            paginationProgress.setVisibility(View.GONE);
            if (response != null && response.isSuccess()) {
                totalPages = response.getTotalPages();
                tvItemCount.setText(response.getTotalProducts() + " items");
                if (page == 1) {
                    productAdapter.setProducts(response.getData());
                } else {
                    productAdapter.addProducts(response.getData());
                }
            }
        });
    }
























    // ******************************************************* cart all method **************************************

    private void addToCart(ProductModel product, int position) {

        android.content.SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = preferences.getString("JWT_TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please login first to add items to cart!", Toast.LENGTH_LONG).show();
            android.content.Intent intent = new android.content.Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }



        if (product.getColours() == null || product.getColours().isEmpty()) {
            Toast.makeText(MainActivity.this, "Product variants not available!", Toast.LENGTH_SHORT).show();
            return;
        }




        com.example.underworld.home_pack.ColourModel defaultColour = product.getColours().get(0);
        if (defaultColour.getSizes() == null || defaultColour.getSizes().isEmpty()) {
            Toast.makeText(MainActivity.this, "Product sizes not available!", Toast.LENGTH_SHORT).show();
            return;
        }
        String defaultSize = defaultColour.getSizes().get(0);

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;



        CartColourModel cartColour = new CartColourModel(
                defaultColour.getName(),
                defaultColour.getHex(),
                defaultColour.getImages() != null && defaultColour.getImages().getFront() != null ?
                        defaultColour.getImages().getFront() : product.getThumbnail()
        );

        AddToCartRequest request = new AddToCartRequest(
                product.get_id(),
                cartColour,
                defaultSize,
                1
        );



        ApiService apiService = RetrofitClient.getInstance().getApi();

        apiService.addToCart(authToken, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(MainActivity.this, "Added to cart: " + product.getName(), Toast.LENGTH_SHORT).show();

                    fetchCartCount();
                } else {
                    String errorMsg = "Failed to add to cart";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }

















    private void loadUserCart(int page, boolean isPagination) {
        android.content.SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = preferences.getString("JWT_TOKEN", "");

        if (token.isEmpty()) {
            showEmptyCartUI();
            return;
        }

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        cartViewModel.fetchCart(authToken, page, 10).observe(this, response -> {
            isCartLoading = false;
            if (response != null && response.isSuccess() && response.getData() != null) {
                cartTotalPages = response.getData().getTotalPages();
                List<CartItemModel> fetchedItems = response.getData().getItems();
                fullCartItemList.addAll(fetchedItems);

                if (page == 1) {
                    fullCartItemList.clear();
                    if (fetchedItems != null) {
                        fullCartItemList.addAll(fetchedItems);
                    }
                } else {
                    if (fetchedItems != null) {
                        fullCartItemList.addAll(fetchedItems);
                    }
                }

                if (fullCartItemList.isEmpty()) {
                    showEmptyCartUI();
                } else {
                    showCartDataUI(fullCartItemList, response.getData().getTotalAmount());
                }

            } else {
                if (page == 1) {
                    showEmptyCartUI();
                }
            }
        });
    }
















    private void showEmptyCartUI() {
        layoutEmptyCart.setVisibility(View.VISIBLE);
        layoutSummary.setVisibility(View.GONE);
        rvCart.setVisibility(View.GONE);
        tvCartCount.setText("0 items");
        fetchCartCount();
    }





















    private void showCartDataUI(List<CartItemModel> items, double totalAmount) {
        layoutEmptyCart.setVisibility(View.GONE);
        layoutSummary.setVisibility(View.VISIBLE);
        rvCart.setVisibility(View.VISIBLE);

        cartAdapter.setCartItems(items);
        tvCartCount.setText(items.size() + " items");

        double deliveryCharge = 60.0;
        tvSubtotal.setText("BDT " + (int) totalAmount);
        tvDelivery.setText("BDT " + (int) deliveryCharge);
        tvTotal.setText("BDT " + (int) (totalAmount + deliveryCharge));

        fetchCartCount();
    }










    private void updateItemQuantity(CartItemModel item, int newQty) {

        item.setQuantity(newQty);
        cartAdapter.notifyDataSetChanged();

        recalculateLocalCartTotals();

        if (quantityRunnable != null) {
            quantityHandler.removeCallbacks(quantityRunnable);
        }

        quantityRunnable = () -> {
            sendQuantityUpdateToBackend(item, newQty);
        };

        quantityHandler.postDelayed(quantityRunnable, 500);



    }
















    private void recalculateLocalCartTotals() {
        double totalAmount = 0;
        for (CartItemModel cartItem : fullCartItemList) {
            totalAmount += (cartItem.getPrice() * cartItem.getQuantity());
        }

        double deliveryCharge = 60.0;
        tvSubtotal.setText("BDT " + (int) totalAmount);
        tvTotal.setText("BDT " + (int) (totalAmount + deliveryCharge));
    }

























    private void sendQuantityUpdateToBackend(CartItemModel item, int finalQty) {
        android.content.SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = preferences.getString("JWT_TOKEN", "");

        if (token.isEmpty()) return;

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        // Prepare request body
        UpdateQuantityRequest request =
                new UpdateQuantityRequest(
                        item.getProductId(),
                        item.getSelectedColour() != null ? item.getSelectedColour().getName() : "",
                        item.getSelectedSize(),
                        finalQty
                );

        ApiService apiService = RetrofitClient.getInstance().getApi();

        apiService.updateCartQuantity(authToken, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    fetchCartCount();
                } else {
                    String errorMsg = "Unknown Error";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 🔍 Logcat-e exact error print korbe
                    android.util.Log.e("CartUpdateError", "Code: " + response.code() + " | Message: " + errorMsg);

                    loadUserCart(cartCurrentPage, false);
                    Toast.makeText(MainActivity.this, "Failed to update quantity: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {

                loadUserCart(cartCurrentPage, false);
                Toast.makeText(MainActivity.this, "Network error during quantity update", Toast.LENGTH_SHORT).show();
            }
        });


    }























    private void removeItemFromCart(CartItemModel item) {
        int position = fullCartItemList.indexOf(item);
        if (position != -1) {
            fullCartItemList.remove(position);
            cartAdapter.notifyItemRemoved(position);
            cartAdapter.notifyItemRangeChanged(position, fullCartItemList.size());

            recalculateLocalCartTotals();

            if (fullCartItemList.isEmpty()) {
                showEmptyCartUI();
            } else {
                tvCartCount.setText(fullCartItemList.size() + " items");
            }
        }

        // 2. Background API Call to delete from Database using $pull
        android.content.SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = preferences.getString("JWT_TOKEN", "");

        if (token.isEmpty()) return;

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        RemoveCartItemRequest request =
                new RemoveCartItemRequest(
                        item.getProductId(),
                        item.getSelectedColour() != null ? item.getSelectedColour().getName() : "",
                        item.getSelectedSize()
                );

        ApiService apiService = RetrofitClient.getInstance().getApi();

        apiService.removeCartItem(authToken, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    fetchCartCount();
                } else {
                    loadUserCart(cartCurrentPage, false);
                    Toast.makeText(MainActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                loadUserCart(cartCurrentPage, false);
                Toast.makeText(MainActivity.this, "Network error during item removal", Toast.LENGTH_SHORT).show();
            }
        });
    }







    private void updateNotificationSettingOnServer(String token, boolean targetState, SwitchCompat switchView, SharedPreferences prefs) {
        NotificationSettingRequest request = new NotificationSettingRequest(targetState);

        ApiService apiService = RetrofitClient.getInstance().getApi();

        apiService.updateNotificationSettings("Bearer " + token, request).enqueue(new Callback<NotificationSettingResponse>() {
            @Override
            public void onResponse(Call<NotificationSettingResponse> call, Response<NotificationSettingResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    NotificationSettingResponse resBody = response.body();

                    if (resBody.isSuccess()) {
                        boolean confirmedState = resBody.isPushNotificationsEnabled();
                        switchView.setChecked(confirmedState);

                        prefs.edit().putBoolean("PUSH_NOTIF_ENABLED", confirmedState).apply();

                        Toast.makeText(getApplicationContext(), "Notification settings updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to update settings", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        android.util.Log.e(TAG, "❌ API Unsuccessful! Code: " + response.code() + " | Error Body: " + errorBody);
                    } catch (Exception e) {
                        android.util.Log.e(TAG, "❌ Error parsing error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationSettingResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error! Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }





















    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.logout_dialog, null);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView btnCancel = dialogView.findViewById(R.id.btnCancelLogout);
        TextView btnConfirm = dialogView.findViewById(R.id.btnConfirmLogout);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();

            SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }






}