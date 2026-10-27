package com.example.underworld.auth_pack;


import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.example.underworld.R;
import com.example.underworld.api_pack.RetrofitClient;
import com.example.underworld.main_pack.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private View bottomBlock;
    private View btnGoogle;
    private View btnGuest;
    private TextView gMark;
    private ProgressBar googleProgress;
    private TextView googleLabel;

    private final List<ObjectAnimator> floaters = new ArrayList<>();
    private boolean busy = false;







    // Google Sign-In & FCM variables
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private String currentFcmToken = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);



        bottomBlock = findViewById(R.id.bottomBlock);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnGuest = findViewById(R.id.btnGuest);
        gMark = findViewById(R.id.gMark);
        googleProgress = findViewById(R.id.googleProgress);
        googleLabel = findViewById(R.id.googleLabel);

        playEntrance();





        // The three cards use the same timing as the web preview.
        // Each one starts at a different point in the cycle so they do not move together.
        startFloating(findViewById(R.id.cardLeft), 0);
        startFloating(findViewById(R.id.cardRight), 2400);
        startFloating(findViewById(R.id.cardMiddle), 1200);



        initGoogleSignIn();
        fetchFCMToken();

        btnGoogle.setOnClickListener(v -> startGoogleSignIn());


        btnGuest.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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
















    // ************************************************* google login all method **************************************




    // 1. Google Sign-In Options & Launcher Setup kora
    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("566095360502-e975q18ro59eml0b4h9emh90rq3c4e97.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleGoogleSignInResult(task);
                    } else {
                        // User sign in cancel korle ba fail korle idle state e fire jabe
                        showIdleState();
                        Toast.makeText(this, "Google Sign-In Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }











    // 2. FCM Token fetch kora
    private void fetchFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        currentFcmToken = task.getResult();
                    }
                });
    }














    /** Title, button and legal text rise in from below. */
    private void playEntrance() {
        bottomBlock.setAlpha(0f);
        bottomBlock.setTranslationY(dp(14));
        bottomBlock.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(500)
                .setDuration(2000)
                .setInterpolator(new PathInterpolator(0.2f, 0.8f, 0.2f, 1f))
                .start();
    }

    /** Moves a card up 12dp and back again, forever (5 second loop). */
    private void startFloating(View card, long phaseMs) {
        float base = card.getTranslationY();
        ObjectAnimator a = ObjectAnimator.ofFloat(card, View.TRANSLATION_Y, base, base - dp(12));
        a.setDuration(2500);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.start();
        a.setCurrentPlayTime(phaseMs);
        floaters.add(a);
    }









    /**
     * Button goes into its "signing in" state: the G badge is replaced by a
     * spinner and the label changes. The screen stays like this.
     */
    private void startGoogleSignIn() {
        if (busy) {
            return;
        }
        busy = true;
        btnGoogle.setClickable(false);

        gMark.setVisibility(View.GONE);
        googleProgress.setVisibility(View.VISIBLE);

        // Fade the old text out, swap it, fade the new text in
        googleLabel.animate()
                .alpha(0f)
                .setDuration(120)
                .withEndAction(() -> {
                    googleLabel.setText("Signing in…");
                    googleLabel.animate().alpha(1f).setDuration(160).start();
                })
                .start();

        // Real Google Sign-In intent launch kora hocche
        Intent signIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signIntent);

    }











    // 3. Google Sign-In Result Handle Kora ebong Backend e pathano
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            if (idToken != null) {
                sendTokenToBackend(idToken);
            } else {
                showIdleState();
                Toast.makeText(this, "Failed to get Google ID Token", Toast.LENGTH_SHORT).show();
            }

        } catch (ApiException e) {
            Log.e("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            showIdleState();
            Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }










    // 4. Retrofit diye Node.js Backend e Token Pathano
    private void sendTokenToBackend(String idToken) {
        LoginRequest request = new LoginRequest(idToken, currentFcmToken);

        RetrofitClient.getInstance().getApi().googleLogin(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        String jwtToken = loginResponse.getToken();

                        SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
                        preferences.edit().putString("JWT_TOKEN", jwtToken).apply();
                        preferences.edit().putString("USER_ID", loginResponse.getUser().getId()).apply();
                        preferences.edit().putString("USER_NAME", loginResponse.getUser().getName()).apply();


                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showIdleState();
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showIdleState();
                    Toast.makeText(LoginActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showIdleState();
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }












    /** Puts the button back to normal. Call this if sign-in fails or is cancelled. */
    @SuppressWarnings("unused")
    private void showIdleState() {
        busy = false;
        btnGoogle.setClickable(true);
        googleProgress.setVisibility(View.GONE);
        gMark.setVisibility(View.VISIBLE);
        googleLabel.setText("Continue with Google");
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDestroy() {
        for (ObjectAnimator a : floaters) {
            a.cancel();
        }
        floaters.clear();
        bottomBlock.animate().cancel();
        super.onDestroy();
    }








}