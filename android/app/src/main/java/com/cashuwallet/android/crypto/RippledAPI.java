package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RippledAPI implements Service {

    private static final int RIPPLE_EPOCH = 946684800; // 2000-01-01T00:00:00Z

    private final String baseUrl;

    public RippledAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl;
            String content = "{\"method\":\"ledger_closed\",\"params\":[{}]}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            data = data.getJSONObject("result");
            long height = data.getLong("ledger_index");
            return height;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            String url = baseUrl;
            String content = "{\"method\":\"server_state\",\"params\":[{}]}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            data = data.getJSONObject("result");
            JSONObject state = data.getJSONObject("state");
            long fee = state.getJSONObject("validated_ledger").getLong("base_fee");
            long base = state.getLong("load_base");
            long factor = state.getLong("load_factor");
            return BigInteger.valueOf((fee * base) / factor);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl;
            String content = "{\"method\":\"account_info\",\"params\":[{\"account\":\"" + address + "\"}]}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            data = data.getJSONObject("result");
            String status = data.getString("status");
            if (!status.equals("success")) {
                String error = data.getString("error");
                if (error.equals("actNotFound")) return BigInteger.ZERO;
                return null;
            }
            data = data.getJSONObject("account_data");
            String balance = data.getString("Balance");
            return new BigInteger(balance);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl;
            String content = "{\"method\":\"account_tx\",\"params\":[{\"account\":\"" + address + "\",\"limit\":100,\"forward\":true,\"ledger_index_min\":" + height + "}]}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            data = data.getJSONObject("result");
            JSONArray items = data.getJSONArray("transactions");
            List<HistoryItem> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject tx = item.getJSONObject("tx");
                JSONObject meta = item.getJSONObject("meta");
                String hash = tx.getString("hash");
                int time = RIPPLE_EPOCH + tx.getInt("date");
                long block = tx.optLong("ledger_index", Long.MAX_VALUE);
                BigInteger fee = new BigInteger(tx.optString("Fee", "0"));
                BigInteger amount = BigInteger.ZERO;
                Object choice = meta.opt("delivered_amount");
                BigInteger value = choice instanceof String ? new BigInteger((String) choice) : BigInteger.ZERO;
                String source = tx.optString("Account", null);
                if (address.equals(source)) {
                    amount = amount.subtract(value);
                    amount = amount.subtract(fee);
                }
                String target = tx.optString("Destination", null);
                if (address.equals(target)) amount = amount.add(value);
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
        try {
            String url = baseUrl;
            String content = "{\"method\":\"account_info\",\"params\":[{\"account\":\"" + address + "\"}]}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            data = data.getJSONObject("result");
            String status = data.getString("status");
            if (!status.equals("success")) {
                String error = data.getString("error");
                if (error.equals("actNotFound")) return 0;
                return -1;
            }
            data = data.getJSONObject("account_data");
            return data.getLong("Sequence");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String broadcast(String transaction) {
        try {
            String url = baseUrl;
            String content = "{\"method\":\"submit\",\"params\":[{\"tx_blob\":\"" + transaction.toUpperCase() + "\"}]}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            data = data.getJSONObject("result");
            data = data.getJSONObject("tx_json");
            return data.getString("hash");
        } catch (Exception e) {
            return null;
        }
    }

}
