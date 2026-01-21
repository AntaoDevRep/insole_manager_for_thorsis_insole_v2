package com.kenp.minga.insolesmanager.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.service.CountService;

public class ServiceTestActivity extends AppCompatActivity {

    private static final String TAG = "SERVICE_TEST_ACTIVITY";
    private static final String SERVICE_REQUEST_ACTION="SERVICE_REQUEST_ACTION";
    private Button bond_service_btn;
    private Button fentch_service_btn;
    private TextView service_console_text;

    private serviceRequstBroadcastReceiver mBroadcastReceiver;

    /** 参数设置 */
    CountService countService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
        fentchUI();


    }

    private void fentchUI(){
        service_console_text=(TextView)findViewById(R.id.service_console_text);
        service_console_text.setText("init text");

        bond_service_btn=(Button)findViewById(R.id.bond_service_btn);
        if(bond_service_btn!=null){
            bond_service_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ServiceTestActivity.this, CountService.class);
                    /** 进入Activity开始服务 */
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);

                }
            });
        }


        fentch_service_btn =(Button)findViewById(R.id.fentch_service_btn);
        if(fentch_service_btn!=null){
            fentch_service_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(countService!=null){
                        service_console_text.setText("count service is started for: "+countService.getConunt()+"s");
                    }else{
                        Log.i(TAG, "onClick: no countService is bonded from service, it's null");
                    }
                    //实例化BroadcastReceiver子类 &  IntentFilter
                    mBroadcastReceiver = new serviceRequstBroadcastReceiver();
                    IntentFilter intentFilter = new IntentFilter();

                    //设置接收广播的类型
                    intentFilter.addAction(SERVICE_REQUEST_ACTION);

                    //调用Context的registerReceiver（）方法进行动态注册
                    registerReceiver(mBroadcastReceiver, intentFilter);


                }
            });
        }

    }

    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            countService = ((CountService.ServiceBinder) service).getService();

            Log.d(TAG, "onServiceDisconnected: Binder of count service is found:"+countService.toString());
//            if(countService!=null){
//                service_console_text.setText("count service is started for: "+countService.getConunt()+"s");
//            }else{
//                Log.i(TAG, "onClick: no countService is bonded from service, it's null");
//            }
            /**将读取服务中数据的代码放在这里同样不能实现持续的更新。。。。只会在bound得时候运行一次*/
        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            countService = null;
            Log.d(TAG, "onServiceDisconnected: no Binder of count service is found... ");
        }

    };

    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(conn);
        unregisterReceiver(mBroadcastReceiver);
        Log.i(TAG, "onDestroy: unbind count service...");
    }


    public class serviceRequstBroadcastReceiver extends BroadcastReceiver {
        //接收到广播后自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            String data=  intent.getStringExtra("name");
            if(countService!=null){
                service_console_text.setText("count service is started for: "+countService.getConunt()+"s"+ "with data"+data);
            }else{
                Log.i(TAG, "onClick: no countService is bonded from service, it's null");
            }



        }
    }
}
