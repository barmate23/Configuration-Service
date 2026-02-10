package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class ItemSupplierPackingProfileMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer organizationId;
    private Integer subOrganizationId;

    @ManyToOne
    private Item item;

    @ManyToOne
    private Supplier supplier;

    @ManyToOne
    private PackingProfileConfigMaster packingProfile;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    private Integer createdBy;
    private Date createdOn;
    private Integer modifiedBy;
    private Date modifiedOn;
}
