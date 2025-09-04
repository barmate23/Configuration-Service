package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Integer> {

    List<Store> findByIsDeletedAndOrganizationId(boolean b, Integer orgId);


    Optional<Store> findByIsActiveAndId(boolean b, Integer store);

    Store findByOrganizationIdAndSubOrganizationIdAndIsActiveAndIsDeletedAndStoreId(Integer orgId, Integer subOrgId, boolean b, boolean b1, String storeId);


    Optional<Store> findByIsDeletedAndStoreId(boolean b, String storeId);

    Page<Store> findByIsDeleted(boolean b, Pageable pageable);

    List<Store> findByIsDeleted(boolean b);

    Page<Store> findByIsDeletedAndErpStoreId(boolean b, String erpStoreId, Pageable pageable);

    Optional<Store> findByIsDeletedAndId(boolean b, Integer storeId);




    List<Store> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);

    Page<Store> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId, Pageable pageable);



    Optional<Store> findByIsDeletedAndIdAndSubOrganizationId(boolean b, Integer storeId, Integer subOrgId);

    List<Store> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b);

    Store findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer store);

    Store findBySubOrganizationIdAndIsDeletedAndStoreId(Integer subOrgId, boolean b, String storeId);

    Page<Store> findByIsDeletedAndSubOrganizationIdAndIdInOrErpStoreIdInOrderByIdAsc(boolean b, Integer subOrgId, List<Integer> storeId, List<String> erpStoreId, Pageable pageable);

    Page<Store> findByIsDeletedAndSubOrganizationIdOrderByIdAsc(boolean b, Integer subOrgId, Pageable pageable);

    Page<Store> findByIsDeletedAndSubOrganizationIdAndIdInOrderByIdAsc(boolean b, Integer subOrgId, List<Integer> storeId, Pageable pageable);

    Page<Store> findByIsDeletedAndSubOrganizationIdAndErpStoreIdInOrderByIdAsc(boolean b, Integer subOrgId, List<String> erpStoreId, Pageable pageable);

    Optional<Store> findByIsDeletedAndSubOrganizationIdAndStoreId(boolean b, Integer subOrgId, String storeId);

    Store findByIsDeletedAndSubOrganizationIdAndIdIn(boolean b, Integer subOrgId, List<Integer> store);

    Store findBySubOrganizationIdAndIsDeletedAndErpStoreId(Integer subOrgId, boolean b, String storeErpCode);

    Optional<Store> findByIsDeletedAndSubOrganizationIdAndErpStoreId(boolean b, Integer subOrgId, String storeErpCode);
}

