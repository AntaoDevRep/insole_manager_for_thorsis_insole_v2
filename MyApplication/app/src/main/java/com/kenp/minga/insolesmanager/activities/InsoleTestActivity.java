package com.kenp.minga.insolesmanager.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.business.DynamicLineChartManager;

public class InsoleTestActivity extends AppCompatActivity {
    private final String BROADCAST_INSOLE_TEST_ACTION="com.kenp.minga.insoletest.ACTION";
    private static final String TAG = "InsoleTestActivity";
    private final String TIME_STAMP ="time_stamp";

    private DynamicLineChartManager dynamicLineChartManager1;
    private DynamicLineChartManager dynamicLineChartManager2;
    private List<Integer> list = new ArrayList<>(); //数据集合
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private Button stopDisplayButton;
    private Button resumeDisplayButton;
    private Button saveChartButton;

    private LineChart mChart1;
    private LineChart mChart2;

    private List<Integer> leftInsolePreList=new ArrayList<>();
    private List<Integer> rightInsolePreList=new ArrayList<>();

    private String insoleDataTimeStamp="";

    private boolean displayInsoleData=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insole_test);
        initUI();
        setEventerToUI();
        registerInsoleDataBR();
        initCharts();
        displayDataThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterInsoleDataBR();
    }

    //按钮点击添加数据
    public void addEntry(View view) {
        dynamicLineChartManager1.addEntry((int) (Math.random() * 100));
    }

    private void initCharts(){
        mChart1 = (LineChart) findViewById(R.id.dynamic_chart1);
        mChart2 = (LineChart) findViewById(R.id.dynamic_chart2);

        Description description1=new Description();
        description1.setText("Left Insole Pressures");
        description1.setTextColor(Color.BLACK);
        mChart1.setDescription(description1);

        Description description2=new Description();
        description2.setText("Right Insole Pressures");
        description2.setTextColor(Color.BLACK);
        mChart2.setDescription(description2);
        //折线名字 9
        names.add("Pre_MTK1");
        names.add("Pre_MTK2");
        names.add("Pre_MTK3");
        names.add("Pre_MTK4");
        names.add("Pre_MTK5");

        names.add("Pre_HEEL1");
        names.add("Pre_HEEL2");
        names.add("Pre_HEEL3");
        names.add("Pre_HEEL4");
        //折线颜色 9
        colour.add(Color.CYAN);
        colour.add(Color.GREEN);
        colour.add(Color.BLUE);
        colour.add(Color.DKGRAY);
        colour.add(Color.GRAY);

        colour.add(Color.RED);
        colour.add(Color.YELLOW);
        colour.add(Color.MAGENTA);
        colour.add(Color.LTGRAY);

        dynamicLineChartManager1 = new DynamicLineChartManager(mChart1, names, colour);
        dynamicLineChartManager2 = new DynamicLineChartManager(mChart2, names, colour);

        dynamicLineChartManager1.setYAxis(10000, 0, 20);
        dynamicLineChartManager2.setYAxis(10000, 0, 20);

    }

    private void initUI(){
        stopDisplayButton=(Button)findViewById(R.id.stopDisplayBtn);
        resumeDisplayButton=(Button)findViewById(R.id.resumeDisplayBtn);
        saveChartButton=(Button)findViewById(R.id.saveChartBtn);
    }

    private void setEventerToUI(){
        stopDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInsoleData=false;
            }
        });

        resumeDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInsoleData=true;
                displayDataThread();
            }
        });

        saveChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dynamicLineChartManager1.saveLineChart("LeftInsolePressures");
                dynamicLineChartManager2.saveLineChart("RightInsolePressures");
            }
        });
    }

    private void displayDataThread(){
        //死循环添加数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (displayInsoleData) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(leftInsolePreList!=null&&leftInsolePreList.size()==9){
                                //dynamicLineChartManager1.addEntry(leftInsolePreList);
                                dynamicLineChartManager1.addEntry(leftInsolePreList, insoleDataTimeStamp);
                                leftInsolePreList.clear();
                            }

                            if(rightInsolePreList!=null&&rightInsolePreList.size()==9){
                                //dynamicLineChartManager2.addEntry(rightInsolePreList);
                                dynamicLineChartManager2.addEntry(rightInsolePreList, insoleDataTimeStamp);
                                rightInsolePreList.clear();
                            }

                        }
                    });
                }
            }
        }).start();
    }

    /*****************************************************************************************/
    private final String LEFT_PRES="left_pres";
    private final String LEFT_ACCE="left_acce";
    private final String LEFT_TEMP_1="left_temp_1";
    private final String LEFT_TEMP_E="left_temp_e";
    private final String LEFT_BATTERY="left_battery";

    private final String RIGHT_PRES="right_pres";
    private final String RIGHT_ACCE="right_acce";
    private final String RIGHT_TEMP_1="right_temp_1";
    private final String RIGHT_TEMP_E="right_temp_e";
    private final String RIGHT_BATTERY="right_battery";

    private final String INSOLE_FRAME="insole_frame";

    private boolean isInsoleDataBRRegistered=false;

    public  boolean registerInsoleDataBR(){
        isInsoleDataBRRegistered=true;
        Log.i(TAG, "registerInsoleDataBR in InsoleTestActivity: ");
        IntentFilter intentFilter =new IntentFilter(BROADCAST_INSOLE_TEST_ACTION);
        registerReceiver(insoleDataReceiver, intentFilter);
        isInsoleDataBRRegistered=true;
        return true;
    }


    public boolean unregisterInsoleDataBR(){
        if(isInsoleDataBRRegistered&& insoleDataReceiver !=null){
            unregisterReceiver(insoleDataReceiver);
            isInsoleDataBRRegistered=false;
        }
        Log.i(TAG, "unregisterInsoleDataBR:  in InsoleTestActivity");
        return true;
    }


    private BroadcastReceiver insoleDataReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            Log.i(TAG, "onReceive: br");

            String action =intent.getAction();
            if(action.equals(BROADCAST_INSOLE_TEST_ACTION)){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    /*************Receive data from both insoles**********/
                                    long insoleDataFame=intent.getLongExtra(INSOLE_FRAME, 0);

                                    insoleDataTimeStamp=intent.getStringExtra(TIME_STAMP);

                                    float[] aPressuresLeft=intent.getFloatArrayExtra(LEFT_PRES);
                                    int[] aAccelerationsLeft=intent.getIntArrayExtra(LEFT_ACCE);
                                    float temp1Left=intent.getFloatExtra(LEFT_TEMP_1, 0.0f);
                                    float tempELeft=intent.getFloatExtra(LEFT_TEMP_E, 0.0f);
                                    float batteryLeft=intent.getFloatExtra(LEFT_BATTERY, 0.0f);

                                    leftInsolePreList.clear();
                                    for(int i=0;i<aPressuresLeft.length;i++){
                                        leftInsolePreList.add((int)aPressuresLeft[i]);
                                    }
                                    //dynamicLineChartManager1.addEntry(leftInsolePreList);

                                    float[] aPressuresRight=intent.getFloatArrayExtra(RIGHT_PRES);
                                    int[] aAccelerationsRight=intent.getIntArrayExtra(RIGHT_ACCE);
                                    float temp1Right=intent.getFloatExtra(RIGHT_TEMP_1, 0.0f);
                                    float tempERight=intent.getFloatExtra(RIGHT_TEMP_E, 0.0f);
                                    float batteryRight=intent.getFloatExtra(RIGHT_BATTERY, 0.0f);

                                    rightInsolePreList.clear();
                                    for(int i=0;i<aPressuresRight.length;i++){
                                        rightInsolePreList.add((int)aPressuresRight[i]);
                                    }
                                    //dynamicLineChartManager2.addEntry(rightInsolePreList);

                                    Log.i(TAG, "onReceive: both insoles data of the frame: "+insoleDataFame);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();




            }
        }
    };

}
