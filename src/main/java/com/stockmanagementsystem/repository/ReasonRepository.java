package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.Reason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReasonRepository extends JpaRepository<Reason,Integer> {


    List<Reason> findBySubOrganizationIdAndIsDeleted( Integer subOrgId,boolean b);

    List<Reason> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b);

    Page<Reason> findAll(Specification<Reason> specification, Pageable pageable);

    Optional<Reason> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId, boolean b, Integer id);


    Page<Reason> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b, Pageable pageable);

    Optional<Reason> findBySubOrganizationIdAndIsDeletedAndId(  Integer subOrgId,boolean b, Integer id);

    List<Reason> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);

    List<Reason> findBySubOrganizationId(Integer subOrgId);


    List<Reason> findByIsDeletedAndSubOrganizationIdAndReasonCategoryMasterReasonCategoryCode(boolean b, Integer subOrgId, String categoryCode);
}
