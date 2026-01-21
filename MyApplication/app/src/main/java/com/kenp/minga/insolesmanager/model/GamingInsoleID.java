package com.kenp.minga.insolesmanager.model;

/**
 * Created by minga on 1/31/2018.
 */

public enum GamingInsoleID {
    EMPTY("","",""),
    GS_38_1_L("GS_38_1_L", "88:6B:0F:33:20:F7", "L"),
    GS_38_1_R("GS_38_1_R", "88:6B:0F:33:18:4A", "R"),
    GS_38_2_L("GS_38_2_L", "88:6B:0F:33:21:07", "L"),
    GS_38_2_R("GS_38_2_R", "88:6B:0F:33:21:10", "R"),
    GS_39_1_L("GS_39_1_L", "88:6B:0F:33:21:08", "L"),
    GS_39_1_R("GS_39_1_R", "88:6B:0F:33:21:0C", "R"),
    GS_39_2_L("GS_39_2_L", "88:6B:0F:33:21:12", "L"),
    GS_39_2_R("GS_39_2_R", "88:6B:0F:33:21:0B", "R"),
    GS_40_1_L("GS_40_1_L", "88:6B:0F:33:21:11", "L"),
    GS_40_1_R("GS_40_1_R", "88:6B:0F:33:21:C4", "R"),
    GS_40_2_L("GS_40_2_L", "88:6B:0F:33:21:CF", "L"),
    GS_40_2_R("GS_40_2_R", "88:6B:0F:33:21:C2", "R"),
    GS_40_3_L("GS_40_3_L", "88:6B:0F:33:20:EA", "L"),
    GS_40_3_R("GS_40_3_R", "88:6B:0F:33:20:E0", "R"),
    GS_41_1_L("GS_41_1_L", "88:6B:0F:33:20:CE", "L"),
    GS_41_1_R("GS_41_1_R", "88:6B:0F:33:20:CF", "R"),
    GS_41_2_L("GS_41_2_L", "88:6B:0F:33:20:E3", "L"),
    GS_41_2_R("GS_41_2_R", "88:6B:0F:33:20:EC", "R"),
    GS_41_3_L("GS_41_3_L", "88:6B:0F:33:21:CC", "L"),
    GS_41_3_R("GS_41_3_R", "88:6B:0F:33:20:D5", "R"),
    GS_42_1_L("GS_42_1_L", "88:6B:0F:33:20:E2", "L"),
    GS_42_1_R("GS_42_1_R", "88:6B:0F:33:20:DB", "R"),
    GS_42_2_L("GS_42_2_L", "88:6B:0F:33:20:D8", "L"),
    GS_42_2_R("GS_42_2_R", "88:6B:0F:33:1A:2A", "R"),
    GS_42_3_L("GS_42_3_L", "88:6B:0F:33:20:D4", "L"),
    GS_42_3_R("GS_42_3_R", "88:6B:0F:33:20:D0", "R"),
    GS_43_1_L("GS_43_1_L", "88:6B:0F:33:20:FE", "L"),
    GS_43_1_R("GS_43_1_R", "88:6B:0F:33:20:F9", "R"),
    GS_43_2_L("GS_43_2_L", "88:6B:0F:33:21:BC", "L"),
    GS_43_2_R("GS_43_2_R", "88:6B:0F:33:21:C1", "R"),
    GS_43_3_L("GS_43_3_L", "88:6B:0F:33:20:CC", "L"),
    GS_43_3_R("GS_43_3_R", "88:6B:0F:33:20:D3", "R"),
    GS_44_1_L("GS_44_1_L", "88:6B:0F:33:21:B6", "L"),
    GS_44_1_R("GS_44_1_R", "88:6B:0F:33:21:B8", "R"),
    GS_44_2_L("GS_44_2_L", "88:6B:0F:33:21:B3", "L"),
    GS_44_2_R("GS_44_2_R", "88:6B:0F:33:21:BA", "R"),
    GS_45_1_L("GS_45_1_L", "88:6B:0F:33:21:9A", "L"),
    GS_45_1_R("GS_45_1_R", "88:6B:0F:33:21:AD", "R"),
    GS_45_2_L("GS_45_2_L", "88:6B:0F:33:21:A9", "L"),
    GS_45_2_R("GS_45_2_R", "88:6B:0F:33:21:BB", "R"),

    //Thorsis Sole V2
    GS_4344_1_R("GS_4344_1_R", "84:0D:8E:CD:57:D2", "R"),
    GS_4344_1_L("GS_4344_1_L", "84:0D:8E:CD:55:0A", "L"),
    GS_4344_2_R("GS_4344_2_R", "84:0D:8E:CD:55:66", "R"),
    GS_4344_2_L("GS_4344_2_L", "84:0D:8E:CD:57:66", "L");

    public String name;
    public String mac;
    public String side;

    GamingInsoleID(String name, String mac, String side){
        this.name = name;
        this.mac = mac;
        this.side = side;
    }

    public static GamingInsoleID fromName(String id) {
        for (GamingInsoleID insoleID : GamingInsoleID.values()) {
            if (insoleID.name.equals(id))
                return insoleID;
        }
        return null;
    }

    public static GamingInsoleID fromMac(String mac) {
        for (GamingInsoleID insoleID : GamingInsoleID.values()) {
            if (insoleID.mac.equals(mac))
                return insoleID;
        }
        return null;
    }

    public static String getSidefromMac(String mac) {
        for (GamingInsoleID insoleID : GamingInsoleID.values()) {
            if (insoleID.mac.equals(mac))
                return insoleID.side;
        }
        return null;
    }

    public String getName(){
        return name;
    }

    public String getMac(){
        return mac;
    }

    public String getSide(){
        return side;
    }


}

