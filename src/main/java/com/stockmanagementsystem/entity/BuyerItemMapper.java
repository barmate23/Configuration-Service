package com.stockmanagementsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_BuyerItemMapper")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerItemMapper {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer buyerItemMapperId;

        @Column(name = "organizationId")
        private Integer organizationId;

        @Column(name = "subOrganizationId")
        private Integer subOrganizationId;

        @ManyToOne
        @JoinColumn(name = "supplierId")
        private Supplier supplier;

        @ManyToOne
        @JoinColumn(name = "item_id")
        private Item item;

        @Column(name = "isDeleted")
        private Boolean isDeleted;

        @Column(name = "createdBy")
        private Integer createdBy;

        @Column(name = "createdOn")
        private Date createdOn;

        @Column(name = "modifiedBy")
        private Integer modifiedBy;

        @Column(name = "modifiedOn")
        private Date modifiedOn;
}
