package com.kenp.minga.insolesmanager.business;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.activities.MainActivity;
import com.kenp.minga.insolesmanager.callbacks.DialogCallBack;
import com.kenp.minga.insolesmanager.model.Const;
import com.kenp.minga.insolesmanager.model.FootSide;
import com.kenp.minga.insolesmanager.model.GamingInsoleID;
import com.kenp.minga.insolesmanager.model.InsoleData;
import com.kenp.minga.insolesmanager.model.InsoleItem;
import com.kenp.minga.insolesmanager.model.InsoleSide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import de.thorsis.android.insole.gaminglibrary.SoleData;
import de.thorsis.android.insole.gaminglibrary.bluetooth.BluetoothConnection;
import de.thorsis.android.insole.gaminglibrary.bluetooth.BluetoothServer;

import static android.content.ContentValues.TAG;

/**
 * Created by minga on 12/1/2017.
 */

public class BluetoothUtil {

    private Context context;
    private BluetoothAdapter btAdapter = null;
    private BluetoothServer btServer  = null;
    private ArrayList<BluetoothDevice> bondedDevices;

    private String rightInsoleMac="";
    private String leftInsoleMac="";

    private BluetoothConnection bluetoothConnection_1;
    private BluetoothConnection bluetoothConnection_2;
    private boolean isInsoleConnected =false;

