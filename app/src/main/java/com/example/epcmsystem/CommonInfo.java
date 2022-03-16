package com.example.epcmsystem;

import java.util.ArrayList;
import java.util.List;

public class CommonInfo {
    private static List<RiskArea> riskAreas = new ArrayList<>();
    private static List<RiskArea> middleRiskAreas = new ArrayList<>();
    private static List<RiskArea> highRiskAreas = new ArrayList<>();

    public static void setRiskAreas(List<RiskArea> riskAreas) {
        CommonInfo.riskAreas = riskAreas;
    }

    public static void setMiddleRiskAreas(List<RiskArea> middleRiskAreas) {
        CommonInfo.middleRiskAreas = middleRiskAreas;
    }

    public static void setHighRiskAreas(List<RiskArea> highRiskAreas) {
        CommonInfo.highRiskAreas = highRiskAreas;
    }

    public static List<RiskArea> getMiddleRiskAreas() {
        return middleRiskAreas;
    }

    public static List<RiskArea> getHighRiskAreas() {
        return highRiskAreas;
    }

    public static List<RiskArea> getRiskAreas() {
        return riskAreas;
    }

    public static void addToRiskAreaList(RiskArea riskArea){
        riskAreas.add(riskArea);
    }

    public static void addToHighRiskAreaList(RiskArea riskArea){
        highRiskAreas.add(riskArea);
    }

    public static void addToMiddleRiskAreaList(RiskArea riskArea){
        middleRiskAreas.add(riskArea);
    }

    public static void clearRiskAreaList(){
        riskAreas.clear();
    }

    public static void clearHighRiskAreaList(){
        highRiskAreas.clear();
    }

    public static void clearMiddleRiskAreaList(){
        middleRiskAreas.clear();
    }
}
