package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.binint;
import com.raugfer.crypto.dict;
import com.raugfer.crypto.transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TronscanAPI implements Service {

    private final String baseUrl;
    private final boolean testnet;

    public TronscanAPI(String baseUrl, boolean testnet) {
        this.baseUrl = baseUrl;
        this.testnet = testnet;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "system/status";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            long height = data.getJSONObject("database").getLong("confirmedBlock");
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
            String url = baseUrl + "account?address=" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            long balance = data.getLong("balance");
            return BigInteger.valueOf(balance);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl + "transfer?address=" + address+ "&limit=50&token=_";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray items = data.getJSONArray("data");
            List<HistoryItem> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("transactionHash");
                long block = item.optLong("block", Long.MAX_VALUE);
                int time = (int)(item.optLong("timestamp", 0) / 1000);
                BigInteger fee = BigInteger.ZERO;
                BigInteger value = BigInteger.valueOf(item.optLong("amount", 0));
                BigInteger amount = BigInteger.ZERO;
                String source = item.optString("transferFromAddress", null);
                if (address.equalsIgnoreCase(source)) amount = amount.subtract(value);
                String target = item.optString("transferToAddress", null);
                if (address.equalsIgnoreCase(target)) amount = amount.add(value);
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
        return new ArrayList<>();
    }

    @Override
    public long getSequence(String address) {
        return 0;
    }

    @Override
    public String broadcast(String _transaction) {
        try {
            String url = baseUrl + "broadcast";
            String content = "{\"transaction\":\"" + _transaction + "\"}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            boolean success = data.optBoolean("success", false);
            if (!success) throw new IllegalArgumentException(data.getString("message"));
            byte[] txn = binint.h2b(_transaction);
            String txnid = transaction.txnid(txn, "tron", testnet);
            return txnid;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        if (name.equals("block")) {
            try {
                String url = baseUrl + "block/latest";
                JSONObject data = new JSONObject(Network.urlFetch(url));
                dict fields = new dict();
                fields.put("hash", data.getString("hash"));
                fields.put("height", BigInteger.valueOf(data.getLong("number")));
                fields.put("timestamp", BigInteger.valueOf(data.getLong("timestamp")));
                return fields;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
