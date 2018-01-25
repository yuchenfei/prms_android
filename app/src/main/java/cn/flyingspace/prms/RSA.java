package cn.flyingspace.prms;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;


public class RSA {
    private static String RSA_CONFIGURATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // 公钥
    private static String PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNmBhsDzjAnZZvPjE6ZCsEumSw" +
                    "5l+ZCDDNeMx3nAskATFy6heqaCU7Nebsn8Qh7C5J3D9rSNzqFQp7MmhDxBIRvYfO" +
                    "CHw5Vp2QLEm5NOLu8kc8D+Z0bKjheeDDyCc1hEjE6wrQe19MwaY5fNfl4d/xCNPW" +
                    "OrC9OzT5L8M3BQKpuQIDAQAB";

    // 加密算法
    static String encrypt(Key key, String toBeEncryptedString) {

        try {
            Cipher c = Cipher.getInstance(RSA_CONFIGURATION);
            c.init(Cipher.ENCRYPT_MODE, key, new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,
                    PSource.PSpecified.DEFAULT));
            byte[] encodedBytes;
            encodedBytes = c.doFinal(toBeEncryptedString.getBytes("UTF-8"));

            return Base64.encodeToString(encodedBytes, 0);
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // 通过Pem格式的字符串（PKCS1）生成公钥，base64是去掉头和尾的b64编码的字符串
    // Pem格式公钥一般采用PKCS1格式
    static PublicKey generatePublicKeyFromPKCS1(String base64) {
        byte[] publicKeyBytes;
        try {
            publicKeyBytes = Base64.decode(base64.getBytes("UTF-8"), 0);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = kf.generatePublic(ks);
            return publicKey;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // 加密数据
    static public String encryptData(String data) {
        Key public_key = generatePublicKeyFromPKCS1(PUBLIC_KEY);
        return encrypt(public_key, data);
    }
}
