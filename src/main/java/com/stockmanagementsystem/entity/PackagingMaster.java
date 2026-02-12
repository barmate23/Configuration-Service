package com.stockmanagementsystem.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tbl_packaging_master")
@Getter
@Setter
public class PackagingMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packagingCode;
    private String packagingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_subtype_id")
    private PackagingSubtype packagingSubtype;

    private String uom; // Unit of Measurement (e.g., "kg", "liters", "pieces")
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal diameter;

    private Boolean isActive = true;
    private Boolean isDeleted = false;
}

