package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_stage")
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "StageId")
    private String stageId;

    @Column(name = "StageCode")
    private String stageCode;

    @Column(name = "StageName")
    private String stageName;

    @Column(name = "erpStageCode")
    private String erpStageCode;

    @ManyToOne
    @JoinColumn(name = "assembly_line_id")
    @JsonIgnore
    private AssemblyLine assemblyLine;

    @Column(name = "sequenceNumber")
    private Integer sequenceNumber;

    @Transient
    private Integer lineId;


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
