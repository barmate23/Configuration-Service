package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreAreaMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer storeId;

    private Integer areaId;

    private String areaName;

    @JsonIgnore
    private String freeField1;
    @JsonIgnore
    private String freeField2;
    @JsonIgnore
    private Integer freeField3;
    @JsonIgnore
    private Integer freeField4;

}
