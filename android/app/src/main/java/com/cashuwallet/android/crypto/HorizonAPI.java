package com.cashuwallet.android.crypto;

import android.net.Uri;
import android.util.Base64;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.binint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HorizonAPI implements Service {

    private final String baseUrl;

    public HorizonAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return data.getLong("core_latest_ledger");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        try {
            String url1 = baseUrl;
            JSONObject data1 = new JSONObject(Network.urlFetch(url1));
            long height = data1.getLong("core_latest_ledger");
            String url2 = baseUrl + "ledgers/" + height;
            JSONObject data2 = new JSONObject(Network.urlFetch(url2));
            return BigInteger.valueOf(data2.getLong("base_fee_in_stroops"));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "accounts/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray balances = data.getJSONArray("balances");
            for (int i = 0; i < balances.length(); i++) {
                JSONObject item = balances.getJSONObject(i);
                String asset_type = item.getString("asset_type");
                if (asset_type.equals("native")) {
                    return new BigDecimal(item.getString("balance")).multiply(BigDecimal.TEN.pow(7)).toBigInteger();
                }
            }
            return BigInteger.ZERO;
        } catch (FileNotFoundException e) {
            return BigInteger.ZERO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String url1 = baseUrl + "accounts/" + address + "/transactions/?limit=100&order=desc";
            JSONObject data1 = new JSONObject(Network.urlFetch(url1));
            data1 = data1.getJSONObject("_embedded");
            JSONArray items = data1.getJSONArray("records");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            List<HistoryItem> list = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String hash = item.getString("hash");
                long block = item.getLong("ledger");
                BigInteger fee = new BigInteger(item.getString("fee_charged"));
                String timestamp = item.getString("created_at");
                int time = (int) (dateFormat.parse(timestamp).getTime() / 1000);
                String source = item.getString("source_account");
                BigInteger amount = source.equals(address) ? fee.negate() : BigInteger.ZERO;
                int op_count = item.getInt("operation_count");
                String url2 = baseUrl + "transactions/" + hash + "/operations/?limit=" + op_count + "&order=desc";
                JSONObject data2 = new JSONObject(Network.urlFetch(url2));
                data2 = data2.getJSONObject("_embedded");
                JSONArray ops = data2.getJSONArray("records");
                for (int j = 0; j < ops.length(); j++) {
                    JSONObject op = ops.getJSONObject(j);
                    String type = op.getString("type");
                    if (type.equals("create_account")) {
                        BigInteger value = new BigDecimal(op.getString("starting_balance")).multiply(BigDecimal.TEN.pow(7)).toBigInteger();
                        String funder = op.getString("funder");
                        if (funder.equals(address)) amount = amount.subtract(value);
                        String account = op.getString("account");
                        if (account.equals(address)) amount = amount.add(value);
                    }
                    if (type.equals("payment")) {
                        String asset_type = op.getString("asset_type");
                        if (asset_type.equals("native")) {
                            BigInteger value = new BigDecimal(op.getString("amount")).multiply(BigDecimal.TEN.pow(7)).toBigInteger();
                            String from = op.getString("from");
                            if (from.equals(address)) amount = amount.subtract(value);
                            String to = op.getString("to");
                            if (to.equals(address)) amount = amount.add(value);
                        }
                    }
                    if (type.equals("path_payment")) {
                        String source_asset_type = op.getString("source_asset_type");
                        if (source_asset_type.equals("native")) {
                            BigInteger value = new BigDecimal(op.getString("source_amount")).multiply(BigDecimal.TEN.pow(7)).toBigInteger();
                            String from = op.getString("from");
                            if (from.equals(address)) amount = amount.subtract(value);
                        }
                        String asset_type = op.getString("asset_type");
                        if (asset_type.equals("native")) {
                            BigInteger value = new BigDecimal(op.getString("amount")).multiply(BigDecimal.TEN.pow(7)).toBigInteger();
                            String to = op.getString("to");
                            if (to.equals(address)) amount = amount.add(value);
                        }
                    }
                    // TODO finish other types, incomplete
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
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
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
        try {
            String url = baseUrl + "accounts/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return new BigInteger(data.getString("sequence")).longValue();
        } catch (FileNotFoundException e) {
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String broadcast(String transaction) {
        try {
            String url = baseUrl + "transactions";
            String content = "tx=" + Uri.encode(Base64.encodeToString(binint.h2b(transaction), Base64.NO_WRAP));
            JSONObject data = new JSONObject(Network.urlFetch(url, "POST", content, "application/x-www-form-urlencoded"));
            return data.getString("hash");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
