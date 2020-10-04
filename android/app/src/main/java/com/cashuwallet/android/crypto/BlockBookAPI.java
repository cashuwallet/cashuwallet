package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.coins;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BlockBookAPI implements Service {

    private final String baseUrl;
    private final boolean accountBased;
    private final int confirmations;
    private final String label;
    private final boolean testnet;
    private final String contractAddress;

    public BlockBookAPI(String baseUrl) {
        this(baseUrl, null);
    }

    public BlockBookAPI(String baseUrl, String contractAddress) {
        this.baseUrl = baseUrl;
        this.accountBased = true;
        this.confirmations = 0;
        this.label = null;
        this.testnet = false;
        this.contractAddress = contractAddress;
    }

    public BlockBookAPI(String baseUrl, int confirmations, String label, boolean testnet) {
        this.baseUrl = baseUrl;
        this.accountBased = false;
        this.confirmations = confirmations;
        this.label = label;
        this.testnet = testnet;
        this.contractAddress = null;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            data = data.getJSONObject("blockbook");
            return data.getLong("bestHeight");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        if (accountBased) return null; // TODO work around this limitation
        try {
            BigInteger fee = BigInteger.valueOf(coins.attr("default_fee", 0, label, testnet));
            if (confirmations > 0 && !label.equals("bitcoinsv")) {
                String url = baseUrl + "v1/estimatefee/" + confirmations;
                JSONObject data = new JSONObject(Network.urlFetch(url));
                BigDecimal value = new BigDecimal(data.getString("result"));
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
            String url = baseUrl + "v2/address/" + address + "?details=tokenBalances";
            if (contractAddress != null) url += "&contract=" + contractAddress;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            if (contractAddress == null) {
                BigInteger confirmed = new BigInteger(data.getString("balance"));
                BigInteger unconfirmed = new BigInteger(data.getString("unconfirmedBalance"));
                return confirmed.add(unconfirmed);
            } else {
                JSONArray tokens = data.optJSONArray("tokens");
                if (tokens != null) {
                    for (int i = 0; i < tokens.length(); i++) {
                        JSONObject token = tokens.getJSONObject(i);
                        String contract = token.optString("contract", null);
                        if (contractAddress.equalsIgnoreCase(contract)) {
                            return new BigInteger(token.getString("balance"));
                        }
                    }
                }
                return BigInteger.ZERO;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url = baseUrl + "v2/address/" + address + "?details=txslight";
            if (contractAddress != null) url += "&contract=" + contractAddress;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray items = data.optJSONArray("transactions");
            List<HistoryItem> list = new ArrayList<>();
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String hash = item.getString("txid");
                    long block = item.optLong("blockheight", -1);
                    int time = item.optInt("blockTime", (int) (System.currentTimeMillis() / 1000));
                    BigInteger fee = new BigInteger(item.optString("fees", "0"));
                    BigInteger amount = BigInteger.ZERO;
                    if (contractAddress == null) {
                        String defaultInputValue = "0";
                        String defaultOutputValue = "0";
                        if (accountBased) {
                            defaultOutputValue = item.optString("value", "0");
                            defaultInputValue = new BigInteger(defaultOutputValue).add(fee).toString();
                        }
                        JSONArray inputs = item.optJSONArray("vin");
                        if (inputs != null) {
                            for (int j = 0; j < inputs.length(); j++) {
                                JSONObject input = inputs.getJSONObject(j);
                                BigInteger value = new BigInteger(input.optString("value", defaultInputValue));
                                JSONArray addresses = input.optJSONArray("addresses");
                                if (addresses != null) {
                                    boolean found = false;
                                    for (int k = 0; k < addresses.length(); k++) {
                                        String source = addresses.optString(k, null);
                                        if (address.equals(source)) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (found) amount = amount.subtract(value);
                                }
                            }
                        }
                        JSONArray outputs = item.optJSONArray("vout");
                        if (outputs != null) {
                            for (int j = 0; j < outputs.length(); j++) {
                                JSONObject output = outputs.getJSONObject(j);
                                BigInteger value = new BigInteger(output.optString("value", defaultOutputValue));
                                JSONArray addresses = output.optJSONArray("addresses");
                                if (addresses != null) {
                                    boolean found = false;
                                    for (int k = 0; k < addresses.length(); k++) {
                                        String target = addresses.optString(k, null);
                                        if (address.equals(target)) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (found) amount = amount.add(value);
                                }
                            }
                        }
                    } else {
                        JSONArray transfers = item.optJSONArray("tokenTransfers");
                        if (transfers != null) {
                            for (int j = 0; j < transfers.length(); j++) {
                                JSONObject transfer = transfers.getJSONObject(j);
                                String contract = transfer.optString("token", null);
                                if (contractAddress.equalsIgnoreCase(contract)) {
                                    String source = transfer.optString("from", null);
                                    String target = transfer.optString("to", null);
                                    BigInteger value = new BigInteger(transfer.optString("value", "0"));
                                    if (address.equalsIgnoreCase(source)) amount = amount.subtract(value);
                                    if (address.equalsIgnoreCase(target)) amount = amount.add(value);
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
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        if (accountBased) return new ArrayList<>();
        try {
            String url = baseUrl + "v2/utxo/" + address;
            JSONArray items = new JSONArray(Network.urlFetch(url));
            List<UTXO> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("txid");
                int index = (int)item.getLong("vout");
                BigInteger amount = new BigInteger(item.getString("value"));
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
        if (!accountBased) return 0;
        String url = baseUrl + "v2/address/" + address + "?details=basic";
        try {
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return Long.parseLong(data.getString("nonce"));
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String broadcast(String transaction) {
        try {
            String url = baseUrl + "v2/sendtx/";
            String content = transaction;
            JSONObject data = new JSONObject(Network.urlFetch(url, "POST", content, "text/plain"));
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
