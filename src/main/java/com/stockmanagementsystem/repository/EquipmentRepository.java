package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment,Integer> {
    Equipment findByIsDeletedAndId(boolean b, Integer id);

    Page<Equipment> findByIsDeleted(boolean b, Pageable pageable);

    Page<Equipment> findByIsDeletedAndTrolleyTypeAndStoreId(boolean b, String trolleyType, Integer storeId, Pageable pageable);

    Page<Equipment> findByIsDeletedAndStoreId(boolean b, Integer storeId, Pageable pageable);

    Page<Equipment> findByIsDeletedAndTrolleyType(boolean b, String trolleyType, Pageable pageable);

    List<Equipment> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<Equipment> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId, Pageable pageable);

    Page<Equipment> findByIsDeletedAndSubOrganizationIdAndTrolleyTypeAndStoreId(boolean b, Integer subOrgId, String trolleyType, Integer storeId, Pageable pageable);

    Page<Equipment> findByIsDeletedAndSubOrganizationIdAndStoreId(boolean b, Integer subOrgId, Integer storeId, Pageable pageable);

    Page<Equipment> findByIsDeletedAndSubOrganizationIdAndTrolleyType(boolean b, Integer subOrgId, String trolleyType, Pageable pageable);

    Equipment findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer id);

    Page<Equipment> findByIsDeletedAndSubOrganizationIdAndTrolleyTypeInAndStoreIdIn(boolean b, Integer subOrgId, List<String> trolleyType, List<Integer> storeId, Pageable pageable);

    Page<Equipment> findByIsDeletedAndSubOrganizationIdAndStoreIdIn(boolean b, Integer subOrgId, List<Integer> storeId, Pageable pageable);

    Page<Equipment> findByIsDeletedAndSubOrganizationIdAndTrolleyTypeIn(boolean b, Integer subOrgId, List<String> trolleyType, Pageable pageable);

    List<Equipment> findByIsDeletedAndOrganizationId(boolean b, Integer subOrgId);

    List<Equipment> findBySubOrganizationId(Integer subOrgId);
}
