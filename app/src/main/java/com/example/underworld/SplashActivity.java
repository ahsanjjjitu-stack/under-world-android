package com.example.underworld;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;


import com.example.underworld.auth_pack.LoginActivity;
import com.example.underworld.main_pack.MainActivity;


public class SplashActivity extends AppCompatActivity {


    private static final String WORD = "UNDER WORLD";
    private static final long TOTAL_MS = 2900;
    private static final long FADE_OUT_MS = 500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean navigated = false;

    private View root;
    private View ring;
    private View logoCard;
    private ImageView logoU;
    private LinearLayout wordmark;
    private TextView tagline;
    private View progressFill;
    private ObjectAnimator ringPulse;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        hideSystemBars();





        root = findViewById(R.id.splashRoot);
        ring = findViewById(R.id.ring);
        logoCard = findViewById(R.id.logoCard);
        logoU = findViewById(R.id.logoU);
        wordmark = findViewById(R.id.wordmark);
        tagline = findViewById(R.id.tagline);
        progressFill = findViewById(R.id.progressFill);

        buildWordmark();
        playIntro();

        long total = animationsEnabled() ? TOTAL_MS : 700;
        handler.postDelayed(this::fadeOutAndContinue, total);

        root.setOnClickListener(v -> goNext());




    }















    private void hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }
















    /** One TextView per letter so each letter can animate on its own. */
    private void buildWordmark() {
        Typeface face = Typeface.create("sans-serif-black", Typeface.NORMAL);
        for (int i = 0; i < WORD.length(); i++) {
            char ch = WORD.charAt(i);
            TextView letter = new TextView(this);
            letter.setText(ch == ' ' ? " " : String.valueOf(ch));
            letter.setTextSize(22f);
            letter.setTypeface(face);
            letter.setLetterSpacing(0.06f);
            letter.setTextColor(ContextCompat.getColor(this, R.color.uw_text));
            letter.setGravity(Gravity.CENTER);
            letter.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            letter.setAlpha(0f);
            letter.setTranslationY(dp(14));
            wordmark.addView(letter);
        }
    }















    private void playIntro() {
        // 1) Logo card pops in with a small overshoot
        logoCard.setScaleX(0.3f);
        logoCard.setScaleY(0.3f);
        logoCard.setAlpha(0f);
        logoCard.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator(1.8f))
                .start();

        // 2) The U is drawn in (animated vector, trimPathEnd 0 -> 1)
        handler.postDelayed(() -> {
            Drawable d = logoU.getDrawable();
            if (d instanceof Animatable) {
                ((Animatable) d).start();
            }
        }, 450);

        // 3) Ring pulses outwards, repeating
        ring.setAlpha(0f);
        ringPulse = ObjectAnimator.ofPropertyValuesHolder(
                ring,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.9f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.9f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 0.6f, 0f));
        ringPulse.setDuration(1800);
        ringPulse.setStartDelay(900);
        ringPulse.setRepeatCount(ValueAnimator.INFINITE);
        ringPulse.setInterpolator(new DecelerateInterpolator());
        ringPulse.start();

        // 4) Letters rise in one after another
        PathInterpolator rise = new PathInterpolator(0.2f, 0.8f, 0.2f, 1f);
        for (int i = 0; i < wordmark.getChildCount(); i++) {
            wordmark.getChildAt(i).animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(900 + i * 55L)
                    .setDuration(550)
                    .setInterpolator(rise)
                    .start();
        }

        // 5) Tagline
        tagline.setAlpha(0f);
        tagline.setTranslationY(dp(14));
        tagline.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(1700)
                .setDuration(600)
                .start();

        // 6) Loading bar fills from the left
        progressFill.setPivotX(0f);
        progressFill.setScaleX(0f);
        progressFill.animate()
                .scaleX(1f)
                .setDuration(2700)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void fadeOutAndContinue() {
        if (navigated) {
            return;
        }
        goNext();
    }












    /** Opens the next screen once. Replace MainActivity with your login or home activity. */
    private void goNext() {
        if (navigated) {
            return;
        }

        SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String token = preferences.getString("JWT_TOKEN", "");
        android.util.Log.d("TOKEN: ", "TOKEN:  " + token);

        // Safety check: token null ba faka kina
        if (token == null || token.trim().isEmpty()) {
            navigated = true;
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            navigated = true;
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }












    private boolean animationsEnabled() {
        float scale = Settings.Global.getFloat(
                getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1f);
        return scale != 0f;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (ringPulse != null) {
            ringPulse.cancel();
        }
        root.animate().cancel();
        super.onDestroy();
    }










}