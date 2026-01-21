package com.kenp.minga.insolesmanager.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.activities.gait.GaitTestActivity;
import com.kenp.minga.insolesmanager.business.BluetoothUtil;
import com.kenp.minga.insolesmanager.business.DateUtil;
import com.kenp.minga.insolesmanager.business.DialogUtil;
import com.kenp.minga.insolesmanager.callbacks.DialogCallBack;
import com.kenp.minga.insolesmanager.callbacks.InsoleDataCallBack;
import com.kenp.minga.insolesmanager.helper.CsvHelper;
import com.kenp.minga.insolesmanager.PluginTestActivity;
import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.model.BatteryStatusTracker;
import com.kenp.minga.insolesmanager.model.Const;
import com.kenp.minga.insolesmanager.model.FootSide;
import com.kenp.minga.insolesmanager.model.GamingInsoleID;
import com.kenp.minga.insolesmanager.model.InsoleData;
import com.kenp.minga.insolesmanager.model.InsoleSide;
import com.kenp.minga.insolesmanager.service.CountService;
import com.kenp.minga.insolesmanager.service.InsoleBluService;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import de.thorsis.android.insole.gaminglibrary.SoleData;
import de.thorsis.android.insole.gaminglibrary.bluetooth.BluetoothConnection;
import de.thorsis.android.insole.gaminglibrary.bluetooth.BluetoothServer;

public class MainActivity extends AppCompatActivity implements  InsoleDataCallBack {

    /**Constant values**/
    private static final String TAG = "MAIN_ACTIVITY";
    private static final String SERVICE_REQUEST_ACTION = "SERVICE_REQUEST_ACTION";
    private static final int FRAME_NUMBER_MAX = 40000;
    private static final int LEFT =1;
    private static final int RIGHT =2;
    private static final float PRESSURE_COLUMN_MAX_HEIGHT=180;
    private static final float PRESSURE_MAX_VALUE=12.0f;
    private static final float PRESSURE_MIN_VALUE=0.0f;
    private static final float RED_BATTERY=3.2f;
    private static final float YELLOW_BATTERY=3.4f;

    /**UI elements**/
    private Button add_device_btn;
    private Button load_insole_data_btn;
    private Button start_service_btn;
    private Button stop_service_btn;
    private Button start_service_activity_btn;

    private TextView console_text_view;

    /**Basic variables**/
    private boolean isHeadWritten=false;
    private boolean sendDataForTest=false;

    /**Functional instances**/
    private mainActivityBroadcastReceiver mBroadcastReceiver;
    private BluetoothAdapter btAdapter;
    private ArrayList<BluetoothDevice> bondedDevices;
    private Context ctx;
    private BluetoothServer bluetoothServer;
    private CsvHelper csvHelper;

    private  BluetoothConnection bluetoothConnection_1;
    private  BluetoothConnection bluetoothConnection_2;

    private Calendar calendar;


    private ImageView leftMtk1Column;
    private ImageView leftMtk2Column;
    private ImageView leftMtk3Column;
    private ImageView leftMtk4Column;
    private ImageView leftMtk5Column;
    private ImageView leftMtk6Column;
    private ImageView leftC1Column;
    private ImageView leftC2Column;
    private ImageView leftC3Column;
    private ImageView leftC4Column;

    private ImageView rightMtk1Column;
    private ImageView rightMtk2Column;
    private ImageView rightMtk3Column;
    private ImageView rightMtk4Column;
    private ImageView rightMtk5Column;
    private ImageView rightMtk6Column;
    private ImageView rightC1Column;
    private ImageView rightC2Column;
    private ImageView rightC3Column;
    private ImageView rightC4Column;

    private TextView leftMtk1PreValue;
    private TextView leftMtk2PreValue;
    private TextView leftMtk3PreValue;
    private TextView leftMtk4PreValue;
    private TextView leftMtk5PreValue;
    private TextView leftMtk6PreValue;
    private TextView leftC1PreValue;
    private TextView leftC2PreValue;
    private TextView leftC3PreValue;
    private TextView leftC4PreValue;

    private TextView leftBatteryValue;
    private TextView leftEnTempValue;
    private TextView leftAcceValue;

    private TextView rightMtk1PreValue;
    private TextView rightMtk2PreValue;
    private TextView rightMtk3PreValue;
    private TextView rightMtk4PreValue;
    private TextView rightMtk5PreValue;
    private TextView rightMtk6PreValue;
    private TextView rightC1PreValue;
    private TextView rightC2PreValue;
    private TextView rightC3PreValue;
    private TextView rightC4PreValue;

    private TextView rightBatteryValue;
    private TextView rightEnTempValue;
    private TextView rightAcceValue;

    private ImageView leftBatteryIcon;
    private ImageView rightBatteryIcon;
    private ImageView leftConnectionIcon;
    private ImageView rightConnectionIcon;
    private TextView leftInsoleNameTv;
    private TextView rightInsoleNameTv;
    private TextView leftInsoleMacTv;
    private TextView rightInsoleMacTv;
    private TextView leftInsoleVersionTv;
    private TextView rightInsoleVersionTv;

    private Button retryConnectionBtn;
    private Switch dataSavingSwitch;

    private InsoleBluService insoleBluService;
    private InsoleBluService.InsoleServiceBinder insoleBluServicebinder;
    private InsoleData leftInsoleData;
    private InsoleData rightInsoleData;
    private GamingInsoleID leftInsoleID;
    private GamingInsoleID rightInsoleID;
    private AlertDialog wrongPairedInsolesDialog = null;
    private AlertDialog wrongInsolesIDDialog = null;

