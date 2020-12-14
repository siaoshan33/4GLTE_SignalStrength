package com.example.api_3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_GPS_SETTING_RETURN = 2;

    private TextView tvDbmInfo;
    private TextView tvMcc;
    private TextView tvMnc;

    TelephonyManager mTelephonyManager;
    MyPhoneStateListener psListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup content stuff
        this.setContentView(R.layout.activity_main);
        tvDbmInfo = (TextView) findViewById(R.id.signalstrength);
        tvMcc = (TextView) findViewById(R.id.mcc);
        tvMnc = (TextView) findViewById(R.id.mnc);

        psListener = new MyPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(psListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Granted");
            } else {
                Toast.makeText(this, "Need GPS Permission", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                Log.d(TAG, "Permission Not Granted, requestPermissions");
            }
        }
    }

    private boolean isGpsEnabled() {
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            Toast.makeText(this, "Please enable GPS first", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), MY_PERMISSIONS_GPS_SETTING_RETURN);
        }
        return false;
    }

    public class MyPhoneStateListener extends PhoneStateListener {
        int dbm;
        String imei;
        String mcc;
        String mnc;

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            checkPermission();

            if (isGpsEnabled()) {
                List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
                imei = mTelephonyManager.getNetworkOperator();

                Log.d(TAG, "cellInfoList.size = " + cellInfoList.size());
                for (CellInfo cellInfo : cellInfoList) {
                    Log.d(TAG, "cellInfo = " + cellInfo.toString());
                    if (cellInfo instanceof CellInfoLte) {
                        Log.d(TAG, "cellInfo(LTE) = " + cellInfo.toString());
                        // cast to CellInfoLte and call all the CellInfoLte methods you need
                        dbm = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                        tvDbmInfo.setText(String.valueOf(dbm));

                        // imei = mcc(3-bit) + mnc(2-bit)
                        if (!TextUtils.isEmpty(imei)) {
                            mcc = imei.substring(0, 3);
                            mnc = imei.substring(3, 5);
                        }
                        tvMcc.setText(String.valueOf(mcc));
                        tvMnc.setText(String.valueOf(mnc));

                        super.onSignalStrengthsChanged(signalStrength);
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (resultCode == 0) {
            switch (requestCode) {
                case MY_PERMISSIONS_GPS_SETTING_RETURN:
                    psListener = new MyPhoneStateListener();
                    break;
            }
        } else if (resultCode == 1) {
            switch (requestCode) {
                case MY_PERMISSIONS_ACCESS_FINE_LOCATION:
                    checkPermission();
                    break;
            }
        }
    }

}