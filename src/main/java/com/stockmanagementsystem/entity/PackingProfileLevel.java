package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_packing_profile_level")
@Data
public class PackingProfileLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_item_mapper_id")
    @JsonIgnore
    private SupplierItemMapper supplierItemMapper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hierarchy_level_id")
    private PackingHierarchyLevel hierarchyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_id")
    private PackagingMaster packagingMaster;

    private Integer levelOrder;
    private Integer unitsPerParent;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    private Integer createdBy;
    private Date createdOn;
}
