package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Dock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface DocksRepository extends JpaRepository<Dock,Integer> {

  Optional<Dock> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId, boolean b, Integer id);
    Page<Dock> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b, Pageable pageable);

    Page<Dock> findAll(Specification<Dock> specification, Pageable pageable);

    List<Dock> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b);
  List<Dock> findByIsDeletedAndDockIdAndOrganizationIdAndSubOrganizationId(boolean b, String dockId, Integer orgId, Integer subOrgId);

    Optional<Dock> findByIsDeletedAndDockId(boolean b, String dockId);

    List<Dock> findBySubOrganizationIdAndIsDeleted( Integer subOrgId,boolean b);

    boolean existsBySubOrganizationIdAndIsDeletedAndDockId( Integer subOrgId,boolean b, String generatedDockId);

    Optional<Dock> findByIsDeletedAndId(boolean b, Integer id);

    Optional<Dock> findBySubOrganizationIdAndIsDeletedAndId(Integer subOrgId, boolean b, Integer id);
    List<Dock> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);

    Optional<Dock> findByIsDeletedAndSubOrganizationIdAndDockId(boolean b, Integer subOrgId, String dockId);

  Optional<Dock> findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer dockId);

    List<Dock> findBySubOrganizationIdOrderByIdAsc(Integer subOrgId);

    Dock findByIsDeletedAndSubOrganizationIdAndDockName(boolean b, Integer subOrgId, String dockName);



  List<Dock> findByOrganizationIdAndSubOrganizationIdAndIsDeletedOrderByIdAsc(Integer orgId, Integer subOrgId, boolean b);

  Page<Dock> findByIsDeletedAndSubOrganizationIdOrderByIdAsc(boolean b, Integer userId, Specification<Dock> specification, Pageable pageable);
}