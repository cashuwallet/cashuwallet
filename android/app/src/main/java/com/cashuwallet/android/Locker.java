package com.cashuwallet.android;

import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public abstract class Locker {

    public interface UserAuthenticationHandler {
        void requestUserAuthentication(Context context, Runnable cont);
    }

    public static final String KEY_STORE_NAME = "AndroidKeyStore";

    public static Locker create(String keyName, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return new UserAuthenticationLocker(keyName, context);
        } else {
            return new InternalLocker(keyName, context);
        }
    }

    protected final String keyName;
    protected final Context context;

    public Locker(String keyName, Context context) {
        this.keyName = keyName;
        this.context = context;
    }

    public void encrypt(String string, Continuation<String> cont) {
        encrypt(string, null, cont);
    }

    public abstract void encrypt(String string, UserAuthenticationHandler handler, Continuation<String> cont);

    public void decrypt(String string, Continuation<String> cont) {
        decrypt(string, null, cont);
    }

    public abstract void decrypt(String string, UserAuthenticationHandler handler, Continuation<String> cont);

    protected KeyStore getKeyStore() {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KEY_STORE_NAME);
        } catch (KeyStoreException e) {
            return null;
        }
        try {
            keyStore.load(null);
        } catch (CertificateException|NoSuchAlgorithmException|IOException e) {
            return null;
        }
        return keyStore;
    }

}
