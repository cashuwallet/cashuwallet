package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DogechainAPI implements Service {

    private final String baseUrl;

    public DogechainAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "../../chain/Dogecoin/q/getblockcount";
            String data = Network.urlFetch(url);
            return Long.parseLong(data.trim());
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
            String url = baseUrl + "address/balance/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return new BigDecimal(data.getString("balance")).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        return null;
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        try {
            String url = baseUrl + "unspent/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray items = data.optJSONArray("unspent_outputs");
            List<UTXO> list = new ArrayList<>();
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String hash = item.getString("tx_hash");
                    int index = (int) item.getLong("tx_output_n");
                    BigInteger amount = new BigInteger(item.getString("value"));
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
        return 0;
    }

    @Override
    public String broadcast(String transaction) {
        try {
            String url = baseUrl + "pushtx";
            String content = "{\"tx\":\"" + transaction + "\"}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            return data.getString("result");
        } catch (Exception e) {
            return null;
        }
    }

}
