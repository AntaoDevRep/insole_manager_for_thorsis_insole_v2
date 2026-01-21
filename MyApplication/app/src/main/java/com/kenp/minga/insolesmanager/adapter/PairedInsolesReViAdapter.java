package com.kenp.minga.insolesmanager.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kenp.minga.insolesmanager.BR;
import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.business.SaveArrayListUtil;
import com.kenp.minga.insolesmanager.databinding.InsoleRowItemBinding;
import com.kenp.minga.insolesmanager.model.InsoleItem;
import com.kenp.minga.insolesmanager.model.InsoleSide;
import com.kenp.minga.insolesmanager.viewmodel.InsoleViewModel;

import java.util.ArrayList;

/**
 * Created by minga on 10/23/2017.
 */

public class PairedInsolesReViAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int REGULAR_INSOLE_ROW = 0;
    public static final int HEADER_ROW = 1;
    private static final String TAG ="Adapter" ;

    private ArrayList<BluetoothDevice> pairedInsoles;

    private Context context;

    public PairedInsolesReViAdapter(@NonNull Context context) {
        this.context=context;
    }

    public boolean setPairedInsoles(ArrayList<BluetoothDevice> devices){
        if(devices!=null){
            this.pairedInsoles=devices;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {

            case REGULAR_INSOLE_ROW:
                InsoleRowItemBinding postBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.insole_row_item,parent,false);
                return new InsoleItemBindingHolder(postBinding);
            case HEADER_ROW:
                InsoleHeaderViewHolder viewHolder= new InsoleHeaderViewHolder(LayoutInflater.from(
                        context).inflate(R.layout.insole_row_header, parent,
                        false));
                return viewHolder;
            default:
                //TODO LOG OUT ERROR
                return null;
        }

    }



    @Override
    public int getItemViewType(int position) {
        return position > 0 ? REGULAR_INSOLE_ROW : HEADER_ROW;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int item_type = getItemViewType(position);
        if(item_type == HEADER_ROW) {

        } else if(item_type == REGULAR_INSOLE_ROW) {
            updatePairedInsoleItems((InsoleItemBindingHolder) holder, position);
        } else {
        }
    }

    @Override
    public int getItemCount() {
        return pairedInsoles.size()+1;
    }

    private static class InsoleItemBindingHolder extends RecyclerView.ViewHolder {

        private InsoleRowItemBinding binding;

        InsoleItemBindingHolder(InsoleRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        InsoleRowItemBinding getBinding() {
            return binding;
        }
    }

    class InsoleHeaderViewHolder extends RecyclerView.ViewHolder
    {
        public InsoleHeaderViewHolder(View view)
        {
            super(view);
        }
    }








    private void updatePairedInsoleHeader(InsoleItemBindingHolder holder, int position){

    }

    private void updatePairedInsoleItems(InsoleItemBindingHolder holder, int position){
        Log.i(TAG, "paired insole num is: "+pairedInsoles.size());
        String name=pairedInsoles.get(position-1).getName();
        String mac=pairedInsoles.get(position-1).getAddress();
        SaveArrayListUtil saveArrayListUtil=new SaveArrayListUtil();
        InsoleSide side=saveArrayListUtil.getInsoleSideInfo(context, pairedInsoles.get(position-1));//TODO define the insole side
        InsoleItem insoleItem=new InsoleItem(name,mac,side);
        InsoleViewModel insoleViewModel=new InsoleViewModel(insoleItem);
        ViewDataBinding dataBinding= holder.getBinding();
        dataBinding.setVariable(BR.viewModel,insoleViewModel);
    }



}
