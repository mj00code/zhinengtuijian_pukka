package com.ubo.iptv.core.util;

import javafx.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class UrlEditUtil {

    /**
     * 通过url获取categoryId
     * Intelligent recommendation
     */
    public static Pair<String, Map<String, String>> getCategoryId(String pageUrl) {
        try {
            pageUrl = URLDecoder.decode(pageUrl, "utf-8");
            int start = pageUrl.indexOf("/", 8);
            int end = pageUrl.indexOf("?");
            String path = pageUrl.substring(start == -1 ? 0 : start, end == -1 ? pageUrl.length() : end);
            Map<String, String> params = new HashMap<>();
            if (end == -1) {
                return new Pair(path, params);
            }
            String query = pageUrl.substring(end + 1, pageUrl.length());
            if (query.length() > 0) {
                if (query.contains("&")) {
                    String[] arr = query.split("&");
                    for (String param : arr) {
                        if (param.contains("=")) {
                            String[] arr2 = param.split("=");
                            params.put(arr2[0], arr2[1]);
                        }
                    }
                } else {
                    if (query.contains("=")) {
                        String[] arr2 = query.split("=");
                        params.put(arr2[0], arr2[1]);
                    }
                }
            }
            Pair pair = new Pair(path, params);
            return pair;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}