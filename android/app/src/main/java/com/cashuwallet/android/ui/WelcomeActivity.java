package com.cashuwallet.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.raugfer.crypto.binint;
import com.raugfer.crypto.mnemonic;
import com.cashuwallet.android.Locker;
import com.cashuwallet.android.MainApplication;
import com.cashuwallet.android.R;
import com.cashuwallet.android.UserAuthenticationLocker;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class WelcomeActivity extends AppCompatActivity {

    private static final int AUTHENTICATION_SCREEN_REQUEST_CODE = 1;
    private static final int CAPTURE_SCREEN_REQUEST_CODE = 2;

    private View rootView;
    private Runnable cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setTheme(R.style.Welcome);

        Resources.Theme theme = getTheme();
        TypedValue colorValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorValue, true);
        @ColorInt int colorPrimary = colorValue.data;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_welcome);

        String[] wordlist = getResources().getStringArray(R.array.mnemonic_english);

        rootView = findViewById(R.id.main_content);

        AppCompatEditText mnemonicView = rootView.findViewById(R.id.mnemonic);
        AppCompatEditText passwordView = rootView.findViewById(R.id.password);

        Button buttonCapture = rootView.findViewById(R.id.button_capture);
        buttonCapture.setTextColor(0xffffffff);
        buttonCapture.setBackgroundColor(colorPrimary);
        buttonCapture.setOnClickListener((View v) -> {
            PackageManager pm = getPackageManager();
            if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Snackbar.make(rootView, R.string.camera_not_available, Snackbar.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, CAPTURE_SCREEN_REQUEST_CODE);
        });

        Button buttonGenerate = rootView.findViewById(R.id.button_generate);
        buttonGenerate.setTextColor(0xffffffff);
        buttonGenerate.setBackgroundColor(colorPrimary);
        buttonGenerate.setOnClickListener((View v) -> {
            SecureRandom secureRandomGenerator;
            try {
                secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                Snackbar.make(rootView, R.string.error_secure_random, Snackbar.LENGTH_LONG).show();
                return;
            }
            byte[] randomBytes = new byte[32];
            secureRandomGenerator.nextBytes(randomBytes);
            String words = mnemonic.mnemonic(binint.b2n(randomBytes), 8*randomBytes.length, wordlist);
            mnemonicView.setText(words);
        });

        Locker.UserAuthenticationHandler handler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handler = UserAuthenticationLocker.defaultHandler((ret) -> {
                Intent intent = (Intent) ret[0];
                cont = (Runnable) ret[1];
                startActivityForResult(intent, AUTHENTICATION_SCREEN_REQUEST_CODE);
            });
        } else {
            handler = null;
        }

        Button accessButton = findViewById(R.id.access_wallet_button);
        accessButton.setTextColor(0xffffffff);
        accessButton.setBackgroundColor(colorPrimary);
        accessButton.setOnClickListener((View view) -> {
            rootView.setVisibility(View.GONE);
            String words = mnemonicView.getText().toString().trim().replaceAll(" +", " ").toLowerCase();
            String password = passwordView.getText().toString();
            setRequestedOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ProgressDialog progressDialog = ProgressDialog.show(WelcomeActivity.this, "", getResources().getString(R.string.preparing_wallet), true);
            MainApplication.app().signup(wordlist, words, password, (success) -> {
                progressDialog.dismiss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                if (success) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    rootView.setVisibility(View.VISIBLE);
                    Snackbar.make(rootView, R.string.error_accessing_wallet, Snackbar.LENGTH_LONG).show();
                }
            }, handler);
        });

        new AlertDialog.Builder(this)
            .setTitle(R.string.read_with_attention)
            .setCancelable(true)
            .setMessage(R.string.warning_untested_software)
            .setPositiveButton(R.string.ok, null)
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHENTICATION_SCREEN_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
            {
                cont.run();
            }
            else
            {
                rootView.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == CAPTURE_SCREEN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String result = data.getStringExtra("result");
            String message = data.getStringExtra("message");
            if (result != null) {
                AppCompatEditText mnemonicView = rootView.findViewById(R.id.mnemonic);
                mnemonicView.setText(result);
            }
            if (message != null) {
                Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
