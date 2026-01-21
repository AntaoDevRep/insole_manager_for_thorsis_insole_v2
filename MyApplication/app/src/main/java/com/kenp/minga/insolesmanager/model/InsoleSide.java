package com.kenp.minga.insolesmanager.model;

/**
 * Created by minga on 10/23/2017.
 */

public enum InsoleSide {
    NULL("null"),
    LEFT("L"),
    RIGHT("R");

    public String name;

    InsoleSide(String name){
        this.name = name;
    }

    public static InsoleSide fromString(String footString) {
        for (InsoleSide type : InsoleSide.values()) {
            if (type.name.equals(footString))
                return type;
        }
        return null;
    }
    public String getText(){
        return name;
    }
}
