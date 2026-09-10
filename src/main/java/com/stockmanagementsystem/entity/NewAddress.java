package com.stockmanagementsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_new_address")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "digipin", length = 50)
    private String digipin;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "taluka", length = 100)
    private String taluka;

    @Column(name = "town", length = 100)
    private String town;

    @Column(name = "village", length = 100)
    private String village;

    @Column(name = "locality", length = 255)
    private String locality;

    @Column(name = "sub_locality", length = 255)
    private String subLocality;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "google_map_link", length = 1000)
    private String googleMapLink;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdOn = now;
        updatedOn = now;

        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}