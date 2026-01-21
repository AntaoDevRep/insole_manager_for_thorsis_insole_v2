package com.kenp.minga.insolesmanager.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.BR;
import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.business.SaveArrayListUtil;
import com.kenp.minga.insolesmanager.dialog.MultiOptionsDialog;
import com.kenp.minga.insolesmanager.model.GamingInsoleID;
import com.kenp.minga.insolesmanager.model.InsoleItem;
import com.kenp.minga.insolesmanager.model.InsoleSide;

/**
 * Created by minga on 10/23/2017.
 */

public class InsoleViewModel extends BaseObservable {

    private InsoleItem insole;

    public InsoleViewModel(InsoleItem insole) {
        this.insole = insole;
    }

    @Bindable
    public String getName() {
        return GamingInsoleID.fromMac(insole.getMacAddress()).getName();
        //return insole.getName();
    }

    @Bindable
    public String getMac() {
        return insole.getMacAddress();
    }

    @Bindable
    public String getInsoleSide() {
        return GamingInsoleID.fromMac(insole.getMacAddress()).getSide();
        //return insole.getInsoleSide().getText();
    }

    public View.OnClickListener onPairedInsoleClicked(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),insole.getName()+"  "+insole.getMacAddress() +" is clicked!", Toast.LENGTH_SHORT).show();
                //dialogOptions(v);
            }
        };
    }

    private void dialogOptions(final View view) {
        final String items[] = {"Set as Left", "Set as Right", "Unpair"};

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Actions");
        builder.setIcon(R.mipmap.insoles);
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Toast.makeText(view.getContext(), items[which],
//                        Toast.LENGTH_SHORT).show();
                switch (which){
                    case 0:
                        insole.setInsoleSide(InsoleSide.LEFT);
                        SaveArrayListUtil saveArrayListUtil_1=new SaveArrayListUtil();
                        saveArrayListUtil_1.saveInsoleInfos(view.getContext(),saveArrayListUtil_1.getInsolesInfo(view.getContext()),insole);
                        //update the data in the InsoleViewModel
                        notifyPropertyChanged(BR.insoleSide);
                        break;
                    case 1:
                        insole.setInsoleSide(InsoleSide.RIGHT);
                        SaveArrayListUtil saveArrayListUtil2=new SaveArrayListUtil();
                        saveArrayListUtil2.saveInsoleInfos(view.getContext(),saveArrayListUtil2.getInsolesInfo(view.getContext()),insole);
                        //update the data in the InsoleViewModel
                        notifyPropertyChanged(BR.insoleSide); //TODO CHECK WHY IT WORKS????
                        break;
                }
            }
        });
        builder.create().show();
    }
}