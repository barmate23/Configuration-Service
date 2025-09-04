package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.AcceptedRejectedStagingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcceptedRejectedStagingAreaRepository extends JpaRepository<AcceptedRejectedStagingArea,Integer> {

    List<AcceptedRejectedStagingArea> findBySubOrganizationIdAndIsAcceptedAndIsDeleted(Integer subOrgId, Boolean isAccepted, boolean b);

    boolean existsByIsDeletedAndAcceptedRejectedCode(boolean b, String generatedAcceptedRejectedCode);

    List<AcceptedRejectedStagingArea> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
}
