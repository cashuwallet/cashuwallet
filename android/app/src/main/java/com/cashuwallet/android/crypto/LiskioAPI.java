package com.cashuwallet.android.crypto;

import com.raugfer.crypto.binint;
import com.raugfer.crypto.dict;
import com.raugfer.crypto.transaction;
import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LiskioAPI implements Service {

    private final String baseUrl;
    private final String version;
    private final boolean testnet;

    public LiskioAPI(String baseUrl, String version, boolean testnet) {
        this.baseUrl = baseUrl;
        this.version = version;
        this.testnet = testnet;
    }

    @Override
    public long getHeight() {
        try {
            if (version.equals("0.9")) {
                String url = baseUrl + "blocks/getHeight";
                JSONObject data = new JSONObject(Network.urlFetch(url));
                long height = data.getLong("height");
                return height;
            }
            else
            if (version.equals("1.0")) {
                String url = baseUrl + "node/status";
                JSONObject data = new JSONObject(Network.urlFetch(url));
                data = data.getJSONObject("data");
                long height = data.getLong("height");
                return height;
            }
            else {
                throw new IllegalStateException("Unsupported version");
            }
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            if (version.equals("0.9")) {
                String url = baseUrl + "blocks/getFees";
                JSONObject data = new JSONObject(Network.urlFetch(url));
                JSONObject fees = data.getJSONObject("fees");
                long amount = fees.getLong("send");
                return BigInteger.valueOf(amount);
            }
            else
            if (version.equals("1.0")) {
                String url = baseUrl + "node/constants";
                JSONObject data = new JSONObject(Network.urlFetch(url));
                data = data.getJSONObject("data");
                JSONObject fees = data.getJSONObject("fees");
                String amount = fees.getString("send");
                return new BigInteger(amount);
            }
            else {
                throw new IllegalStateException("Unsupported version");
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            if (version.equals("0.9")) {
                String url = baseUrl + "accounts?address=" + address;
                JSONObject data = new JSONObject(Network.urlFetch(url));
                boolean success = data.getBoolean("success");
                if (!success) {
                    String error = data.getString("error");
                    if (error.equals("Account not found")) return BigInteger.ZERO;
                }
                JSONObject account = data.getJSONObject("account");
                String balance = account.getString("balance");
                return new BigInteger(balance);
            }
            else
            if (version.equals("1.0")) {
                String url = baseUrl + "accounts?address=" + address;
                JSONObject data = new JSONObject(Network.urlFetch(url));
                JSONArray list = data.getJSONArray("data");
                if (list.length() == 0) return BigInteger.ZERO;
                data = list.getJSONObject(0);
                String balance = data.getString("balance");
                return new BigInteger(balance);
            }
            else {
                throw new IllegalStateException("Unsupported version");
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            if (version.equals("0.9")) {
                String url = baseUrl + "transactions?senderId=" + address + "&recipientId=" + address + "&orderBy=timestamp:desc&limit=100";
                JSONObject data = new JSONObject(Network.urlFetch(url));
                JSONArray items = data.getJSONArray("transactions");
                List<HistoryItem> list = new ArrayList<>();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String hash = item.getString("id");
                    long block = item.optLong("height", Long.MAX_VALUE);
                    if (block < 0) block = Long.MAX_VALUE;
                    int time = item.optInt("timestamp", 0) + 1464109200; // lisk epoch 2016-05-24T17:00:00.000Z
                    BigInteger fee = BigInteger.valueOf(item.getLong("fee"));
                    String source = item.getString("senderId");
                    String target = item.getString("recipientId");
                    BigInteger value = BigInteger.valueOf(item.getLong("amount"));
                    BigInteger amount = BigInteger.ZERO;
                    if (address.equals(source)) {
                        amount = amount.subtract(fee);
                        amount = amount.subtract(value);
                    }
                    if (address.equals(target)) {
                        amount = amount.add(value);
                    }
                    HistoryItem o = new HistoryItem();
                    o.hash = hash;
                    o.time = time;
                    o.block = block;
                    o.amount = amount;
                    o.fee = fee;
                    list.add(o);
                }
                return list;
            }
            else
            if (version.equals("1.0")) {
                String url = baseUrl + "transactions?senderIdOrRecipientId=" + address + "&sort=timestamp:desc&limit=100";
                JSONObject data = new JSONObject(Network.urlFetch(url));
                JSONArray items = data.getJSONArray("data");
                List<HistoryItem> list = new ArrayList<>();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String hash = item.getString("id");
                    long block = item.optLong("height", Long.MAX_VALUE);
                    if (block < 0) block = Long.MAX_VALUE;
                    int time = item.optInt("timestamp", 0) + 1464109200; // lisk epoch 2016-05-24T17:00:00.000Z
                    BigInteger fee = new BigInteger(item.getString("fee"));
                    String source = item.getString("senderId");
                    String target = item.getString("recipientId");
                    BigInteger value = new BigInteger(item.getString("amount"));
                    BigInteger amount = BigInteger.ZERO;
                    if (address.equals(source)) {
                        amount = amount.subtract(fee);
                        amount = amount.subtract(value);
                    }
                    if (address.equals(target)) {
                        amount = amount.add(value);
                    }
                    HistoryItem o = new HistoryItem();
                    o.hash = hash;
                    o.time = time;
                    o.block = block;
                    o.amount = amount;
                    o.fee = fee;
                    list.add(o);
                }
                return list;
            }
            else {
                throw new IllegalStateException("Unsupported version");
            }
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
        BigInteger fee = getFeeEstimate();
        if (fee == null) return null;
        try {
            byte[] txn = binint.h2b(_transaction);
            String txnid = transaction.txnid(txn, "lisk", testnet);
            dict fields = transaction.transaction_decode(txn, "lisk", testnet);
            BigInteger timestamp = fields.get("timestamp");
            String publickey = fields.get("publickey");
            String recipient = fields.get("recipient");
            BigInteger amount = fields.get("amount");
            String signature = binint.b2h(fields.get("signature"));
            boolean success;
            if (version.equals("0.9")) {
                String url = baseUrl + "transactions";
                String content = "{" +
                    "\"type\": 0," +
                    "\"timestamp\": " + timestamp + "," +
                    "\"senderPublicKey\": \"" + publickey + "\"," +
                    "\"recipientId\": \"" + recipient + "\"," +
                    "\"amount\": \"" + amount + "\"," +
                    "\"fee\": \"" + fee + "\"," +
                    "\"signature\": \"" + signature + "\"," +
                    "\"asset\": {}," +
                    "\"id\": \"" + txnid + "\"" +
                "}";
                JSONObject data = new JSONObject(Network.urlFetch(url, "PUT", content));
                success = data.getBoolean("success");
            }
            else
            if (version.equals("1.0")) {
                String url = baseUrl + "transactions";
                String content = "{" +
                    "\"type\": 0," +
                    "\"timestamp\": " + timestamp + "," +
                    "\"senderPublicKey\": \"" + publickey + "\"," +
                    "\"recipientId\": \"" + recipient + "\"," +
                    "\"amount\": \"" + amount + "\"," +
                    "\"fee\": \"" + fee + "\"," +
                    "\"signature\": \"" + signature + "\"," +
                    "\"asset\": {}," +
                    "\"id\": \"" + txnid + "\"" +
                "}";
                JSONObject data = new JSONObject(Network.urlFetch(url, content));
                JSONObject meta = data.getJSONObject("meta");
                success = meta.getBoolean("status");
            }
            else {
                throw new IllegalStateException("Unsupported version");
            }
            if (!success) throw new IllegalArgumentException("Transaction rejected");
            return txnid;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
