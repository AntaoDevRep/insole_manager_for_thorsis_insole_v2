package com.kenp.minga.insolesmanager.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.kenp.minga.insolesmanager.activities.DebugActivity;
import com.kenp.minga.insolesmanager.activities.MainActivity;
import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.business.BluetoothUtil;
import com.kenp.minga.insolesmanager.callbacks.InsoleDataCallBack;
import com.kenp.minga.insolesmanager.helper.CsvHelper;
import com.kenp.minga.insolesmanager.model.Const;
import com.kenp.minga.insolesmanager.model.FootSide;
import com.kenp.minga.insolesmanager.model.GamingInsoleID;
import com.kenp.minga.insolesmanager.model.InsoleData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.thorsis.android.insole.gaminglibrary.SoleData;
import de.thorsis.android.insole.gaminglibrary.bluetooth.BluetoothConnection;
import de.thorsis.android.insole.gaminglibrary.bluetooth.BluetoothServer;
import de.thorsis.android.insole.gaminglibrary.util.InsoleInfo;

/**
 * Created by minga on 7/10/2017.
 */

public class InsoleBluService extends Service {

    private static final String TAG = "IN_INSOLE_BLU_SERVICE";
    private Context context;
    private InsoleDataCallBack insoleDataCallBack = null;
    private InsoleData receivedSoleData;
    private float leftInsoleBattery = 0.0f;
    private float rightInsoleBattery = 0.0f;
    private String leftInsoleSerialNo;
    private String rightInsoleSerialNo;
    BluetoothUtil bluetoothUtil = null;

    @Override
    public void onCreate() {
        super.onCreate();
        /**Init Variables**/
        context = getApplicationContext();
        bluetoothUtil = new BluetoothUtil(context);
        //bluetoothUtil.enable_bluetooth();
        bluetoothUtil.startBluetoothServer(bluetoothServerCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**show a notification to user that the service is running...*/
            Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification noti = null;
        Log.i(TAG, "onStartCommand: left insole battery "+ leftInsoleBattery);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = new Notification.Builder(this)
                    .setContentTitle("Bluetooth Service")
                    .setContentText("Insole battery: L-> "+ leftInsoleBattery+ " | " + rightInsoleBattery +" <-R")
                    .setSmallIcon(R.mipmap.insoles)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(12346, noti);
        return Service.START_STICKY;
        /**return an int value: START_STICKY
         * means the service will be restarted after being killed for some reasons*/    }



    /***provide an instance of the service itself  ***/
    public class InsoleServiceBinder extends Binder {
        public InsoleBluService getService() {
            return InsoleBluService.this;
        }

        public InsoleData getNewInsoleData(){
            return getReceivedSoleData();
        }

        public void startDefinedInsoleBluConnection(GamingInsoleID leftInsoleID, GamingInsoleID rightInsoleID){
            if (bluetoothUtil != null){
                bluetoothUtil.closeInsoleConnection();
                bluetoothUtil.setInsolesMacAddress(leftInsoleID, rightInsoleID);
                bluetoothUtil.startInsoleConnection(soleDataCallback);
            }
        }

        public void stopInsoleBluConnection(){
            if (bluetoothUtil != null){
                bluetoothUtil.closeInsoleConnection();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("--onBind()--InsoleBluService");
        return new InsoleServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("--onUnbind()--InsoleBluService");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**Close the bluetooth server**/
        if (bluetoothUtil != null){
            bluetoothUtil.closeInsoleConnection();
            bluetoothUtil.closeBluetoothServer();
        }
        System.out.println("--onDestroy()--InsoleBluService");
    }


    public InsoleDataCallBack getInsoleDataCallBack() {
        return insoleDataCallBack;
    }

    public void setInsoleDataCallBack(InsoleDataCallBack insoleDataCallBack) {
        this.insoleDataCallBack = insoleDataCallBack;
    }

    public InsoleData getReceivedSoleData(){
        return receivedSoleData;
    }

    /**Callback of BluetoothConnection
     * Response to the events of BluetoothConnection
     * */
    private BluetoothConnection.SoleDataCallback soleDataCallback = new BluetoothConnection.SoleDataCallback(){
        @Override
        public void onNewData(BluetoothDevice bluetoothDevice, SoleData soleData) {
            receivedSoleData = bluetoothUtil.convertSoleDataToInsoleData(bluetoothDevice, soleData);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (insoleDataCallBack != null){
                        insoleDataCallBack.onNewInsoleDataReceived(receivedSoleData);
                    }

                    if (receivedSoleData.getFoot_side() == FootSide.LEFT){
                        leftInsoleBattery = receivedSoleData.getBattery();
                    } else if (receivedSoleData.getFoot_side() == FootSide.RIGHT){
                        rightInsoleBattery = receivedSoleData.getBattery();
                    }
                }
            }).start();



        }

        @Override
        public void onVersion(BluetoothDevice bluetoothDevice, String s, String s1, String s2) {
            Log.i(TAG, "onVersion: Firmware Version "+ s + " Hardware Version " + s1 + " Insole Serial No "  + s2);
            if (insoleDataCallBack != null){
                insoleDataCallBack.onVersionReceived(bluetoothDevice, s, s1, s2);
            }
        }

        @Override
        public void onStart() {
            Log.i(TAG, "onStart: BluConnection started in InsoleBluService");
            if (insoleDataCallBack != null){
                insoleDataCallBack.onConnectionStarted();
            }
        }

        @Override
        public void onStop() {
            Log.i(TAG, "onStop:  BluConnection stopped in InsoleBluService");
            if (insoleDataCallBack != null){
                insoleDataCallBack.onConnectionStopped();
            }
        }
    };

