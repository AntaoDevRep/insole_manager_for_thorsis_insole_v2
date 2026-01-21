package com.kenp.minga.insolesmanager.dialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.R;

/**
 * Created by minga on 10/23/2017.
 */

public class MultiOptionsDialog {

    // 信息列表提示框
    public AlertDialog alertDialog1;
    public void showListAlertDialog(View view){
        final String[] items = {"Struts2","Spring","Hibernate","Mybatis","Spring MVC"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());
        alertBuilder.setTitle("java EE 常用框架");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int index) {
//                Toast.makeText(MainActivity.this, items[index], Toast.LENGTH_SHORT).show();
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
    }

    public static void dialogOptions(final View view) {
        final String items[] = {"Set as Left", "Set as Right", "Unpair"};

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),3);
        builder.setTitle("Actions");
        // builder.setMessage("是否确认退出?"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(view.getContext(), items[which],
                        Toast.LENGTH_SHORT).show();

            }
        });
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Toast.makeText(view.getContext(), "Yes", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
        builder.create().show();
    }



}
