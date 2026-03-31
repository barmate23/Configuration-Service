package com.stockmanagementsystem.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "tbl_production_shop")
public class ProductionShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "erp_shop_code")
    private String erpShopCode;

    @Column(name = "shop_code", unique = true)
    private String shopCode;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "description")
    private String description;

    @Column(name = "shop_type")
    private String shopType; // Assembly / Machine / Formulation

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

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

    @OneToMany(mappedBy = "productionShop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssemblyLine> assemblyLines;
}
