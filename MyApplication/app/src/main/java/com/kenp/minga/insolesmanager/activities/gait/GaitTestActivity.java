package com.kenp.minga.insolesmanager.activities.gait;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.kenp.minga.insolesmanager.business.BluetoothUtil;
import com.kenp.minga.insolesmanager.helper.DynamicLineChartManager;
import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.callbacks.InsoleDataCallBack;
import com.kenp.minga.insolesmanager.model.FootSide;
import com.kenp.minga.insolesmanager.model.GaitData;
import com.kenp.minga.insolesmanager.model.InsoleData;
import com.kenp.minga.insolesmanager.service.InsoleBluService;

import java.util.ArrayList;
import java.util.List;

import de.thorsis.android.insole.gaminglibrary.SoleData;

public class GaitTestActivity extends AppCompatActivity implements InsoleDataCallBack {
    private static final String TAG = GaitTestActivity.class.getSimpleName();
    private DynamicLineChartManager dynamicLineChartManager1;
    private DynamicLineChartManager dynamicLineChartManager2;
    private DynamicLineChartManager dynamicLineChartManager3;
    private DynamicLineChartManager dynamicLineChartManager4;
    private List<Integer> leftAcceList = new ArrayList<>(); //数据集合
    private List<Integer> rightAcceList = new ArrayList<>(); //数据集合
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private InsoleBluService insoleBluService;
    private InsoleBluService.InsoleServiceBinder insoleBluServicebinder;
    private boolean showGaitData;
    private boolean saveGaitData;
    private BluetoothUtil bluetoothUtil = null;
    private InsoleData newInsoleData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gait_test);

        showGaitData = false;
        saveGaitData = false;

        LineChart mChart1 = (LineChart) findViewById(R.id.dynamic_chart1);
        LineChart mChart2 = (LineChart) findViewById(R.id.dynamic_chart2);
        LineChart mChart3 = (LineChart) findViewById(R.id.dynamic_chart3);
        LineChart mChart4 = (LineChart) findViewById(R.id.dynamic_chart4);
        //折线名字
        names.add("x");
        names.add("y");
        names.add("z");
        //折线颜色
        colour.add(Color.CYAN);
        colour.add(Color.GREEN);
        colour.add(Color.BLUE);

        dynamicLineChartManager1 = new DynamicLineChartManager(mChart1, names, colour);
        dynamicLineChartManager2 = new DynamicLineChartManager(mChart2, names, colour);

        dynamicLineChartManager3 = new DynamicLineChartManager(mChart3, "SUM", Color.RED);
        dynamicLineChartManager4 = new DynamicLineChartManager(mChart4, "SUM", Color.RED);

        dynamicLineChartManager1.setYAxis(180, -180, 10);
        dynamicLineChartManager2.setYAxis(180, -180, 10);
        dynamicLineChartManager3.setYAxis(180, 0, 10);
        dynamicLineChartManager4.setYAxis(180, 0, 10);

        dynamicLineChartManager1.setDescription("acceleration of left insole");
        dynamicLineChartManager2.setDescription("acceleration of right insole");
        dynamicLineChartManager3.setDescription("acceleration sum of left insole");
        dynamicLineChartManager4.setDescription("acceleration sum of right insole");
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindInsoleBluService();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (insoleBluService != null){
            unbindInsoleBluService();
        }
    }

    //按钮点击添加数据
    public void addEntry(View view) {
        dynamicLineChartManager1.addEntry((int) (Math.random() * 100));
    }

    public void startDataViewing(View view){
        showGaitData = true;
    }

    public void stopDataViewing(View view){
        showGaitData = false;
    }

    public void startDataSaving(View view){
        saveGaitData = true;
    }

    public void stopDataSaving(View view){
        saveGaitData = false;
    }



    //bind to the Bluetooth Service
    private void bindInsoleBluService() {
        Intent intent = new Intent(this, InsoleBluService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    //unbind to the Bluetooth Service
    private void unbindInsoleBluService() {
        if (insoleBluService != null) {
            insoleBluService = null;
            unbindService(serviceConnection);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: in " + TAG);
            insoleBluServicebinder = (InsoleBluService.InsoleServiceBinder) service;
            insoleBluService = insoleBluServicebinder.getService(); //got an instance of the Bluetooth Service
            insoleBluService.setInsoleDataCallBack(GaitTestActivity.this);
            //After successful binding with InsoleBluService, try to start the connection to the insoles
            Log.i(TAG, "onServiceConnected: in " + TAG + ". After successful binding with InsoleBluService, try to start the connection to the insoles.");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: in " + TAG);
            insoleBluService=null;
        }
    };

    @Override
    public void onNewInsoleDataReceived(final InsoleData soleData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                newInsoleData = soleData;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ( newInsoleData != null && showGaitData )
                        {
                            if ( newInsoleData.getFoot_side() == FootSide.LEFT ){
                                leftAcceList.add((int)newInsoleData.getAcce_x());
                                leftAcceList.add((int)newInsoleData.getAcce_y());
                                leftAcceList.add((int)newInsoleData.getAcce_z());
                                dynamicLineChartManager1.addEntry(leftAcceList);
                                leftAcceList.clear();

                                GaitData gaitData = new GaitData(newInsoleData);
                                dynamicLineChartManager3.addEntry((int)gaitData.getAccelerationVectorSum());
                            } else {
                                rightAcceList.add((int)newInsoleData.getAcce_x());
                                rightAcceList.add((int)newInsoleData.getAcce_y());
                                rightAcceList.add((int)newInsoleData.getAcce_z());
                                dynamicLineChartManager2.addEntry(rightAcceList);
                                rightAcceList.clear();

                                GaitData gaitData = new GaitData(newInsoleData);
                                dynamicLineChartManager4.addEntry((int)gaitData.getAccelerationVectorSum());
                            }
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void onConnectionStarted() {
        Log.d(TAG, "onConnectionStarted: InsoleBluService");

    }

    @Override
    public void onConnectionStopped() {
        Log.d(TAG, "onConnectionStopped: InsoleBluService");

    }

    @Override
    public void onVersionReceived(BluetoothDevice bluetoothDevice, String firmwareVersion, String hardwareSerial, String insoleSerial) {

    }
}
