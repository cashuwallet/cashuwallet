package com.cashuwallet.android.ui;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.ColorInt;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cashuwallet.android.Locker;
import com.cashuwallet.android.MainApplication;
import com.cashuwallet.android.R;
import com.cashuwallet.android.UserAuthenticationLocker;
import com.cashuwallet.android.crypto.Coin;
import com.cashuwallet.android.crypto.Sync;
import com.cashuwallet.android.db.Multiwallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;

import static android.app.Activity.RESULT_OK;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class UseFragment extends Fragment {

    private static final int AUTHENTICATION_SCREEN_REQUEST_CODE = 1;
    private static final int CAPTURE_SCREEN_REQUEST_CODE = 2;

    private Sync sync;
    private View rootView;
    private Runnable cont;
    private ProgressDialog signingDialog;
    private TextInputLayout targetAddressLayout;
    private TextInputLayout amountLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sync = MainApplication.app().getSync();

        int id = getArguments().getInt("multiwallet");

        Multiwallet multiwallet = sync.findMultiwallet(id);
        Coin coin = multiwallet.getCoin();

        DetailActivity activity = (DetailActivity) getActivity();

        Resources.Theme theme = getActivity().getTheme();
        TypedValue colorValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorValue, true);
        @ColorInt int colorPrimary = colorValue.data;

        Locker.UserAuthenticationHandler handler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handler = UserAuthenticationLocker.defaultHandler((ret) -> {
                Intent intent = (Intent) ret[0];
                cont = (Runnable) ret[1];
                startActivityForResult(intent, AUTHENTICATION_SCREEN_REQUEST_CODE);
            });
        } else {
            handler = null;
        }

        rootView = inflater.inflate(R.layout.fragment_detail_use, container, false);

        targetAddressLayout = rootView.findViewById(R.id.target_address_layout);
        AppCompatEditText targetAddressEdit = rootView.findViewById(R.id.target_address);
        targetAddressEdit.addTextChangedListener(new TextValidator() {
            @Override
            public void validate(Editable editable) {
                targetAddressLayout.setError(null);
            }
        });

        Button buttonPaste = rootView.findViewById(R.id.button_paste);
        buttonPaste.setTextColor(0xffffffff);
        buttonPaste.setBackgroundColor(colorPrimary);
        buttonPaste.setOnClickListener((View v) -> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard == null) {
                Snackbar.make(rootView, R.string.clipboard_unavailable, Snackbar.LENGTH_LONG).show();
                return;
            }
            if (!(clipboard.hasPrimaryClip())) {
                Snackbar.make(rootView, R.string.empty_clipboard, Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData clip = clipboard.getPrimaryClip();
            if (!(clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                // since the clipboard has data but it is not plain text
                Snackbar.make(rootView, R.string.incompatible_clipboard_type, Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData.Item item = clip.getItemAt(0);
            targetAddressEdit.setText(item.getText());
        });

        Button buttonCapture = rootView.findViewById(R.id.button_capture);
        buttonCapture.setTextColor(0xffffffff);
        buttonCapture.setBackgroundColor(colorPrimary);
        buttonCapture.setOnClickListener((View v) -> {
            PackageManager pm = getActivity().getPackageManager();
            if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Snackbar.make(rootView, R.string.camera_not_available, Snackbar.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(getContext(), CaptureActivity.class);
            startActivityForResult(intent, CAPTURE_SCREEN_REQUEST_CODE);
        });

        //TextView textView = rootView.findViewById(R.id.currency_symbol);
        //textView.setText(coin.getSymbol());

        TextView feeView = rootView.findViewById(R.id.network_fee_value);

        amountLayout = rootView.findViewById(R.id.amount_layout);
        AppCompatEditText amountEdit = rootView.findViewById(R.id.amount);
        char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        char nonseparator = separator == '.' ? ',' : '.';
        amountEdit.addTextChangedListener(new TextValidator() {
            @Override
            public void validate(Editable editable) {
                amountLayout.setError(null);
                String amount = editable.toString();
                int length = amount.length();
                int nonseparatorIndex = amount.indexOf(nonseparator);
                if (nonseparatorIndex >= 0) {
                    editable.replace(nonseparatorIndex, nonseparatorIndex+1, ""+separator);
                    return;
                }
                int separatorIndex = amount.lastIndexOf(separator);
                if (separatorIndex >= 0) {
                    int firstSeparatorIndex = amount.indexOf(separator);
                    if (firstSeparatorIndex < separatorIndex) {
                        editable.replace(firstSeparatorIndex, firstSeparatorIndex+1, "");
                        return;
                    }
                    int decimalsLimit = separatorIndex + 1 + coin.getDecimals();
                    if (length > decimalsLimit) {
                        editable.replace(decimalsLimit, length, "");
                        return;
                    }
                    if (separatorIndex == 0) {
                        editable.insert(0, "0");
                        return;
                    }
                }
                if (amount.length() > 0) {
                    amount = amount.replace(',', '.');
                    if (amount.charAt(amount.length()-1) == '.') amount += "0";
                    BigInteger amt = new BigDecimal(amount).multiply(BigDecimal.TEN.pow(coin.getDecimals())).toBigInteger();
                    BigInteger fee;
                    try {
                        fee = sync.estimateFee(multiwallet, amt);
                    } catch (Exception e) {
                        fee = null;
                    }
                    feeView.setText(fee == null ? null : activity.formatAmount(coin.getFeeCoin(), fee));
                } else {
                    feeView.setText(null);
                }
            }
        });

        Button buttonSendPayment = rootView.findViewById(R.id.send_payment_button);
        buttonSendPayment.setTextColor(0xffffffff);
        buttonSendPayment.setBackgroundColor(colorPrimary);
        buttonSendPayment.setOnClickListener((View v) -> {
            getActivity().setRequestedOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            targetAddressLayout.setEnabled(false);
            amountLayout.setEnabled(false);
            ((DetailActivity) getActivity()).hideKeyboard();
            boolean hasErrors = false;
            String address = targetAddressEdit.getText().toString();
            if (address.length() == 0) {
                targetAddressLayout.setError(getResources().getString(R.string.error_empty_target_address));
                hasErrors |= true;
            } else {
                if (!sync.validateAddress(multiwallet, address)) {
                    targetAddressLayout.setError(getResources().getString(R.string.error_invalid_target_address));
                    hasErrors |= true;
                } else {
                    targetAddressLayout.setError(null);
                }
            }
            String amount = amountEdit.getText().toString();
            if (amount.length() == 0) {
                amountLayout.setError(getResources().getString(R.string.error_empty_amount));
                hasErrors |= true;
            } else {
                amountLayout.setError(null);
            }
            if (hasErrors) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                targetAddressLayout.setEnabled(true);
                amountLayout.setEnabled(true);
                return;
            }
            amount = amount.replace(',', '.');
            if (amount.charAt(amount.length()-1) == '.') amount += "0";
            BigInteger amt = new BigDecimal(amount).multiply(BigDecimal.TEN.pow(coin.getDecimals())).toBigInteger();
            BigInteger fee;
            try {
                fee = sync.estimateFee(multiwallet, amt); // TODO apply multiplication factor
            } catch (Exception e) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                targetAddressLayout.setEnabled(true);
                amountLayout.setEnabled(true);
                Snackbar.make(rootView, R.string.unsuccessful_transaction_creation, Snackbar.LENGTH_LONG).show();
                return;
            }
            feeView.setText(activity.formatAmount(coin, fee));
            ProgressDialog creatingDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.creating_transaction), true);
            new Handler().postDelayed(() -> {
                Object[] txn;
                try {
                    txn = sync.createTransaction(multiwallet, address, amt, fee);
                } catch (Exception e) {
                    if (creatingDialog.isShowing()) creatingDialog.dismiss();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    targetAddressLayout.setEnabled(true);
                    amountLayout.setEnabled(true);
                    Snackbar.make(rootView, R.string.unsuccessful_transaction_creation, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (creatingDialog.isShowing()) creatingDialog.dismiss();
                LayoutInflater li = LayoutInflater.from(getActivity());
                View dialogView = li.inflate(R.layout.dialog_password, null);
                AppCompatEditText passwordView = dialogView.findViewById(R.id.password);
                new AlertDialog.Builder(getActivity())
                        //.setTitle(R.string.action_send_payment)
                        .setView(dialogView)
                        .setMessage(R.string.action_send_payment_confirmation)
                        .setOnCancelListener((DialogInterface dialog) -> {
                            new Handler().postDelayed(() -> {
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                targetAddressLayout.setEnabled(true);
                                amountLayout.setEnabled(true);
                                ((DetailActivity) getActivity()).hideKeyboard();
                            }, 250);
                        })
                        .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> {
                            new Handler().postDelayed(() -> {
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                targetAddressLayout.setEnabled(true);
                                amountLayout.setEnabled(true);
                                ((DetailActivity) getActivity()).hideKeyboard();
                            }, 250);
                        })
                        .setPositiveButton(R.string.ok, (DialogInterface dialog, int which) -> {
                            new Handler().postDelayed(() -> {
                                ((DetailActivity) getActivity()).hideKeyboard();
                                String[] wordlist = getResources().getStringArray(R.array.mnemonic_english);
                                String password = passwordView.getText().toString();
                                signingDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.signing_transaction), true);
                                MainApplication.app().authenticate(wordlist, password, coin, (secrets) -> {
                                    if (secrets != null) {
                                        Object[] signedTxn = sync.signTransaction(multiwallet, txn, secrets);
                                        if (signingDialog.isShowing()) signingDialog.dismiss();
                                        ProgressDialog broadcastDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.broadcasting_transaction), true);
                                        boolean[] success = { false };
                                        sync.broadcastTransaction(multiwallet, signedTxn, success, () -> {
                                            if (broadcastDialog.isShowing()) broadcastDialog.dismiss();
                                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                            targetAddressLayout.setEnabled(true);
                                            amountLayout.setEnabled(true);
                                            if (success[0]) {
                                                activity.refreshPending = true;
                                                ViewPager viewPager = activity.findViewById(R.id.container);
                                                viewPager.setCurrentItem(2);
                                                Snackbar snackbar = Snackbar.make(rootView, R.string.successful_broadcast, Snackbar.LENGTH_INDEFINITE);
                                                snackbar.setAction(R.string.dismiss, (View view) -> snackbar.dismiss()).show();
                                            } else {
                                                Snackbar snackbar = Snackbar.make(rootView, R.string.unsuccessful_broadcast, Snackbar.LENGTH_INDEFINITE);
                                                snackbar.setAction(R.string.dismiss, (View view) -> snackbar.dismiss()).show();
                                            }
                                        });
                                    } else {
                                        if (signingDialog.isShowing()) signingDialog.dismiss();
                                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                        targetAddressLayout.setEnabled(true);
                                        amountLayout.setEnabled(true);
                                        Snackbar snackbar = Snackbar.make(rootView, R.string.password_mismatch, Snackbar.LENGTH_INDEFINITE);
                                        snackbar.setAction(R.string.dismiss, (View view) -> snackbar.dismiss()).show();
                                    }
                                }, handler);
                            }, 250);
                        })
                        .show();
            }, 250);
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHENTICATION_SCREEN_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
            {
                cont.run();
            }
            else
            {
                if (signingDialog.isShowing()) signingDialog.dismiss();
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                targetAddressLayout.setEnabled(true);
                amountLayout.setEnabled(true);
            }
        }
        if (requestCode == CAPTURE_SCREEN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String result = data.getStringExtra("result");
            String message = data.getStringExtra("message");
            if (result != null) {
                AppCompatEditText textView = rootView.findViewById(R.id.target_address);
                textView.setText(result);
            }
            if (message != null) {
                Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private abstract class TextValidator implements TextWatcher {

        public abstract void validate(Editable editable);

        @Override
        public void afterTextChanged(Editable editable) {
            validate(editable);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

    }

}
