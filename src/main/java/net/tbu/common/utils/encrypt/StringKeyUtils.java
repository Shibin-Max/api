package net.tbu.common.utils.encrypt;

import net.tbu.exception.CustomizeRuntimeException;

import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class StringKeyUtils {

    private StringKeyUtils() {
    }

    private final static ZoneId jiliZoneId = ZoneId.of("UTC-4");

    private final static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMd");

    public static String queryStringKey(
            Map<String, Object> requestMD5Params, String agentId, String agentKey) {
        ZonedDateTime utc4DateTime = ZonedDateTime.now(jiliZoneId);
        String dateFormatted = fmt.format(utc4DateTime);
        String keyGEncode;
        String keyEncode;
        try {
            String valueMap = buildParamsString(requestMD5Params);
            keyGEncode = md5Encode(dateFormatted + agentId + agentKey);
            keyEncode = md5Encode(valueMap + keyGEncode);
        } catch (Exception e) {
            throw new CustomizeRuntimeException(e.getMessage());
        }
        return generateRandomSixCharString() + keyEncode + generateRandomSixCharString();
        // ?Token=pretest005361710496203220&GameId=87&Lang=en-US&HomeUrl=string&Platform=C66&AgentId=Bingoplus_Seamless
    /*加密流程請參考以下範例
    1. dateStr = 240315
    2. agentId = Bingoplus_Seamless
    3. agentKey = 09f25b953e59beb91dabd4a8f210aa2ee57e267e
    4. keyG = MD5(240315Bingoplus_Seamless09f25b953e59beb91dabd4a8f210aa2ee57e267e)
            = 875b3f81af28c6ce531718416fb90d91
    5. params= Token=pretest005361710495308624&GameId=87&Lang=en-US&AgentId=Bingoplus_Seamless
    6. key = 000000 + MD5(params + keyG) + 000000
            = 000000fda47595bb66443baeea10a14ddc3d48000000*/
    }

    public static String buildParamsString(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    /**
     * agent md5
     *
     * @param origin origin
     * @return String String
     * @throws Exception @Author edwin.c @Data Apr 18, 2013 1:30:45 PM
     */
    public static String md5Encode(String origin) throws Exception {
        String resultString;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {
            throw new Exception("Encryption password failure!");
        }
        return resultString;
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte value : b) {
            resultSb.append(byteToHexString(value));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    private static final String[] HEX_DIGITS = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    };

    private static String generateRandomSixCharString() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

}
