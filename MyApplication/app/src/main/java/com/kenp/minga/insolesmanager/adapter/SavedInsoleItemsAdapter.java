package com.kenp.minga.insolesmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.business.SaveArrayListUtil;
import com.kenp.minga.insolesmanager.model.GamingInsoleID;

import java.util.ArrayList;

/**
 * Created by minga on 1/16/2018.
 */

public class SavedInsoleItemsAdapter extends ArrayAdapter<GamingInsoleID> {
    private Context cx;
    private GamingInsoleID[] gamingInsoleIDS;

    public SavedInsoleItemsAdapter(@NonNull Context context, int resource, @NonNull GamingInsoleID[] objects) {
        super(context, resource, objects);
        this.cx = context;
        this.gamingInsoleIDS = objects;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(cx);
        View v = layoutInflater.inflate(R.layout.saved_insole_item_row, parent, false);
        String mac = gamingInsoleIDS[position].getMac();
        String name = gamingInsoleIDS[position].getName();
        String side = gamingInsoleIDS[position].getSide();

        TextView macTextView = v.findViewById(R.id.insole_mac_address_text_view);
        macTextView.setText(mac);
        TextView sideTextView = v.findViewById(R.id.insole_side_text_view);
        sideTextView.setText(side);
        TextView nameTextView = v.findViewById(R.id.saved_insole_name_text);
        nameTextView.setText(name);
        TextView numberTextView = v.findViewById(R.id.saved_insole_no_text_view);
        numberTextView.setText(String.valueOf(position));

        return v;
    }

    @Override
    public int getCount() {
        return gamingInsoleIDS.length;
    }
}
