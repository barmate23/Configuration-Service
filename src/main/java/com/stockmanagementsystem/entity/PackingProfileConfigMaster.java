package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class PackingProfileConfigMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer organizationId;
    private Integer subOrganizationId;


    private String description;

    private String packingLevel; // 1,2,3

    // -------------------------
    // PRIMARY
    // -------------------------
    private String primaryUom;
    private Integer primaryUnits;

    // -------------------------
    // SECONDARY
    // -------------------------
    private String secondaryUom;
    private Integer secondaryUnits;

    // -------------------------
    // TERTIARY
    // -------------------------
    private String tertiaryUom;
    private Integer tertiaryUnits;

    private String moqLevel;   // PRIMARY / SECONDARY / TERTIARY
    private Integer moqQty;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    private Integer createdBy;
    private Date createdOn;
    private Integer modifiedBy;
    private Date modifiedOn;
}

