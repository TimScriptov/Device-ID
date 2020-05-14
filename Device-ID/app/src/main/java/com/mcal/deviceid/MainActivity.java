package com.mcal.deviceid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
	public TextView android_id;
	public TextView android_id_old;
	private static Context context;
	
	private ClipboardManager myClipboard;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }
		
		android_id = (TextView) findViewById(R.id.android_id);
		android_id.setText(getDeviceIdNew().toString());
		findViewById(R.id.android_id).setOnClickListener(this);
		
		android_id_old = (TextView) findViewById(R.id.android_id_old);
		android_id_old.setText(getDeviceIdOld().toString());
		findViewById(R.id.android_id_old).setOnClickListener(this);
		
		myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    }
	
	@SuppressLint("HardwareIds")
    private static String getDeviceIdOld() {
		
        String str = Build.SERIAL;
        if ("unknown".equals(str)) {
            try {
                str = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }
	
	
	@SuppressLint({"HardwareIds", "MissingPermission"})
    private static String getDeviceIdNew() {
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deviceId = Build.getSerial();
        } else {
            deviceId = Build.SERIAL;
        }
        if ("unknown".equals(deviceId)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deviceId = Settings.Secure.getString(
					context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
            } else {
                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephony.getDeviceId() != null) {
                    deviceId = mTelephony.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(
						context.getContentResolver(),
						Settings.Secure.ANDROID_ID);
                }
            }
        }
        return deviceId;
    }

	public void onClick(View v) {
		if (v == android_id) {
            myClipboard.setText(android_id.getText().toString());
			Toast.makeText(getApplicationContext(), getDeviceIdNew() + " copied", Toast.LENGTH_SHORT).show();
        } else if (v == android_id_old) {
            myClipboard.setText(android_id_old.getText().toString());
			Toast.makeText(getApplicationContext(), getDeviceIdOld() + " copied", Toast.LENGTH_SHORT).show();
        }
	}
}
