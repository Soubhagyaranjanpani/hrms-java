package com.hrms.Recuirment.domain;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Source {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String SourceCode;
    private String SourceName;
    private  String Category;
    private  String Description;
    private String DisplayOrder;





}
