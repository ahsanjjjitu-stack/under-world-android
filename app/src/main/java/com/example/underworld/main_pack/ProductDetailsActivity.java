package com.example.underworld.main_pack;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.underworld.R;
import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;
import com.example.underworld.auth_pack.LoginActivity;
import com.example.underworld.cart_pack.AddToCartRequest;
import com.example.underworld.cart_pack.CartColourModel;
import com.example.underworld.cart_pack.CartResponse;
import com.example.underworld.details_pack.ColourAdapter;
import com.example.underworld.details_pack.DetailsImageAdapter;
import com.example.underworld.details_pack.SizeAdapter;
import com.example.underworld.home_pack.ColourModel;
import com.example.underworld.home_pack.ProductModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    // UI Variables
    private ViewPager2 viewPager;
    private LinearLayout dotIndicator;
    private TextView tvName, tvCategory, tvPrice, tvDescription, tvColourName, tvQty, btnAddToBag, tvTopCartBadge;
    private ImageButton btnMinus, btnPlus;
    private ImageView btnTopReview, btnBack;
    private androidx.recyclerview.widget.RecyclerView rvColours, rvSizes;

    // Adapters
    private DetailsImageAdapter imageAdapter;
    private ColourAdapter colourAdapter;
    private SizeAdapter sizeAdapter;

    // Data & State
    private ProductModel product;
    private ColourModel selectedColour;
    private String selectedSize = "";
    private int quantity = 1;
    private View[] dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);



        btnBack = findViewById(R.id.btnBack);
        btnTopReview = findViewById(R.id.btnTopReview);
        tvTopCartBadge = findViewById(R.id.tvTopCartBadge);

        viewPager = findViewById(R.id.viewPager);
        dotIndicator = findViewById(R.id.dotIndicator);

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvColourName = findViewById(R.id.tvColourName);

        rvColours = findViewById(R.id.rvColours);
        rvSizes = findViewById(R.id.rvSizes);

        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        tvQty = findViewById(R.id.tvQty);
        btnAddToBag = findViewById(R.id.btnAddToBag);

        // Adapters Setup
        imageAdapter = new DetailsImageAdapter(this);
        viewPager.setAdapter(imageAdapter);

        rvColours.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        rvSizes.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 4));















        // 2. Get Product from Intent
        String productJson = getIntent().getStringExtra("product_data");
        if (productJson != null) {
            product = new Gson().fromJson(productJson, ProductModel.class);
            bindProductData();
        } else {
            Toast.makeText(this, "Product details not found!", Toast.LENGTH_SHORT).show();
            finish();
        }






        // 3. Back button action
        btnBack.setOnClickListener(v -> finish());

        btnTopReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonDialog();
            }
        });




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
    }
















    private void bindProductData() {
        if (product == null) return;

        tvName.setText(product.getName());
        tvCategory.setText(product.getCategory());
        tvPrice.setText(product.getCurrency() + " " + product.getPrice());
        tvDescription.setText(product.getDescription());

        if (product.getColours() != null && !product.getColours().isEmpty()) {
            selectedColour = product.getColours().get(0);
            updateColourSelection(selectedColour);

            colourAdapter = new ColourAdapter(this, (colour, position) -> {
                selectedColour = colour;
                updateColourSelection(selectedColour);
            });
            rvColours.setAdapter(colourAdapter);
            colourAdapter.setColours(product.getColours());
        }

        // Stepper Logic
        btnPlus.setOnClickListener(v -> {
            quantity++;
            updateQuantityAndPrice();
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityAndPrice();
            }
        });

        updateQuantityAndPrice();

        // Add to Bag Button Click
        btnAddToBag.setOnClickListener(v -> {

            if (selectedColour == null || selectedSize.isEmpty()) {
                Toast.makeText(this, "Please select colour and size!", Toast.LENGTH_SHORT).show();
                return;
            }

            android.content.SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            String token = preferences.getString("JWT_TOKEN", "");

            if (token.isEmpty()) {
                Toast.makeText(this, "Please login first to add items to cart!", Toast.LENGTH_LONG).show();

                android.content.Intent intent = new android.content.Intent(ProductDetailsActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }





            // Bearer token formatting
            String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;


            CartColourModel cartColour = new CartColourModel(
                    selectedColour.getName(),
                    selectedColour.getHex(),
                    selectedColour.getImages() != null ? selectedColour.getImages().getFront() : product.getThumbnail()
            );

            AddToCartRequest request = new AddToCartRequest(
                    product.get_id(),
                    cartColour,
                    selectedSize,
                    quantity
            );




            btnAddToBag.setEnabled(false);
            btnAddToBag.setText("Adding...");





            // 3. Retrofit API Call
            ApiService apiService = RetrofitClient.getInstance().getApi();


            apiService.addToCart(authToken, request).enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    btnAddToBag.setEnabled(true);
                    updateQuantityAndPrice();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(ProductDetailsActivity.this, "Successfully added to cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = "Failed to add to cart";
                        try {
                            if (response.errorBody() != null) {
                                errorMsg = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ProductDetailsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {

                }
            });



        });
    }
















    private void updateColourSelection(ColourModel colour) {
        if (colour == null) return;

        // Set Colour Name Text
        tvColourName.setText(colour.getName());

        // Extract Images (front, back, folded) for ViewPager
        List<String> images = new ArrayList<>();
        if (colour.getImages() != null) {
            if (colour.getImages().getFront() != null) images.add(colour.getImages().getFront());
            if (colour.getImages().getBack() != null) images.add(colour.getImages().getBack());
            if (colour.getImages().getFolded() != null) images.add(colour.getImages().getFolded());
        }
        imageAdapter.setImages(images);
        setupDotIndicators(images.size());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });

        if (colour.getSizes() != null && !colour.getSizes().isEmpty()) {
            selectedSize = colour.getSizes().get(0);

            sizeAdapter = new SizeAdapter(this, (size, position) -> {
                selectedSize = size;
            });
            rvSizes.setAdapter(sizeAdapter);
            sizeAdapter.setSizes(colour.getSizes());
        }
    }









    private void updateQuantityAndPrice() {
        tvQty.setText(String.valueOf(quantity));
        double totalPrice = product.getPrice() * quantity;
        btnAddToBag.setText("Add to cart: " + product.getCurrency() + " " + (int)totalPrice);
    }












    // Dot Indicators Management
    private void setupDotIndicators(int count) {
        dotIndicator.removeAllViews();
        if (count <= 1) return;
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
        if (dots == null || dots.length <= 1) return;
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













    private void showComingSoonDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_coming_soon);

        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.75f);

            android.view.WindowManager.LayoutParams params = window.getAttributes();
            params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = android.view.Gravity.CENTER;

            params.y = -dpToPx(50);

            window.setAttributes(params);
        }

        TextView btnGotIt = dialog.findViewById(R.id.btnGotIt);
        btnGotIt.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }





}