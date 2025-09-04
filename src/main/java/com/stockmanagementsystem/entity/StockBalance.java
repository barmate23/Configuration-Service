package com.stockmanagementsystem.entity;

import lombok.Data;
import org.hibernate.criterion.Order;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_StockBalance")
public class StockBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name="ItemId")
    private Item itemId;

    @Column(name = "BalanceQuantity")
    private Integer balanceQuantity;

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
