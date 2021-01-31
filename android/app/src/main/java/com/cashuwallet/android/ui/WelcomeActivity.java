package com.cashuwallet.android.ui;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.raugfer.crypto.binint;
import com.raugfer.crypto.mnemonic;
import com.cashuwallet.android.Locker;
import com.cashuwallet.android.MainApplication;
import com.cashuwallet.android.R;
import com.cashuwallet.android.UserAuthenticationLocker;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class WelcomeActivity extends AppCompatActivity {

    private static final int AUTHENTICATION_SCREEN_REQUEST_CODE = 1;
    private static final int CAPTURE_SCREEN_REQUEST_CODE = 2;

    private View rootView;
    private Runnable cont;
    private ProgressDialog progressDialog;
    private TextInputLayout mnemonicLayout;
    private TextInputLayout passwordLayout;

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

        mnemonicLayout = rootView.findViewById(R.id.mnemonic_layout);
        passwordLayout = rootView.findViewById(R.id.password_layout);
        AppCompatEditText mnemonicView = rootView.findViewById(R.id.mnemonic);
        AppCompatEditText passwordView = rootView.findViewById(R.id.password);

        Button buttonPaste = rootView.findViewById(R.id.button_paste);
        buttonPaste.setTextColor(0xffffffff);
        buttonPaste.setBackgroundColor(colorPrimary);
        buttonPaste.setOnClickListener((View v) -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard == null) {
                Snackbar.make(rootView, R.string.clipboard_unavailable, Snackbar.LENGTH_LONG).show();
                return;
            }
            if (!(clipboard.hasPrimaryClip())) {
                Snackbar.make(rootView, R.string.empty_clipboard, Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData clip = clipboard.getPrimaryClip();
            if (!(clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                // since the clipboard has data but it is not plain text
                Snackbar.make(rootView, R.string.incompatible_clipboard_type, Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData.Item item = clip.getItemAt(0);
            mnemonicView.setText(item.getText());
        });

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
            hideKeyboard();
            setRequestedOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mnemonicLayout.setEnabled(false);
            passwordLayout.setEnabled(false);
            String words = mnemonicView.getText().toString().trim().replaceAll(" +", " ").toLowerCase();
            String password = passwordView.getText().toString();
            progressDialog = ProgressDialog.show(WelcomeActivity.this, "", getResources().getString(R.string.preparing_wallet), true);
            MainApplication.app().signup(wordlist, words, password, (success) -> {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                mnemonicLayout.setEnabled(true);
                passwordLayout.setEnabled(true);
                if (success) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Snackbar.make(rootView, R.string.error_accessing_wallet, Snackbar.LENGTH_LONG).show();
                }
            }, handler);
        });

        setRequestedOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mnemonicLayout.setEnabled(false);
        passwordLayout.setEnabled(false);
        new AlertDialog.Builder(this)
            .setTitle(R.string.read_with_attention)
            .setMessage(R.string.warning_untested_software)
            .setOnCancelListener((DialogInterface dialog) -> {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                mnemonicLayout.setEnabled(true);
                passwordLayout.setEnabled(true);
            })
            .setPositiveButton(R.string.ok, (DialogInterface dialog, int which) -> {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                mnemonicLayout.setEnabled(true);
                passwordLayout.setEnabled(true);
            })
            .show();
    }

    void hideKeyboard() {
        View view = getCurrentFocus();
        if (view == null) view = new View(this);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                if (progressDialog.isShowing()) progressDialog.dismiss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                mnemonicLayout.setEnabled(true);
                passwordLayout.setEnabled(true);
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
