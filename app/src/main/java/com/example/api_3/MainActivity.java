package com.example.api_3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView ss;
    int dbm = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup content stuff
        this.setContentView(R.layout.activity_main);
        ss = (TextView) findViewById(R.id.ss);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        for (CellInfo cellInfo : cellInfoList)
        {
            if (cellInfo instanceof CellInfoLte)
            {
                // cast to CellInfoLte and call all the CellInfoLte methods you need
                dbm = ((CellInfoLte)cellInfo).getCellSignalStrength().getDbm();

            }
        }
        ss.setText(dbm);
    }
}