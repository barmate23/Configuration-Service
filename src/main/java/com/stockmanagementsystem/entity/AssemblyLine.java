package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "tbl_AssemblyLine")
public class AssemblyLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "erpLineCode")
    private String erpLineCode;

    @Column(name = "lineCode")
    private String lineCode;

    @Column(name = "lineName")
    private String lineName;

    @ManyToOne
    @JoinColumn(name = "shopId")
    @JsonIgnore
    private ProductionShop productionShop;

    @Column(name = "description")
    private String description;

    @Column(name = "sequenceNumber")
    private Integer sequenceNumber;

    @OneToMany(mappedBy = "assemblyLine", cascade = CascadeType.ALL)
    private List<Stage> stages;

    @Transient
    private Integer shopId;

    @Column(name = "AssemblyLineId")
    private String assemblyLineId;

    @Column(name = "LineNumber")
    private String lineNumber;

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