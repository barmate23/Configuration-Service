package com.stockmanagementsystem.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "tbl_packaging_master")
@Getter
@Setter
public class PackagingMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "packaging_code", unique = true, nullable = false)
    private String packagingCode;

    @Column(name = "packaging_name", nullable = false)
    private String packagingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_subtype_id", nullable = false)
    private PackagingSubtype packagingSubtype;

    private String uom;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal diameter;
    private BigDecimal weight;
    private BigDecimal volume;
    private Boolean isStackable;
    private BigDecimal numberOfStackLevel;
    private BigDecimal maxWeightCapacity;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    @Column(name = "created_date")
    private Date createdDate;
}

