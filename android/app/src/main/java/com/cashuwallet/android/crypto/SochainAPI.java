package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SochainAPI implements Service {

    private final String baseUrl;

    public SochainAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String urlFetch(String url) throws IOException {
        return urlFetch(url, null);
    }

    private String urlFetch(String url, String content) throws IOException {
        try {
            return content == null ? Network.urlFetch(url) : Network.urlFetch(url, content);
        } catch (FileNotFoundException e) {
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
        return content == null ? Network.urlFetch(url) : Network.urlFetch(url, content);
    }

    // SoChain API v3: baseUrl is "https://sochain.com/api/v3/*/<NETWORK>",
    // where "*" is replaced by the endpoint. Amounts are decimal strings.

    private static BigInteger toUnits(String value) {
        return new BigDecimal(value).movePointRight(8).toBigIntegerExact();
    }

    private String endpoint(String name) {
        return baseUrl.replace("*", name);
    }

    @Override
    public long getHeight() {
        try {
            String url = endpoint("network_info");
            JSONObject data = new JSONObject(urlFetch(url));
            data = data.getJSONObject("data");
            long height = data.getLong("block_count");
            return height;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            long height = getHeight();
            if (height == -1) return null;
            String url = endpoint("block") + "/" + height;
            JSONObject data = new JSONObject(urlFetch(url));
            data = data.getJSONObject("data");
            BigInteger fees = toUnits(data.getString("fees"));
            BigInteger size = BigInteger.valueOf(data.getLong("size"));
            BigInteger fee = fees.multiply(BigInteger.valueOf(1024)).divide(size);
            return fee.max(BigInteger.valueOf(1024));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = endpoint("balance") + "/" + address;
            JSONObject data = new JSONObject(urlFetch(url));
            data = data.getJSONObject("data");
            BigInteger confirmed = toUnits(data.getString("confirmed"));
            BigInteger unconfirmed = toUnits(data.getString("unconfirmed"));
            return confirmed.add(unconfirmed);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            List<HistoryItem> list = new ArrayList<>();
            String url = endpoint("transactions") + "/" + address + "?from_height=" + height + "&page_size=100";
            for (int page = 0; page < 100; page++) {
                JSONObject data = new JSONObject(urlFetch(url));
                data = data.getJSONObject("data");
                JSONArray items = data.getJSONArray("transactions");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    HistoryItem o = new HistoryItem();
                    o.hash = item.getString("hash");
                    o.time = item.optInt("time", 0);
                    o.block = item.isNull("block") ? Long.MAX_VALUE : item.getLong("block");
                    o.amount = toUnits(item.getString("balance_change"));
                    o.fee = BigInteger.ZERO; // TODO information missing
                    list.add(o);
                }
                if (data.isNull("next_cursor")) return list;
                url = endpoint("transactions") + "/" + address + "?cursor=" + URLEncoder.encode(data.getString("next_cursor"), "UTF-8");
            }
            return null; // too many to page through here; leave it to the other services
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        try {
            List<UTXO> list = new ArrayList<>();
            for (int page = 1; ; page++) {
                String url = endpoint("unspent_outputs") + "/" + address + "/" + page;
                JSONObject data = new JSONObject(urlFetch(url));
                JSONArray items = data.getJSONObject("data").getJSONArray("outputs");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    UTXO o = new UTXO();
                    o.hash = item.getString("hash");
                    o.index = item.getInt("index");
                    o.amount = toUnits(item.getString("value"));
                    list.add(o);
                }
                if (items.length() < 10) return list; // ten per page
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getSequence(String address) {
        return 0;
    }

    @Override
    public String broadcast(String transaction) {
        return null; // SoChain's API has no broadcast; the other services send it
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
