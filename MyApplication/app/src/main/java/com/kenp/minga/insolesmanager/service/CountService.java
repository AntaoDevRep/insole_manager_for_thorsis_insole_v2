package com.kenp.minga.insolesmanager.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.activities.MainActivity;
import com.kenp.minga.insolesmanager.R;

/**
 * Created by minga on 6/24/2017.
 */

public class CountService  extends Service {
    /** 创建参数 */
    boolean threadDisable;
    int count;
    private static final String TAG = "IN_COUNT_SERVICE";
    private static final String SERVICE_REQUEST_ACTION="SERVICE_REQUEST_ACTION";

    /**** 服务第一次建立的时候会调用这个方法，执行一次性设置程序，在上面2个方法执行前调用。如果服务已存在，则不执行该方法。****/
    public void onCreate() {
        super.onCreate();
        /** 创建一个线程，每秒计数器加一，并在控制台进行Log输出 */
        new Thread(new Runnable() {
            public void run() {
                while (!threadDisable) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                    count++;
                    Log.i(TAG, "count service is running : "+count);

                    Intent intent = new Intent();
                    intent.putExtra("name", " frame: "+String.valueOf(count));


                    //对应BroadcastReceiver中intentFilter的action
                    intent.setAction(SERVICE_REQUEST_ACTION);
                    //发送广播
                    sendBroadcast(intent);


                }
            }
        }).start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub


        //TODO: check whether the Bluetooth Server is still running??
        //TODO: if not, create a new thread to start the Bluetooth Server


        /**show a notification to user that the service is running...*/
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification noti = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = new Notification.Builder(this)
                    .setContentTitle("Count Service")
                    .setContentText("The count service is running ...")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(12346, noti);
        return Service.START_STICKY;
        /**返回一个整型值，用来描述系统在杀掉服务后是否要继续启动服务
         * 当服务被杀死后将继续重新创建服务。。。。。。
         * 系统重新创建服务并且调用onStartCommand()方法，但并不会传递最后一次传递的intent，只是传递一个空的intent。
         * 除非存在将要传递来的intent，那么就会传递这些intent。这个适合播放器一类的服务，不需要执行命令，只需要独自运行，等待任务。*/
    }

    public void onDestroy() {
        super.onDestroy();
        /** 服务停止时，终止计数进程 */
        this.threadDisable = true;
    }

    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("onBind.....");
        IBinder result = null;
        if ( null == result ) result = new ServiceBinder() ;
        Toast.makeText(this, "onBind",Toast.LENGTH_LONG);
        Log.i(TAG, "onBind: ....in Count Service");
        return result;
    }

    public int getConunt() {
        return count;
    }

    /***此方法是为了可以在Acitity中获得服务的实例  ***/
    public class ServiceBinder extends Binder {
        public CountService getService() {
            return CountService.this;
        }
    }


    /**Thread to start the BluetoothServer
     * better to be called in the onCreate method*/
    private Thread startBluetoothServerThread(){
        Thread bls_thread=new Thread(new Runnable() {
            @Override
            public void run() {

                //TODO override the Callback of BluetoothServer
                /**Whenever get the events, create a intent with data and sendBroadcast*/


                //TODO: create the instance of BluetoothServer

                //TODO: set the events CallBack to the BluetoothServer

                //TODO: start the BluetoothServer


            }
        });


        return bls_thread;
    }
}