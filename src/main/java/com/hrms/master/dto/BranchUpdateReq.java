package com.hrms.master.dto;



import lombok.Data;

@Data
public class BranchUpdateReq {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
