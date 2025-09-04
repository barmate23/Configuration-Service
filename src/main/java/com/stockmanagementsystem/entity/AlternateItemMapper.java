package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_AlternateItemMapper")
public class AlternateItemMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;


    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "ItemId")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "AlternateItemId")
    private Item alternateItemId;

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
