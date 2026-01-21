package com.kenp.minga.insolesmanager.callbacks;

import android.bluetooth.BluetoothDevice;

import com.kenp.minga.insolesmanager.model.InsoleData;

import de.thorsis.android.insole.gaminglibrary.SoleData;

/**
 * Created by minga on 12/1/2017.
 */

public interface InsoleDataCallBack {
    void onNewInsoleDataReceived(InsoleData newInsoleData);

    void onConnectionStarted();

    void onConnectionStopped();

    void onVersionReceived(BluetoothDevice bluetoothDevice, String firmwareVersion, String hardwareSerial, String insoleSerial);
}
