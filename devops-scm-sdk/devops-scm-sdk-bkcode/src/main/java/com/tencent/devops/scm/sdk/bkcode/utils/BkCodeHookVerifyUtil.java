package com.tencent.devops.scm.sdk.bkcode.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class BkCodeHookVerifyUtil {
    public static boolean verifySignature(String payloadBody, String secretToken, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretToken.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] digest = mac.doFinal(payloadBody.getBytes());
            String expectedSignature = "sha256=" + bytesToHex(digest);

            return MessageDigest.isEqual(signature.getBytes(), expectedSignature.getBytes());
        } catch (Exception e) {
            return false;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
