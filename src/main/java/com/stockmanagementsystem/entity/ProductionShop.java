package com.stockmanagementsystem.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "tbl_ProductionShop")
public class ProductionShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "erpShopCode")
    private String erpShopCode;

    @Column(name = "shopCode", unique = true)
    private String shopCode;

    @Column(name = "shopName")
    private String shopName;

    @Column(name = "description")
    private String description;

    @Column(name = "shopType")
    private String shopType; // Assembly / Machine / Formulation

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedOn")
    private Date createdOn;

    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Column(name = "ModifiedOn")
    private Date modifiedOn;

    @OneToMany(mappedBy = "productionShop", cascade = CascadeType.ALL)
    private List<ProductionLine> productionLines;
}
