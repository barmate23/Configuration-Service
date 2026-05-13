package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_PackingHierarchyLevel")
public class PackingHierarchyLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer organizationId;
    private Integer subOrganizationId;

    /**
     * PRIMARY / SECONDARY / TERTIARY
     */
    private String levelCode;

    private Integer levelOrder;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    private Integer createdBy;
    private Date createdOn;
    private Integer modifiedBy;
    private Date modifiedOn;
}
