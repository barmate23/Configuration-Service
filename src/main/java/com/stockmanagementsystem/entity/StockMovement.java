package com.stockmanagementsystem.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_StockMovement")
@EntityListeners(AuditingEntityListener.class)
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "acceptedRejectedContainerId")
    private AcceptedRejectedContainer acceptedRejectedContainer;

    @ManyToOne
    @JoinColumn(name = "AcceptedRejectedContainerBarcodeId", referencedColumnName = "id")
    private AcceptedRejectedContainerBarcode acceptedRejectedContainerBarcode;

    @ManyToOne
    @JoinColumn(name = "serialBatchNumberId")
    private SerialBatchNumber serialBatchNumbers;

//    @ManyToOne
//    @JoinColumn(name = "serialBatchNumberId")
//    private SerialBatchNumber serialBatchNumbers;

    @ManyToOne
    @JoinColumn(name = "ppeLineId")
    private PPELine ppeLine;

    @ManyToOne
    @JoinColumn(name = "StoreLocationId", referencedColumnName = "id")
    private Location storeLocation;

    @ManyToOne
    @JoinColumn(name = "transactionStatusId", referencedColumnName = "id")
    private TransactionStatus transactionStatus;

    @ManyToOne
    @JoinColumn(name = "ItemId", referencedColumnName = "id")
    private Item item;

    @Column(name = "CrrOrGrrBarcode", length = 30)
    private String crrGrrBarcode;

    @Column(name = "isAccepted")
    private Boolean isAccepted;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @CreatedBy
    @Column(name = "CreatedBy")
    private Integer createdBy;

    @CreatedDate
    @Column(name = "CreatedOn")
    private Date createdOn;

    @LastModifiedBy
    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "ModifiedOn")
    private Date modifiedOn;
}