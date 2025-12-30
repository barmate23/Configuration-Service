package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_Reason")
@Data
public class Reason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    @Column(name = "OrganizationId")
    private Integer organizationId;
    @JsonIgnore
    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;
    @JsonIgnore
    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "reasonId")
    private String reasonId;

    @Column(name = "rejectedReason")
    private String rejectedReason;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "reasonCategoryMasterId")
    private ReasonCategoryMaster reasonCategoryMaster;
    @JsonIgnore
    @Column(name = "isUserCreated")
    private Boolean isUserCreated;
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

