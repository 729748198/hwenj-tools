package com.application;


import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaUtil {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 私钥的字符串
     *
     * @param
     * @return 私钥字符串
     */
    public static String getPrivateString(KeyPair keyPair) {
        return new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
    }

    /**
     * 获取私钥
     *
     * @param privateString 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String privateString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateString.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥的字符串
     *
     * @param
     * @return 公钥字符串
     */
    public static String getPublicString(KeyPair keyPair) {
        return new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
    }

    /**
     * 获取公钥
     *
     * @param publicString 公钥字符串
     * @return
     */
    public static PublicKey getPublicKey(String publicString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicString.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return
     */
    public static String encrypt(String data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        return new String(Base64.encodeBase64String(encryptedData));
    }

    /**
     * RSA解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return
     */
    public static String decrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // 解密后的内容
        return new String(decryptedData,StandardCharsets.UTF_8);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    public static void test(){
        try {
            // 生成密钥对
            KeyPair keyPair = getKeyPair();
            String privateString = getPrivateString(keyPair);
            // System.out.println(privateString);
            String publicString = getPublicString(keyPair);
            //  System.out.println("私钥:" + privateString);
            //   System.out.println("公钥:" + publicString);
            // RSA加密
            String data1 = "Jowto@2307";

            String data = "YsK01@KILL";
            //String gy ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWyMtoojRtI/7XGCfTmITDbMbrrsLvRk+H0wS+jU278YUbA4MMbf8as9AxD/83vHWU17G0cyAyhb5dlhiLeMq6m17fCmtUa0QLf1R8JclABQvgtrjct97hb3FnJt9kZKiL0ZYpn2iUkbScyiB4tFfl3KTvzxi4tepSPtDCMoooeQIDAQAB";

            String encryptData1 = encrypt(data, getPublicKey(publicString));
            System.out.println("加密后内容1:" + encryptData1);

            //String a ="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbIy2iiNG0j/tcYJ9OYhMNsxuuuwu9GT4fTBL6NTbvxhRsDgwxt/xqz0DEP/ze8dZTXsbRzIDKFvl2WGIt4yrqbXt8Ka1RrRAt/VHwlyUAFC+C2uNy33uFvcWcm32RkqIvRlimfaJSRtJzKIHi0V+XcpO/PGLi16lI+0MIyiih5AgMBAAECgYEAkJFWTSzv3RCc/re/P6XE7OX3VvLJHcao7A4LR3ONazwmpX7Zf+pao8xxOQkgZtz/U0T2sY+L+ES4/1uwjtCydXX8GvZ5a8dzT+MEgYMoXBD/K79OXMn3SFoh5ocaqSekLcVHK6hWogehOsc9Fmhokt0sfOBBMmXQyvJaJUttfPECQQDvPc+pJpGxGqMW0/1dkXyjrdGGZgaJOX+bya9ulMu59Ew/mUGc+uxYxk0toO9tNI+Ws9enQvdfQ7jI3PotJGWtAkEAoVi6yjJ0VB+9DToUTy7hcCS4efav1bkalp58q0DYiDsy/tug+GaRCxyf2JF0soAc22i9djOB17UOOLmyizjvfQJAYdnhNqkMy+g+LlcUeRHJbdTpLtRJ9hRraheqxkrbrLoL+bNAT+mdg43QAUqiwwLNe/Eg0kuki/VR/e3L8WzCnQJABA6nI86A/lyN9Hcxa4xSPu+tc+OwKD+vW3pAyE2pJEPY1G/RbG2n2A+wNVHzDUoLb7kCiqZg7XuwD90bkESuQQJAfSieLd1daXhHO0AdMWSflRpW2tGym06uVAtKwtcnuMxIhiMvW1xXs7+eIuOLNFC/NxI1ltDblY3EtuzgK5Q2Fg==";
            String a = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIXSUg3nmApPKKJ36FurIexZL4mz2YZQZ6itscYIjl89a06BuLCDMAJge6C7DpOkisl6jcoY0NP23YN48Jp4823pvisC8kzT6krBBFZ0vh6tLyIETcugcu4eCWIIBsmTqxiSH7+d/vG0sOV+T7mzfqkrPvUAgn4c7AxeN5EwMIQhAgMBAAECgYBx9S28+4hk1w4SdKvhTDVCMJyj1g0aLORpJyjpLCYZgpMbsqoVXlIG5CTDNifuoMMzWfLetA05Q9NU/ytdUDxzDkUD40zmgOk0Sh95Zi8SR2ersTN7RNGYcdtj1srCLX7okJgauNAp3cMu75Fn2GZDkJhSlSMkCGCiHPHJ28jmwQJBAOLBqMerpn6zGg7M1Ava7dDfN6xEJYZCVry5qwiAwUCvWqZoiXmSk93JD24J8VYxhXE4R43pAPaHbkfMH9K3pckCQQCXFGzMnk4Wbdb2WrQhbYjuI0TbS8dNWsqL/q+BsVuJfunfLkNRFWWpKJMvZ0s/km6bUO6o8/yJBEOIEJ12bneZAkBrHg7MHBMvwQ2rNilpXEeBpyDWmO6dCNOOnark/mXMV8nb2IAw1QRS/frzAXFnRUeDX5G1wP86dmmrMwtJcIW5AkAk2X2EFySywc3Jvuvqr13Tv9R+rKgLDRcUOGW0e375NXRz1EPlFhP6w4PK7yXidqP1zDK6EPDKtriGCo9toJoZAkEA1J2VGk+rJ3QzJmXVvbP+y3dy1+j3cG+NtYPYmTcIfeibSC3sUgwprB/IqtN/FJkOOYr5bKdkrNBnVXaVbyxQCA==";
            // String a = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALU4RTLyYzCJ+GVyA7rjDEvfA5n2aD2E2ESLdhbDCBJmoko4apqjTtqEln8Bu8qjvtISQZvWNu+69L22vVKJ+aF+MrwTGSGlmD5AaqPyJAsII+HsaWeTwlivwsEZKeSy9Dre7JUd/C9xAttmhIrWxzHLbZWti5rSBVjqtVh5kbkFAgMBAAECgYEAhMhaS942t5Ygn5RPD1zV4bHi3aki8BCqtm7JdBwrifAR8fgshGosmQiVjTIj+3LHmv0tfZYUYsvGEEwZpKTcdy/+R0E0SdLAcy26jcsGytRF+2e+mdTCqPO/v+TREmRzLugJ/DyB2N90Y4DQ1rPT96lU9wkl12cmg0mu3hLq9PECQQDjJV3QhSDIFzSB6g1D1ggUJa+a0YQ5x1h7SW50VgX0GDORBza7h/Xd2hC5a0G+R3ylxRGcMTM0EYwfEFs7THyfAkEAzD1qi7O9RZkPeAQI+LPeWBBztiuHuUbdMt7vi88TiWl6HoeVEYm2bCvpHv2m0CbvyEvm379v4ramWLG4ogrD2wJBAN9YBn7Z5/nWl1KGiLSC5z6oL5rTGGiXknDnKyxIyWHRyiJ4LWEHtsGFgZxP0jz+nZmBxGcIGYjBGD4THyGXe5ECQACx6M3m46r79ViEMv+dpREJpUsmZMTVn+UPayM/piJekILQFUrE/DDsCVQIquukZkZ5M6TpQtkzo+dDW4x5mucCQGuk0nWx6zoLJCEKAalHZfyHZvKTunqAsqrioOQjmFa1u7gzGVDHYTPUNuFckdcqxXKrdZCjTESYUJMz8+PdoQg=";
            String decryptData2 = decrypt(encryptData1, getPrivateKey(privateString));
            System.out.println("解密后内容2:" + decryptData2);


            String encryptData = encrypt(data, getPublicKey(publicString));
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            String decryptData = decrypt(encryptData, getPrivateKey(privateString));
            System.out.println("解密后内容:" + decryptData);

            // RSA签名
            String sign = sign(data, getPrivateKey(privateString));
            System.out.println(sign);
            // RSA验签
            boolean result = verify(data, getPublicKey(publicString), sign);
            System.out.println("验签结果:" + result);
            //   System.out.println("Md5加密："+ Md5.encodePassword("zhangsan12"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }
    }

    public static void testGy(){
        String data = "YsK01@KILL";
        String encryptData1 = null;
        String gy ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPeUeYgGoouu2mwkf2gNO3604cLJFhQTb1d8Xat8F7DhNkbK5D0Op4KxJQbIk/cI+r8QqoyRfdx8NOJZyD1n8ELoV1SlJpDAJ4JoLZ6/lh2GmgIdzkpHwWvObXogAqVP7mSidNxHZwwYnyI8PMvQyJE/cO4aWe7nfcoizJN8xbEQIDAQAB";
        try {
            encryptData1 = encrypt(data, getPublicKey(gy));
        } catch (Exception e){

        }
        System.out.println("加密后内容1:\n" + encryptData1);
    }

    public static void main(String[] args) {
            testGy();
    }
}