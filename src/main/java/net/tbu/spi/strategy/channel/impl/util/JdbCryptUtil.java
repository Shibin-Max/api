package net.tbu.spi.strategy.channel.impl.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tbu.json.JsonUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JdbCryptUtil {


    public static String encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        int plainTextLength = dataBytes.length;
        if (plainTextLength % blockSize != 0) {
            plainTextLength = plainTextLength + (blockSize - plainTextLength % blockSize);
        }
        byte[] plaintext = new byte[plainTextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);
        return Base64.encodeBase64URLSafeString(encrypted);
    }


    public static String safeEncrypt(Object obj, String key, String iv) {
        try {
            String json = JsonUtils.toJsonString(obj);
            return JdbCryptUtil.encrypt(json, key, iv);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    public static String decrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"),
                new IvParameterSpec(iv.getBytes()));
        return new String(cipher.doFinal(Base64.decodeBase64(data)));
    }

    // Sample
    public static void main(String[] args) throws Exception {
        String encryptData = "GhKRVa6BHoOJAhjVYMKuFw";
        String key = "key1234567Sample"; // $ { KEY }
        String iv = "iv12345678Sample"; // $ { IV }
        System.out.println(decrypt(encryptData, key, iv)); // return “SampleData”
    }

}
