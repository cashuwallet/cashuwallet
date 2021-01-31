package com.cashuwallet.android.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashuwallet.android.MainApplication;
import com.cashuwallet.android.R;
import com.cashuwallet.android.crypto.Coin;
import com.cashuwallet.android.crypto.Sync;
import com.cashuwallet.android.db.Multiwallet;
import com.cashuwallet.android.db.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

public class ActivityFragment extends Fragment {

    private static final int PAGE_SIZE = 25; // TODO must be larger than what fits the screen
    private static final int PAGE_BOTTOM = 5;

    private Sync sync;
    Multiwallet multiwallet;
    private Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        int id = getArguments().getInt("multiwallet");

        DetailActivity activity = (DetailActivity) getActivity();

        sync = MainApplication.app().getSync();
        multiwallet = sync.findMultiwallet(id);
        adapter = new Adapter(sync.findTransactions(multiwallet, 0, PAGE_SIZE));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        NestedScrollView emptyText = view.findViewById(R.id.empty_list);

        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int count = linearLayoutManager.getItemCount();
                int last = linearLayoutManager.findLastVisibleItemPosition();
                if (count - last <= PAGE_BOTTOM) {
                    int size = adapter.getItemCount();
                    adapter.items.addAll(sync.findTransactions(multiwallet, size, PAGE_SIZE));
                    int increment = adapter.getItemCount() - size;
                    if (increment > 0) {
                        adapter.notifyItemRangeInserted(size, increment);
                    }
                }
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> refresh(view, true));

        if (activity.refreshPending) {
            activity.refreshPending = false;
            refresh(view, false);
        }

        return view;
    }

    void refresh(View view, boolean requested)
    {
        DetailActivity activity = (DetailActivity) getActivity();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        NestedScrollView emptyText = view.findViewById(R.id.empty_list);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        if (!MainApplication.app().networkAvailable()) {
            if (requested) {
                Snackbar.make(recyclerView, R.string.network_not_available, Snackbar.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }
        if (!requested) swipeRefreshLayout.setRefreshing(true);
        sync.sync(multiwallet, () -> {
            activity.refresh(multiwallet);
            int size = adapter.getItemCount();
            adapter.items.clear();
            adapter.items.addAll(sync.findTransactions(multiwallet, 0, PAGE_SIZE));
            if (size >= adapter.getItemCount()) {
                adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                adapter.notifyItemRangeRemoved(adapter.getItemCount(), size - adapter.getItemCount());
            } else {
                adapter.notifyItemRangeChanged(0, size);
                adapter.notifyItemRangeInserted(size, adapter.getItemCount() - size);
            }
            if (adapter.getItemCount() == 0) {
                recyclerView.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.GONE);
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Transaction> items;

        Adapter(List<Transaction> items) {
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_activity_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, final int position) {
            Transaction transaction = items.get(position);
            vh.transaction = transaction;
            updateViewHolder(vh, transaction);
        }

        private void updateViewHolder(ViewHolder vh, Transaction transaction) {
            Coin coin = transaction.getCoin();

            int res;
            switch (transaction.getType()) {
                default:
                // TODO add icon for Transaction.Type.NONE
                case NONE: res = R.drawable.ic_sent; break;
                case INCOMING: res = R.drawable.ic_receive; break;
                case OUTGOING: res = R.drawable.ic_sent; break;
            }

            int color = getResources().getColor(transaction.confirmed ? R.color.status_confirmed : R.color.status_pending);

            vh.image.setImageResource(res);
            vh.amount.setText(formatAmount(coin, transaction.getAbsAmount()));
            vh.time.setText(formatDateTime(transaction.getTime()));
            vh.status.setTextColor(color);
            vh.itemView.setOnClickListener((View view) -> {
                String url = sync.getUrl(transaction);
                if (url == null) {
                    Snackbar.make(view, R.string.transaction_url_not_available, Snackbar.LENGTH_LONG).show();
                    return;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(view, R.string.failure_open_url_activity, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        private String formatDateTime(int time) {
            long timestamp = 1000 * (long) time;
            return DateUtils.formatDateTime(getContext(), timestamp,DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_TIME);
        }

        private String formatAmount(Coin coin, BigInteger amount) {
            //String symbol = coin.getSymbol();
            String code = coin.getCode();
            int decimals = coin.getDecimals();

            NumberFormat format = NumberFormat.getNumberInstance();
            format.setMinimumFractionDigits(decimals);
            format.setMaximumFractionDigits(decimals);
            format.setRoundingMode(RoundingMode.UNNECESSARY);

            BigDecimal decimal = new BigDecimal(amount);
            decimal = decimal.divide(BigDecimal.TEN.pow(decimals));
            String value = format.format(decimal);
            return /*(symbol == null ? "" : symbol + " ") +*/ value + " " + code;
        }

    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        Transaction transaction;
        ImageView image;
        TextView amount;
        TextView time;
        TextView status;
        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            amount = view.findViewById(R.id.amount);
            time = view.findViewById(R.id.time);
            status = view.findViewById(R.id.status);
        }
    }

}
