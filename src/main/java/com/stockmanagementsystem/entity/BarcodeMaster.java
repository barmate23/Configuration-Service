package com.stockmanagementsystem.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_BarcodeMaster")
public class BarcodeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "barcode_name")
    private String barcodeName;

    @Column(name = "value")
    private String value;

    @Column(name = "type")
    private String type;

    @Column(name = "text_x_cordinate")
    private String text_x_cordinate;

    @Column(name = "text_y_cordinate")
    private String text_y_cordinate;

    @Column(name = "labelFor")
    private String labelFor;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "labelWidth")
    private Integer labelWidth;

    @Column(name = "labelHeight")
    private Integer labelHeight;

    @Column(name = "labelGap")
    private Integer labelGap;

    @Column(name = "direction")
    private Integer direction;

    @Column(name = "errorLevel")
    private String errorLevel;

    @Column(name = "xCordinate")
    private Integer x_Cordinate;

    @Column(name = "yCordinate")
    private Integer y_Cordinate;

    @Column(name = "tsplCommand")
    private String tsplCommand;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "free_fields")
    private String freeFields;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @Column(name = "createdBy")
    private Integer createdBy;

    @Column(name = "createdOn")
    private Date createdOn;

    @Column(name = "modifiedBy")
    private Integer modifiedBy;

    @Column(name = "modifiedOn")
    private Date modifiedOn;

    @Column(name = "freeField1")
    private String freeField1;

    @Column(name = "freeField2")
    private String freeField2;

    @Column(name = "freeField3")
    private Integer freeField3;

    @Column(name = "freeField4")
    private Integer freeField4;
}
