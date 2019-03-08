package com.cashuwallet.android.crypto;

import android.util.Base64;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.binint;
import com.raugfer.crypto.transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CardanoExplorerAPI implements Service {

    private final String baseUrl;
    private final String label;
    private final boolean testnet;

    public CardanoExplorerAPI(String baseUrl, String label, boolean testnet) {
        this.baseUrl = baseUrl;
        this.label = label;
        this.testnet = testnet;
    }

    @Override
    public long getHeight() {
        try {
            String url1 = baseUrl + "blocks/pages/total?pageSize=10";
            JSONObject data1 = new JSONObject(Network.urlFetch(url1));
            long pages = data1.getLong("Right");
            String url2 = baseUrl + "blocks/pages?page=" + pages;
            JSONObject data2 = new JSONObject(Network.urlFetch(url2));
            JSONArray pagedata = data2.getJSONArray("Right");
            JSONArray items = pagedata.getJSONArray(1);
            long height = 10 * (pages - 1) + items.length();
            return height;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "addresses/summary/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONObject item = data.getJSONObject("Right");
            String balance = item.getJSONObject("caBalance").getString("getCoin");
            return new BigInteger(balance);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            List<HistoryItem> list = new ArrayList<>();
            String url1 = baseUrl + "addresses/summary/" + address;
            JSONObject data1 = new JSONObject(Network.urlFetch(url1));
            JSONArray items = data1.getJSONObject("Right").getJSONArray("caTxList");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("ctbId");
                int time = item.getInt("ctbTimeIssued");
                BigInteger amount = BigInteger.ZERO;
                JSONArray inputs = item.optJSONArray("ctbInputs");
                if (inputs != null) {
                    for (int j = 0; j < inputs.length(); j++) {
                        JSONArray input = inputs.getJSONArray(j);
                        String source = input.getString(0);
                        BigInteger value = new BigInteger(input.getJSONObject(1).getString("getCoin"));
                        if (address.equals(source)) amount = amount.subtract(value);
                    }
                }
                JSONArray outputs = item.optJSONArray("ctbOutputs");
                if (outputs != null) {
                    for (int j = 0; j < outputs.length(); j++) {
                        JSONArray output = outputs.getJSONArray(j);
                        String target = output.getString(0);
                        BigInteger value = new BigInteger(output.getJSONObject(1).getString("getCoin"));
                        if (address.equals(target)) amount = amount.add(value);
                    }
                }
                BigInteger fee = new BigInteger(item.getJSONObject("ctbInputSum").getString("getCoin")).subtract(new BigInteger(item.getJSONObject("ctbOutputSum").getString("getCoin")));
                String url2 = baseUrl + "txs/summary/" + hash;
                JSONObject data2 = new JSONObject(Network.urlFetch(url2));
                long block = data2.getJSONObject("Right").getLong("ctsBlockHeight");
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
        try {
            List<UTXO> list = new ArrayList<>();
            String url = baseUrl + "bulk/addresses/utxo";
            String content = "[\"" + address + "\"]";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            JSONArray items = data.getJSONArray("Right");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("cuId");
                int index = item.getInt("cuOutIndex");
                BigInteger amount = new BigInteger(item.getJSONObject("cuCoins").getString("getCoin"));
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
    public String broadcast(String _transaction) {
        try {
            byte[] txn = binint.h2b(_transaction);
            String txnid = transaction.txnid(txn, label, testnet);
            String url = baseUrl + "v2/txs/signed";
            String content = "{\"signedTx\":\"" + Base64.encodeToString(txn, Base64.DEFAULT) + "\"}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
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