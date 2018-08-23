package com.cashuwallet.android;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class Network {

    public static String urlFetch(String url) throws IOException {
        return urlFetch(url, "GET", null);
    }

    public static String urlFetch(String url, int timeout) throws IOException {
        return urlFetch(url, "GET", null, timeout);
    }

    public static String urlFetch(String url, String content) throws IOException {
        return urlFetch(url, "POST", content);
    }

    public static String urlFetch(String url, String content, int timeout) throws IOException {
        return urlFetch(url, "POST", content, timeout);
    }

    public static String urlFetch(String url, String method, String content) throws IOException {
        return urlFetch(url, method, content, "application/json");
    }

    public static String urlFetch(String url, String method, String content, int timeout) throws IOException {
        return urlFetch(url, method, content, "application/json", timeout);
    }

    public static String urlFetch(String url, String method, String content, String contentType) throws IOException {
        return urlFetch(url, method, content, contentType, 5);
    }

    public static String urlFetch(String url, String method, String content, String contentType, int timeout) throws IOException {
        try {
            return _urlFetch(url, method, content, contentType, timeout);
        } catch (NetworkOnMainThreadException e) {
            ExecutorService exec = MainApplication.app().getExec();
            AsyncUrlFetch async = new AsyncUrlFetch();
            String s;
            try {
                s = async.executeOnExecutor(exec, url, method, content, contentType, Integer.toString(timeout)).get();
            } catch (InterruptedException|ExecutionException ue) {
                throw new IOException(ue.getMessage());
            }
            if (async.exception != null) throw async.exception;
            return s;
        }
    }

    private static class AsyncUrlFetch extends AsyncTask<String, Void, String> {
        private IOException exception;

        @Override
        protected String doInBackground(String... objects) {
            String url = objects[0];
            String method = objects[1];
            String content = objects[2];
            String contentType = objects[3];
            int timeout = Integer.parseInt(objects[4]);
            try {
                return _urlFetch(url, method, content, contentType, timeout);
            } catch (IOException e) {
                exception = e;
                return null;
            }
        }
    };

    private static String _urlFetch(String url, String method, String content, String contentType, int timeout) throws IOException {
        if (MainApplication.app().shuttingDown()) throw new IOException("App shutting down");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setConnectTimeout(timeout*1000);
        connection.setReadTimeout(timeout*1000);
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
