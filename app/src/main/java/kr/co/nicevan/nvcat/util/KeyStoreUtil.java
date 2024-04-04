package kr.co.nicevan.nvcat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class KeyStoreUtil {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String USER_CREDENTIAL_KEY = "USER_CREDENTIALS_KEY";
    private SharedPreferences sharedPreferences;

    public static String ID_KEY = "id";
    public static String PW_KEY = "pw";
    public static String CAT_ID_KEY = "cat_id";
    public static String SHOP_ID_KEY = "shop_id";
    private static KeyStoreUtil instance;

    private KeyStoreUtil(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static KeyStoreUtil init(Context context) {
        if (instance != null) {
            return instance;
        }
        instance = new KeyStoreUtil(context);
        return instance;
    }

    public static KeyStoreUtil getInstance() {
        if (instance == null) {
            throw new IllegalStateException("KeyStoreUtil이 초기화되지 않았습니다.");
        }
        return instance;
    }

    public void storeData(String key, String data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public Optional<String> getData(String key) {
            String encryptedData = sharedPreferences.getString(key, null);
            if (encryptedData == null) {
                return Optional.empty();
            }
            return Optional.of(encryptedData);
    }

    public String getData(String key, String defaultValue) {
        return getData(key).orElse(defaultValue);
    }
}
