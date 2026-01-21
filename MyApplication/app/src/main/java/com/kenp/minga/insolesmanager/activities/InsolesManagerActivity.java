package com.kenp.minga.insolesmanager.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.adapter.PairedInsolesReViAdapter;
import com.kenp.minga.insolesmanager.adapter.SavedInsoleItemsAdapter;
import com.kenp.minga.insolesmanager.business.SaveArrayListUtil;
import com.kenp.minga.insolesmanager.model.Const;
import com.kenp.minga.insolesmanager.model.GamingInsoleID;
import com.kenp.minga.insolesmanager.model.InsoleItem;

import java.util.ArrayList;
import java.util.Set;

public class InsolesManagerActivity extends AppCompatActivity {

    private final String TAG="IMA";
    //UI elements
    private RecyclerView pairedInsolesRecyclerView;
    private ListView savedInsolesInfoListView;

    private PairedInsolesReViAdapter pairedInsolesReViAdapter;
    private SavedInsoleItemsAdapter savedInsoleItemsListViewAdapter;
    private BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insoles_manager);
        initUI();

    }

    /** Connect the menu.xml to the activity **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insoles_manager_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** default method to listen to the selection event of the menu action bar **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear_saved_info_item:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: in InsolesManagerActivity");
        //set the result before leaving the InsolesManagerActivity
        Intent resultIntent=new Intent();
        //resultIntent.putExtra("bian", "");
        setResult(Const.RESULT_OK, resultIntent);
        super.onBackPressed();
    }


    private void initUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pairedInsolesRecyclerView=(RecyclerView)findViewById(R.id.paired_insoles_recycler_view);
                pairedInsolesRecyclerView.setLayoutManager(new LinearLayoutManager(InsolesManagerActivity.this));
                pairedInsolesReViAdapter = new PairedInsolesReViAdapter(InsolesManagerActivity.this);
                pairedInsolesReViAdapter.setPairedInsoles(getPairedInsoles());
                pairedInsolesRecyclerView.setAdapter(pairedInsolesReViAdapter);
                pairedInsolesRecyclerView.setItemAnimator(new DefaultItemAnimator());

                savedInsolesInfoListView =findViewById(R.id.saved_insole_info_list);
                savedInsoleItemsListViewAdapter = new SavedInsoleItemsAdapter(InsolesManagerActivity.this, R.id.saved_insole_info_list, GamingInsoleID.values());
                savedInsolesInfoListView.setAdapter(savedInsoleItemsListViewAdapter);
                //savedInsoleItemsListViewAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * get the paired insoles info from the BluetoothAdapter
     * @return a array list of paired BluetoothDevices
     * */
    private ArrayList<BluetoothDevice> getPairedInsoles(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        ArrayList<BluetoothDevice> pairedInsoles=new ArrayList<>();
        // If the adapter is null, then Bluetooth is not supported
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        } else {
            // find all the already paired devices
            Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
            for (BluetoothDevice d: devices) {
                pairedInsoles.add(d);
            }
        }
        return pairedInsoles;
    }



}
