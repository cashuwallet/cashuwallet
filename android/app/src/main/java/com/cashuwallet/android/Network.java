package com.cashuwallet.android;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36");
        if (content != null) headers.put("Content-Type", contentType);
        String[] result = _insertCredentials(url, content, headers);
        url = result[0];
        content = result[1];
        try {
            return _urlFetch(url, method, content, headers, timeout);
        } catch (NetworkOnMainThreadException e) {
            ExecutorService exec = MainApplication.app().getExec();
            AsyncUrlFetch async = new AsyncUrlFetch();
            String s;
            try {
                s = async.executeOnExecutor(exec, url, method, content, new JSONObject(headers).toString(), Integer.toString(timeout)).get();
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
            JSONObject data; try { data = new JSONObject(objects[3]); } catch (JSONException e) { data = new JSONObject(); }
            int timeout = Integer.parseInt(objects[4]);
            HashMap<String, String> headers = new HashMap<>();
            for (Iterator iterator = data.keys(); iterator.hasNext(); ) {
                String key = (String)iterator.next();
                String value = data.optString(key, null);
                headers.put(key, value);
            }
            try {
                return _urlFetch(url, method, content, headers, timeout);
            } catch (IOException e) {
                exception = e;
                return null;
            }
        }
    };

    private static String _urlFetch(String url, String method, String content, HashMap<String, String> headers, int timeout) throws IOException {
        if (MainApplication.app().shuttingDown()) throw new IOException("App shutting down");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setConnectTimeout(timeout*1000);
        connection.setReadTimeout(timeout*1000);
        connection.setRequestMethod(method);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            connection.setRequestProperty(key, value);
        }
        if (content != null) {
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            out.write(content.getBytes());
            out.close();
        }
        int status = connection.getResponseCode();
        boolean error = status < 200 || status >= 300;
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(error ? connection.getErrorStream() : connection.getInputStream()));
        try {
            String line;
            while ((line = in.readLine()) != null) sb.append(line).append('\n');
        } finally {
            in.close();
        }
        if (status == 429) {
            String header = connection.getHeaderField("Retry-After");
            if (header.matches("\\d+")) {
                int retryAfter = Integer.parseInt(header);
                try {
                    Thread.sleep(retryAfter * 1000);
                } catch (InterruptedException e) {
                    throw new IOException(e.getMessage());
                }
                return _urlFetch(url, method, content, headers, timeout);
            }
        }
        if (error) {
            if (status == 404) throw new FileNotFoundException(sb.toString());
            throw new IOException(sb.toString());
        }
        return sb.toString();
    }

    private static String[] _insertCredentials(String url, String content, HashMap<String, String> headers) {
        if (url.contains("etherscan.io")) {
            try {
                URI uri = new URI(url);
                String query = uri.getQuery();
                String keyval = "apikey=" + ETHERSCAN_APIKEY;
                query = query == null ? keyval : query + "&" + keyval;
                uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
                url = uri.toString();
            } catch (URISyntaxException e) { }
        }
        else
        if (url.contains("bscscan.com")) {
            try {
                URI uri = new URI(url);
                String query = uri.getQuery();
                String keyval = "apikey=" + BSCSCAN_APIKEY;
                query = query == null ? keyval : query + "&" + keyval;
                uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
                url = uri.toString();
            } catch (URISyntaxException e) { }
        }
        else
        if (url.contains("blockcypher.com")) {
            try {
                URI uri = new URI(url);
                String query = uri.getQuery();
                String keyval = "token=" + BLOCKCYPHER_APIKEY;
                query = query == null ? keyval : query + "&" + keyval;
                uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
                url = uri.toString();
            } catch (URISyntaxException e) { }
        }
        else
        if (url.contains("nownodes.io")) {
            try {
                if (content != null) {
                    JSONObject data = new JSONObject(content);
                    data.put("API_key", NOWNODES_APIKEY);
                    content = data.toString();
                }
            } catch (JSONException e) { }
            headers.put("api-key", NOWNODES_APIKEY);
        }
        return new String[]{ url, content };
    }

    private static final String ETHERSCAN_APIKEY = "4CZPSU5199IJ5T66YQJRD2X3JW7SUQWP8H";
    private static final String BSCSCAN_APIKEY = "ZV6XYXGK37PDUPCSIED4K7A63NVWEEJNRF";
    private static final String BLOCKCYPHER_APIKEY = "1acd2d05de634ab7bdce60ee9ba47b99";
    private static final String NOWNODES_APIKEY = "ZwmbBjvevr4JeRgeATb7Y3Ad";

}
