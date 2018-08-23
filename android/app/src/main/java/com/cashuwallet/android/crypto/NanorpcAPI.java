package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.binint;
import com.raugfer.crypto.dict;
import com.raugfer.crypto.transaction;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.List;

public class NanorpcAPI  implements Service {

    private final String baseUrl;
    private final boolean testnet;

    public NanorpcAPI(String baseUrl, boolean testnet) {
        this.baseUrl = baseUrl;
        this.testnet = testnet;
    }

    @Override
    public long getHeight() {
        // TODO
        return -1;
    }

    @Override
    public BigInteger getFeeEstimate() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getBalance(String address) {
        // TODO
        return null;
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        // TODO
        return null;
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        // TODO
        return null;
    }

    @Override
    public long getSequence(String address) {
        return 0;
    }

    @Override
    public String broadcast(String _transaction) {
        try {
            byte[] txn = binint.h2b(_transaction);
            dict fields = transaction.transaction_decode(txn, "nano", testnet);
            String account = fields.get("account");
            String previous = fields.get("previous");
            String representative = fields.get("representative");
            BigInteger balance = fields.get("balance");
            String link = fields.get("link");
            byte[] signature = fields.get("signature");
            byte[] work = fields.get("work");
            String url = baseUrl;
            String block = "{" +
                "\"account\": \"" + account + "\"," +
                "\"previous\": \"" + previous + "\"," +
                "\"representative\": \"" + representative + "\"," +
                "\"balance\": \"" + balance + "\"," +
                "\"link\": \"" + link + "\"," +
                "\"signature\": \"" + binint.b2h(signature).toUpperCase() + "\"," +
                "\"work\": \"" + binint.b2h(work) + "\"," +
                "\"type\": \"state\"" +
            "}";
            String content = "{\"action\":\"process\",\"block\":\"" + block.replace("\"", "\\\"") + "\"}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            return data.getString("hash");
        } catch (Exception e) {
            return null;
        }
    }

    public Object custom(String name, Object arg) {
        if (name.equals("work")) {
            try {
                String url = baseUrl;
                String content = "{\"action\":\"work_generate\",\"hash\":\"" + arg + "\"}";
                JSONObject data = new JSONObject(Network.urlFetch(url, content, 60));
                return binint.h2b(data.getString("work"));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
