package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BlockcypherAPI implements Service {

    private final String baseUrl;
    private final int confirmations;

    public BlockcypherAPI(String baseUrl) {
        this(baseUrl, 0);
    }

    public BlockcypherAPI(String baseUrl, int confirmations) {
        this.baseUrl = baseUrl;
        this.confirmations = confirmations;
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
            String url = baseUrl;
            JSONObject data = new JSONObject(urlFetch(url));
            return data.getLong("height");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            String url = baseUrl;
            JSONObject data = new JSONObject(urlFetch(url));
            if (confirmations > 6) {
                return BigInteger.valueOf(data.optLong("low_fee_per_kb", 0));
            }
            if (confirmations > 2) {
                return BigInteger.valueOf(data.optLong("medium_fee_per_kb", 0));
            }
            if (confirmations > 0) {
                return BigInteger.valueOf(data.optLong("high_fee_per_kb", 0));
            }
            return BigInteger.valueOf(data.optLong("low_gas_price", 0));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "/addrs/" + address+ "/balance";
            JSONObject data = new JSONObject(urlFetch(url));
            long balance = data.getLong("final_balance");
            return BigInteger.valueOf(balance);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        if (confirmations != 0)
            try {
                String url = baseUrl + "/addrs/" + address + "/full?after=" + height + "&limit=50";
                JSONObject data = new JSONObject(urlFetch(url));
                List<HistoryItem> list = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                JSONArray items = data.optJSONArray("txs");
                if (items != null) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String hash = item.getString("hash");
                        long block = item.optLong("block_height", Long.MAX_VALUE);
                        if (block < 0) block = Long.MAX_VALUE;
                        String timestamp = item.optString("confirmed", "");
                        int time = timestamp.equals("") ? 0 : (int) (dateFormat.parse(timestamp).getTime() / 1000);
                        BigInteger fee = BigInteger.valueOf(item.optLong("fees", 0));
                        BigInteger amount = BigInteger.ZERO;
                        JSONArray inputs = item.optJSONArray("inputs");
                        if (inputs != null) {
                            for (int j = 0; j < inputs.length(); j++) {
                                JSONObject input = inputs.getJSONObject(j);
                                BigInteger value = BigInteger.valueOf(input.optLong("output_value", 0));
                                JSONArray addresses = input.optJSONArray("addresses");
                                if (addresses != null) {
                                    boolean found = false;
                                    for (int k = 0; k < addresses.length(); k++) {
                                        String source = addresses.optString(k, null);
                                        if (address.equals(source)) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (found) amount = amount.subtract(value);
                                }
                            }
                        }
                        JSONArray outputs = item.optJSONArray("outputs");
                        if (outputs != null) {
                            for (int j = 0; j < outputs.length(); j++) {
                                JSONObject output = outputs.getJSONObject(j);
                                BigInteger value = BigInteger.valueOf(output.optLong("value", 0));
                                JSONArray addresses = output.optJSONArray("addresses");
                                if (addresses != null) {
                                    boolean found = false;
                                    for (int k = 0; k < addresses.length(); k++) {
                                        String target = addresses.optString(k, null);
                                        if (address.equals(target)) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (found) amount = amount.add(value);
                                }
                            }
                        }
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
        else
            try {
                String url = baseUrl + "/addrs/" + address + "?after=" + height + "&limit=50";
                JSONObject data = new JSONObject(urlFetch(url));
                List<HistoryItem> list = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                JSONArray items1 = data.optJSONArray("txrefs");
                if (items1 != null) {
                    for (int i = 0; i < items1.length(); i++) {
                        JSONObject item = items1.getJSONObject(i);
                        String hash = item.getString("tx_hash");
                        long block = item.optLong("block_height", Long.MAX_VALUE);
                        if (block < 0) block = Long.MAX_VALUE;
                        String timestamp = item.optString("confirmed", "");
                        int time = timestamp.equals("") ? 0 : (int) (dateFormat.parse(timestamp).getTime() / 1000);
                        BigInteger fee = BigInteger.ZERO; // TODO missing fee
                        BigInteger amount = BigInteger.valueOf(item.optLong("value", 0));
                        if (item.getInt("tx_output_n") == -1) amount = amount.negate();
                        HistoryItem o = new HistoryItem();
                        o.hash = hash;
                        o.time = time;
                        o.block = block;
                        o.amount = amount;
                        o.fee = fee;
                        list.add(o);
                    }
                }
                JSONArray items2 = data.optJSONArray("unconfirmed_txrefs");
                if (items2 != null) {
                    for (int i = 0; i < items2.length(); i++) {
                        JSONObject item = items2.getJSONObject(i);
                        String hash = item.getString("tx_hash");
                        long block = item.optLong("block_height", Long.MAX_VALUE);
                        if (block < 0) block = Long.MAX_VALUE;
                        String timestamp = item.optString("confirmed", "");
                        int time = timestamp.equals("") ? 0 : (int) (dateFormat.parse(timestamp).getTime() / 1000);
                        BigInteger fee = BigInteger.ZERO; // TODO missing fee
                        BigInteger amount = BigInteger.valueOf(item.optLong("value", 0));
                        if (item.getInt("tx_output_n") == -1) amount = amount.negate();
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
        if (confirmations == 0) return new ArrayList<>();
        try {
            String url = baseUrl + "/addrs/" + address + "?unspentOnly=1";
            JSONObject data = new JSONObject(urlFetch(url));
            List<UTXO> list = new ArrayList<>();
            JSONArray items1 = data.optJSONArray("txrefs");
            if (items1 != null) {
                for (int i = 0; i < items1.length(); i++) {
                    JSONObject item = items1.getJSONObject(i);
                    String hash = item.getString("tx_hash");
                    int index = (int) item.getLong("tx_output_n");
                    BigInteger amount = BigInteger.valueOf(item.getLong("value"));
                    boolean spent = item.optBoolean("spent", true);
                    if (spent) continue;
                    UTXO o = new UTXO();
                    o.hash = hash;
                    o.index = index;
                    o.amount = amount;
                    list.add(o);
                }
            }
            JSONArray items2 = data.optJSONArray("unconfirmed_txrefs");
            if (items2 != null) {
                for (int i = 0; i < items2.length(); i++) {
                    JSONObject item = items2.getJSONObject(i);
                    String hash = item.getString("tx_hash");
                    int index = (int) item.getLong("tx_output_n");
                    BigInteger amount = BigInteger.valueOf(item.getLong("value"));
                    boolean spent = item.optBoolean("spent", true);
                    if (spent) continue;
                    UTXO o = new UTXO();
                    o.hash = hash;
                    o.index = index;
                    o.amount = amount;
                    list.add(o);
                }
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getSequence(String address) {
        if (confirmations != 0) return 0;
        try {
            String url = baseUrl + "/addrs/" + address+ "/balance";
            JSONObject data = new JSONObject(urlFetch(url));
            return data.getLong("nonce");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String broadcast(String transaction) {
        try {
            String url = baseUrl + "/txs/push";
            String content = "{\"tx\":\"" + transaction + "\"}";
            JSONObject data = new JSONObject(urlFetch(url, content));
            return data.getString("hash");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
