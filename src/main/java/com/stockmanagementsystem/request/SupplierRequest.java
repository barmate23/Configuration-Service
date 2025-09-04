package com.stockmanagementsystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {

   // private String supplierId;
    private String erpSupplierId;
    private String supplierName;
    private Date dateOfRegistration;
    private String supplierCategory;
    private String supplierGroup;
    private String otherOrganizationName;
    private String supplierGSTRegistrationNumber;
    private String supplierPANNumber;
    private String supplierTANNumber;
    private String paymentTerms;
    private String paymentMethod;
    private Integer creditLimitRs;
    private Integer creditLimitDays;
    private String supplierPrimaryBanker;
    private String fullBranchAddress;
    private String micrCode;
    private String ifscCode;
    private String branchCode;

    private String country;
    private Integer countryCode;
    private Integer postCode;
    private String state;
    private String district;
    private String taluka;
    private String city;
    private String town;
    private String village;
    private String address1;
    private String address2;
    private String building;
    private String street;
    private String landmark;
    private String subLocality;
    private String locality;
    private Integer areaCode;
    private String latitude;
    private String longitude;
    private String officePrimaryPhone;
    private String officeAlternatePhone;
    private String contactPersonName;
    private String designation;
    private String department;
    private String primaryPhone;
    private String alternatePhone;
    private String primaryEmail;
    private String alternateEmail;
    private List<Integer> itemId;
}
