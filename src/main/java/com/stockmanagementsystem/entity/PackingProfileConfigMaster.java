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

    @ManyToOne
    @JoinColumn(name = "packingHierarchyLevelId")
    private PackingHierarchyLevel packingHierarchyLevel;

    // ===== PACKAGING FK =====
    @ManyToOne
    @JoinColumn(name = "primary_packaging_id")
    private PackagingMaster primaryPackaging;

    @ManyToOne
    @JoinColumn(name = "secondary_packaging_id")
    private PackagingMaster secondaryPackaging;

    @ManyToOne
    @JoinColumn(name = "tertiary_packaging_id")
    private PackagingMaster tertiaryPackaging;

    private Integer primaryUnits;

    private Integer secondaryUnits;

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

