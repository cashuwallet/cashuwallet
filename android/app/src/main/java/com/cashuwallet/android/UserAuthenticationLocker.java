package com.cashuwallet.android;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import androidx.annotation.RequiresApi;
import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@RequiresApi(Build.VERSION_CODES.M)
public class UserAuthenticationLocker extends Locker {

    public static UserAuthenticationHandler defaultHandler(Continuation<Object[]> ret) {
        return (context, cont) -> {
            KeyguardManager keyguardManager = context.getSystemService(KeyguardManager.class);
            Intent intent = keyguardManager == null ? null : keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent == null) {
                cont.run();
                return;
            }
            ret.cont(new Object[]{ intent, cont });
        };
    }

    public UserAuthenticationLocker(String keyName, Context context) {
        super(keyName, context);
    }

    public void encrypt(String string, UserAuthenticationHandler handler, Continuation<String> cont) {
        SecretKey key = getSecretKey();
        if (key == null) {
            cont.cont(null);
            return;
        }
        Cipher cipher = getCipher();
        if (cipher == null) {
            cont.cont(null);
            return;
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            if (e instanceof UserNotAuthenticatedException && handler != null) {
                handler.requestUserAuthentication(context, () -> encrypt(string, handler, cont));
                return;
            }
            if (e instanceof KeyPermanentlyInvalidatedException && deleteSecretKey()) {
                encrypt(string, handler, cont);
                return;
            }
            cont.cont(null);
            return;
        }
        byte[] IV = cipher.getIV();
        byte[] bytes = string.getBytes();
        byte[] encoded;
        try {
            encoded = cipher.doFinal(bytes);
        } catch (BadPaddingException|IllegalBlockSizeException e) {
            cont.cont(null);
            return;
        }
        cont.cont(Base64.encodeToString(IV, Base64.NO_WRAP) + ":" + Base64.encodeToString(encoded, Base64.NO_WRAP));
    }

    public void decrypt(String string, UserAuthenticationHandler handler, Continuation<String> cont) {
        SecretKey key = getSecretKey();
        if (key == null) {
            cont.cont(null);
            return;
        }
        String[] parts = string.split(":");
        if (parts.length != 2) {
            cont.cont(null);
            return;
        }
        byte[] IV;
        try {
            IV = Base64.decode(parts[0], Base64.NO_WRAP);
        } catch (IllegalArgumentException e) {
            cont.cont(null);
            return;
        }
        IvParameterSpec iv = new IvParameterSpec(IV);
        byte[] encoded;
        try {
            encoded = Base64.decode(parts[1], Base64.NO_WRAP);
        } catch (IllegalArgumentException e) {
            cont.cont(null);
            return;
        }
        Cipher cipher = getCipher();
        if (cipher == null) {
            cont.cont(null);
            return;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        } catch (InvalidAlgorithmParameterException e) {
            cont.cont(null);
            return;
        } catch (InvalidKeyException e) {
            if (e instanceof UserNotAuthenticatedException && handler != null) {
                handler.requestUserAuthentication(context, () -> decrypt(string, handler, cont));
                return;
            }
            if (e instanceof KeyPermanentlyInvalidatedException && deleteSecretKey()) {
                decrypt(string, handler, cont);
                return;
            }
            cont.cont(null);
            return;
        }
        byte[] decoded;
        try {
            decoded = cipher.doFinal(encoded);
        } catch (BadPaddingException|IllegalBlockSizeException e) {
            cont.cont(null);
            return;
        }
        cont.cont(new String(decoded));
    }

    private Cipher getCipher() {
        String name = KeyProperties.KEY_ALGORITHM_AES
                + "/" + KeyProperties.BLOCK_MODE_CBC
                + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;
        try {
            return Cipher.getInstance(name);
        } catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
            return null;
        }
    }

    private SecretKey getSecretKey() {
        KeyStore keyStore = getKeyStore();
        if (keyStore == null) return createSecretKey();
        Key key;
        try {
            key = keyStore.getKey(keyName, null);
        } catch (KeyStoreException|NoSuchAlgorithmException|UnrecoverableKeyException e) {
            key = null;
        }
        SecretKey secretKey = key instanceof SecretKey ? (SecretKey) key: null;
        if (secretKey == null) {
            if (key != null) deleteSecretKey();
            return createSecretKey();
        }
        return secretKey;
    }

    private SecretKey createSecretKey() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_NAME);
        } catch (NoSuchAlgorithmException|NoSuchProviderException e) {
            return null;
        }
        int purposes = KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT;
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName, purposes)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(60);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setInvalidatedByBiometricEnrollment(false);
        }
        AlgorithmParameterSpec spec = builder.build();
        try {
            keyGenerator.init(spec);
        } catch (InvalidAlgorithmParameterException e) {
            return null;
        }
        return keyGenerator.generateKey();
    }

    private boolean deleteSecretKey() {
        KeyStore keyStore = getKeyStore();
        if (keyStore == null) return false;
        try {
            keyStore.deleteEntry(keyName);
        } catch (KeyStoreException e) {
            return false;
        }
        return true;
    }

}
