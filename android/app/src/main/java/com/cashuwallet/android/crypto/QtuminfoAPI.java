package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.coins;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QtuminfoAPI implements Service {

    private final String baseUrl;

    public QtuminfoAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "info";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return data.getLong("height");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            String url = baseUrl + "info";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            BigDecimal value = new BigDecimal(data.getString("feeRate"));
            return value.multiply(BigDecimal.TEN.pow(8)).toBigInteger();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "address/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            BigInteger confirmed = new BigInteger(data.getString("balance"));
            BigInteger unconfirmed = new BigInteger(data.getString("unconfirmed"));
            return confirmed.add(unconfirmed);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl + "address/" + address + "/basic-txs";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray items = data.optJSONArray("transactions");
            List<HistoryItem> list = new ArrayList<>();
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String hash = item.getString("id");
                    long block = item.optLong("blockHeight", -1);
                    int time = item.optInt("timestamp", (int) (System.currentTimeMillis() / 1000));
                    BigInteger fee = new BigInteger(item.optString("fees", "0"));
                    BigInteger amount = new BigInteger(item.optString("amount", "0"));
                    String type = item.optString("type", "send");
                    if (type.equals("send")) amount = amount.negate();
                    HistoryItem o = new HistoryItem();
                    o.hash = hash;
                    o.time = time;
                    o.block = block;
                    o.amount = amount;
                    o.fee = fee;
                    list.add(o);
                }
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        try {
            String url = baseUrl + "address/" + address + "/utxo";
            JSONArray items = new JSONArray(Network.urlFetch(url));
            List<UTXO> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("transactionId");
                int index = (int)item.getLong("outputIndex");
                BigInteger amount = new BigInteger(item.getString("value"));
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
            String url = baseUrl + "tx/send";
            String content = "rawtx=" + transaction;
            JSONObject data = new JSONObject(Network.urlFetch(url, "POST", content, "application/x-www-form-urlencoded"));
            return data.getString("id");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
