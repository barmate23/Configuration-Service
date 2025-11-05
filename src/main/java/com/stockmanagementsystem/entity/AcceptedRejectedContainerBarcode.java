package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_AcceptedRejectedContainerBarcode")
public class AcceptedRejectedContainerBarcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;
    @JsonIgnore
    @Column(name = "OrganizationId")
    private Integer organizationId;

    @JsonIgnore
    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "acceptedRejectedContainerId", referencedColumnName = "id")
    private AcceptedRejectedContainer acceptedRejectedContainer;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "accepted_rejected_staging_area_id")
    private AcceptedRejectedStagingArea acceptedRejectedStagingArea;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "status_id")
    private CommonMaster status;

    @Column(name = "crrContainerCode")
    private String crrContainerCode;

    @Column(name = "containerCode")
    private String containerCode;

    @Column(name = "containerType")
    private String containerType;

    @Column(name = "isAccepted")
    private Boolean isAccepted;
    @JsonIgnore
    @Column(name = "IsDeleted")
    private Boolean isDeleted;
    @JsonIgnore
    @Column(name = "CreatedBy")
    private Integer createdBy;
    @JsonIgnore
    @Column(name = "CreatedOn")
    private Date createdOn;
    @JsonIgnore
    @Column(name = "ModifiedBy")
    private Integer modifiedBy;
    @JsonIgnore
    @Column(name = "ModifiedOn")
    private Date modifiedOn;
}
