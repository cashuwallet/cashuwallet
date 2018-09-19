package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.base58;
import com.raugfer.crypto.binint;
import com.raugfer.crypto.dict;
import com.raugfer.crypto.transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WavesnodesAPI implements Service {

    private final String baseUrl;
    private final String assetId;
    private final boolean testnet;

    public WavesnodesAPI(String baseUrl, boolean testnet) {
        this(baseUrl, null, testnet);
    }

    public WavesnodesAPI(String baseUrl, String assetId, boolean testnet) {
        this.baseUrl = baseUrl;
        this.assetId = assetId;
        this.testnet = testnet;
    }

    private static boolean assetEquals(String asset1, String asset2) {
        return asset1 == null ? asset2 == null : asset1.equals(asset2);
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "blocks/height";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return data.getLong("height");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        return BigInteger.valueOf(100000);
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            if (assetId == null) {
                String url = baseUrl + "addresses/balance/details/" + address;
                JSONObject data = new JSONObject(Network.urlFetch(url));
                long available = data.getLong("available");
                return BigInteger.valueOf(available);
            } else {
                String url = baseUrl + "assets/balance/" + address + "/" + assetId;
                JSONObject data = new JSONObject(Network.urlFetch(url));
                long balance = data.getLong("balance");
                return BigInteger.valueOf(balance);
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl + "transactions/address/" + address + "/limit/100";
            JSONArray data = new JSONArray(Network.urlFetch(url));
            JSONArray items = data.getJSONArray(0);
            List<HistoryItem> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String asset = item.isNull("assetId") ? null : item.getString("assetId");
                String fee_asset = item.isNull("feeAssetId") ? null : item.getString("feeAssetId");
                if (assetEquals(asset, assetId) || assetEquals(fee_asset, assetId)) {
                    String hash = item.getString("id");
                    long block = item.optLong("height", Long.MAX_VALUE);
                    if (block == -1) block = Long.MAX_VALUE;
                    int time = (int)(item.optLong("timestamp", 0) / 1000);
                    BigInteger amount = BigInteger.ZERO;
                    BigInteger fee = BigInteger.ZERO;
                    String source = item.getString("sender");
                    if (assetEquals(asset, assetId)) {
                        if (item.has("recipient")) {
                            String target = item.getString("recipient");
                            BigInteger value = BigInteger.valueOf(item.getLong("amount"));
                            if (address.equals(target)) amount = amount.add(value);
                            if (address.equals(source)) amount = amount.subtract(value);
                        }
                        if (item.has("transfers")) {
                            JSONArray transfers = item.getJSONArray("transfers");
                            for (int j = 0; j < transfers.length(); j++) {
                                JSONObject transfer = transfers.getJSONObject(j);
                                String target = transfer.getString("recipient");
                                BigInteger value = BigInteger.valueOf(transfer.getLong("amount"));
                                if (address.equals(target)) amount = amount.add(value);
                            }
                            BigInteger value = BigInteger.valueOf(item.getLong("totalAmount"));
                            if (address.equals(source)) amount = amount.subtract(value);
                        }
                    }
                    if (assetEquals(fee_asset, assetId)) {
                        BigInteger fee_value = BigInteger.valueOf(item.getLong("fee"));
                        fee = fee.add(fee_value);
                        if (address.equals(source)) amount = amount.subtract(fee_value);
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
            byte[] txn = binint.h2b(_transaction);
            dict fields = transaction.transaction_decode(txn, "waves", testnet);
            BigInteger version = fields.get("version");
            String signature = base58.encode(fields.get("signature"));
            String publickey = fields.get("publickey");
            String asset = fields.get("asset", "");
            String fee_asset = fields.get("fee_asset", "");
            BigInteger timestamp = fields.get("timestamp");
            BigInteger amount = fields.get("amount");
            BigInteger fee = fields.get("fee");
            String recipient = fields.get("recipient");
            String attachment = fields.get("attachment", "");
            String url = baseUrl + "transactions/broadcast";
            String content = "{" +
                "\"version\": " + version + "," +
                "\"type\": 4," +
                "\"signature\": \"" + signature + "\"," +
                "\"proofs\": [\"" + signature + "\"]," +
                "\"senderPublicKey\": \"" + publickey + "\"," +
                "\"assetId\": \"" + asset + "\"," +
                "\"feeAssetId\": \"" + fee_asset + "\"," +
                "\"timestamp\": " + timestamp + "," +
                "\"amount\": " + amount + "," +
                "\"fee\": " + fee + "," +
                "\"recipient\": \"" + recipient + "\"," +
                "\"attachment\": \"" + attachment + "\"" +
            "}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
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
