package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="tbl_PpeStatus")
public class PpeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String statusName;
    private String displayName;
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

}
