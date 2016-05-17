package com.stockita.newpointofsales.barcodeScanner;


import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


/**
 *
 */
public class BarcodeScanner extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    // Constant
    public static final String SCAN_FORMAT = "scanFormat";
    public static final String SCAN_CONTENTS = "scanContents";

    // Member variable
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        setupFormats();
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        Intent resultIntent = new Intent();
        resultIntent.putExtra(SCAN_CONTENTS, rawResult.getContents());
        resultIntent.putExtra(SCAN_FORMAT, rawResult.getBarcodeFormat().getName());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }

    /**
     * Most type of barcode format
     */
    private void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        formats.add(BarcodeFormat.ISBN13);
        formats.add(BarcodeFormat.EAN13);
        formats.add(BarcodeFormat.CODABAR);
        formats.add(BarcodeFormat.CODE128);
        formats.add(BarcodeFormat.DATABAR);
        formats.add(BarcodeFormat.DATABAR_EXP);
        formats.add(BarcodeFormat.CODE39);
        formats.add(BarcodeFormat.CODE93);

        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

}
