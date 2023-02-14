package kr.co.nicevan.nvcat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

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
    private KeyStore keyStore;
    private SharedPreferences sharedPreferences;
    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;
    public static String ID_KEY = "id";
    public static String PW_KEY = "pw";

    public KeyStoreUtil(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            if (!keyStore.containsAlias(USER_CREDENTIAL_KEY)) {
                createUserCredentialKey();
            }
        } catch (KeyStoreException | CertificateException | IOException |
                 NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        getData("id");
    }

    public void createUserCredentialKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(USER_CREDENTIAL_KEY, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                 InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public void storeData(String key, String data) {
        byte[] encryptedData = null;
        try {
            SecretKey secretKey = (SecretKey) keyStore.getKey(USER_CREDENTIAL_KEY, null);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] payload = cipher.doFinal(data.getBytes());

            encryptedData = new byte[iv.length + payload.length];

            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(payload, 0, encryptedData, iv.length, payload.length);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException |
                 NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException |
                 InvalidKeyException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = Arrays.toString(encryptedData);
        editor.putString(key, value);
        editor.apply();
    }

    public Optional<String> getData(String key) {
        try {
            String encryptedData = sharedPreferences.getString(key, "");
            String[] split = encryptedData.substring(1, encryptedData.length() - 1).split(", ");
            byte[] encryptedDataBytes = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                encryptedDataBytes[i] = Byte.parseByte(split[i]);
            }

            byte[] iv = new byte[16];
            System.arraycopy(encryptedDataBytes, 0, iv, 0, iv.length);

            int payloadLength = encryptedDataBytes.length - iv.length;
            byte[] payload = new byte[payloadLength];
            System.arraycopy(encryptedDataBytes, iv.length, payload, 0, payloadLength);

            SecretKey secretKey = (SecretKey) keyStore.getKey(USER_CREDENTIAL_KEY, null);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] bytes = cipher.doFinal(payload);
            return Optional.of(new String(bytes));
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchPaddingException |
                 UnrecoverableKeyException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
