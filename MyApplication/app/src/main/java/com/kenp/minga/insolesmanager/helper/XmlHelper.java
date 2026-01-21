package com.kenp.minga.insolesmanager.helper;

import android.os.Environment;
import android.util.Log;

import com.kenp.minga.insolesmanager.model.InsoleItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by minga on 10/23/2017.
 */

public class XmlHelper {
    String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/insoles.txt";

    //保存数据
    public void save(InsoleItem insoleItem){
        ObjectOutputStream fos=null;
        try {

            //如果文件不存在就创建文件
            File file=new File(path);
            //file.createNewFile();
            //获取输出流
            //这里如果文件不存在会创建文件，这是写文件和读文件不同的地方
            fos=new ObjectOutputStream(new FileOutputStream(file));
            //获取输入框内的文件进行写入
            //这里不能再用普通的write的方法了
            //要使用writeObject
            fos.writeObject(insoleItem);;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (fos!=null) {
                    fos.close();
                }
            } catch (IOException e) {
            }

        }

    }

    //读取数据
    public void read(){
        ObjectInputStream ois=null;
        try {
            Log.e("TAG", new File(path).getAbsolutePath()+"<---");
            //获取输入流
            ois=new ObjectInputStream(new FileInputStream(new File(path)));
            //获取文件中的数据
            Object insoleItem=ois.readObject();
            //把数据显示在TextView中

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
