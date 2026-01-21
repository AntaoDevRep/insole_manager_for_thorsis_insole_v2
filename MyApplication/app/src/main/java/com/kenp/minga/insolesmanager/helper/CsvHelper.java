package com.kenp.minga.insolesmanager.helper;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.kenp.minga.insolesmanager.model.InsoleData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by minga on 7/20/2017.
 */

public class CsvHelper {
    private static final String TAG = CsvHelper.class.getSimpleName();
    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String mComma = ",";
    private static final String changLine = "\r\n";
    private static final String INSOLE_DATA_PATH = Environment.getExternalStorageDirectory().getPath() + "/insole_manager/";
    private static final String INSOLE_DATA_FILE_NAME = "insole_data.txt";

    private Context context;
    private File savedFile;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_TIME_FORMAT, Locale.getDefault());


    public CsvHelper(Context context) {
        this.context = context;
        savedFile=null;

    }

    public boolean exportCSV(boolean isStartLine, InsoleData insoleData){
        /**Build the string*/
        StringBuilder stringBuilder=new StringBuilder();
        if(isStartLine){
            stringBuilder.append("Foot_side");stringBuilder.append(mComma);
            stringBuilder.append("Foot_side");stringBuilder.append(mComma);
            stringBuilder.append("Name");stringBuilder.append(mComma);
            stringBuilder.append("Mac");stringBuilder.append(mComma);
            stringBuilder.append("Battery");stringBuilder.append(mComma);
            stringBuilder.append("Pres_1");stringBuilder.append(mComma);
            stringBuilder.append("Pres_1");stringBuilder.append(mComma);
            stringBuilder.append("Pres_2");stringBuilder.append(mComma);
            stringBuilder.append("Pres_3");stringBuilder.append(mComma);
            stringBuilder.append("Pres_4");stringBuilder.append(mComma);
            stringBuilder.append("Pres_5");stringBuilder.append(mComma);
            stringBuilder.append("Pres_6");stringBuilder.append(mComma);
            stringBuilder.append("Pres_7");stringBuilder.append(mComma);
            stringBuilder.append("Pres_8");stringBuilder.append(mComma);
            stringBuilder.append("Pres_9");stringBuilder.append(mComma);
            stringBuilder.append("Pres_10");stringBuilder.append(mComma);
            stringBuilder.append("Acce_x");stringBuilder.append(mComma);
            stringBuilder.append("Acce_y");stringBuilder.append(mComma);
            stringBuilder.append("Acce_z");stringBuilder.append(mComma);
            stringBuilder.append("Temp_e");stringBuilder.append(mComma);
            stringBuilder.append("\r\n");
        }

        stringBuilder.append(simpleDateFormat.format(insoleData.getTimeSample()));stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getFoot_side());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getName());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getMac());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getBattery());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_1());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_1());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_2());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_3());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_4());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_5());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_6());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_7());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_8());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_9());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getPres_10());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getAcce_x());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getAcce_y());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getAcce_z());stringBuilder.append(mComma);
        stringBuilder.append(insoleData.getTemp_e());stringBuilder.append(mComma);
        stringBuilder.append("\r\n");



        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "exportCSV: no external saving allowed ");
            return false;
        }

        try {
            //create folder
            File dir = new File(INSOLE_DATA_PATH);
            File file = new File(INSOLE_DATA_PATH + INSOLE_DATA_FILE_NAME);

            if (!dir.exists()) {
                dir.mkdirs();
                Log.i(TAG, "dumpCommandDataToSDCard: create new folder " + dir );
            }

            if (!file.exists()){
                file = new File(INSOLE_DATA_PATH + INSOLE_DATA_FILE_NAME);
                FileOutputStream fileInput = new FileOutputStream(file);
                PrintStream printstream = new PrintStream(fileInput);
                printstream.print(stringBuilder.toString() +"\n");
                fileInput.close();
                Log.i(TAG, "dumpInsoleDataToSDCard: create the  file ");
            }else{
                OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file,true));
                BufferedWriter buffered_writer = new BufferedWriter(file_writer);
                buffered_writer.write(stringBuilder.toString() + "\n");
                buffered_writer.close();
                //Log.i(TAG, "dumpInsoleDataToSDCard: update the  file ");
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "an io exception occurred. Cannot write log." + e);
            return false;
        }
    }

    public boolean exportText( InsoleData insoleData){



        try {
            savedFile=new File(this.context.getFilesDir(),"insoledata.txt");
            FileOutputStream fileOut=new FileOutputStream(savedFile,true); //write a new line in the end...
            OutputStreamWriter   outputWriter=new OutputStreamWriter(fileOut)  ;
            outputWriter.write(insoleData.convertToStringData());
            outputWriter.write("\n");
            //outputWriter.flush();
            outputWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String importText(){
        try
        {
            FileInputStream instream =new FileInputStream(savedFile);
            if (instream != null)
            {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line,line1 = "";
                try
                {
                    while ((line = buffreader.readLine()) != null){
                        line1+=line;
                        line1+=changLine;
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                return line1;
            }
            return "no FileInputSteam";
        }
        catch (Exception e)
        {
            String error="";
            error=e.getMessage();
            return error;
        }
    }
}
