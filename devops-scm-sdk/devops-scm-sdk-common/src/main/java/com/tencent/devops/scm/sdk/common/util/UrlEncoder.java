package com.tencent.devops.scm.sdk.common.util;

import com.tencent.devops.scm.sdk.common.exception.ScmSdkException;

import java.net.URLEncoder;

public class UrlEncoder {

    public static String urlEncode(String s) {
        String encoded = urlEncodeUtf8(s);
        // Since the encode method encodes plus signs as %2B,
        // we can simply replace the encoded spaces with the correct encoding here
        encoded = encoded.replace("+", "%20");
        encoded = encoded.replace(".", "%2E");
        encoded = encoded.replace("-", "%2D");
        encoded = encoded.replace("_", "%5F");
        return encoded;
    }

    public static String urlEncodeUtf8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            throw new ScmSdkException(e);
        }
    }
}
