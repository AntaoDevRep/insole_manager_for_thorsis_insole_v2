package com.kenp.minga.insolesmanager.model;

import java.io.Serializable;
import com.kenp.minga.insolesmanager.BR;

/**
 * Created by minga on 10/23/2017.
 */

public class InsoleItem implements Serializable {

    private String macAddress;
    private String name;
    private InsoleSide insoleSide;

    public InsoleItem(String name, String mac, InsoleSide insoleSide) {
        super();
        this.name=name;
        this.macAddress=mac;
        this.insoleSide=insoleSide;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public InsoleSide getInsoleSide() {
        return insoleSide;
    }

    public void setInsoleSide(InsoleSide insoleSide) {
        this.insoleSide = insoleSide;

    }
}
