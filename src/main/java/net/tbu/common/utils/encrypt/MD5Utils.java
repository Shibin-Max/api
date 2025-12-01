package net.tbu.common.utils.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String md5Encrypt(String message) {
        try {
            // 创建 MD5 MessageDigest 实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 获取字节数组
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

            // 更新 MD5 处理
            md.update(bytes);

            // 获取 MD5 摘要（结果是一个字节数组）
            byte[] digest = md.digest();

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
