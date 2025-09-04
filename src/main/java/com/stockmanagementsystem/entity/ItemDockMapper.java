package com.stockmanagementsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDockMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer orgId;

    private String itemId;

    private String itemName;

    private String dockId;

    private String dockName;

    private String freeField1;

    private String freeField2;

    private Integer freeField3;

    private Integer freeField4;
}
