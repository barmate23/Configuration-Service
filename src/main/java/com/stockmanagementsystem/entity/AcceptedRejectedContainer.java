package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_AcceptedRejectedContainer")
public class AcceptedRejectedContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "purchaseOrderLineId", referencedColumnName = "id")
    private PurchaseOrderLine purchaseOrderLine;

    @ManyToOne
    @JoinColumn(name = "AsnOrderLineId", referencedColumnName = "id")
    private ASNLine asnLine;

    @ManyToOne
    @JoinColumn(name = "StatusId", referencedColumnName = "id")
    private CommonMaster status;

    @ManyToOne
    @JoinColumn(name = "dock_id")
    private Dock dock;

    @Column(name = "acceptedRejectedContainerQuantity")
    private Integer acceptedRejectedContainerQuantity;

    @Column(name = "qnNumber")
    private Integer qnNumber;

    @Column(name = "qcDate")
    private Date qcDate;

    @ManyToOne
    @JoinColumn(name = "qcEngineer", referencedColumnName = "id")
    private Users qcEngineer;

    @Column(name = "startTime")
    private Time startTime;

    @Column(name = "endTime")
    private Time endTime;

    //added new column
    @ManyToOne
    @JoinColumn(name = "storeOperator", referencedColumnName = "id")
    private Users storeOperator;

    //Added new column
    @Column(name = "AssignCrrStoreOperatorDate")
    private Date assignCrrStoreOperatorDate;

    @Column(name = "storeOperatorStartTime")
    private Time storeOperatorStartTime;

    @Column(name = "storeOperatorEndTime")
    private Time storeOperatorEndTime;

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