package com.keymetrics.helper;

import org.springframework.http.HttpHeaders;

public class HttpHelper {

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }
}
