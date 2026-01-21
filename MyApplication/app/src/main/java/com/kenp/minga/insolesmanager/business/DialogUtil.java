package com.kenp.minga.insolesmanager.business;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.kenp.minga.insolesmanager.R;
import com.kenp.minga.insolesmanager.callbacks.DialogCallBack;

/**
 * Created by minga on 1/31/2018.
 */

public class DialogUtil {


    public static AlertDialog pushDialog(final Context context, String title, String message, String posBtnStr, String negBtnStr, final DialogCallBack dialogCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        if ( posBtnStr != null && !posBtnStr.equals("") ){
            builder.setPositiveButton(posBtnStr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (dialogCallBack != null){
                        dialogCallBack.onPositiveBtnClicked();
                    }
                }
            });
        }

        if ( negBtnStr != null && !negBtnStr.equals("") ){
            builder.setNegativeButton(negBtnStr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (dialogCallBack != null){
                        dialogCallBack.onNegativeBtnClicked();
                    }
                }
            });
        }

        AlertDialog alertDialog = builder.show();

        return alertDialog;
    }
}
