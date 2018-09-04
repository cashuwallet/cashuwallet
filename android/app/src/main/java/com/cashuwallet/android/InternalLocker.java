package com.cashuwallet.android;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class InternalLocker extends Locker {

    public InternalLocker(String keyName, Context context) {
        super(keyName, context);
    }

    public void encrypt(String string, UserAuthenticationHandler handler, Continuation<String> cont) {
        Cipher cipher = getCipher();
        if (cipher == null) {
            cont.cont(null);
            return;
        }
        KeyPair keyPair = getSecretKeyPair();
        if (keyPair == null) {
            cont.cont(null);
            return;
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        } catch (InvalidKeyException e) {
            cont.cont(null);
            return;
        }
        byte[] bytes = string.getBytes();
        byte[] encoded;
        try {
            encoded = cipher.doFinal(bytes);
        } catch (BadPaddingException|IllegalBlockSizeException e) {
            cont.cont(null);
            return;
        }
        cont.cont(Base64.encodeToString(encoded, Base64.NO_WRAP));
    }

    public void decrypt(String string, UserAuthenticationHandler handler, Continuation<String> cont) {
        byte[] encoded;
        try {
            encoded = Base64.decode(string, Base64.NO_WRAP);
        } catch (IllegalArgumentException e) {
            cont.cont(null);
            return;
        }
        Cipher cipher = getCipher();
        if (cipher == null) {
            cont.cont(null);
            return;
        }
        KeyPair keyPair = getSecretKeyPair();
        if (keyPair == null) {
            cont.cont(null);
            return;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        } catch (InvalidKeyException e) {
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
        try {
            return Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
            return null;
        }
    }

    private KeyPair getSecretKeyPair() {
        KeyStore keyStore = getKeyStore();
        if (keyStore == null) return createSecretKeyPair();
        KeyStore.Entry entry;
        try {
            entry = keyStore.getEntry(keyName, null);
        } catch (KeyStoreException|NoSuchAlgorithmException|UnrecoverableEntryException e) {
            entry = null;
        }
        KeyStore.PrivateKeyEntry secretEntry = entry instanceof KeyStore.PrivateKeyEntry ? (KeyStore.PrivateKeyEntry) entry : null;
        if (secretEntry == null) {
            if (entry != null) deleteSecretKeyPair();
            return createSecretKeyPair();
        }
        return new KeyPair(secretEntry.getCertificate().getPublicKey(), secretEntry.getPrivateKey());
    }

    private KeyPair createSecretKeyPair() {
        KeyPairGenerator keyGenerator;
        try {
            keyGenerator = KeyPairGenerator.getInstance("RSA", KEY_STORE_NAME);
        } catch (NoSuchAlgorithmException|NoSuchProviderException e) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 100);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(keyName)
                .setSubject(new X500Principal("CN=" + keyName))
                .setSerialNumber(BigInteger.valueOf(10))
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        try {
            keyGenerator.initialize(spec);
        } catch (InvalidAlgorithmParameterException e) {
            return null;
        }
        return keyGenerator.generateKeyPair();
    }

    private boolean deleteSecretKeyPair() {
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
