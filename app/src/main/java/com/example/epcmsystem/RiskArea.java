package com.example.epcmsystem;

import java.util.List;

public class RiskArea {
    private String province;
    private String city;
    private String district;
    private boolean riskType; //false-中风险,true-高风险
    private List<String> streets;

    public RiskArea(String province, String city, String district, boolean riskType, List<String> streets){
        this.province = province;
        this.city = city;
        this.district = district;
        this.riskType = riskType;
        this.streets = streets;
    }

    public String getProvince(){
        return province;
    }

    public String getCity(){
        return city;
    }

    public String getDistrict(){
        return district;
    }

    public boolean getRiskType(){
        return riskType;
    }

    public List<String> getStreets(){
        return streets;
    }
}