    public BluetoothUtil(Context context) {
        this.context = context;
        bondedDevices = new ArrayList<>();
        if (btAdapter == null) {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        bluetoothConnection_1 = null;
        bluetoothConnection_2 = null;
        isInsoleConnected = false;
    }

    public void setInsolesMacAddress(GamingInsoleID leftInsoleID, GamingInsoleID rightInsoleID){
        rightInsoleMac = rightInsoleID.getMac();
        leftInsoleMac = leftInsoleID.getMac();
    }

    /**
     * Enable the bluetooth
     * If the bluetooth is disabled, push a dialog to ask the user open the bluetooth
     * **/
    public void enable_bluetooth(){
        if (btAdapter == null) {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (btAdapter != null){
            // find all the already paired devices
            Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
            //paired devices...
            if (devices.size() > 0) {
                for (BluetoothDevice bluetoothDevice : devices) {
                    if(!bondedDevices.contains(bluetoothDevice)){
                        bondedDevices.add(bluetoothDevice);
                    }
                }
            }
        }

        if (!btAdapter.isEnabled()){
            btAdapter.enable();
            Log.i(TAG, "blu is re-enabled");
        }else{
            Log.i(TAG, "blu is enabled");
        }

    }


    public void startBluetoothServer(final BluetoothServer.BluetoothServerCallback bluetoothServerCallback){

        /**Init the thread to start the Bluetooth Server...**/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
                    btServer=new BluetoothServer(adapter, context, bluetoothServerCallback);
                    btServer.start();
                    Log.i(TAG, "try to start bluetooth server...");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "failed to start bluetooth server...");
                }
            }
        }).start();
    }

    public void closeBluetoothServer(){
        if(this.btServer!=null){
            this.btServer.close();
        }
    }

    /**
     * Connect to the bonded insoles and set the BluetoothConnectionCallback
     * **/
    public void startInsoleConnection(final BluetoothConnection.SoleDataCallback soleDataCallback){

        /**Check the Bluetooth again*/
        if (!btAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
        }else{
            Log.i(TAG, "blu is enabled");
        }

        /**When the BLU server started, try to connect to the insoles*/
        try {
            if (btAdapter != null){
                // find all the already paired devices
                Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
                //paired devices...
                if (devices.size() > 0) {
                    for (BluetoothDevice bluetoothDevice : devices) {
                        if(!bondedDevices.contains(bluetoothDevice)){
                            // select only insoles not all BT devices
                            if (bluetoothDevice.getAddress().equals(leftInsoleMac) || bluetoothDevice.getAddress().equals(rightInsoleMac) ){
                                bondedDevices.add(bluetoothDevice);
                            } else {
                                Log.d(TAG, "startInsoleConnection: Ignore unrecognized insole, maybe only a normal Bluetooth device:"+bluetoothDevice.getName()+bluetoothDevice.getAddress());
                            }
                        }
                    }
                }
            }



            if(bondedDevices.size()!=2){
                Log.e(TAG, "startInsoleConnection: wrong num of bonded devices is "+ bondedDevices.size());
            }else{

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bluetoothConnection_1= new BluetoothConnection(bondedDevices.get(0),soleDataCallback);
                            bluetoothConnection_1.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bluetoothConnection_2= new BluetoothConnection(bondedDevices.get(1),soleDataCallback);
                            bluetoothConnection_2.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                isInsoleConnected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeInsoleConnection(){
        /**Check the Bluetooth again*/
        if (!btAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
        }else{
            Log.i(TAG, "blu is enabled");
        }

        /**When the BLU server started, try to connect to the insoles*/
        try {
            if(bondedDevices.size()!=2){
                Log.e(TAG, "startInsoleConnection: wrong num of bonded devices is "+ bondedDevices.size());
                //  console_text_view.setText("startInsoleConnection: wrong num of bonded devices is "+ bondedDevices.size());
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(bluetoothConnection_1!=null){
                                bluetoothConnection_1.close();
                            }else {
                                Log.e(TAG, "run: bluetoothConnection_1 is null");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(bluetoothConnection_2!=null){
                                bluetoothConnection_2.close();
                            }else{
                                Log.e(TAG, "run: bluetoothConnection_2 is null");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            isInsoleConnected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public InsoleData convertSoleDataToInsoleData(BluetoothDevice bluetoothDevice, SoleData soleData){
        InsoleData insoleData= new InsoleData();
        if(soleData!=null){
            /*** TODO Check how to recognize the right or left insole?**/
            if(bluetoothDevice.getAddress().equals(leftInsoleMac)){
                insoleData.setFoot_side(FootSide.LEFT);
            }else if(bluetoothDevice.getAddress().equals(rightInsoleMac)){
                insoleData.setFoot_side(FootSide.RIGHT);
            }

            insoleData.setName(bluetoothDevice.getName());
            insoleData.setMac(bluetoothDevice.getAddress());
            insoleData.setBattery(soleData.battery);
            insoleData.setPres_1(soleData.pressSensor1 * Const.PRESSURE_SCALE_VALUE_MTK);
            insoleData.setPres_2(soleData.pressSensor2 * Const.PRESSURE_SCALE_VALUE_MTK);
            insoleData.setPres_3(soleData.pressSensor3 * Const.PRESSURE_SCALE_VALUE_MTK);
            insoleData.setPres_4(soleData.pressSensor4 * Const.PRESSURE_SCALE_VALUE_MTK);
            insoleData.setPres_5(soleData.pressSensor5 * Const.PRESSURE_SCALE_VALUE_MTK);
            insoleData.setPres_6(soleData.pressSensor6 * Const.PRESSURE_SCALE_VALUE_HEEL);
            insoleData.setPres_7(soleData.pressSensor7 * Const.PRESSURE_SCALE_VALUE_HEEL);
            insoleData.setPres_8(soleData.pressSensor8 * Const.PRESSURE_SCALE_VALUE_HEEL);
            insoleData.setPres_9(soleData.pressSensor9 * Const.PRESSURE_SCALE_VALUE_HEEL);
            insoleData.setPres_10(soleData.pressSensor10 * Const.PRESSURE_SCALE_VALUE_HEEL);
            insoleData.setAcce_x(soleData.accelerationX);
            insoleData.setAcce_y(soleData.accelerationY);
            insoleData.setAcce_z(soleData.accelerationZ);
            insoleData.setTemp_e(soleData.tempEnvironment);
            Calendar calendar=Calendar.getInstance();
            insoleData.setTimeSample(calendar.getTime());

            return insoleData;
        }else {
            return null;
        }
    }

//    public void getPairedInsolesSideInfo(){
//        if (btAdapter == null) {
//            btAdapter = BluetoothAdapter.getDefaultAdapter();
//        }
//
//        SaveArrayListUtil saveArrayListUtil=new SaveArrayListUtil();
//        ArrayList<InsoleItem> savedInsoleItemsInfo= saveArrayListUtil.getInsolesInfo(context);
//        /**Check the Bluetooth again*/
//        if (!btAdapter.isEnabled()){
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            context.startActivity(enableBtIntent);
//        }else{
//            Log.i(TAG, "blu is enabled");
//        }
//
//        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
//
//        for (BluetoothDevice insole: devices) {
//            for (InsoleItem insoleItem:savedInsoleItemsInfo ) {
//                if(insoleItem.getName().equals(insole.getName())&&insoleItem.getMacAddress().equals(insole.getAddress())){
//                    if(insoleItem.getInsoleSide()== InsoleSide.RIGHT){
//                        rightInsoleMac=insoleItem.getMacAddress();
//                    }else if(insoleItem.getInsoleSide()== InsoleSide.LEFT){
//                        leftInsoleMac=insoleItem.getMacAddress();
//                    }
//                }
//            }
//        }
//        //rightInsoleSide
//    }
}
