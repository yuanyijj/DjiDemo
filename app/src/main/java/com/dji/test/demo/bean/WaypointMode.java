package com.dji.test.demo.bean;

import java.util.List;

import dji.common.mission.waypoint.WaypointActionType;

public class WaypointMode {


    public WaypointActionType getMode() {
        return Mode;
    }

    public void setMode(WaypointActionType mode) {
        Mode = mode;
    }

    public int getVar() {
        return Var;
    }

    public void setVar(int var) {
        Var = var;
    }

    private WaypointActionType Mode;
    private int Var;

}
