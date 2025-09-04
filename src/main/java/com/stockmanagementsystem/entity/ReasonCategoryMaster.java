package com.stockmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tbl_ReasonCategoryMaster")
public class ReasonCategoryMaster {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "reasonCategoryCode")
    private String reasonCategoryCode;

    @Column(name = "reasonCategoryName")
    private String reasonCategoryName;
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
