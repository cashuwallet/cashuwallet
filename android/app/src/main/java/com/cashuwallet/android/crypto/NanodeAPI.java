package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NanodeAPI implements Service {

    private final String baseUrl;

    public NanodeAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "summary/network";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            long height = data.getLong("block_count");
            return height;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "account?id=" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONObject info = data.getJSONObject("info");
            if (info.optString("error", "").equals("Account not found")) {
                BigInteger balance = BigInteger.ZERO;
                JSONArray items = data.getJSONArray("pending");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    BigInteger amount = new BigInteger(item.getString("amount"));
                    balance = balance.add(amount);
                }
                return balance;
            }
            BigInteger balance = new BigInteger(info.getString("balance"));
            BigInteger pending = new BigInteger(info.getString("pending"));
            return balance.add(pending);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl + "account?id=" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            int time = ((int) System.currentTimeMillis() / 1000);
            List<HistoryItem> list = new ArrayList<>();
            // TODO show unpocketed transactions as pending
            /*
            JSONArray items1 = data.getJSONArray("pending");
            for (int i = 0; i < items1.length(); i++) {
                JSONObject item = items1.getJSONObject(i);
                String hash = item.getString("hash");
                String timestamp = item.optString("date", null);
                if (timestamp != null) time = (int) (dateFormat.parse(timestamp).getTime() / 1000);
                BigInteger amount = new BigInteger(item.getString("amount"));
                BigInteger fee = BigInteger.ZERO;
                HistoryItem o = new HistoryItem();
                o.hash = hash;
                o.time = time;
                o.block = Long.MAX_VALUE; // TODO improve
                o.amount = amount;
                o.fee = fee;
                list.add(o);
            }
            */
            JSONObject info = data.getJSONObject("info");
            if (info.optString("error", "").equals("Account not found")) return list;
            JSONArray items2 = data.getJSONArray("history");
            for (int i = 0; i < items2.length(); i++) {
                JSONObject item = items2.getJSONObject(i);
                String hash = item.getString("hash");
                String timestamp = item.optString("date", null);
                if (timestamp != null) time = (int) (dateFormat.parse(timestamp).getTime() / 1000);
                String type = item.getString("type");
                BigInteger amount = new BigInteger(item.getString("amount"));
                if (type.equals("send")) amount = amount.negate();
                BigInteger fee = BigInteger.ZERO;
                HistoryItem o = new HistoryItem();
                o.hash = hash;
                o.time = time;
                o.block = 0; // TODO improve
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
            String url = baseUrl + "account?id=" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            List<UTXO> list = new ArrayList<>();
            JSONArray items = data.getJSONArray("pending");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("hash");
                int index = i + 1;
                BigInteger amount = new BigInteger(item.getString("amount"));
                UTXO o = new UTXO();
                o.hash = hash;
                o.index = index;
                o.amount = amount;
                list.add(o);
            }
            JSONObject info = data.getJSONObject("info");
            if (info.optString("error", "").equals("Account not found")) {
                String hash = "0000000000000000000000000000000000000000000000000000000000000000";
                int index = 0;
                BigInteger amount = BigInteger.ZERO;
                UTXO o = new UTXO();
                o.hash = hash;
                o.index = index;
                o.amount = amount;
                list.add(o);
            } else {
                String hash = info.getString("frontier");
                int index = 0;
                BigInteger amount = new BigInteger(info.getString("balance"));
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
        // TODO find an option
        return null;
    }

    @Override
    public Object custom(String name, Object arg) {
        // TODO find an option
        return null;
    }

}
