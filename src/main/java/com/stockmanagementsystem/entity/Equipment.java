package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_Equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "trolleyId")
    private String trolleyId;

    @Column(name = "equipmentName")
    private String equipmentName;

    @Column(name = "assetId")
    private String assetId;

    @Column(name = "trolleyType")
    private String trolleyType;

    @ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;

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
