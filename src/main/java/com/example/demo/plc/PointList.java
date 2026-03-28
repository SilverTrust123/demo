package com.example.demo.plc;

import java.util.HashMap;
import java.util.Map;

// --------------------------- 8192 是M0 ------------------------------------------------
// M0不可以被操作

public class PointList {
    // private final MPoint RTESTART = new MPoint(90, "重來");
    // private final MPoint RESET_ALL_TIMERELAY = new MPoint(91, "重置所有計時繼電器");

    // private final DPoint STATE = new DPoint(200, "狀態");
    // private final DPoint COUNT_METAL = new DPoint(1000, "金屬件數");
    // private final DPoint COUNT_NON_METAL = new DPoint(1001, "非金屬件數");

    // private final DPoint T14 = new DPoint(14, "東西來等多久");
    // private final DPoint T0 = new DPoint(0, "等判斷");
    // private final DPoint T30 = new DPoint(30, "龍門等多久開始向下");
    // private final DPoint T3 = new DPoint(3, "龍門確保向下等多久");
    // private final DPoint T4 = new DPoint(4, "龍門夾到等多久");
    // private final DPoint T31 = new DPoint(31, "龍門到左邊停多久");
    // private final DPoint T5 = new DPoint(5, "龍們向下停多久");
    // private final DPoint T6 = new DPoint(6, "龍門放開停多久");
    // private final DPoint T15 = new DPoint(15, "龍門向上停多久");
    // private final DPoint T7 = new DPoint(7, "輸送帶2上面的感測器感測沒有東西停多久才繼續");
    // private final DPoint T8 = new DPoint(8, "選轉機械臂向下停多久");
    // private final DPoint T9 = new DPoint(9, "選轉機械臂真空泵抽真空之後等多久");
    // private final DPoint T32 = new DPoint(32, "旋轉機械臂迴轉過去停多久");
    // private final DPoint T10 = new DPoint(10, "旋轉機械臂向下停多久");
    // private final DPoint T11 = new DPoint(11, "選轉機械臂放真空等多久");
    // private final DPoint T40 = new DPoint(40, "滑台機械臂向下停多久");
    // private final DPoint T12 = new DPoint(12, "滑台機械臂夾到之後等多久");
    // private final DPoint T13 = new DPoint(13, "滑台機械臂放開之後等多久");.

    private final MPoint RTESTART = new MPoint(90, "Restart");
    private final MPoint RESET_ALL_TIMERELAY = new MPoint(91, "Reset All Time Relays");
    private final MPoint test1 = new MPoint(8213, "Reset All Time Relays");
    private final MPoint test2 = new MPoint(8192, "Reset All Time Relays");

    private final DPoint STATE = new DPoint(200, "Status");
    private final DPoint COUNT_METAL = new DPoint(1000, "Metal Count");
    private final DPoint COUNT_NON_METAL = new DPoint(1001, "Non-metal Count");

    private final DPoint T14 = new DPoint(14, "Item Arrival Wait Time");
    private final DPoint T0 = new DPoint(0, "Decision Delay");
    private final DPoint T30 = new DPoint(30, "Gantry Wait Before Moving Down");
    private final DPoint T3 = new DPoint(3, "Gantry Down Safety Delay");
    private final DPoint T4 = new DPoint(4, "Gantry Clamp Hold Time");
    private final DPoint T31 = new DPoint(31, "Gantry Left Position Hold Time");
    private final DPoint T5 = new DPoint(5, "Gantry Down Stop Time");
    private final DPoint T6 = new DPoint(6, "Gantry Release Stop Time");
    private final DPoint T15 = new DPoint(15, "Gantry Up Stop Time");

    private final DPoint T7 = new DPoint(7, "Conveyor 2 No-Item Sensor Delay");
    private final DPoint T8 = new DPoint(8, "Rotary Arm Down Stop Time");
    private final DPoint T9 = new DPoint(9, "Vacuum Pump Delay After Suction");
    private final DPoint T32 = new DPoint(32, "Rotary Arm Rotation Hold Time");
    private final DPoint T10 = new DPoint(10, "Rotary Arm Down Hold Time");
    private final DPoint T11 = new DPoint(11, "Vacuum Release Delay");

    private final DPoint T40 = new DPoint(40, "Slide Arm Down Stop Time");
    private final DPoint T12 = new DPoint(12, "Slide Arm Clamp Hold Time");
    private final DPoint T13 = new DPoint(13, "Slide Arm Release Hold Time");

    private final Map<String, MPoint> MPointMap = new HashMap<>();
    private final Map<String, DPoint> DPointMap = new HashMap<>();

    public PointList() {
        // 添加M點
        MPointMap.put("RTESTART", RTESTART);
        MPointMap.put("RESET_ALL_TIMERELAY", RESET_ALL_TIMERELAY);
        MPointMap.put("10", test1);
        MPointMap.put("0", test2);
        // 添加D點
        // 這裡的可以寫
        DPointMap.put("STATE", STATE);
        DPointMap.put("COUNT_METAL", COUNT_METAL);
        DPointMap.put("COUNT_NON_METAL", COUNT_NON_METAL);
        // 下面的D點才能寫
        DPointMap.put("T14", T14);
        DPointMap.put("T0", T0);
        DPointMap.put("T30", T30);
        DPointMap.put("T3", T3);
        DPointMap.put("T4", T4);
        DPointMap.put("T31", T31);
        DPointMap.put("T5", T5);
        DPointMap.put("T6", T6);
        DPointMap.put("T15", T15);
        DPointMap.put("T7", T7);
        DPointMap.put("T8", T8);
        DPointMap.put("T9", T9);
        DPointMap.put("T32", T32);
        DPointMap.put("T10", T10);
        DPointMap.put("T11", T11);
        DPointMap.put("T40", T40);
        DPointMap.put("T12", T12);
        DPointMap.put("T13", T13);
    }

    public Map<String, MPoint> getMPointMap() {
        return MPointMap;
    }

    public Map<String, DPoint> getDPointMap() {
        return DPointMap;
    }
}