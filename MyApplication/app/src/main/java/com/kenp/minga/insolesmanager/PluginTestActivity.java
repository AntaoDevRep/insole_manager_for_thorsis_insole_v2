package com.kenp.minga.insolesmanager;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.Log;
import android.widget.TextView;

import com.kenp.minga.insolesmanager.model.InsoleData;

import java.util.ArrayList;

public class PluginTestActivity extends AppCompatActivity {

    private final String TAG="PluginTestActivity";
    private final String BROADCAST_ACTION="com.kenp.minga.insolesmanager.ACTION";
    private final String PRES="pres";
    private final String ACCE="acce";
    private final String TEMP_1="temp_1";
    private final String TEMP_E="temp_e";

    private final String BATTERY="battery";
    private final String FOOT_SIDE="foot_side";
    private final String FRAME="frame";

    private TextView insole_data_text;
    private long frame_num=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_test);
        insole_data_text=(TextView)findViewById(R.id.insole_data_text_view) ;
        insole_data_update();
    }

    private void insole_data_update(){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();

            /**Create a random data every 1000ms (1s), totally 50000**/
            CountDownTimer cdt = new CountDownTimer(500000, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    InsoleData insole_data=new InsoleData();
                    insole_data.randomAInsoleData();
                    broadcast_insole_data(insole_data);
                    insole_data_text.setText("("+frame_num+") "+insole_data.convertToStringData());

                    frame_num++;

                }
                @Override
                public void onFinish() {
                    insole_data_text.setText("50000 times random is finished");
                    Log.i(TAG, "onFinish: once....");
                    this.start();
                }
            };

            cdt.start();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    private void broadcast_insole_data(InsoleData insoleData){
        Log.i(TAG, "broadcast_insole_data(): ...");


        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(FOOT_SIDE, insoleData.getFoot_side());

        intent.putExtra(BATTERY, insoleData.getBattery());

        ArrayList<Integer> pressures=new ArrayList<>();
        //pressures.add(insoleData.getPres_1());
        //pressures.add(insoleData.getPres_2());
        //pressures.add(insoleData.getPres_3());
        //pressures.add(insoleData.getPres_4());
        //pressures.add(insoleData.getPres_5());
        //pressures.add(insoleData.getPres_6());
        //pressures.add(insoleData.getPres_7());
        //pressures.add(insoleData.getPres_8());
        //pressures.add(insoleData.getPres_9());

        intent.putIntegerArrayListExtra(PRES, pressures);

        ArrayList<Float> accelerations=new ArrayList<>();
        accelerations.add(insoleData.getAcce_x());
        accelerations.add(insoleData.getAcce_y());
        accelerations.add(insoleData.getAcce_z());

        intent.putExtra(ACCE,accelerations);

        intent.putExtra(TEMP_E, insoleData.getTemp_e());
        intent.putExtra(FRAME, frame_num);

        sendBroadcast(intent);
    }
}
