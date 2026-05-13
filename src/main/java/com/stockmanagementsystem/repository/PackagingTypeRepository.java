package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackagingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackagingTypeRepository extends JpaRepository<PackagingType, Long> {

    Optional<PackagingType> findByTypeNameAndIsActiveTrue(String typeName);
}