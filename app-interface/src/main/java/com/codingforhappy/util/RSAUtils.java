package com.codingforhappy.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * 用法
 * encodedBase64是从客户端收到的密码，密码经过公钥加密，Base64编码。要得到明文需要先解码，再解密。
 * byte[] decodedBase64 = Base64.getDecoder().decode(encodedBase64);
 * PrivateKey privateKey = RSAUtils.getPrivateKey()
 * String decrypted = new String(RSAUtils.decrypt(decodedBase64, privateKey));
 */
public class RSAUtils {

    public static PrivateKey getPrivateKey() throws Exception {
        final String PRIVATE_KEY = "";
        byte[] keyBytes = Base64.getDecoder().decode(PRIVATE_KEY);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    //私钥解密
    public static byte[] decrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }


    public static void main(String[] args) {

    }

}
