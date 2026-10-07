package com.hrms.master.dto;



import lombok.Data;

@Data
public class BranchResponse {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private Boolean isActive;
}