package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.coins;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class InsightAPI implements Service {

    private final String baseUrl;
    private final int confirmations;
    private final String label;
    private final boolean testnet;

    public InsightAPI(String baseUrl, int confirmations, String label, boolean testnet) {
        this.baseUrl = baseUrl;
        this.confirmations = confirmations;
        this.label = label;
        this.testnet = testnet;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "status?q=getInfo";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONObject info = data.optJSONObject("info");
            if (info != null) data = info;
            return data.getLong("blocks");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            BigInteger fee = BigInteger.valueOf(coins.attr("default_fee", 0, label, testnet));
            if (confirmations > 0 && !label.equals("bitcoinsv")) {
                String url = baseUrl + "utils/estimatefee?nbBlocks=" + confirmations;
                JSONObject data = new JSONObject(Network.urlFetch(url));
                BigDecimal value = new BigDecimal(data.getString("" + confirmations));
                if (value.compareTo(BigDecimal.ZERO) >= 0) {
                    fee = value.multiply(BigDecimal.TEN.pow(8)).toBigInteger();
                }
            }
            return fee;
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
            long chain_height = -1;
            JSONObject data;
            try {
                String url = baseUrl + "addrs/" + address + "/txs?from=0&to=20&noAsm=1&noSpent=1&noScriptSig=1";
                data = new JSONObject(Network.urlFetch(url));
            } catch (Exception e) {
                String url = baseUrl + "txs/?address=" + address;
                data = new JSONObject(Network.urlFetch(url));
            }
            JSONArray items = data.optJSONArray("items");
            if (items == null) items = data.getJSONArray("txs");
            List<HistoryItem> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("txid");
                long block = item.optLong("blockheight", -1);
                if (block <= 0) {
                    block = Long.MAX_VALUE;
                    if (item.has("confirmations")) {
                        long tx_confirmations = item.getLong("confirmations");
                        if (tx_confirmations > 0) {
                            if (chain_height == -1) chain_height = getHeight();
                            if (chain_height > tx_confirmations) {
                                block = chain_height - tx_confirmations;
                            }
                        }
                    }
                }
                int time = item.optInt("time", (int) (System.currentTimeMillis() / 1000));
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
                BigInteger amount;
                if (item.has("satoshis")) {
                    amount = BigInteger.valueOf(item.getLong("satoshis"));
                } else {
                    amount = new BigDecimal(item.getDouble("amount")).multiply(BigDecimal.TEN.pow(8)).toBigInteger();
                }
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
