package com.kenp.minga.insolesmanager.business;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

/**
 * Created by minga on 1/31/2018.
 */

public class InsolesUtil {
    private Context context;
    private BluetoothAdapter btAdapter = null;

    public InsolesUtil(Context context){
        this.context = context;
    }
}
