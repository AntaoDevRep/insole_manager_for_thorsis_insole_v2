package com.kenp.minga.insolesmanager.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.R;

import java.util.Set;

import de.thorsis.android.insole.gaminglibrary.ble.BleCommunication;

import static java.lang.Thread.sleep;

public class DebugActivity extends AppCompatActivity {

    private Button updateFirmwareBtn;
    private Button calibrationBtn;
    private BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        updateFirmwareBtn = findViewById(R.id.update_gaming_insole_firmware_btn);
        updateFirmwareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleCommandToInsoles(DebugActivity.this, BleCommunication.Option.FIRMWARE);
            }
        });
        calibrationBtn = findViewById(R.id.gaming_insole_calibration_btn);
        calibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bleCommandToInsoles(DebugActivity.this, BleCommunication.Option.CALIBRATION);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DebugActivity.this, "Kalibrierung bei V 2.0.0 ist deaktiviert.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void bleCommandToInsoles(final Context context, final BleCommunication.Option option){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        for (final BluetoothDevice insole: pairedDevices) {
            final BleCommunication  bleCommunication= new BleCommunication(insole, context, option);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int retriesNum = 10;
                    while (retriesNum > 0){
                        int result = bleCommunication.start();

                        if (result == BleCommunication.OPTION_SUCCESS){
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, option+ " to insole succeed! " + insole.getName() + insole.getAddress(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        } else {
                            try {
                                sleep(2000);
                            } catch (Exception ie) {
                                ie.printStackTrace();
                            }
                            retriesNum --;
                        }
                    }
                }
            }).start();
        }
    }
}
