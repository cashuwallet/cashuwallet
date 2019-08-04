package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EtherscanAPI implements Service {

    private final String baseUrl;
    private final String contractAddress;
    private final boolean proxyDisabled;

    public EtherscanAPI(String baseUrl) {
        this(baseUrl, null);
    }

    public EtherscanAPI(String baseUrl, String contractAddress) {
        this(baseUrl, contractAddress, false);
    }

    public EtherscanAPI(String baseUrl, boolean proxyDisabled) {
        this(baseUrl, null, proxyDisabled);
    }

    public EtherscanAPI(String baseUrl, String contractAddress, boolean proxyDisabled) {
        this.baseUrl = baseUrl;
        this.contractAddress = contractAddress;
        this.proxyDisabled = proxyDisabled;
    }

    @Override
    public long getHeight() {
        if (proxyDisabled) return -1;
        try {
            String url = baseUrl + "?module=proxy&action=eth_blockNumber";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            String height = data.getString("result").replace("x", "");
            return Long.parseLong(height, 16);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        if (proxyDisabled) return null;
        try {
            String url = baseUrl + "?module=proxy&action=eth_gasPrice";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            String height = data.getString("result").replace("x", "");
            return new BigInteger(height, 16);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String action = contractAddress == null ? "balance" : "tokenbalance";
            String url = baseUrl + "?module=account&action=" + action + "&address=" + address+ "&tag=latest";
            if (contractAddress != null) url += "&contractaddress=" + contractAddress;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            String balance = data.getString("result");
            return new BigInteger(balance);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String action = contractAddress == null ? "txlist" : "tokentx";
            String url = baseUrl + "?module=account&action=" + action + "&address=" + address+ "&startblock=" + height + "&page=1&offset=100&sort=asc";
            if (contractAddress != null) url += "&contractaddress=" + contractAddress;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray items = data.getJSONArray("result");
            List<HistoryItem> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("hash");
                long block = Long.parseLong(item.optString("blockNumber", Long.toString(Long.MAX_VALUE)));
                int time = Integer.valueOf(item.optString("timeStamp", "0"));
                BigInteger gasUsed = new BigInteger(item.optString("gasUsed", "0"));
                BigInteger gasPrice = new BigInteger(item.optString("gasPrice", "0"));
                BigInteger fee = gasUsed.multiply(gasPrice);
                BigInteger value = new BigInteger(item.optString("value", "0"));
                BigInteger amount = BigInteger.ZERO;
                String source = item.optString("from", null);
                if (address.equalsIgnoreCase(source)) {
                    amount = amount.subtract(value);
                    if (contractAddress == null) {
                        amount = amount.subtract(fee);
                    }
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
        if (proxyDisabled) return -1;
        try {
            String url = baseUrl + "?module=proxy&action=eth_getTransactionCount&address=" + address + "&tag=latest";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            String sequence = data.getString("result").replace("x", "");
            return Long.parseLong(sequence, 16);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String broadcast(String transaction) {
        if (proxyDisabled) return null;
        try {
            String url = baseUrl + "?module=proxy&action=eth_sendRawTransaction&hex=0x" + transaction;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return data.getString("result");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
