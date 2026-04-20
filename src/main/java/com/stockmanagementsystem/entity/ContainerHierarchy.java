package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_ContainerHierarchy")
public class ContainerHierarchy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ContainerCode")
    private String containerCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PackingLevelId")
    private PackingProfileLevel packingLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SerialBatchNumberId")
    private SerialBatchNumber serialBatchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ContainerHierarchyId")
    private ContainerHierarchy parentContainerHierarchy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AsnLineId")
    private ASNLine asnLine;

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
