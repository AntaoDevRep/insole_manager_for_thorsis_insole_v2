package com.kenp.minga.insolesmanager.model;

public enum FootSide {
    LEFT (1),
    RIGHT (2);
    public int value;
    FootSide(int side){
        this.value = side;
    }

    public static FootSide fromInt(int value) {
        for (FootSide side : FootSide.values()) {
            if ( side.value == value )
                return side;
        }
        return null;
    }
}
