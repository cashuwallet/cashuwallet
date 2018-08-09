package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class InsightAPI implements Service {

    private final String baseUrl;
    private final int confirmations;

    public InsightAPI(String baseUrl, int confirmations) {
        this.baseUrl = baseUrl;
        this.confirmations = confirmations;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "status?q=getInfo";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            data = data.getJSONObject("info");
            long height = data.getLong("blocks");
            return height;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            String url = baseUrl + "utils/estimatefee?nbBlocks=" + confirmations;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            BigInteger fee = new BigDecimal(data.getString(""+confirmations)).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
            return fee.max(BigInteger.valueOf(1024));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "addr/" + address + "?noTxList=1";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            long confirmed = data.getLong("balanceSat");
            long unconfirmed = data.getLong("unconfirmedBalanceSat");
            return BigInteger.valueOf(confirmed + unconfirmed);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl + "addrs/" + address + "/txs?from=0&to=20&noAsm=1&noSpent=1&noScriptSig=1";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray items = data.getJSONArray("items");
            List<HistoryItem> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("txid");
                long block = item.optLong("blockheight", Long.MAX_VALUE);
                if (block == -1) block = Long.MAX_VALUE;
                int time = item.optInt("time", 0);
                BigInteger fee = new BigDecimal(item.optDouble("fees", 0)).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
                BigInteger amount = BigInteger.ZERO;
                JSONArray inputs = item.optJSONArray("vin");
                if (inputs != null) {
                    for (int j = 0; j < inputs.length(); j++) {
                        JSONObject input = inputs.getJSONObject(j);
                        BigInteger value = BigInteger.valueOf(input.optLong("valueSat", 0));
                        String source = input.optString("addr", null);
                        if (address.equals(source)) amount = amount.subtract(value);
                    }
                }
                JSONArray outputs = item.optJSONArray("vout");
                if (outputs != null) {
                    for (int j = 0; j < outputs.length(); j++) {
                        JSONObject output = outputs.getJSONObject(j);
                        BigInteger value = new BigDecimal(output.optString("value", "0")).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
                        JSONObject scriptPubKey = output.optJSONObject("scriptPubKey");
                        if (scriptPubKey != null) {
                            JSONArray addresses = scriptPubKey.optJSONArray("addresses");
                            if (addresses != null) {
                                boolean found = false;
                                for (int k = 0; k < addresses.length(); k++) {
                                    String target = addresses.optString(k, null);
                                    if(address.equals(target)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (found) amount = amount.add(value);
                            }
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
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        try {
            String url = baseUrl + "addr/" + address + "/utxo";
            JSONArray items = new JSONArray(Network.urlFetch(url));
            List<UTXO> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("txid");
                int index = (int)item.getLong("vout");
                BigInteger amount = BigInteger.valueOf(item.getLong("satoshis"));
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
    public String broadcast(String transaction) {
        try {
            String url = baseUrl + "tx/send";
            String content = "{\"rawtx\":\"" + transaction + "\"}";
            JSONObject data = new JSONObject(Network.urlFetch(url, content));
            return data.getString("txid");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