    /**Implementation of BluetoothServerCallback**/
    BluetoothServer.BluetoothServerCallback bluetoothServerCallback=new BluetoothServer.BluetoothServerCallback() {
        @Override
        public void onStartSuccess() {
            Log.i(TAG, "onStartSuccess: BLU SERVER START SUCCESS");
        }

        @Override
        public void onStartFailure() {
            Log.i(TAG, "onStartSuccess: BLU SERVER START FAILURE");
        }

        @Override
        public void onServerClose() {
            //TODO transfer this info. to the iQ-Game App.

            Log.i(TAG, "onStartSuccess: BLU SERVER CLOSED");
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice bluetoothDevice, int i) {
            Log.i(TAG, "onStartSuccess: BLU_DEVICE: "+bluetoothDevice.getName()+(i==1?" is connected!":" is disconnected!"));
        }

        @Override
        public void onEventData(BluetoothDevice bluetoothDevice, InsoleInfo insoleInfo) {
            Log.i(TAG, "onStartSuccess: BLU_DEVICE: "+bluetoothDevice.getName()+" with insole info: "+ insoleInfo.toString());

        }
    };

    private Date lastFrameTimestamp1 = null;
    private Date lastFrameTimestamp2 = null;
    private String insole1Mac = null;
    private String insole2Mac = null;
    private int insole1CountingSamplesMax = 10;
    private int insole2CountingSamplesMax = 10;
    private int insole1CountingSamples = 0;
    private int insole2CountingSamples = 0;
    private void frequencyCounter(final BluetoothDevice bluetoothDevice){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Date currentTimestamp = new Date();


                if (insole1Mac == null && insole2Mac == null){
                    insole1Mac = bluetoothDevice.getAddress();
                    if ( lastFrameTimestamp1 == null ){
                        lastFrameTimestamp1 = currentTimestamp;
                    }
                } else if (insole1Mac == null && !insole2Mac.equals(bluetoothDevice.getAddress()) ){
                    insole1Mac = bluetoothDevice.getAddress();
                    if ( lastFrameTimestamp1 == null ){
                        lastFrameTimestamp1 = currentTimestamp;
                    }
                } else if ( !insole1Mac.equals(bluetoothDevice.getAddress()) && insole2Mac == null){
                    insole2Mac = bluetoothDevice.getAddress();
                    if ( lastFrameTimestamp2 == null ){
                        lastFrameTimestamp2 = currentTimestamp;
                    }
                } else {
                    if (insole1Mac.equals(bluetoothDevice.getAddress())) {
                        if (insole1CountingSamples < insole1CountingSamplesMax) {
                            insole1CountingSamples++;
                        } else {
                            insole1CountingSamples = 0;

                            if (currentTimestamp.before(lastFrameTimestamp1)) {
                                Log.e(TAG, "frequencyCounter: wrong timestamp ");
                                return;
                            }

                            long diffInmills = currentTimestamp.getTime() - lastFrameTimestamp1.getTime();
                            lastFrameTimestamp1 = currentTimestamp;
                            Log.d(TAG, "frequencyCounter: measuring interval of insole with MAC " + insole1Mac + " is " + diffInmills + " mills");
                        }
                    } else if (insole2Mac.equals(bluetoothDevice.getAddress())) {
                        if (insole2CountingSamples < insole2CountingSamplesMax) {
                            insole2CountingSamples++;
                        } else {
                            insole2CountingSamples = 0;

                            if (currentTimestamp.before(lastFrameTimestamp2)) {
                                Log.e(TAG, "frequencyCounter: wrong timestamp ");
                                return;
                            }

                            long diffInmills = currentTimestamp.getTime() - lastFrameTimestamp2.getTime();
                            lastFrameTimestamp2 = currentTimestamp;
                            Log.d(TAG, "frequencyCounter: measuring interval of insole with MAC " + insole1Mac + " is " + diffInmills + " mills");
                        }
                    } else {
                        Log.e(TAG, "frequencyCounter: impossible status ");
                    }
                }
            }
        }).start();

    }
}