    private int leftInsoleConnectionFlag = 0;
    private int rightInsoleConnectionFlag = 0;
    private int insoleConnectionFlagMax = 10;

    private ProgressDialog connectionProgressDialog;
    private boolean retringConnection = false;
    private boolean firstLowBatteryErrorSent = false;
    private boolean firstNoConnectionErrorSent = false;

    private BatteryStatusTracker bst1 = null;
    private BatteryStatusTracker bst2 = null;
    private boolean dataRecordingEnabled = false;
    private InsoleData newInsoleData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fentchUI();
        setEventListener();
        initVariables();
        //open bluetooth
        //BluetoothUtil bluetoothUtil =new BluetoothUtil(MainActivity.this);
        //bluetoothUtil.enable_bluetooth();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                if ( !btAdapter.isEnabled()) {
                    btAdapter.enable();
                    while (!btAdapter.isEnabled()) {
                        Log.i(TAG, "run: waiting for the opening of BLUETOOTH...");
                    }
                }
                //start InsoleBluService
                startInsoleBluService();
                bindInsoleBluService();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateConnectionProgressDialog(true);
                    }
                });
            }
        }).start();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = getString(R.string.app_version_key)  + pInfo.versionName;
            TextView versionTextView = findViewById(R.id.versionTextView);
            versionTextView.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wrongPairedInsolesDialog != null){
            wrongPairedInsolesDialog.dismiss();
        }
        if (wrongInsolesIDDialog != null){
            wrongInsolesIDDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.i(TAG, "onOptionsItemSelected: try to build connection to after app resumed...");
        //getGamingInsolePropertyAndBuildConnection(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver);
        }

        if (insoleBluService != null){
            unbindInsoleBluService();
            closeInsoleBluService();
        }
    }

    /** Connect the menu.xml to the activity **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /** default method to listen to the selection event of the menu action bar **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting_item:
                Log.i(TAG, "onOptionsItemSelected: open setting ...");
                Context context7 = MainActivity.this;
                Intent newStartIntent7= new Intent(context7,SettingsActivity.class);
                newStartIntent7.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(newStartIntent7);
                break;
            case R.id.connect_item:
                Log.i(TAG, "onOptionsItemSelected: build connection to insoles manually...");
                getGamingInsolePropertyAndBuildConnection(MainActivity.this);
                break;
            case R.id.add_insole_item:
                //stop the all the connections with insoles
                if (insoleBluServicebinder != null){
                    insoleBluServicebinder.stopInsoleBluConnection();
                    leftConnectionIcon.setVisibility(View.INVISIBLE);
                    rightConnectionIcon.setVisibility(View.INVISIBLE);
                }
                //open the InsolesManagerActivity for setting (insole side)
                openInsoleManagerForResult();
                break;
            case R.id.test_plugin_item:
                Context context1 = MainActivity.this;
                Intent newStartIntent1= new Intent(context1,PluginTestActivity.class);
                newStartIntent1.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(newStartIntent1);
                break;
            case R.id.test_insole_item:
                sendDataForTest=true;
                Context context2 = MainActivity.this;
                Intent newStartIntent2= new Intent(context2,InsoleTestActivity.class);
                newStartIntent2.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(newStartIntent2);
                break;
            case R.id.debug_item:
                sendDataForTest=true;
                Context context3 = MainActivity.this;
                Intent newStartIntent3= new Intent(context3, DebugActivity.class);
                newStartIntent3.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(newStartIntent3);
                break;
            case R.id.gait_test_item:
                Context context4 = MainActivity.this;
                Intent newStartIntent4= new Intent(context4, GaitTestActivity.class);
                newStartIntent4.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(newStartIntent4);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== Const.INSOLE_MANAGER_REQUESTCODE && resultCode==Const.RESULT_OK){
            Log.i(TAG, "onActivityResult: after set the insole data, rebuild the connection to the insoles...");
            getGamingInsolePropertyAndBuildConnection(MainActivity.this);
        }
    }

    @Override
    public void onNewInsoleDataReceived(final InsoleData soleData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                newInsoleData = soleData;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateInsoleDataUI(newInsoleData);
                        checkInsoleBattery(newInsoleData);
                        if (newInsoleData.getFoot_side() == FootSide.LEFT){
                            leftInsoleData = newInsoleData;
                            if (leftInsoleConnectionFlag < insoleConnectionFlagMax){
                                leftInsoleConnectionFlag ++;
                                leftConnectionIcon.setVisibility(View.VISIBLE);
                            }else {
                                leftInsoleConnectionFlag = 0;
                                leftConnectionIcon.setVisibility(View.INVISIBLE);
                            }
                        }else if (newInsoleData.getFoot_side() == FootSide.RIGHT){
                            rightInsoleData = newInsoleData;
                            if (rightInsoleConnectionFlag < insoleConnectionFlagMax){
                                rightInsoleConnectionFlag ++;
                                rightConnectionIcon.setVisibility(View.VISIBLE);
                            }else {
                                rightInsoleConnectionFlag = 0;
                                rightConnectionIcon.setVisibility(View.INVISIBLE);
                            }
                        }

                        if (leftInsoleData != null && rightInsoleData != null){
                            updateConnectionProgressDialog(false);
                            broadcast_both_insoles_data(leftInsoleData, rightInsoleData);
                            //Log.i(TAG, "onNewInsoleDataReceived: UPDATE BOTH INSOLES DATA " + new SimpleDateFormat("MM:dd:mm:ss").format(calendar.getTime()));
                            leftInsoleData = null;
                            rightInsoleData = null;
                        }
                    }
                });

                if (dataRecordingEnabled){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new CsvHelper(getBaseContext()).exportCSV(false, newInsoleData);

                        }
                    }).start();
                }
            }
        }).start();

    }

    @Override
    public void onConnectionStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean noConnectionWarningEnabled = sp.getBoolean(getString(R.string.pref_key_connection_warning_switch), true);
                if (noConnectionWarningEnabled){
                    firstLowBatteryErrorSent = false;
                    firstNoConnectionErrorSent = false;
                    retringConnection = false;
                    broadcast_connection_started();
                }
            }
        });
    }

    @Override
    public void onVersionReceived(BluetoothDevice bluetoothDevice, String firmwareVersion, String hardwareSerial, String insoleSerial) {
        GamingInsoleID insoleID = GamingInsoleID.fromMac(bluetoothDevice.getAddress());
        if ( insoleID != null ){
            if ( bst1 == null ){
                bst1 = new BatteryStatusTracker(insoleID, bluetoothDevice.getAddress(), new Date());
            } else if ( bst2 == null ){
                bst2 = new BatteryStatusTracker(insoleID, bluetoothDevice.getAddress(), new Date());
            }
        } else {
            Log.e(TAG, "onVersionReceived: could not find insole id due to mac address: " + bluetoothDevice.getAddress());
        }

        // show version in UI
        showInsoleInfo(bluetoothDevice.getAddress(), firmwareVersion, hardwareSerial, insoleSerial);
    }


    @Override
    public void onConnectionStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean noConnectionWarningEnabled = sp.getBoolean(getString(R.string.pref_key_connection_warning_switch), true);
                if (noConnectionWarningEnabled){
                    if (!retringConnection && !firstNoConnectionErrorSent){
                        broadcast_connection_error();
                        firstNoConnectionErrorSent = true;
                    }
                }
            }
        });
    }


    /** get the reference for the UI elements **/
    private void fentchUI(){

        add_device_btn=(Button)findViewById(R.id.add_device_btn);

        load_insole_data_btn=(Button)findViewById(R.id.load_data_btn);

        start_service_btn=(Button)findViewById(R.id.start_service_btn);

        stop_service_btn=(Button)findViewById(R.id.stop_service_btn);

        start_service_activity_btn=(Button)findViewById(R.id.start_service_activity_btn);

        console_text_view=(TextView)findViewById(R.id.activity_main_console_text_view);

        console_text_view.setText("startsdas ");


        leftMtk1Column = findViewById(R.id.left_mtk_1_column);
        leftMtk2Column = findViewById(R.id.left_mtk_2_column);
        leftMtk3Column = findViewById(R.id.left_mtk_3_column);
        leftMtk4Column = findViewById(R.id.left_mtk_4_column);
        leftMtk5Column = findViewById(R.id.left_mtk_5_column);
        leftMtk6Column = findViewById(R.id.left_mtk_6_column);
        leftC1Column = findViewById(R.id.left_c_1_column);
        leftC2Column = findViewById(R.id.left_c_2_column);
        leftC3Column = findViewById(R.id.left_c_3_column);
        leftC4Column = findViewById(R.id.left_c_4_column);
        rightMtk1Column = findViewById(R.id.right_mtk_1_column);
        rightMtk2Column = findViewById(R.id.right_mtk_2_column);
        rightMtk3Column = findViewById(R.id.right_mtk_3_column);
        rightMtk4Column = findViewById(R.id.right_mtk_4_column);
        rightMtk5Column = findViewById(R.id.right_mtk_5_column);
        rightMtk6Column = findViewById(R.id.right_mtk_6_column);
        rightC1Column = findViewById(R.id.right_c_1_column);
        rightC2Column = findViewById(R.id.right_c_2_column);
        rightC3Column = findViewById(R.id.right_c_3_column);
        rightC4Column = findViewById(R.id.right_c_4_column);

        leftMtk1PreValue =  findViewById(R.id.left_mtk_1_value_text);
        leftMtk2PreValue =  findViewById(R.id.left_mtk_2_value_text);
        leftMtk3PreValue =  findViewById(R.id.left_mtk_3_value_text);
        leftMtk4PreValue =  findViewById(R.id.left_mtk_4_value_text);
        leftMtk5PreValue =  findViewById(R.id.left_mtk_5_value_text);
        leftMtk6PreValue =  findViewById(R.id.left_mtk_6_value_text);
        leftC1PreValue =  findViewById(R.id.left_c_1_value_text);
        leftC2PreValue =  findViewById(R.id.left_c_2_value_text);
        leftC3PreValue =  findViewById(R.id.left_c_3_value_text);
        leftC4PreValue =  findViewById(R.id.left_c_4_value_text);
        rightMtk1PreValue =  findViewById(R.id.right_mtk_1_value_text);
        rightMtk2PreValue =  findViewById(R.id.right_mtk_2_value_text);
        rightMtk3PreValue =  findViewById(R.id.right_mtk_3_value_text);
        rightMtk4PreValue =  findViewById(R.id.right_mtk_4_value_text);
        rightMtk5PreValue =  findViewById(R.id.right_mtk_5_value_text);
        rightMtk6PreValue =  findViewById(R.id.right_mtk_6_value_text);
        rightC1PreValue = findViewById(R.id.right_c_1_value_text);
        rightC2PreValue = findViewById(R.id.right_c_2_value_text);
        rightC3PreValue = findViewById(R.id.right_c_3_value_text);
        rightC4PreValue = findViewById(R.id.right_c_4_value_text);

        leftBatteryValue = findViewById(R.id.left_insole_battery_value_text);
        leftEnTempValue = findViewById(R.id.left_insole_en_temp_value_text);
        leftAcceValue = findViewById(R.id.left_insole_acce_value_text);

        rightBatteryValue = findViewById(R.id.right_insole_battery_value_text);
        rightEnTempValue = findViewById(R.id.right_insole_en_temp_value_text);
        rightAcceValue = findViewById(R.id.right_insole_acce_value_text);

        leftBatteryIcon =  findViewById(R.id.left_battery_icon);
        rightBatteryIcon =  findViewById(R.id.right_battery_icon);

        leftInsoleNameTv = findViewById(R.id.left_insole_name);
        rightInsoleNameTv = findViewById(R.id.right_insole_name);

        leftConnectionIcon = findViewById(R.id.left_insole_connection_status);
        rightConnectionIcon = findViewById(R.id.right_insole_connection_status);
        leftConnectionIcon.setVisibility(View.INVISIBLE);
        rightConnectionIcon.setVisibility(View.INVISIBLE);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        if (devices.size()!= 2){

        } else {
            for (BluetoothDevice insole: devices) {
                String mac = GamingInsoleID.getSidefromMac(insole.getAddress());
                if (mac != null){
                    if (mac.equals(InsoleSide.LEFT.getText())){
                        leftInsoleNameTv.setText(GamingInsoleID.fromMac(insole.getAddress()).toString());
                    } else if (mac.equals(InsoleSide.RIGHT.getText())){
                        rightInsoleNameTv.setText(GamingInsoleID.fromMac(insole.getAddress()).toString());
                    }
                }
            }

        }

        retryConnectionBtn = findViewById(R.id.retry_connection_btn);

        leftInsoleMacTv = findViewById(R.id.left_insole_mac);
        rightInsoleMacTv = findViewById(R.id.right_insole_mac);
        leftInsoleVersionTv = findViewById(R.id.left_insole_version_info);
        rightInsoleVersionTv = findViewById(R.id.right_insole_version_info);

        dataSavingSwitch = findViewById(R.id.data_saving_switch);
        dataSavingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataRecordingEnabled = isChecked;
            }
        });
    }

    /** set event listener to the UI elements **/
    private void setEventListener(){
        add_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                Intent newStartIntent= new Intent(context,AddDeviceActivity.class);
                newStartIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(newStartIntent);
            }
        });

        load_insole_data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if(start_service_btn!=null){
            start_service_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, CountService.class);
                    startService(intent);
                    Log.i(TAG, "onClick: count service starts in main activity...");

                    mBroadcastReceiver = new mainActivityBroadcastReceiver();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(SERVICE_REQUEST_ACTION);
                    registerReceiver(mBroadcastReceiver, intentFilter);
                }
            });
        }

        if(stop_service_btn!=null){
            stop_service_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, CountService.class);
                    stopService(intent);
                    Log.i(TAG, "onClick: count service stops in main activity!");

                }
            });
        }

        if(start_service_activity_btn!=null){
            start_service_activity_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ServiceTestActivity.class);
                    startActivity(intent);
                    Log.i(TAG, "onClick: service test activity starts from main activity");

                }
            });
        }

        if(retryConnectionBtn != null){
            retryConnectionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retringConnection = true;
                    updateConnectionProgressDialog(true);
                    getGamingInsolePropertyAndBuildConnection(MainActivity.this);
                    bst1 = null;
                    bst2 = null;
                }
            });
        }

    }

    /**Initialize the variables**/
    private void initVariables(){
        btAdapter= BluetoothAdapter.getDefaultAdapter();
        bondedDevices=new ArrayList<>();
        ctx=getApplicationContext();
        csvHelper=new CsvHelper(this.ctx);
        calendar=Calendar.getInstance();
        sendDataForTest=false;
        leftInsoleData = null;
        rightInsoleData = null;
        calendar=Calendar.getInstance();
        retringConnection = false;
        firstLowBatteryErrorSent = false;
        firstNoConnectionErrorSent = false;
    }

    /**
     * show the insole mac address, firmware, hardware and serial no in the user interface
     */
    private void showInsoleInfo(final String mac, final String firmware, final String hardware, final String insoleSerialNo){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GamingInsoleID insoleID = GamingInsoleID.fromMac(mac);
                        if ( insoleID == null ){
                            Log.e(TAG, "run: unable to find the insole with a provided MAC address: " + mac );
                            Toast.makeText(MainActivity.this, "Unable to find the insole with a provided MAC address: " + mac, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (insoleID.side.equals( InsoleSide.RIGHT.name ) ){
                            String macStr = "MAC: " + mac;
                            rightInsoleMacTv.setText(macStr);
                            String versionInfo = "Firmware: " + firmware + " Hardware: " + hardware + " Seriennummer: " + insoleSerialNo;
                            rightInsoleVersionTv.setText(versionInfo);
                        } else  if (insoleID.side.equals( InsoleSide.LEFT.name ) ){
                            String macStr = "MAC: " + mac;
                            leftInsoleMacTv.setText(macStr);
                            String versionInfo = "Firmware: " + firmware + " Hardware: " + hardware + " Seriennummer: " + insoleSerialNo;
                            leftInsoleVersionTv.setText(versionInfo);
                        } else {
                            Log.e(TAG, "run: unable to identify the side of the insole with a MAC address: " + mac );
                            Toast.makeText(MainActivity.this, " unable to identify the side of the insole with a MAC address: " + mac, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

    }

    private void openInsoleManagerForResult(){
        startActivityForResult(new Intent(MainActivity.this,
                InsolesManagerActivity.class), Const.INSOLE_MANAGER_REQUESTCODE);
    }

    private void updateInsoleDataUI(final InsoleData insoleData){
        final DecimalFormat df= new DecimalFormat("##.##");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (insoleData!=null){
                    if (insoleData.getFoot_side() == FootSide.LEFT ){
                        //left insole data
                        leftMtk1PreValue.setText(df.format(insoleData.getPres_1()));
                        leftMtk2PreValue.setText(df.format(insoleData.getPres_2()));
                        leftMtk3PreValue.setText(df.format(insoleData.getPres_3()));
                        leftMtk4PreValue.setText(df.format(insoleData.getPres_4()));
                        leftMtk5PreValue.setText(df.format(insoleData.getPres_5()));
                        leftMtk6PreValue.setText(df.format(insoleData.getPres_6()));
                        leftC1PreValue.setText(df.format(insoleData.getPres_7()));
                        leftC2PreValue.setText(df.format(insoleData.getPres_8()));
                        leftC3PreValue.setText(df.format(insoleData.getPres_9()));
                        leftC4PreValue.setText(df.format(insoleData.getPres_10()));

                        String batteryTextL = df.format (insoleData.getBattery()) + "%";
                        leftBatteryValue.setText(batteryTextL);
                        String enTempValueTextL = "Umg. Temp: "+ df.format(insoleData.getTemp_e())+ "°C";
                        leftEnTempValue.setText(enTempValueTextL);
                        String acceTextL = "Beschleunigungen(N/cm2) ( "+ insoleData.getAcce_x() + ", "+ insoleData.getAcce_y() + ", " + insoleData.getAcce_z() + ") ";
                        leftAcceValue.setText(acceTextL);

                        setColumnHeight(leftMtk1Column, insoleData.getPres_1());
                        setColumnHeight(leftMtk2Column, insoleData.getPres_2());
                        setColumnHeight(leftMtk3Column, insoleData.getPres_3());
                        setColumnHeight(leftMtk4Column, insoleData.getPres_4());
                        setColumnHeight(leftMtk5Column, insoleData.getPres_5());
                        setColumnHeight(leftMtk6Column, insoleData.getPres_6());
                        setColumnHeight(leftC1Column, insoleData.getPres_7());
                        setColumnHeight(leftC2Column, insoleData.getPres_8());
                        setColumnHeight(leftC3Column, insoleData.getPres_9());
                        setColumnHeight(leftC4Column, insoleData.getPres_10());

                        if (insoleData.getBattery() > YELLOW_BATTERY){
                            leftBatteryIcon.setImageResource(R.mipmap.left_battery_full);
                        } else if (insoleData.getBattery() > RED_BATTERY){
                            leftBatteryIcon.setImageResource(R.mipmap.left_battery_middle);
                        } else {
                            leftBatteryIcon.setImageResource(R.mipmap.left_battery_low);
                        }
                    }else if (insoleData.getFoot_side() == FootSide.RIGHT ){
                        //right insole data
                        rightMtk1PreValue.setText(df.format(insoleData.getPres_1()));
                        rightMtk2PreValue.setText(df.format(insoleData.getPres_2()));
                        rightMtk3PreValue.setText(df.format(insoleData.getPres_3()));
                        rightMtk4PreValue.setText(df.format(insoleData.getPres_4()));
                        rightMtk5PreValue.setText(df.format(insoleData.getPres_5()));
                        rightMtk6PreValue.setText(df.format(insoleData.getPres_6()));
                        rightC1PreValue.setText(df.format(insoleData.getPres_7()));
                        rightC2PreValue.setText(df.format(insoleData.getPres_8()));
                        rightC3PreValue.setText(df.format(insoleData.getPres_9()));
                        rightC4PreValue.setText(df.format(insoleData.getPres_10()));

                        String batteryTextR = df.format (insoleData.getBattery()) + "%";
                        rightBatteryValue.setText(batteryTextR);
                        String enTempValueTextR = "En Temp: "+ df.format(insoleData.getTemp_e()) + "°C";
                        rightEnTempValue.setText(enTempValueTextR);
                        String acceTextR = "Beschleunigungen ( "+ insoleData.getAcce_x() + ", "+ insoleData.getAcce_y() + ", " + insoleData.getAcce_z() + ") ";
                        rightAcceValue.setText(acceTextR);

                        setColumnHeight(rightMtk1Column, insoleData.getPres_1());
                        setColumnHeight(rightMtk2Column, insoleData.getPres_2());
                        setColumnHeight(rightMtk3Column, insoleData.getPres_3());
                        setColumnHeight(rightMtk4Column, insoleData.getPres_4());
                        setColumnHeight(rightMtk5Column, insoleData.getPres_5());
                        setColumnHeight(rightMtk6Column, insoleData.getPres_6());
                        setColumnHeight(rightC1Column, insoleData.getPres_7());
                        setColumnHeight(rightC2Column, insoleData.getPres_8());
                        setColumnHeight(rightC3Column, insoleData.getPres_9());
                        setColumnHeight(rightC4Column, insoleData.getPres_10());

                        if (insoleData.getBattery() > YELLOW_BATTERY){
                            rightBatteryIcon.setImageResource(R.mipmap.right_battery_full);
                        } else if (insoleData.getBattery() > RED_BATTERY){
                            rightBatteryIcon.setImageResource(R.mipmap.right_battery_middle);
                        } else {
                            rightBatteryIcon.setImageResource(R.mipmap.right_battery_low);
                        }
                    }
                }
            }
        });
    }

    private void setColumnHeight(ImageView column, float pre){
        if (column != null){
            int height = (int)((pre - PRESSURE_MIN_VALUE)/(PRESSURE_MAX_VALUE-PRESSURE_MIN_VALUE)*PRESSURE_COLUMN_MAX_HEIGHT);
            ViewGroup.LayoutParams para;
            para = column.getLayoutParams();
            para.height = height;
            column.setLayoutParams(para);
        }
    }

    private void checkInsoleBattery(final InsoleData newInsoleData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean lowBatteryWarningEnabled = sp.getBoolean(getString(R.string.pref_key_battery_warning_switch), true);
                if ( newInsoleData != null){
                    final String mac = newInsoleData.getMac();
                    final float battery = newInsoleData.getBattery();
                    final FootSide side = newInsoleData.getFoot_side();
                    float lowBatteryThreshold = Float.valueOf(sp.getString(getString(R.string.pref_key_battery_low_threshold), "3.2"));

                    if ( bst1 != null && bst1.getMac().equals( mac) && bst1.getStartBattery() == 0.0f ){
                        bst1.setStartBattery( battery );
                    } else if  ( bst2 != null && bst2.getMac().equals( mac) && bst2.getStartBattery() == 0.0f){
                        bst2.setStartBattery( battery );
                    }

                    if ( battery < lowBatteryThreshold ){
                        if (lowBatteryWarningEnabled){
                            if ( side == FootSide.LEFT && !firstLowBatteryErrorSent) {
                                //if (insoleBluServicebinder != null) {
                                //    insoleBluServicebinder.stopInsoleBluConnection();
                                //}
                                firstLowBatteryErrorSent = true;
                                broadcast_battery_error(InsoleSide.LEFT);
                                onLowBattery(MainActivity.this, side);
                            } else if ( side == FootSide.RIGHT && !firstLowBatteryErrorSent) {
                                //if (insoleBluServicebinder != null) {
                                //    insoleBluServicebinder.stopInsoleBluConnection();
                                //}
                                firstLowBatteryErrorSent = true;
                                broadcast_battery_error(InsoleSide.RIGHT);
                                onLowBattery(MainActivity.this, side);
                            }
                        }

                        if ( bst1 != null && bst1.getMac().equals( mac) ){
                            bst1.setLowBatteryAt(new Date());
                            bst1.setEmptyBattery(battery);
                            DialogUtil.pushDialog(MainActivity.this, bst1.getInsoleID().getName(),
                                    bst1.getReport(),
                                    "Ok",
                                    "",
                                    null).show();
                            bst1 = null;
                        } else if  ( bst2 !=null && bst2.getMac().equals( mac) ){
                            bst2.setLowBatteryAt(new Date());
                            bst2.setEmptyBattery(battery);
                            DialogUtil.pushDialog(MainActivity.this, bst2.getInsoleID().getName(),
                                    bst2.getReport(),
                                    "Ok",
                                    "",
                                    null).show();
                            bst2 = null;
                        }
                    }
                }
            }
        });

    }

    /**Start Bluetooth Service
     *
     * */
    private void startInsoleBluService() {
        // start the bluetooth server
        Intent intent = new Intent(MainActivity.this, InsoleBluService.class);
        startService(intent);
        Log.i(TAG, "onClick: InsoleBluService starts in main activity...");
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

    private void closeInsoleBluService() {
        // start the bluetooth server
        Intent intent = new Intent(MainActivity.this, InsoleBluService.class);
        stopService(intent);
        Log.i(TAG, "onClick: InsoleBluService stopped in main activity...");
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: in Main Activity");
            insoleBluServicebinder = (InsoleBluService.InsoleServiceBinder) service;
            insoleBluService = insoleBluServicebinder.getService(); //got an instance of the Bluetooth Service
            insoleBluService.setInsoleDataCallBack(MainActivity.this);
            //After successful binding with InsoleBluService, try to start the connection to the insoles
            Log.i(TAG, "onServiceConnected: in Main Activity. After successful binding with InsoleBluService, try to start the connection to the insoles.");
            getGamingInsolePropertyAndBuildConnection(MainActivity.this);
               
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: in Study Scan Activity");
            insoleBluService=null;
        }
    };

    private void updateConnectionProgressDialog(boolean onOrOff){
        if (onOrOff){
            connectionProgressDialog = new ProgressDialog(this);
            connectionProgressDialog.setTitle("Hinweis");
            connectionProgressDialog.setMessage("Die Verbindung zur Einlegesohle wird geladen ...");
            connectionProgressDialog.show();
        } else {
            if (connectionProgressDialog != null && connectionProgressDialog.isShowing()){
                connectionProgressDialog.cancel();
            }
        }
    }

    private void getGamingInsolePropertyAndBuildConnection(final Context context){
        if (btAdapter == null) {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        /**Check the Bluetooth again*/
        if (!btAdapter.isEnabled()){
            btAdapter.enable();
        }else{
            Log.i(TAG, "blu is enabled");
        }

        final Set<BluetoothDevice> devices = btAdapter.getBondedDevices();

        if (devices.size() < 2){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String pairedInsoleNum = String.valueOf(devices.size());
                    wrongPairedInsolesDialog = DialogUtil.pushDialog(context,  getResources().getString(R.string.less_than_two_insoles_number_dialog_title),
                            getResources().getString(R.string.less_than_two_insoles_number_dialog_mes) + pairedInsoleNum,
                            getResources().getString(R.string.less_than_two_insoles_number_dialog_pos_btn),
                            getResources().getString(R.string.less_than_two_insoles_number_dialog_neg_btn),
                            new DialogCallBack() {
                                @Override
                                public void onPositiveBtnClicked() {
                                    getGamingInsolePropertyAndBuildConnection(context);
                                }

                                @Override
                                public void onNegativeBtnClicked() {

                                }
                            }
                    );
                }
            });
        } else {
            // search for pair of insoles
            leftInsoleID = GamingInsoleID.EMPTY;
            rightInsoleID = GamingInsoleID.EMPTY;
            for (final BluetoothDevice insole: devices) {
                GamingInsoleID insoleID = GamingInsoleID.fromMac(insole.getAddress());
                if (insoleID != null){
                    Log.i(TAG, "getGamingInsolePropertyAndBuildConnection: find new paired gaming insole ->  " + insoleID.getName());
                    if (insoleID.getSide().equals("L")) {
                        leftInsoleID = insoleID;
                    } else if (insoleID.getSide().equals("R")) {
                        rightInsoleID = insoleID;
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Unknown side insole with mac address: " + insole.getAddress(), Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.e(TAG, "getGamingInsolePropertyAndBuildConnection: Unknown side insole with mac address: " + insole.getAddress());
                }
            }

            if (leftInsoleID == GamingInsoleID.EMPTY){
                // inform the user that the left insole was not found
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectionProgressDialog != null && connectionProgressDialog.isShowing()){
                            connectionProgressDialog.cancel();
                            connectionProgressDialog.setTitle("Hinweis");
                            connectionProgressDialog.setMessage("Es wurde keine linke Einlegesohle mit diesem Gerät gekoppelt.");
                            connectionProgressDialog.show();
                        }
                        Toast.makeText(MainActivity.this, "Left insole was not found!! ", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "getGamingInsolePropertyAndBuildConnection: Left insole was not found!!");
            } else if (rightInsoleID == GamingInsoleID.EMPTY){
                // inform the user that the right insole was not found
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectionProgressDialog != null && connectionProgressDialog.isShowing()){
                            connectionProgressDialog.cancel();
                            connectionProgressDialog.setTitle("Hinweis");
                            connectionProgressDialog.setMessage("Es wurde keine rechte Einlegesohle mit diesem Gerät gekoppelt.");
                            connectionProgressDialog.show();
                        }
                        Toast.makeText(MainActivity.this, "Right insole was not found!! ", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "getGamingInsolePropertyAndBuildConnection: Right insole was not found!! ");
            } else {
                // insoles were found and go to next step
                if (insoleBluServicebinder != null){
                    insoleBluServicebinder.stopInsoleBluConnection();
                    Log.i(TAG, "getGamingInsolePropertyAndBuildConnection: close the connection firstly...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            insoleBluServicebinder.startDefinedInsoleBluConnection(leftInsoleID, rightInsoleID);
                            leftConnectionIcon.setVisibility(View.VISIBLE);
                            rightConnectionIcon.setVisibility(View.VISIBLE);
                            Log.i(TAG, "onServiceConnected: in Main Activity. start the connection to the insoles...");
                        }
                    }, 2*1000);
                }
            }

        }
    }


    public void onLowBattery(final Context  context, final FootSide insoleSide){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtil.pushDialog(context,  getResources().getString(R.string.wrong_paired_insoles_number_dialog_title),
                        "Low battery level of insole "+ (insoleSide == FootSide.LEFT?"L":"R")+ "\nPlease change the battery..."  ,
                        getResources().getString(R.string.wrong_paired_insoles_number_dialog_pos_btn),
                        getResources().getString(R.string.wrong_paired_insoles_number_dialog_neg_btn),
                        new DialogCallBack() {
                            @Override
                            public void onPositiveBtnClicked() {
                                //skip
                                firstLowBatteryErrorSent = false;

                            }

                            @Override
                            public void onNegativeBtnClicked() {
                                //ok
                                //if (insoleBluServicebinder != null) {
                                //    insoleBluServicebinder.stopInsoleBluConnection();
                                //}
                            }
                        });
            }
        });


    }


    public class mainActivityBroadcastReceiver extends BroadcastReceiver {
        //接收到广播后自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            String data=  intent.getStringExtra("name");

            Log.i(TAG, "count service is started for: with data"+data);
        }
    }

    /***********************************************************************************/
    private final String BROADCAST_ACTION="com.kenp.minga.insolesmanager.ACTION";
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

    private final String TIME_STAMP ="time_stamp";
    private final String CONNECTION="connection";
    private final String STARTED="started";
    private final String STOPPED="stopped";
    private final String LOW_BATTERY="low_battery";


    private final String INSOLE_VERSION ="insole_version";
    private final String INSOLE_V2 ="2";
    private final String INSOLE_FRAME ="insole_frame";
    private long insole_frame_num=0;

    private void broadcast_both_insoles_data(final InsoleData leftInsoleData, final InsoleData rightInsoleData){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if ( insole_frame_num > FRAME_NUMBER_MAX){
                        insole_frame_num = 0;
                    } else {
                        insole_frame_num++;
                    }

                    calendar=Calendar.getInstance();
                    Intent intent = new Intent(BROADCAST_ACTION);
                    //insole frame
                    intent.putExtra(INSOLE_VERSION, INSOLE_V2);
                    intent.putExtra(INSOLE_FRAME, insole_frame_num);
                    //left insole data
                    float[] leftPressureArray={
                            leftInsoleData.getPres_1(),
                            leftInsoleData.getPres_2(),
                            leftInsoleData.getPres_3(),
                            leftInsoleData.getPres_4(),
                            leftInsoleData.getPres_5(),
                            leftInsoleData.getPres_6(),
                            leftInsoleData.getPres_7(),
                            leftInsoleData.getPres_8(),
                            leftInsoleData.getPres_9(),
                            leftInsoleData.getPres_10()};
                    intent.putExtra(LEFT_PRES, leftPressureArray);
                    float[] leftAccelerationArray={
                            leftInsoleData.getAcce_x(),
                            leftInsoleData.getAcce_y(),
                            leftInsoleData.getAcce_z()};
                    intent.putExtra(LEFT_ACCE, leftAccelerationArray);
                    intent.putExtra(LEFT_TEMP_E, leftInsoleData.getTemp_e());
                    intent.putExtra(LEFT_BATTERY, leftInsoleData.getBattery());
                    //right insole data
                    float[] rightPressureArray={
                            rightInsoleData.getPres_1(),
                            rightInsoleData.getPres_2(),
                            rightInsoleData.getPres_3(),
                            rightInsoleData.getPres_4(),
                            rightInsoleData.getPres_5(),
                            rightInsoleData.getPres_6(),
                            rightInsoleData.getPres_7(),
                            rightInsoleData.getPres_8(),
                            rightInsoleData.getPres_9(),
                            rightInsoleData.getPres_10()};
                    intent.putExtra(RIGHT_PRES, rightPressureArray);
                    float[] rightAccelerationArray={
                            rightInsoleData.getAcce_x(),
                            rightInsoleData.getAcce_y(),
                            rightInsoleData.getAcce_z()};
                    intent.putExtra(RIGHT_ACCE, rightAccelerationArray);
                    intent.putExtra(RIGHT_TEMP_E, rightInsoleData.getTemp_e());
                    intent.putExtra(RIGHT_BATTERY, rightInsoleData.getBattery());
                    sendBroadcast(intent);
                    //Log.d(TAG, "broadcast_both_insoles_data: frame " + insole_frame_num);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void broadcast_connection_started(){
        try {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(CONNECTION, STARTED);
            sendBroadcast(intent);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void broadcast_connection_error(){
        try {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(CONNECTION, STOPPED);
            sendBroadcast(intent);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void broadcast_battery_error(InsoleSide insoleSide){
        try {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(LOW_BATTERY, insoleSide.getText());
            sendBroadcast(intent);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //broadcast the insole data to the InsoleTestActivity
    private final String BROADCAST_INSOLE_TEST_ACTION="com.kenp.minga.insoletest.ACTION";
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");//设置日期格式

    @Deprecated // use the "broadcast_both_insoles_data" instead
    private boolean broadcast_both_insoles_data_for_test(InsoleData leftInsoleData, InsoleData rightInsoleData){
        try {
            insole_frame_num++;
            calendar=Calendar.getInstance();
            //Log.i(TAG, "broadcast_insole_data(): of frame "+insole_frame_num+" both insoles --> "+calendar.getTime().toString());
            Log.i(TAG, "broadcast_insole_data(): of frame "+insole_frame_num);
            Intent intent = new Intent(BROADCAST_INSOLE_TEST_ACTION);
            //insole frame
            intent.putExtra(INSOLE_FRAME, insole_frame_num);
            //left insole data
            float[] leftPressureArray={
                    leftInsoleData.getPres_1(),
                    leftInsoleData.getPres_2(),
                    leftInsoleData.getPres_3(),
                    leftInsoleData.getPres_4(),
                    leftInsoleData.getPres_5(),
                    leftInsoleData.getPres_6(),
                    leftInsoleData.getPres_7(),
                    leftInsoleData.getPres_8(),
                    leftInsoleData.getPres_9(),
                    leftInsoleData.getPres_10()};
            intent.putExtra(LEFT_PRES, leftPressureArray);
            float[] leftAccelerationArray={
                    leftInsoleData.getAcce_x(),
                    leftInsoleData.getAcce_y(),
                    leftInsoleData.getAcce_z()};
            intent.putExtra(LEFT_ACCE, leftAccelerationArray);
            intent.putExtra(LEFT_TEMP_E, leftInsoleData.getTemp_e());
            intent.putExtra(LEFT_BATTERY, leftInsoleData.getBattery());
            //right insole data
            float[] rightPressureArray={
                    rightInsoleData.getPres_1(),
                    rightInsoleData.getPres_2(),
                    rightInsoleData.getPres_3(),
                    rightInsoleData.getPres_4(),
                    rightInsoleData.getPres_5(),
                    rightInsoleData.getPres_6(),
                    rightInsoleData.getPres_7(),
                    rightInsoleData.getPres_8(),
                    rightInsoleData.getPres_9(),
                    rightInsoleData.getPres_10()};
            intent.putExtra(RIGHT_PRES, rightPressureArray);
            float[] rightAccelerationArray={
                    rightInsoleData.getAcce_x(),
                    rightInsoleData.getAcce_y(),
                    rightInsoleData.getAcce_z()};
            intent.putExtra(RIGHT_ACCE, rightAccelerationArray);
            intent.putExtra(RIGHT_TEMP_E, rightInsoleData.getTemp_e());
            intent.putExtra(RIGHT_BATTERY, rightInsoleData.getBattery());
            intent.putExtra(TIME_STAMP, df.format(System.currentTimeMillis()));
            sendBroadcast(intent);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    
    @Deprecated
    private void startNewActivity(){
        Context context = MainActivity.this;
        Intent newStartIntent= new Intent(context,InsolesManagerActivity.class);
        newStartIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(newStartIntent);
    }
}
