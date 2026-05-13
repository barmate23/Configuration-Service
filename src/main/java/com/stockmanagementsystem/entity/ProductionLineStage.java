package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_ProductionLineStage")
public class ProductionLineStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "erpStageCode")
    private String erpStageCode;

    @Column(name = "stageCode")
    private String stageCode;

    @Column(name = "stageName")
    private String stageName;

    @ManyToOne
    @JoinColumn(name = "productionLineId")
    @JsonIgnore
    private ProductionLine productionLine;

    @Column(name = "sequenceNumber")
    private Integer sequenceNumber;

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

    @Transient
    private Integer lineId;
}
