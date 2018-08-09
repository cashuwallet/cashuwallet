package com.cashuwallet.android.crypto;

import java.math.BigInteger;
import java.util.List;

public interface Service {

    long getHeight();
    BigInteger getFeeEstimate();
    BigInteger getBalance(String address);
    List<HistoryItem> getHistory(String address, long height);
    List<UTXO> getUTXOs(String address);
    long getSequence(String address);
    String broadcast(String transaction);
    Object custom(String name, Object arg);

    public final class HistoryItem {
        public String hash;
        public int time;
        public long block;
        public BigInteger amount;
        public BigInteger fee;
    };

    public final class UTXO {
        public String hash;
        public int index;
        public BigInteger amount;
    }

    public static class Multi implements Service {

        private final Service[] services;

        public Multi(Service[] services) {
            this.services = services;
        }

        @Override
        public long getHeight() {
            for (Service service : services) {
                long height = service.getHeight();
                if (height != -1) return height;
            }
            return -1;
        }

        @Override
        public BigInteger getFeeEstimate() {
            for (Service service : services) {
                BigInteger fee = service.getFeeEstimate();
                if (fee != null) return fee;
            }
            return null;
        }

        @Override
        public BigInteger getBalance(String address) {
            for (Service service : services) {
                BigInteger balance = service.getBalance(address);
                if (balance != null) return balance;
            }
            return null;
        }

        @Override
        public List<HistoryItem> getHistory(String address, long height) {
            for (Service service : services) {
                List<HistoryItem> history = service.getHistory(address, height);
                if (history != null) return history;
            }
            return null;
        }

        @Override
        public List<UTXO> getUTXOs(String address) {
            for (Service service : services) {
                List<UTXO> utxos = service.getUTXOs(address);
                if (utxos != null) return utxos;
            }
            return null;
        }

        @Override
        public long getSequence(String address) {
            for (Service service : services) {
                long sequence = service.getSequence(address);
                if (sequence != -1) return sequence;
            }
            return -1;
        }

        @Override
        public String broadcast(String transaction) {
            for (Service service : services) {
                String id = service.broadcast(transaction);
                if (id != null) return id;
            }
            return null;
        }

        @Override
        public Object custom(String name, Object arg) {
            for (Service service : services) {
                Object ret = service.custom(name, arg);
                if (ret != null) return ret;
            }
            return null;
        }

    }

}
