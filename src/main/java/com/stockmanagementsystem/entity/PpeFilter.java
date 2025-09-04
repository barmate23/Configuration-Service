package com.stockmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="tbl_PpeFilter")
public class PpeFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String statusName;
    private Integer sequence;
    private boolean isDeleted;
}
