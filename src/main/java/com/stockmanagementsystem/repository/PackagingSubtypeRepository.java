package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackagingSubtype;
import com.stockmanagementsystem.entity.PackagingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackagingSubtypeRepository extends JpaRepository<PackagingSubtype, Long> {

    Optional<PackagingSubtype> findBySubtypeNameAndPackagingTypeAndIsActiveTrue(
            String subtypeName, PackagingType packagingType);
}
