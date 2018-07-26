package com.cashuwallet.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Network {

    public static String urlFetch(String url) throws IOException {
        return urlFetch(url, "GET", null, "application/json");
    }

    public static String urlFetch(String url, String content) throws IOException {
        return urlFetch(url, "POST", content, "application/json");
    }

    public static String urlFetch(String url, String method, String content) throws IOException {
        return urlFetch(url, method, content, "application/json");
    }

    public static String urlFetch(String url, String method, String content, String contentType) throws IOException {
        if (MainApplication.app().shuttingDown()) throw new IOException("App shutting down");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod(method);
        if (content != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", contentType);
            OutputStream out = connection.getOutputStream();
            out.write(content.getBytes());
            out.close();
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        try {
            String line;
            while ((line = in.readLine()) != null) sb.append(line).append('\n');
        } finally {
            in.close();
        }
        return sb.toString();
    }

}
