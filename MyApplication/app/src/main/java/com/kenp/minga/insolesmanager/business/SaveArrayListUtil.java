package com.kenp.minga.insolesmanager.business;

/**
 * Created by minga on 10/23/2017.
 */
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.kenp.minga.insolesmanager.activities.MainActivity;
import com.kenp.minga.insolesmanager.model.InsoleItem;
import com.kenp.minga.insolesmanager.model.InsoleSide;

import java.util.ArrayList;

public class SaveArrayListUtil {
    private final String TAG="SaveArrayListUtil";
    /**
     * 将arrayList的内容保存到sp里
     */
    public void saveArrayList(Context context, ArrayList searchList, String content) {
        //searchList里“无数据”
        if (searchList.size() == 0) {
            //直接存
            searchList.add(content + "");
        } else {
            //searchList里“有数据”
            //但不包含这条数据，直接在0的位置加上这条数据
            if (!searchList.contains(content)) {
                searchList.add(0, content + "");
            } else {
                //包含了这条数据，就删除掉，并放在0位置或者原位置（自由选择）。
                for (int i = 0; i < searchList.size(); i++) {
                    if (searchList.get(i).equals(content)) {
                        searchList.remove(i);
                        searchList.add(0, content + "");//0或者i均可。
                    }
                }
            }
        }
        //定义SP.Editor和文件名称
        SharedPreferences.Editor editor = context.getSharedPreferences(
                "SearchDataList", context.MODE_PRIVATE).edit();
        //将结果放入文件，关键是把集合大小放入，为了后面的取出判断大小。
        editor.putInt("searchNums", searchList.size());
        for (int i = 0; i < searchList.size(); i++) {
            //用条目+i,代表键，解决键的问题，也方便等一下取出，值也对应。
            editor.putString("item_" + i, searchList.get(i) + "");
        }
        editor.commit();
    }

    /**
     * 读取sp里的数组
     */
    public ArrayList<String> getSearchArrayList(Context context) {
        //先定位到文件
        SharedPreferences preferDataList = context.getSharedPreferences(
                "SearchDataList", context.MODE_PRIVATE);
        //定义一个集合等下返回结果
        ArrayList<String> list = new ArrayList<>();
        //刚才存的大小此时派上用场了
        int searchNums = preferDataList.getInt("searchNums", 0);
        //根据键获取到值。
        for (int i = 0; i < searchNums; i++) {
            String searchItem = preferDataList.getString("item_" + i, null);
            //放入新集合并返回
            list.add(searchItem);
        }
        return list;
    }


    public void saveInsoleInfos(Context context, ArrayList<InsoleItem> searchList, InsoleItem content) {
        int insole_index=getInsoleInfoIndex(searchList,content);
        if (insole_index == -1){
            Log.i(TAG, " Insole info is already saved : "+content.getName()+" : "+content.getMacAddress()+" : "+content.getInsoleSide().getText());
        }else{
            //定义SP.Editor和文件名称
            SharedPreferences.Editor editor = context.getSharedPreferences(
                    "InsolesList", context.MODE_PRIVATE).edit();
            //将结果放入文件，关键是把集合大小放入，为了后面的取出判断大小。

            editor.putString("insole_name_" + insole_index, content.getName() + "");
            editor.putString("insole_mac_" + insole_index, content.getMacAddress() + "");
            editor.putString("insole_side_" + insole_index, content.getInsoleSide().getText() + "");
            if (insole_index != searchList.size()){
                editor.putInt("searchInsoles",searchList.size() );
                Toast.makeText(context, "REPLACE the row : ."+insole_index +" with the insole : "
                                +content.getName()+" : "+content.getMacAddress()+" : "+content.getInsoleSide().getText(),
                        Toast.LENGTH_SHORT).show();
            } else {
                editor.putInt("searchInsoles",searchList.size()+1 );
                Toast.makeText(context, "ADD info to the row : ."+insole_index +" with the insole : "
                                +content.getName()+" : "+content.getMacAddress()+" : "+content.getInsoleSide().getText(),
                        Toast.LENGTH_SHORT).show();
            }
            editor.commit();
        }
    }

    private int getInsoleInfoIndex(ArrayList<InsoleItem> searchList, InsoleItem content){
        int index=-1;
        if (searchList.size()==0){
            index=0;
        }else{
            if(searchList.contains(content)){
                //the content info has already been saved in the data
                index=-1;
            }else{
                ArrayList<Boolean> flags=new ArrayList<>();

                for (InsoleItem insole: searchList) {
                    if(insole.getMacAddress().equals(content.getMacAddress())&&
                            (!insole.getInsoleSide().equals(content.getInsoleSide()))){
                        //only the insole side info is different
                        flags.add(true);
                    }else{
                        //all the elements are different
                        flags.add(false);
                    }
                }

                if(flags.contains(true)){
                    index=flags.indexOf(true);
                }else{
                    index=searchList.size();
                }
            }
        }
       return index;
    }

    /**
     * 读取sp里的数组
     */
    public ArrayList<InsoleItem> getInsolesInfo(Context context) {
        //先定位到文件
        SharedPreferences preferDataList = context.getSharedPreferences(
                "InsolesList", context.MODE_PRIVATE);
        //定义一个集合等下返回结果
        ArrayList<InsoleItem> list = new ArrayList<>();
        //刚才存的大小此时派上用场了
        int searchNums = preferDataList.getInt("searchInsoles", 0);
        //根据键获取到值。
        for (int i = 0; i < searchNums; i++) {
            String searchInsoleName = preferDataList.getString("insole_name_" + i, null);
            String searchInsoleMac = preferDataList.getString("insole_mac_" + i, null);
            InsoleSide searchInsoleSide = InsoleSide.fromString(preferDataList.getString("insole_side_" + i, null));
            InsoleItem insoleItem =new InsoleItem(searchInsoleName,searchInsoleMac,searchInsoleSide);
            //放入新集合并返回
            list.add(insoleItem);
        }
        return list;
    }

    public void clearInsolesInfo(Context context){
        //先定位到文件
        SharedPreferences preferDataList = context.getSharedPreferences(
                "InsolesList", context.MODE_PRIVATE);
        preferDataList.edit().clear().commit();
        Toast.makeText(context, "All insoles info are cleared.", Toast.LENGTH_SHORT).show();

    }

    public InsoleSide getInsoleSideInfo(Context context, BluetoothDevice device){
        ArrayList<InsoleItem> savedInsoleItemsInfo= getInsolesInfo(context);
        InsoleSide insoleSide=InsoleSide.NULL;
        for (InsoleItem insoleItem:savedInsoleItemsInfo ) {
            if(insoleItem.getName().equals(device.getName())&&insoleItem.getMacAddress().equals(device.getAddress())){
                insoleSide=insoleItem.getInsoleSide();
                break;
            }
        }
        return insoleSide;

    }
}
