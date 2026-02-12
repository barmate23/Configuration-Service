package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_packaging_type")
@Data
public class PackagingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeName;

    private Boolean isActive = true;

    private Boolean isDeleted = false;

    private Integer createdBy;
    private Date createdOn;
    private Integer modifiedBy;
    private Date modifiedOn;
}
