package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Web3rpcAPI implements Service {

    private final String baseUrl;

    public Web3rpcAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl;
            String content = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":1}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            String height = data.getString("result").replace("x", "");
            return Long.parseLong(height, 16);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            String url = baseUrl;
            String content = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_gasPrice\",\"params\":[],\"id\":1}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            String height = data.getString("result").replace("x", "");
            return new BigInteger(height, 16);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl;
            String content = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"" + address + "\",\"latest\"],\"id\":1}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            String balance = data.getString("result");
            return new BigInteger(balance.replaceAll("^0x", ""), 16);
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
        return new ArrayList<>();
    }

    @Override
    public long getSequence(String address) {
        try {
            String url = baseUrl;
            String content = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\":[\"" + address + "\",\"latest\"],\"id\":1}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            String sequence = data.getString("result").replace("x", "");
            return Long.parseLong(sequence, 16);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String broadcast(String transaction) {
        try {
            String url = baseUrl;
            String content = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendRawTransaction\",\"params\":[\"0x" + transaction + "\"],\"id\":1}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
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
