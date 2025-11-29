package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_BoMHead")
public class BoMHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "Model", length = 255)
    private String model;

    @Column(name = "Product", length = 255)
    private String product;

    @Column(name = "Variant", length = 255)
    private String variant;

    @Column(name = "Colour", length = 255)
    private String colour;

    @Column(name = "BomId")
    private String bomId;

    @Column(name = "BomERPCode")
    private String bomERPCode;

    @Column(name = "Date")
    private Date date;

    @Column(name = "Version", length = 10)
    private Float version;

    @Column(name = "LifecyclePhase", length = 50)
    private String lifecyclePhase;

    @Column(name = "AssemblyLine", length = 50)
    private Integer assemblyLine;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedOn")
    private Date createdOn;

    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Column(name = "ModifiedOn")
    private Date modifiedOn;
}