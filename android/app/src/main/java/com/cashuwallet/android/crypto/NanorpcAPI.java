package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.binint;
import com.raugfer.crypto.transaction;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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
            Map<String, Object> fields = transaction.transaction_decode(txn, "nano", testnet);
            String account = (String) fields.get("account");
            String previous = (String) fields.get("previous");
            String representative = (String) fields.get("representative");
            BigInteger balance = (BigInteger) fields.get("balance");
            String link = (String) fields.get("link");
            byte[] signature = (byte[]) fields.get("signature");
            byte[] work = (byte[]) fields.get("work");
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
                JSONObject data = new JSONObject(Network.urlFetch(url, content));
                return binint.h2b(data.getString("work"));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
