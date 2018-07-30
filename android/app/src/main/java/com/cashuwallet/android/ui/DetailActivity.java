package com.cashuwallet.android.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashuwallet.android.MainApplication;
import com.cashuwallet.android.R;
import com.cashuwallet.android.crypto.Coin;
import com.cashuwallet.android.crypto.Sync;
import com.cashuwallet.android.db.Multiwallet;
import com.cashuwallet.android.db.Wallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class DetailActivity extends AppCompatActivity {

    private AddFragment addFragment;
    private UseFragment useFragment;
    private ActivityFragment activityFragment;

    private Sync sync;
    private Multiwallet multiwallet;

    boolean refreshPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        int id = getIntent().getIntExtra("multiwallet", 0);

        sync = MainApplication.app().getSync();
        multiwallet = sync.findMultiwallet(id);

        Coin coin = multiwallet.getCoin();

        int theme_res = MainApplication.app().findTheme(multiwallet.coin);
        setTheme(theme_res);

        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(coin.getName());

        if (sync.isTestnet()) {
            toolbar.setTitle(toolbar.getTitle() + " Testnet");
        }

        TextView balance = findViewById(R.id.balance);
        balance.setText(formatAmount(coin, multiwallet.getBalance()));

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                Bundle args = new Bundle();
                args.putInt("multiwallet", multiwallet.id());
                if (position == 0) {
                    addFragment = new AddFragment();
                    addFragment.setArguments(args);
                    return addFragment;
                }
                if (position == 1) {
                    useFragment = new UseFragment();
                    useFragment.setArguments(args);
                    return useFragment;
                }
                if (position == 2) {
                    activityFragment = new ActivityFragment();
                    activityFragment.setArguments(args);
                    return activityFragment;
                }
                return null;
            }
        });
        viewPager.setCurrentItem(2);

        TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        /*
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                appBarLayout.setExpanded(false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        */

        int res = MainApplication.app().findDrawable(multiwallet.coin);
        ImageView imageView = findViewById(R.id.icon);
        imageView.setImageResource(res);
        Bitmap bitmap = getBitmap(res);

        // change task title, picture and  color
        Resources.Theme theme = this.getTheme();
        TypedValue colorValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorValue, true);
        @ColorInt int color = colorValue.data;
        setTaskDescription(new ActivityManager.TaskDescription(coin.getName(), bitmap, color));

        refreshPending = true;
    }

    private Bitmap getBitmap(int drawableId) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        drawable = (DrawableCompat.wrap(drawable)).mutate();
        int iconSize = am.getLauncherLargeIconSize();
        Bitmap bitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    void refresh(Multiwallet multiwallet) {
        this.multiwallet = multiwallet;
        Wallet wallet = sync.findDepositWallet(multiwallet, false);
        Coin coin = multiwallet.getCoin();
        TextView balance = findViewById(R.id.balance);
        if (balance != null) {
            balance.setText(formatAmount(coin, multiwallet.getBalance()));
        }
        TextView textView = findViewById(R.id.address);
        if (textView != null) {
            textView.setText(wallet.address);
        }
    }

    public String formatAmount(Coin coin, BigInteger amount) {
        String symbol = coin.getSymbol();
        String code = coin.getCode();
        int decimals = coin.getDecimals();

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(decimals);
        format.setMaximumFractionDigits(decimals);
        format.setRoundingMode(RoundingMode.UNNECESSARY);

        BigDecimal decimal = new BigDecimal(amount);
        decimal = decimal.divide(BigDecimal.TEN.pow(decimals));
        String value = format.format(decimal);
        return (symbol == null ? "" : symbol + " ") + value + " " + code;
    }

    // Back arrow click event to go to the parent Activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
