package com.stockmanagementsystem.entity;

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

    @Column(name = "erp_line_code")
    private String erpLineCode;

    @Column(name = "line_code")
    private String lineCode;

    @Column(name = "line_name")
    private String lineName;

    @Column(name = "description")
    private String description;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private ProductionShop productionShop;

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

    @OneToMany(mappedBy = "assemblyLine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stage> stages;

}
