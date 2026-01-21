package com.kenp.minga.insolesmanager.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.adapter.DevicesListViewAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import de.thorsis.android.insole.gaminglibrary.SoleData;
import de.thorsis.android.insole.gaminglibrary.bluetooth.BluetoothConnection;

public class AddDeviceActivity extends AppCompatActivity {

    private final String TAG = "ADD_DEVICE_ACTIVITY";

    private final String BROADCAST_ACTION="NEW_INSOLE_DATA_UPDATED_ACTION";

    private Button scan_device_btn;
    private Button pair_device_btn;
    private Button unpair_device_btn;
    private Button unpair_all_device_btn;
    private ListView discovered_devices_list;
    private ArrayList<BluetoothDevice> blu_devices;
    private ArrayList<BluetoothDevice> selected_devices;


    private BluetoothAdapter btAdapter;
    private DevicesListViewAdapter DevicesListViewAdapter;

    private boolean isResceiver1Registered=false;
    private boolean isResceiver2Registered=false;
    private boolean isResceiver3Registered=false;
    private boolean isResceiver4Registered=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        blu_devices =new ArrayList<>();
        selected_devices =new ArrayList<>();
        fentchUI();
    }


    @Override
    protected void onStart() {
        enableBluetooth();



        //Broadcasts when bond state changes (ie: pairing)
        IntentFilter filter =new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver4,filter);
        isResceiver4Registered=true;

        super.onStart();
    }

    private void fentchUI(){
        scan_device_btn=(Button)findViewById(R.id.scan_btn);
        pair_device_btn=(Button)findViewById(R.id.pair_btn);
        unpair_device_btn=(Button)findViewById(R.id.unpair_btn);
        unpair_all_device_btn=(Button)findViewById(R.id.unpair_all_btn);


        discovered_devices_list=(ListView)findViewById(R.id.discovered_devices_list);
        DevicesListViewAdapter =new DevicesListViewAdapter(this, R.layout.device_list_row_layout, blu_devices);
        discovered_devices_list.setAdapter(DevicesListViewAdapter);
        /** click the item to select the device, and  click again to cancel the selection**/
        discovered_devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!selected_devices.contains(blu_devices.get(position))){
                    //when the insole is not already selected for connection
                    //then add it as the target insoles and highlight it
                    selected_devices.add(blu_devices.get(position));
                    view.setBackgroundColor(getResources().getColor(R.color.bg_lite_blue));
                }else{
                    //when the insole is already selected for connection
                    //then delete it from the target insoles group and normalize it
                    selected_devices.remove(blu_devices.get(position));
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        });




        scan_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
                blu_devices.clear();
                DevicesListViewAdapter.notifyDataSetChanged();

            }
        });

        pair_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BluetoothDevice bld:selected_devices) {
                    try {
                        /**** use the method in library instead
                         * **createBond(bld.getClass(), bld);*/
                        startConnection(bld);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /**after all the selected BluetoothDevice are paired, clear the selected devices.. */
                selected_devices.clear();
            }
        });

        unpair_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_devices!=null&&selected_devices.size()>0){
                    unpairDevice(selected_devices);
                    /**real time the color in list**/
                    DevicesListViewAdapter.notifyDataSetChanged();

                }
            }
        });

        unpair_all_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
                ArrayList<BluetoothDevice> devices_al=new ArrayList<>();
                if (devices.size() > 0) {
                    for (BluetoothDevice bluetoothDevice : devices) {
                        devices_al.add(bluetoothDevice);
                    }
                }

                unpairDevice(devices_al);

            }
        });


    }











    private void enableBluetooth(){
        // Get local Bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }else{
            // find all the already paired devices
            Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
            //paired devices...
            if (devices.size() > 0) {
                for (BluetoothDevice bluetoothDevice : devices) {
                       if(!blu_devices.contains(bluetoothDevice)){
                           blu_devices.add(bluetoothDevice);
                           DevicesListViewAdapter.notifyDataSetChanged();
                       }
                }
            }
        }

        // If BT is not on, request that it be enabled.
        if (!btAdapter.isEnabled()){
            //1. enable the bluetooth when it is switched off...
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);

        }else{

            Log.i(TAG, "blu is enabled");
            //2. enable the bluetooth discoverability when it is switched off...
            openDiscoverability();

            //3. start the bluetooth discovery
            startDiscovery();
        }
        IntentFilter intentFilter =new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver1, intentFilter);
        isResceiver1Registered=true;

    }


    private void openDiscoverability(){

//        if(btAdapter.getScanMode()==BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
//            Log.i(TAG, "openDiscoverability: blu is already dicoverable !!!");
//        }else{
            //2. enable the bluetooth discoverability when it is switched off...
            Intent discoverableIntent =new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            IntentFilter intentFilter =new IntentFilter(btAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(broadcastReceiver2, intentFilter);
            isResceiver2Registered=true;

//        }
        
    }


    private void startDiscovery(){

        if(btAdapter.isEnabled()){
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
                Log.i(TAG, "onActivityResult: blu is discovering and canceled...");
            }
            btAdapter.startDiscovery();


            IntentFilter intentFilterDiscovery =new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver3, intentFilterDiscovery);
            isResceiver3Registered=true;

            Log.i(TAG, "onActivityResult: blu starts new discovery");
        }
    }


    private void pairDevice(BluetoothDevice bld){
        if(bld.getBondState()==BluetoothDevice.BOND_NONE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {



                bld.createBond();
                Log.i(TAG, "onClick: start to connect with "+bld.getName()+" : "+bld.getAddress());
            }
        }else if(bld.getBondState()==BluetoothDevice.BOND_BONDING){
            Log.i(TAG, "onClick: connecting with "+bld.getName()+" : "+bld.getAddress());
        }else if(bld.getBondState()==BluetoothDevice.BOND_BONDED){
            Log.i(TAG, "onClick: already connected with "+bld.getName()+" : "+bld.getAddress());
        }
    }




    /**
     * pair device
     *
     * @param btClass
     * @param btDevice
     * @return
     * @throws Exception
     */
    public boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        if(btDevice.getBondState()==BluetoothDevice.BOND_NONE){
            Method createBondMethod = btClass.getMethod("createBond");//获取蓝牙的连接方法
            Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
            return returnValue.booleanValue();//返回连接状态
        }else if(btDevice.getBondState()==BluetoothDevice.BOND_BONDING){
            Log.i(TAG, "onClick: connecting with "+btDevice.getName()+" : "+btDevice.getAddress());
            return false;
        }else if(btDevice.getBondState()==BluetoothDevice.BOND_BONDED){
            Log.i(TAG, "onClick: already connected with "+btDevice.getName()+" : "+btDevice.getAddress());
            return false;
        }else{
            return false;
        }
    }








    private void unpairDevice(ArrayList<BluetoothDevice> devices ) {
        try {
            /**** use the methods in library instead
            for (BluetoothDevice device: devices)
            {
                Method m = device.getClass()
                        .getMethod("removeBond", (Class[]) null);
                m.invoke(device, (Object[]) null);
            }**/
            for (BluetoothDevice device: devices){
                closeConnection(device);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * BroadcastReceiver to catch the changes during enabling the bluetooth...
     * */
    private final BroadcastReceiver broadcastReceiver1=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(action.equals(btAdapter.ACTION_STATE_CHANGED)){
                final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "onReceive: STATE ON");
                        //2. enable the bluetooth discoverability when it is switched off...
                        openDiscoverability();
                        //3. start the bluetooth discovery
                        startDiscovery();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * BroadcastReceiver to catch the changes setting discoverable...
     * */
    private final BroadcastReceiver broadcastReceiver2=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(action.equals(btAdapter.ACTION_SCAN_MODE_CHANGED)){
                final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, btAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.i(TAG, "onbroadcastReceiver2: dicoverability enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.i(TAG, "onbroadcastReceiver2:dicoverability disabled, able to receive connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.i(TAG, "onbroadcastReceiver2:dicoverability disabled, unable to receive connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.i(TAG, "onbroadcastReceiver2: connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.i(TAG, "onbroadcastReceiver2: connected");
                        break;
                }
            }
        }
    };


    /**
     * BroadcastReceiver to catch the changes during discovering...
     * */
    private final BroadcastReceiver broadcastReceiver3=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "onReceive: "+ device.getName()+" : "+ device.getAddress());
                if(!blu_devices.contains(device)){
                    blu_devices.add(device);
                }
                DevicesListViewAdapter.notifyDataSetChanged();

                if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    //bonded already
                    Log.i(TAG, "onReceive:"+device.getName()+" bonded already");
                }else if(device.getBondState()==BluetoothDevice.BOND_BONDING){
                    //create a bond
                    Log.i(TAG, "onReceive:"+device.getName()+" create a bond");
                }else if(device.getBondState()==BluetoothDevice.BOND_NONE){
                    //breaking a bond
                    Log.i(TAG, "onReceive:"+device.getName()+" breaking a bond");
                }
            }
        }
    };

    /**
     * BroadcastReceiver to catch the changes after pairing the insoles...
     * */
    private final BroadcastReceiver broadcastReceiver4=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    //bonded already
                    Log.i(TAG, "onReceive:"+device.getName()+" bonded already");
                }else if(device.getBondState()==BluetoothDevice.BOND_BONDING){
                    //create a bond
                    Log.i(TAG, "onReceive:"+device.getName()+" create a bond");
                }else if(device.getBondState()==BluetoothDevice.BOND_NONE){
                    //breaking a bond
                    Log.i(TAG, "onReceive:"+device.getName()+" breaking a bond");
                }

                DevicesListViewAdapter.notifyDataSetChanged();

            }
        }
    };


    @Override
    protected void onDestroy() {

        if (null != broadcastReceiver1&&isResceiver1Registered==true) {
            unregisterReceiver(broadcastReceiver1);
        }

        if (null != broadcastReceiver2&&isResceiver2Registered==true) {
            unregisterReceiver(broadcastReceiver2);
        }

        if (null != broadcastReceiver3&&isResceiver3Registered==true) {
            unregisterReceiver(broadcastReceiver3);
        }

        if (null != broadcastReceiver4&&isResceiver4Registered==true) {
            unregisterReceiver(broadcastReceiver4);
        }

        super.onDestroy();
    }



    private void startConnection(BluetoothDevice device){

        try{
            BluetoothConnection bluetoothConnection=new BluetoothConnection(device, soleDataCallback);
            bluetoothConnection.start();

        }catch (java.lang.Exception ex){
            ex.printStackTrace();
        }
    }


    private void closeConnection(BluetoothDevice device){

        try{
            BluetoothConnection bluetoothConnection=new BluetoothConnection(device, soleDataCallback);
            bluetoothConnection.close();

        }catch (java.lang.Exception ex){
            ex.printStackTrace();
        }
    }

    BluetoothConnection.SoleDataCallback soleDataCallback=new BluetoothConnection.SoleDataCallback() {
        @Override
        public void onNewData(BluetoothDevice bluetoothDevice, SoleData soleData) {
            Log.i(TAG, "onStart: get data from connection with "+bluetoothDevice.getName());

            /**send broadcast with data**/
            Intent localIntent = new Intent(BROADCAST_ACTION);
            float[] arrayOfPress=new float[9];
            arrayOfPress[0]=soleData.pressSensor1;
            arrayOfPress[1]=soleData.pressSensor2;
            arrayOfPress[2]=soleData.pressSensor3;
            arrayOfPress[3]=soleData.pressSensor4;
            arrayOfPress[4]=soleData.pressSensor5;
            arrayOfPress[5]=soleData.pressSensor6;
            arrayOfPress[6]=soleData.pressSensor7;
            arrayOfPress[7]=soleData.pressSensor8;
            arrayOfPress[8]=soleData.pressSensor9;
            float[] arrayOfTemp=new float[2];
            //arrayOfTemp[0]= soleData.tempSensor1;
            // the temp sensor was deleted in version 2.0.0
            arrayOfTemp[0]= 0.0f;

            arrayOfTemp[1]=soleData.tempEnvironment;
            localIntent.putExtra("battery",soleData.battery);
            localIntent.putExtra("temp",arrayOfTemp);
            localIntent.putExtra("pressure",arrayOfPress);
            //TODO check the right or left insole...
            getApplicationContext().sendBroadcast(localIntent);

        }

        @Override
        public void onVersion(BluetoothDevice bluetoothDevice, String s, String s1, String s2) {
            Log.i(TAG, "onVersion: "+ s + "  " + s1 + "  "  + s2);
        }

        @Override
        public void onStart() {
            Log.i(TAG, "onStart: connection start");

        }

        @Override
        public void onStop() {
            Log.i(TAG, "onStart: connection stop");

        }
    };


    private  final BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


}
