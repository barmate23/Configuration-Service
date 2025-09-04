package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_AcceptedRejectedStagingArea")
@Data
public class AcceptedRejectedStagingArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "StagingAreaId")
    private StagingArea stagingArea;

    @Column(name = "AcceptedRejectedCode")
    private String acceptedRejectedCode;

    @Column(name = "IsAccepted")
    private Boolean isAccepted;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @Column(name = "CreatedOn")
    private Date createdOn;

    @Column(name = "CreatedBy")
    private Integer createdBy;


    @Column(name = "ModifiedOn")
    private Date modifiedOn;

    @Column(name = "ModifiedBy")
    private Integer modifiedBy;


}

