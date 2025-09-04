package com.stockmanagementsystem.response;

import java.util.Objects;

public class SupplierPair {
    private String supplierId;
    private String supplierName;

    public SupplierPair(String supplierId, String supplierName) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierPair that = (SupplierPair) o;
        return Objects.equals(supplierId, that.supplierId) &&
                Objects.equals(supplierName, that.supplierName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId, supplierName);
    }
}
