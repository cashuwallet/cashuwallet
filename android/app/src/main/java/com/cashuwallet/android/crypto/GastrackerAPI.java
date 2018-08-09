package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GastrackerAPI implements Service {

    private final String baseUrl;

    public GastrackerAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "status";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            data = data.getJSONObject("block");
            long height = data.optLong("height", 0);
            return height;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        return null;
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "addr/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            data = data.getJSONObject("balance");
            BigInteger balance = new BigInteger(data.optString("wei", "0"));
            return balance;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl + "addr/" + address + "/transactions";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray items = data.getJSONArray("items");
            List<HistoryItem> list = new ArrayList<>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("hash");
                long block = Long.parseLong(item.optString("height", Long.toString(Long.MAX_VALUE)));
                int time  = (int)(dateFormat.parse(item.getString("timestamp")).getTime()/1000);
                BigInteger fee = BigInteger.ZERO; // TODO missing fee
                BigInteger value = new BigInteger(item.getJSONObject("value").optString("wei", "0"));
                BigInteger amount = BigInteger.ZERO;
                String source = item.optString("from", null);
                if (address.equalsIgnoreCase(source)) {
                    amount = amount.subtract(value);
                    amount = amount.subtract(fee);
                }
                String target = item.optString("to", null);
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
            return -1;
    }

    @Override
    public String broadcast(String transaction) {
        return null;
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
