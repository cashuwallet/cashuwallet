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
    private final boolean testnet;

    public WavesnodesAPI(String baseUrl, boolean testnet) {
        this.baseUrl = baseUrl;
        this.testnet = testnet;
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
            String url = baseUrl + "addresses/balance/details/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            long available = data.getLong("available");
            return BigInteger.valueOf(available);
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
                if (asset == null || fee_asset == null) {
                    String hash = item.getString("id");
                    long block = item.optLong("height", Long.MAX_VALUE);
                    if (block == -1) block = Long.MAX_VALUE;
                    int time = (int)(item.optLong("timestamp", 0) / 1000);
                    String source = item.getString("sender");
                    String target = item.getString("recipient");
                    BigInteger value = BigInteger.valueOf(item.getLong("amount"));
                    BigInteger fee_value = BigInteger.valueOf(item.getLong("fee"));
                    BigInteger fee = BigInteger.ZERO;
                    if (fee_asset == null) fee = fee.add(fee_value);
                    BigInteger amount = BigInteger.ZERO;
                    if (address.equals(source)) {
                        if (fee_asset == null) amount = amount.subtract(fee_value);
                        if (asset == null) amount = amount.subtract(value);
                    }
                    if (address.equals(target)) {
                        if (asset == null) amount = amount.add(value);
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
                "\"version\": 2," +
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
