package com.cashuwallet.android.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashuwallet.android.MainApplication;
import com.cashuwallet.android.R;
import com.cashuwallet.android.crypto.Coin;
import com.cashuwallet.android.crypto.Coins;
import com.cashuwallet.android.crypto.Sync;
import com.cashuwallet.android.db.Multiwallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Sync sync;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().performIdentifierAction(R.id.nav_wallet, 0);

        sync = MainApplication.app().getSync();
        List<Multiwallet> multiwallets = sync.findMultiwallets(0);
        Collections.sort(multiwallets, (Multiwallet m1, Multiwallet m2) -> m1.getCoin().getName().compareTo(m2.getCoin().getName()));
        adapter = new Adapter(multiwallets);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!MainApplication.app().networkAvailable()) {
                Snackbar.make(recyclerView, R.string.network_not_available, Snackbar.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            adapter.refresh();
            swipeRefreshLayout.setRefreshing(false);
        });
        swipeRefreshLayout.post(() -> adapter.refresh());
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Multiwallet multiwallet : adapter.items) {
            sync.refresh(multiwallet);
        }
        adapter.notifyItemRangeChanged(0, adapter.items.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_wallet:
                this.setTitle(item.getTitle());
                return true;
            //case R.id.nav_portfolio:
            //    this.setTitle(item.getTitle());
            //    return true;
            //case R.id.nav_converter:
            //    this.setTitle(item.getTitle());
            //    return true;
            //case R.id.nav_markets:
            //    this.setTitle(item.getTitle());
            //    return true;
            case R.id.nav_manage:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            //case R.id.nav_share:
            //    return true;
            //case R.id.nav_about:
            //    return true;
            case R.id.nav_exit:
                new AlertDialog.Builder(this)
                    //.setTitle(R.string.action_exit)
                    .setMessage(R.string.action_exit_confirmation)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, (DialogInterface dialog, int which) -> {
                        setRequestedOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.shutting_wallet), true);
                        new Handler().postDelayed(() -> {
                            MainApplication.app().logout();
                            progressDialog.dismiss();
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            finishAndRemoveTask();
                        }, 500);
                    })
                    .show();
                return true;
            default:
                return true;
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Multiwallet> items;
        private final Set<Multiwallet> refreshing = new HashSet<>();
        private final Map<Multiwallet, ViewHolder> bindings = new HashMap<>();

        Adapter(List<Multiwallet> items) {
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_row, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            Multiwallet multiwallet = items.get(position);
            vh.multiwallet = multiwallet;
            bindings.put(multiwallet, vh);
            updateViewHolder(vh, multiwallet);
        }

        private void refresh() {
            for (Multiwallet multiwallet : items) {
                ((Runnable) () -> {
                    refreshing.add(multiwallet);
                    ViewHolder vh = bindings.get(multiwallet);
                    if (vh == null) return;
                    if (vh.multiwallet != multiwallet) return;
                    updateViewHolder(vh, multiwallet);
                }).run();
                sync.sync(multiwallet, () -> {
                    refreshing.remove(multiwallet);
                    ViewHolder vh = bindings.get(multiwallet);
                    if (vh == null) return;
                    if (vh.multiwallet != multiwallet) return;
                    updateViewHolder(vh, multiwallet);
                });
            }
        }

        private void updateViewHolder(ViewHolder vh, Multiwallet multiwallet) {
            Coin coin = multiwallet.getCoin();

            int res = MainApplication.app().findDrawable(multiwallet.coin);

            int color = multiwallet.confirmed ? Color.TRANSPARENT : Color.parseColor("#f7b500");

            if (refreshing.contains(multiwallet)) color = Color.LTGRAY;

            vh.image.setImageResource(res);
            vh.name.setText(coin.getName() + (sync.isTestnet() ? " Testnet": ""));
            vh.tag.setText(coin instanceof Coins.ERC20Token ? "ERC-20" : "");
            vh.balance.setText(formatAmount(coin, multiwallet.getBalance()));
            vh.status.setTextColor(color);
            vh.itemView.setOnClickListener((View view) -> {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("multiwallet", multiwallet.id());
                startActivity(intent);
            });
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
        Multiwallet multiwallet;
        ImageView image;
        TextView name;
        TextView tag;
        TextView balance;
        TextView status;
        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            tag = view.findViewById(R.id.tag);
            balance = view.findViewById(R.id.balance);
            status = view.findViewById(R.id.status);
        }
    }

}
