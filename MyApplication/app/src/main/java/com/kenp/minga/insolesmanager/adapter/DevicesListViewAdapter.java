package com.kenp.minga.insolesmanager.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kenp.minga.insolesmanager.R;

import java.util.ArrayList;

/**
 * Created by minga on 6/22/2017.
 */

public class DevicesListViewAdapter extends ArrayAdapter<BluetoothDevice> {

    private ArrayList<BluetoothDevice> avialable_insoles=null;


    public DevicesListViewAdapter(Context context, int resource, ArrayList<BluetoothDevice> objects) {


        super(context, resource, objects);
        this.avialable_insoles = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.device_list_row_layout,parent,false);
        }
        v.setBackgroundColor(Color.WHITE);

        TextView insole_info_text_view=(TextView)v.findViewById(R.id.available_insole_name);
        insole_info_text_view.setText(avialable_insoles.get(position).getName());

        TextView insole_statu_text_view=(TextView)v.findViewById(R.id.available_insole_status);

        switch (avialable_insoles.get(position).getBondState()){
            case BluetoothDevice.BOND_BONDED:
                insole_statu_text_view.setBackgroundColor(Color.GREEN);
                break;
            case BluetoothDevice.BOND_BONDING:
                insole_statu_text_view.setBackgroundColor(Color.YELLOW);
                break;
            case BluetoothDevice.BOND_NONE:
                insole_statu_text_view.setBackgroundColor(Color.RED);
                break;
        }

        return v;
    }

}
