package com.kenp.minga.insolesmanager.model;

import java.util.Date;

public class GaitData {
    private int id;
    private Date timeStamp;
    private float acce_x;
    private float acce_y;
    private float acce_z;

    public GaitData(InsoleData insoleData) {
        if ( insoleData != null ){
            this.timeStamp = insoleData.getTimeSample();
            this.acce_x = insoleData.getAcce_x();
            this.acce_y = insoleData.getAcce_y();
            this.acce_z = insoleData.getAcce_z();
        }
    }

    public int getId() {
        return id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public float getAcce_x() {
        return acce_x;
    }

    public float getAcce_y() {
        return acce_y;
    }

    public float getAcce_z() {
        return acce_z;
    }

    public double getAccelerationVectorSum(){
        return Math.sqrt( this.acce_x * this.acce_x + this.acce_y * this.acce_y + this.acce_z * this.acce_z );
    }
}
