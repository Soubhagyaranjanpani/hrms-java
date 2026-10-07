package com.hrms.master.dto;



import lombok.Data;

@Data
public class BranchCreateReq {
    private String code;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
