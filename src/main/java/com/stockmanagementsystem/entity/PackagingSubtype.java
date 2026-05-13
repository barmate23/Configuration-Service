package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_packaging_subtype")
@Data
public class PackagingSubtype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subtypeName;
    private String materialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_type_id")
    private PackagingType packagingType;

    private Boolean isActive = true;

    private Boolean isDeleted = false;

    private Integer createdBy;
    private Date createdOn;
    private Integer modifiedBy;
    private Date modifiedOn;
}
