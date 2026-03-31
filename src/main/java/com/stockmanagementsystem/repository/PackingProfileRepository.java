package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackingProfileConfigMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackingProfileRepository extends JpaRepository<PackingProfileConfigMaster, Integer> {

    List<PackingProfileConfigMaster> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b);
}
