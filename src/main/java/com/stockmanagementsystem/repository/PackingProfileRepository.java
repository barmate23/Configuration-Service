package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.PackingProfileConfigMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackingProfileRepository extends JpaRepository<PackingProfileConfigMaster, Integer> {
    Optional<PackingProfileConfigMaster> findByOrganizationIdAndSubOrganizationIdAndDescriptionAndIsDeleted(Integer orgId, Integer subOrgId, String profileDesc, boolean b);

    List<PackingProfileConfigMaster> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b);
}
