package com.didiglobal.logi.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 登录密码的加解密
 */
public class AESUtils {
    private static final Logger logger = LoggerFactory.getLogger(AESUtils.class);

    private static final String ALGORITHM = "AES";

    private static final String AES_KEY = "Szjx2022@666666$";

    private static final String CHARSET_NAME = "utf-8";

    public static String encrypt(String sSrc) {
        try {
            byte[] raw = AES_KEY.getBytes(CHARSET_NAME);

            SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); //"算法/模式/补码方式"

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] encrypted = cipher.doFinal(sSrc.getBytes(CHARSET_NAME));

            return Base64.getEncoder().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
            logger.error("encrypt failed, sSrc:{}.", sSrc, e);
        }

        return null;
    }

    public static String decrypt(String sSrc) {
        try {
            byte[] raw = AES_KEY.getBytes(CHARSET_NAME);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc.getBytes(CHARSET_NAME));//先用base64解密

            return new String(cipher.doFinal(encrypted1), CHARSET_NAME);
        } catch (Exception e) {
            logger.error("encrypt failed, sSrc:{}.", sSrc, e);
        }

        return null;
    }

    public static void main(String[] args) {
        // 需要加密的字串
        String cSrc = "admin";
        System.out.println(cSrc);

        // 加密
        String enString = AESUtils.encrypt(cSrc);
        System.out.println("加密后的字串是：" + enString);

        // 解密
        String DeString = AESUtils.decrypt(enString);
        System.out.println("解密后的字串是：" + DeString);
    }
}
