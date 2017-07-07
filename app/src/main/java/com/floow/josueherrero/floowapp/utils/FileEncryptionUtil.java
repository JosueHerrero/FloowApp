package com.floow.josueherrero.floowapp.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by JosuéManuel on 07/07/2017.
 *
 * This is the util for encrypting/decrypting a file
 */

public final class FileEncryptionUtil {

    private static final String AES = "AES";

    public static String crypto(
            final String key,
            final String inString,
            final boolean decrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        final SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), AES);
        final byte[] inputByte = inString.getBytes("UTF-8");
        if (decrypt){
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            return new String (cipher.doFinal(Base64.decode(inputByte, Base64.DEFAULT)));
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return new String (Base64.encode(cipher.doFinal(inputByte), Base64.DEFAULT));
        }
    }

}