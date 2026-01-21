package com.kenp.minga.insolesmanager.model;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by minga on 7/16/2017.
 */

public class InsoleData {
    private String serialNumber;
    private Date timeSample;
    private String name;
    private String Mac;
    private float pres_1;
    private float pres_2;
    private float pres_3;
    private float pres_4;
    private float pres_5;
    private float pres_6;
    private float pres_7;
    private float pres_8;
    private float pres_9;
    private float pres_10;

    private float acce_x;
    private float acce_y;
    private float acce_z;

    private float temp_e;

    private float battery;
    private FootSide foot_side; //1: left, 2:right

    public InsoleData() {
        this.serialNumber = "";
        this.pres_1=0.0f;
        this.pres_2=0.0f;
        this.pres_3=0.0f;
        this.pres_4=0.0f;
        this.pres_5=0.0f;
        this.pres_6=0.0f;
        this.pres_7=0.0f;
        this.pres_8=0.0f;
        this.pres_9=0.0f;
        this.acce_x=0;
        this.acce_y=0;
        this.acce_z=0;
        this.temp_e=0.0f;
        this.battery=0.0f;
        //this.foot_side
    }

    public void randomAInsoleData(){
        Random random=new Random();
        this.pres_1= random.nextInt(1000);
        this.pres_2= random.nextInt(1000);
        this.pres_3= random.nextInt(1000);
        this.pres_4= random.nextInt(1000);
        this.pres_5= random.nextInt(1000);
        this.pres_6= random.nextInt(1000);
        this.pres_7= random.nextInt(1000);
        this.pres_8= random.nextInt(1000);
        this.pres_9= random.nextInt(1000);

        this.acce_x= random.nextInt(100);
        this.acce_y= random.nextInt(100);
        this.acce_z= random.nextInt(100);

        this.temp_e=random.nextFloat();

        this.battery=random.nextFloat();

        this.foot_side = FootSide.fromInt( random.nextInt(2));
    }

    public String convertToStringData(){
        DecimalFormat df= new DecimalFormat("##.##");
        String string="Insole data:"+"\r\n";
        string+="name: "+getName()+"\r\n";
        string+="mac: "+getMac()+"\r\n";
        string+="serialNo: "+getSerialNumber()+"\r\n";
        string+="foot_side: "+(getFoot_side() == FootSide.LEFT ? "left" : "right")+"\r\n";
        string+="battery: "+df.format(getBattery())+"\r\n";

        string+="pres_1: "+df.format(getPres_1())+"\r\n";
        string+="pres_2: "+df.format(getPres_2())+"\r\n";
        string+="pres_3: "+df.format(getPres_3())+"\r\n";
        string+="pres_4: "+df.format(getPres_4())+"\r\n";
        string+="pres_5: "+df.format(getPres_5())+"\r\n";
        string+="pres_6: "+df.format(getPres_6())+"\r\n";
        string+="pres_7: "+df.format(getPres_7())+"\r\n";
        string+="pres_8: "+df.format(getPres_8())+"\r\n";
        string+="pres_9: "+df.format(getPres_9())+"\r\n";
        string+="acce_x: "+df.format(getAcce_x())+"\r\n";
        string+="acce_y: "+df.format(getAcce_y())+"\r\n";
        string+="acce_z: "+df.format(getAcce_z())+"\r\n";
        string+="temp_e: "+df.format(getTemp_e())+"\r\n";
        string+="mtk: "+df.format((getPres_1()+getPres_2()+getPres_3()+getPres_4()+getPres_5())/5.0f)+"\r\n";
        string+="heel: "+df.format((getPres_6()+getPres_7()+getPres_8()+getPres_9())/4.0f)+"\r\n";

        return string;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }

    public float getPres_1() {
        return pres_1;
    }

    public void setPres_1(float pres_1) {
        this.pres_1 = pres_1;
    }

    public float getPres_2() {
        return pres_2;
    }

    public void setPres_2(float pres_2) {
        this.pres_2 = pres_2;
    }

    public float getPres_3() {
        return pres_3;
    }

    public void setPres_3(float pres_3) {
        this.pres_3 = pres_3;
    }

    public float getPres_4() {
        return pres_4;
    }

    public void setPres_4(float pres_4) {
        this.pres_4 = pres_4;
    }

    public float getPres_5() {
        return pres_5;
    }

    public void setPres_5(float pres_5) {
        this.pres_5 = pres_5;
    }

    public float getPres_6() {
        return pres_6;
    }

    public void setPres_6(float pres_6) {
        this.pres_6 = pres_6;
    }

    public float getPres_7() {
        return pres_7;
    }

    public void setPres_7(float pres_7) {
        this.pres_7 = pres_7;
    }

    public float getPres_8() {
        return pres_8;
    }

    public void setPres_8(float pres_8) {
        this.pres_8 = pres_8;
    }

    public float getPres_9() {
        return pres_9;
    }

    public void setPres_9(float pres_9) {
        this.pres_9 = pres_9;
    }

    public float getPres_10() {
        return pres_10;
    }

    public void setPres_10(float pres_10) {
        this.pres_10 = pres_10;
    }

    public float getAcce_x() {
        return acce_x;
    }

    public void setAcce_x(float acce_x) {
        this.acce_x = acce_x;
    }

    public float getAcce_y() {
        return acce_y;
    }

    public void setAcce_y(float acce_y) {
        this.acce_y = acce_y;
    }

    public float getAcce_z() {
        return acce_z;
    }

    public void setAcce_z(float acce_z) {
        this.acce_z = acce_z;
    }

    public float getTemp_e() {
        return temp_e;
    }

    public void setTemp_e(float temp_e) {
        this.temp_e = temp_e;
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public FootSide getFoot_side() {
        return foot_side;
    }

    public void setFoot_side(FootSide foot_side) {
        this.foot_side = foot_side;
    }

    public Date getTimeSample() {
        return timeSample;
    }

    public void setTimeSample(Date timeSample) {
        this.timeSample = timeSample;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
