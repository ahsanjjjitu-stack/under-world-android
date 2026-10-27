package com.example.underworld.main_pack;

import static com.google.android.gms.common.util.DeviceProperties.isPhone;
import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;
import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;
import com.example.underworld.cart_pack.CartItemModel;
import com.example.underworld.checkout_pack.OrderLineAdapter;
import com.example.underworld.checkout_pack.OrderRequest;
import com.example.underworld.checkout_pack.OrderResponse;
import com.example.underworld.checkout_pack.PaymentInfo;
import com.example.underworld.checkout_pack.ShippingAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends AppCompatActivity {
    private int step = 1;
    private String method = "cod";

    private View stepAddress, stepPayment, stepReview;
    private TextView btnContinue;

    private EditText etName, etPhone, etDistrict, etArea, etAddress, etSender, etTrx;
    private TextView tvNameError, tvPhoneError, tvDistrictError, tvAreaError, tvAddressError, tvSenderError, tvTrxError;

    private View optCod, optBkash, optNagad, panelCod, panelMfs;
    private TextView tvMfsSteps, tvMfsNumber;

    // Your personal numbers (customers send money here)
    private static final String BKASH_NUMBER = "01XXXXXXXXX";
    private static final String NAGAD_NUMBER = "01XXXXXXXXX";
    private double totalAmount , subtotal, delivery;
    ArrayList<CartItemModel> cartItems;

    // Review step
    private TextView tvRecapAddress, tvRecapPay;
    private RecyclerView rvOrderLines;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);



        cartItems = (ArrayList<CartItemModel>) getIntent().getSerializableExtra("cart_items");
        subtotal = getIntent().getDoubleExtra("subtotal", 0);
        delivery = getIntent().getDoubleExtra("delivery", 60.0);
        totalAmount = getIntent().getDoubleExtra("total", 0);



        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        if (subtotal == 0) {
            for (CartItemModel item : cartItems) {
                subtotal += item.getPrice() * item.getQuantity();
            }
        }
        if (totalAmount == 0) {
            totalAmount = subtotal + delivery;
        }



        // ****************************************** all id find *****************************************

        stepAddress = findViewById(R.id.stepAddress);
        stepPayment = findViewById(R.id.stepPayment);
        stepReview = findViewById(R.id.stepReview);
        btnContinue = findViewById(R.id.btnContinue);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDistrict = findViewById(R.id.actDistrict);
        etArea = findViewById(R.id.etArea);
        etAddress = findViewById(R.id.etAddress);
        etSender = findViewById(R.id.etSender);
        etTrx = findViewById(R.id.etTrx);

        tvNameError = findViewById(R.id.tvNameError);
        tvPhoneError = findViewById(R.id.tvPhoneError);
        tvDistrictError = findViewById(R.id.tvDistrictError);
        tvAreaError = findViewById(R.id.tvAreaError);
        tvAddressError = findViewById(R.id.tvAddressError);
        tvSenderError = findViewById(R.id.tvSenderError);
        tvTrxError = findViewById(R.id.tvTrxError);

        optCod = findViewById(R.id.optCod);
        optBkash = findViewById(R.id.optBkash);
        optNagad = findViewById(R.id.optNagad);
        panelCod = findViewById(R.id.panelCod);
        panelMfs = findViewById(R.id.panelMfs);
        tvMfsSteps = findViewById(R.id.tvMfsSteps);
        tvMfsNumber = findViewById(R.id.tvMfsNumber);
        tvRecapAddress = findViewById(R.id.tvRecapAddress);
        tvRecapPay = findViewById(R.id.tvRecapPay);
        rvOrderLines = findViewById(R.id.rvOrderLines);

        // Review step: Edit buttons
        findViewById(R.id.btnEditAddress).setOnClickListener(v -> {
            step = 1;
            showStep();
        });
        findViewById(R.id.btnEditPay).setOnClickListener(v -> {
            step = 2;
            showStep();
        });


        // Copy button: copies the bKash / Nagad number
        findViewById(R.id.btnCopy).setOnClickListener(v -> copyNumber());







        // payment options
        optCod.setOnClickListener(v -> selectMethod("cod"));
        optBkash.setOnClickListener(v -> selectMethod("bkash"));
        optNagad.setOnClickListener(v -> selectMethod("nagad"));
        selectMethod("cod");










        // Continue button
        btnContinue.setOnClickListener(v -> onContinue());

        // Back button (top left arrow)
        findViewById(R.id.btnBack).setOnClickListener(v -> goBack());











        // Phone back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBack();
            }
        });

        showStep();























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











    private void selectMethod(String m) {
        method = m;
        optCod.setSelected(m.equals("cod"));
        optBkash.setSelected(m.equals("bkash"));
        optNagad.setSelected(m.equals("nagad"));
        panelCod.setVisibility(m.equals("cod") ? View.VISIBLE : View.GONE);
        panelMfs.setVisibility(m.equals("cod") ? View.GONE : View.VISIBLE);

        // change the texts for bKash / Nagad
        if (!m.equals("cod")) {
            boolean bkash = m.equals("bkash");
            String name = bkash ? "bKash" : "Nagad";
            // Fix: totalAmount-ke (long)-e cast kora hoyeche jate %,d thik moto kaj kore
            String amount = totalAmount > 0 ? "৳" + String.format(java.util.Locale.US, "%,d", (long) totalAmount) : "the total amount";

            tvMfsSteps.setText(HtmlCompat.fromHtml(
                    "1. Open the <b>" + name + "</b> app and choose <b>Send Money</b>.<br>"
                            + "2. Send <b>" + amount + "</b> to the number below.<br>"
                            + "3. Type your sending number and the TrxID here.",
                    HtmlCompat.FROM_HTML_MODE_LEGACY));
            tvMfsNumber.setText(bkash ? BKASH_NUMBER : NAGAD_NUMBER);
        }
        else {
            // Fix: totalAmount-ke (long)-e cast kora hoyeche
            String amount = totalAmount > 0 ? "৳" + String.format(java.util.Locale.US, "%,d", (long) totalAmount) : "in";

            // Cash on delivery
            ((TextView) panelCod).setText(HtmlCompat.fromHtml(
                    "You will pay <b>" + amount + "</b> cash to the delivery person. We confirm the order first.",
                    HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }




    private void copyNumber() {
        String number = method.equals("bkash") ? BKASH_NUMBER : NAGAD_NUMBER;
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("number", number));
        Toast.makeText(this, R.string.mfs_copied, Toast.LENGTH_SHORT).show();
    }







    // ---------------------------------------------------------- Continue

    private void onContinue() {
        if (step == 1) {
            if (!checkAddress()) {
                return;
            }
            step = 2;
        } else if (step == 2) {
            if (!checkPayment()) {
                return;
            }
            fillReview();
            step = 3;
        } else {
            goToOrder();
            return;
        }
        showStep();
    }

    // ---------------------------------------------------------- Back

    private void goBack() {
        if (step > 1) {
            step--;
            showStep();
        } else {
            finish();
        }
    }










    private void fillReview() {

        // Deliver to
        tvRecapAddress.setText(text(etName) + "\n"
                + text(etAddress) + ", " + text(etArea) + ", " + text(etDistrict) + "\n"
                + text(etPhone));



        if (method.equals("cod")) {
            tvRecapPay.setText("Cash on delivery");
        } else {
            String name = method.equals("bkash") ? "bKash" : "Nagad";
            tvRecapPay.setText(name + " · TrxID " + text(etTrx).toUpperCase());
        }



        // Order lines: one row per item in the bag
        rvOrderLines.setLayoutManager(new LinearLayoutManager(this));
        rvOrderLines.setAdapter(new OrderLineAdapter(cartItems));

        // Subtotal, delivery, total
        ((TextView) findViewById(R.id.tvSubtotal)).setText(OrderLineAdapter.money(subtotal));
        ((TextView) findViewById(R.id.tvDelivery)).setText(OrderLineAdapter.money(delivery));
        ((TextView) findViewById(R.id.tvTotal)).setText(OrderLineAdapter.money(totalAmount));





    }






















    // ---------------------------------------------------------- Steps

    private void showStep() {
        stepAddress.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        stepPayment.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        stepReview.setVisibility(step == 3 ? View.VISIBLE : View.GONE);

        // progress bars + names: selected = orange / white
        findViewById(R.id.stepBar1).setSelected(step >= 1);
        findViewById(R.id.stepBar2).setSelected(step >= 2);
        findViewById(R.id.stepBar3).setSelected(step >= 3);
        findViewById(R.id.stepLabel1).setSelected(step >= 1);
        findViewById(R.id.stepLabel2).setSelected(step >= 2);
        findViewById(R.id.stepLabel3).setSelected(step >= 3);

        btnContinue.setText(step == 3 ? "Place order" : "Continue");
    }










    private boolean checkAddress() {
        boolean ok = true;
        ok &= check(etName, tvNameError, text(etName).length() >= 2);
        ok &= check(etPhone, tvPhoneError, isPhone(text(etPhone)));
        ok &= check(etDistrict, tvDistrictError, text(etDistrict).length() >= 2);
        ok &= check(etArea, tvAreaError, text(etArea).length() >= 2);
        ok &= check(etAddress, tvAddressError, text(etAddress).length() >= 5);
        return ok;
    }





    /** Step 2: only bKash / Nagad need the sender number and TrxID. */
    private boolean checkPayment() {
        if (method.equals("cod")) {
            return true;
        }
        boolean ok = true;
        ok &= check(etSender, tvSenderError, isPhone(text(etSender)));
        ok &= check(etTrx, tvTrxError, text(etTrx).matches("[A-Za-z0-9]{8,10}"));
        return ok;
    }








    /** Shows the red error under the field when it is not ok, hides it when ok. */
    private boolean check(EditText field, TextView error, boolean ok) {
        error.setVisibility(ok ? View.GONE : View.VISIBLE);
        field.setActivated(!ok);
        return ok;
    }

    private String text(EditText et) {
        return et.getText().toString().trim();
    }

    /** 01XXXXXXXXX (Bangladesh mobile number). */
    private boolean isPhone(String s) {
        return s.replace(" ", "").replace("-", "").matches("^(\\+?88)?01[3-9]\\d{8}$");
    }



















    private void goToOrder(){

        android.content.SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", "");

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;


        ShippingAddress shippingAddress = new ShippingAddress(
                        text(etName),
                        text(etPhone),
                        text(etDistrict),
                        text(etArea),
                        text(etAddress)
                );




        String paymentMethod = method;
        String senderNum = paymentMethod.equals("cod") ? "" : text(etSender);
        String trxId = paymentMethod.equals("cod") ? "" : text(etTrx);

        PaymentInfo paymentInfo = new PaymentInfo(
                        paymentMethod,
                        senderNum,
                        trxId
                );



       OrderRequest orderRequest = new OrderRequest(shippingAddress, paymentInfo);

       btnContinue.setEnabled(false);
       btnContinue.setText("Placing Order...");


        ApiService apiService = RetrofitClient.getInstance().getApi();


        apiService.placeOrder(authHeader, orderRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                btnContinue.setEnabled(true);
                btnContinue.setText("Place order");

                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse res = response.body();
                    if (res.isSuccess()) {
                        Toast.makeText(CheckOutActivity.this, "Order placed successfully! 🎉", Toast.LENGTH_SHORT).show();
                         Intent intent = new Intent(CheckOutActivity.this, OrderSuccessActivity.class);
                         intent.putExtra("order_data", (Serializable) res.getData());
                         startActivity(intent);
                         finish();
                    } else {
                        Toast.makeText(CheckOutActivity.this, "Failed: " + res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckOutActivity.this, "Something went wrong! Error code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                btnContinue.setEnabled(true);
                btnContinue.setText("Place order");
                Toast.makeText(CheckOutActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }









}