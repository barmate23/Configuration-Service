package com.stockmanagementsystem.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_PrintQueueDetail")
public class PrintQueueDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private DeviceMaster deviceMaster;

    @ManyToOne
    private SubModule subModules;

    @ManyToOne
    private BarcodeMaster barcodeMaster;

    @Column(name = "value")
    private String value;

    @Column(name = "printJobStatus")
    private String printJobStatus;

    @Column(name = "timeStamp")
    private Date timeStamp;

    @Column(name = "createdBy")
    private Integer createdBy;

    @Column(name = "createdOn")
    private Date createdOn;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @Column(name = "isActive")
    private Boolean isActive;

    @Column(name = "modifiedBy")
    private Integer modifiedBy;

    @Column(name = "modifiedOn")
    private Date modifiedOn;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "freeField1")
    private String freeField1;

    @Column(name = "freeField2")
    private String freeField2;

    @Column(name = "freeField3")
    private Integer freeField3;

    @Column(name = "freeField4")
    private Integer freeField4;
}


