package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackagingMaster;
import com.stockmanagementsystem.entity.PackagingSubtype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackagingMasterRepository extends JpaRepository<PackagingMaster, Long> {

    @Query(
            "SELECT pm " +
                    "FROM PackagingMaster pm " +
                    "WHERE pm.packagingSubtype = :subtype " +
                    "AND pm.length = :length " +
                    "AND pm.width = :width " +
                    "AND pm.height = :height " +
                    "AND ( " +
                    "     (:diameter IS NULL AND pm.diameter IS NULL) " +
                    "     OR pm.diameter = :diameter " +
                    ") " +
                    "AND pm.isActive = true " +
                    "AND pm.isDeleted = false"
    )
    Optional<PackagingMaster> findExactMatch(
            @Param("subtype") PackagingSubtype subtype,
            @Param("length") BigDecimal length,
            @Param("width") BigDecimal width,
            @Param("height") BigDecimal height,
            @Param("diameter") BigDecimal diameter
    );

    @Query(
            "SELECT pm " +
                    "FROM PackagingMaster pm " +
                    "JOIN FETCH pm.packagingSubtype pst " +
                    "JOIN FETCH pst.packagingType pt " +
                    "WHERE pm.isDeleted = false " +
                    "AND pm.isActive = true " +
                    "AND pst.isActive = true " +
                    "AND pt.isActive = true"
    )
    List<PackagingMaster> findAllActiveWithHierarchy();
}