package com.stockmanagementsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "Role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "RoleName", length = 255)
    private String roleName;

    @Column(name = "DisplayName", length = 255)
    private String displayName;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "IsDeleted")
    private boolean isDeleted;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedOn")
    private Date createdOn;

    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Column(name = "ModifiedOn")
    private Date modifiedOn;
}