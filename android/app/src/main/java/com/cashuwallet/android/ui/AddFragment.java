package com.cashuwallet.android.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.cashuwallet.android.MainApplication;
import com.cashuwallet.android.R;
import com.cashuwallet.android.crypto.Sync;
import com.cashuwallet.android.db.Multiwallet;
import com.cashuwallet.android.db.Wallet;

public class AddFragment extends Fragment {

    private Sync sync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sync = MainApplication.app().getSync();

        int id = getArguments().getInt("multiwallet");

        Multiwallet multiwallet = sync.findMultiwallet(id);
        Wallet wallet = sync.findDepositWallet(multiwallet, false);

        Resources.Theme theme = getActivity().getTheme();
        TypedValue colorValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorValue, true);
        @ColorInt int colorPrimary = colorValue.data;

        View rootView = inflater.inflate(R.layout.fragment_detail_add, container, false);

        TextView textView = rootView.findViewById(R.id.address);
        textView.setText(wallet.address);

        Button buttonCopy = rootView.findViewById(R.id.button_copy);
        buttonCopy.setTextColor(0xffffffff);
        buttonCopy.setBackgroundColor(colorPrimary);
        buttonCopy.setOnClickListener((View v) -> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard == null) {
                Snackbar.make(rootView, R.string.clipboard_unavailable, Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData clip = ClipData.newPlainText("address", wallet.address);
            clipboard.setPrimaryClip(clip);
            //Toast.makeText(getActivity(), getResources().getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show();
            Snackbar.make(rootView, R.string.address_copied_to_clipboard, Snackbar.LENGTH_LONG).show();
        });

        Button buttonShare = rootView.findViewById(R.id.button_share);
        buttonShare.setTextColor(0xffffffff);
        buttonShare.setBackgroundColor(colorPrimary);
        buttonShare.setOnClickListener((View v) -> {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            String extraText = wallet.address;
            //intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, extraText);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.action_share)));
        });

        ImageView imageView = rootView.findViewById(R.id.qrcode);

        int dim = 850;

        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(wallet.address, BarcodeFormat.DATA_MATRIX.QR_CODE, dim, dim, null);
        } catch (Exception e) {
            return rootView;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        @ColorInt int colorBlack = textView.getCurrentTextColor();
        @ColorInt int colorWhite = 0xefffffff;

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? 0x00000000 : colorWhite;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, dim, 0, 0, bitMatrixWidth, bitMatrixHeight);
        imageView.setImageBitmap(bitmap);
        imageView.setBackgroundColor(colorBlack);

        return rootView;
    }

}
