package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackingHierarchyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface PackingHierarchyLevelRepository extends JpaRepository<PackingHierarchyLevel,Long> {

    List<PackingHierarchyLevel> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndIsActive(Integer orgId, Integer subOrgId, boolean b, boolean b1);

    List<PackingHierarchyLevel> findByIsDeletedFalse();
}
