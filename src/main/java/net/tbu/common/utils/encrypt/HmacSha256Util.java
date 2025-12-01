package net.tbu.common.utils.encrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacSha256Util {

    public static String hmacSHA256(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return bytesToHex(hmacBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean verifyHmacSha256(String data, String secret, String signature) throws NoSuchAlgorithmException, InvalidKeyException {
        String computedSig = hmacSHA256(data, secret);
        return computedSig.equals(signature);
    }

    public static void main(String[] args) {
        try {
            String X = "LVO86bzw7hb46R7frYec/gRSsh6V3+qmGdWcEP3bjnlnUAyYnBPCYSWTRPnnKzsT";
            String secret = "vQgksQimJNjRsrquQbX11stK";

            String data="BJzJeWCakWEeGfpm6B1dmBlv8GxFP6SMDFRierU6x7vYEWURlG6kod/Z5PXl9ZNmOYK676uSQHrDoXlx2SPuP0CPlr6dKzanL5eAaj+bF8wpCLci3ETHxqqaOm6zZLOg8STnFCcSz16/f+dMqi5FTxFaR2l9X28qfF4hKfcvylkFd21A0ITfABD+7yQVhbygmd2faxaAp+MPefrjzOE6o/XMPTcsNOOo+QcxaN46uF/zCEqdRtDtPFWbbB85G3QL";
            // 生成签名
            String signature = hmacSHA256(data, secret); // 67172b322e174cee89ff36d9e5a34a237f9e3e9a95d6827da8f87b1ca2b2cf55
            System.out.println("Generated Signature: " + signature);
            // 08b4fd5c9db0eaf3616dec38658aacf8e346433857d080634c3271d05db88609
            // 验签
            boolean isValid = verifyHmacSha256(data, secret, signature);
            System.out.println("Is Signature Valid: " + isValid);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}