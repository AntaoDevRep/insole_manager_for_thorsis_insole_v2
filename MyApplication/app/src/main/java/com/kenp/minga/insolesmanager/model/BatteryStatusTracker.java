package com.kenp.minga.insolesmanager.model;

import android.util.Log;

import com.kenp.minga.insolesmanager.business.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BatteryStatusTracker {
    private static final String TAG = BatteryStatusTracker.class.getSimpleName();
    public static final String DEFAULT_TIME_FORMAT = "yy.MM.dd HH:mm:ss";
    private GamingInsoleID insoleID;
    private String mac;
    private float startBattery;
    private float emptyBattery;
    private Date connectionStartedAt;
    private Date lowBatteryAt;
    private Date connectionStoppedAt;
    private long lifecycle;
    private SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.GERMANY);

    public BatteryStatusTracker(GamingInsoleID insoleID, String mac, Date connectionStartedAt) {
        this.insoleID = insoleID;
        this.mac = mac;
        this.connectionStartedAt = connectionStartedAt;
        this.startBattery = 0.0f;
        this.emptyBattery = 0.0f;
    }




    public String getReport(){
        return "Connection started at " + formatter.format(connectionStartedAt)+ " with battery level " + startBattery + " V " + "\r\n"
                + " stopped at " + formatter.format(lowBatteryAt) + " with battery level " + emptyBattery + " V " + "\r\n"
                + " battery lived for " + lifecycle + " minutes.";
    }

    public float getStartBattery() {
        return startBattery;
    }

    public void setStartBattery(float startBattery) {
        this.startBattery = startBattery;
    }

    public float getEmptyBattery() {
        return emptyBattery;
    }

    public void setEmptyBattery(float emptyBattery) {
        this.emptyBattery = emptyBattery;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public GamingInsoleID getInsoleID() {
        return insoleID;
    }

    public void setInsoleID(GamingInsoleID insoleID) {
        this.insoleID = insoleID;
    }

    public Date getLowBatteryAt() {
        return lowBatteryAt;
    }

    public void setLowBatteryAt(Date lowBatteryAt) {
        this.lowBatteryAt = lowBatteryAt;
        this.lifecycle = DateUtil.dateDiffInMin(connectionStartedAt, lowBatteryAt);
        Log.d(TAG, "setLowBatteryAt: " + formatter.format(lowBatteryAt));
    }

    public Date getConnectionStartedAt() {
        return connectionStartedAt;
    }

    public void setConnectionStartedAt(Date connectionStartedAt) {
        this.connectionStartedAt = connectionStartedAt;
    }

    public Date getConnectionStoppedAt() {
        return connectionStoppedAt;
    }

    public void setConnectionStoppedAt(Date connectionStoppedAt) {
        this.connectionStoppedAt = connectionStoppedAt;
    }

    public long getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(long lifecycle) {
        this.lifecycle = lifecycle;
    }
}
