package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return Network.urlFetch(url, content);
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl.replace("*", "get_info");
            JSONObject data = new JSONObject(urlFetch(url));
            data = data.getJSONObject("data");
            long height = data.getLong("blocks");
            return height;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            String url1 = baseUrl.replace("*", "get_info");
            JSONObject data1 = new JSONObject(urlFetch(url1));
            data1 = data1.getJSONObject("data");
            long height = data1.getLong("blocks");
            String url2 = baseUrl.replace("*", "block") + "/" + height;
            JSONObject data2 = new JSONObject(urlFetch(url2));
            data2 = data2.getJSONObject("data");
            BigInteger fees = new BigDecimal(data2.getString("fee")).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
            BigInteger size = BigInteger.valueOf(data2.getLong("size"));
            BigInteger fee = fees.multiply(BigInteger.valueOf(1024)).divide(size);
            return fee.max(BigInteger.valueOf(1024));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl.replace("*", "get_address_balance") + "/" + address;
            JSONObject data = new JSONObject(urlFetch(url));
            data = data.getJSONObject("data");
            BigDecimal confirmed = new BigDecimal(data.getString("confirmed_balance"));
            BigDecimal unconfirmed = new BigDecimal(data.getString("unconfirmed_balance"));
            BigDecimal balance = confirmed.add(unconfirmed);
            balance = balance.multiply(BigDecimal.TEN.pow(8));
            return balance.toBigIntegerExact();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url1 = baseUrl.replace("*", "block") + "/" + height;
            JSONObject data1 = new JSONObject(urlFetch(url1));
            data1 = data1.getJSONObject("data");
            JSONArray items1 = data1.getJSONArray("txs");
            String after = items1.getJSONObject(0).getString("txid");
            class Data {
                long confirmations;
                int time;
                BigInteger amount = BigInteger.ZERO;
            };
            Map<String, Data> map = new HashMap<>();
            String url2 = baseUrl.replace("*", "get_tx_received") + "/" + address + "/" + after;
            JSONObject data2 = new JSONObject(urlFetch(url2));
            data2 = data2.getJSONObject("data");
            JSONArray items2 = data2.getJSONArray("txs");
            for (int i = 0; i < items2.length(); i++) {
                JSONObject item = items2.getJSONObject(i);
                String hash = item.getString("txid");
                long confirmations = item.optLong("confirmations", 0);
                int time = item.optInt("time", 0);
                BigInteger value = new BigDecimal(item.optString("value", "0")).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
                Data data = map.get(hash);
                if (data == null) data = new Data();
                data.confirmations = confirmations;
                data.time = time;
                data.amount = data.amount.add(value);
                map.put(hash, data);
            }
            String url3 = baseUrl.replace("*", "get_tx_spent") + "/" + address + "/" + after;
            JSONObject data3 = new JSONObject(urlFetch(url3));
            data3 = data3.getJSONObject("data");
            JSONArray items3 = data3.getJSONArray("txs");
            for (int i = 0; i < items3.length(); i++) {
                JSONObject item = items3.getJSONObject(i);
                String hash = item.getString("txid");
                long confirmations = item.optLong("confirmations", 0);
                int time = item.optInt("time", 0);
                BigInteger value = new BigDecimal(item.optString("value", "0")).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
                Data data = map.get(hash);
                if (data == null) data = new Data();
                data.confirmations = confirmations;
                data.time = time;
                data.amount = data.amount.subtract(value);
                map.put(hash, data);
            }
            String url4 = baseUrl.replace("*", "get_info");
            JSONObject data4 = new JSONObject(urlFetch(url4));
            data4 = data4.getJSONObject("data");
            long blocks = data4.getLong("blocks");
            List<HistoryItem> list = new ArrayList<>();
            for (Map.Entry<String, Data> entry : map.entrySet()) {
                String hash = entry.getKey();
                Data data = entry.getValue();
                long block = data.confirmations > 0 ? (blocks - (data.confirmations-1)) : Long.MAX_VALUE;
                int time = data.time;
                BigInteger fee = BigInteger.ZERO; // TODO information missing
                BigInteger amount = data.amount;
                HistoryItem o = new HistoryItem();
                o.hash = hash;
                o.time = time;
                o.block = block;
                o.amount = amount;
                o.fee = fee;
                list.add(o);
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        try {
            String url = baseUrl.replace("*", "get_tx_unspent") + "/" + address;
            JSONObject data = new JSONObject(urlFetch(url));
            JSONArray items = data.getJSONObject("data").getJSONArray("txs");
            List<UTXO> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("txid");
                int index = (int)item.getLong("output_no");
                BigInteger amount = new BigDecimal(item.getString("value")).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
                UTXO o = new UTXO();
                o.hash = hash;
                o.index = index;
                o.amount = amount;
                list.add(o);
            }
            return list;
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
        try {
            String url = baseUrl.replace("*", "send_tx");
            String content = "{\"tx_hex\":\"" + transaction + "\"}";
            JSONObject data = new JSONObject(urlFetch(url, content));
            data = data.getJSONObject("data");
            return data.getString("txid");
        } catch (Exception e) {
            return null;
        }
    }

}
