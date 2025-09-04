package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Area;
import com.stockmanagementsystem.entity.LoginUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area,Integer> {
    Optional<Area> findByIsDeletedAndAreaId(boolean b, String area);

    Page<Area> findByIsDeleted(boolean b, Pageable pageable);
    Area findByIsDeleted(Boolean b);
    Optional<List<Area>>findByIsDeleted(boolean b);

    Area findByIsDeletedAndId(boolean b, Integer areaId);

    List<Area> findByIsDeletedAndStoreId(boolean b, Integer storeId);

    Page<Area> findByIsDeletedAndStoreIdAndId(boolean b, Pageable pageable, Integer storeId, Integer areaId);

    List<Area> findByIsDeletedAndStoreIdIn(boolean b, List<Integer> storeId);

    Page<Area> findByIsDeletedAndStoreIdInAndIdIn(boolean b, Pageable pageable, List<Integer> storeId, List<Integer> areaId);



    List<Area> findByIsDeletedAndSubOrganizationIdAndStoreId(boolean b, Integer subOrgId, Integer storeId);

    Area findByIsDeletedAndIdAndOrganizationId(boolean b, Integer areaId, Integer subOrgId);

    List<Area> findByIsDeletedAndSubOrganizationIdAndStoreIdAndOrganizationId(boolean b, Integer subOrgId, Integer storeId, Integer subOrgId1);

    Area findByIsDeletedAndIdAndSubOrganizationId(boolean b, Integer areaId, Integer subOrgId);

    Page<Area> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId, Pageable pageable);
    List<Area> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);

    Page<Area> findByIsDeletedAndSubOrganizationIdAndIdIn(boolean b, Integer subOrgId, List<Integer> areaId, Pageable pageable);

    Page<Area> findByIsDeletedAndSubOrganizationIdAndStoreIdIn(boolean b, Integer subOrgId, List<Integer> storeId, Pageable pageable);

    Area findByIsDeletedAndSubOrganizationIdAndAreaIdAndStoreStoreId(boolean b, LoginUser loginUser, String areaId, String storeId);

    Area findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer areaId);

    Page<Area> findByIsDeletedAndSubOrganizationIdOrderById(boolean b, Integer subOrgId, Pageable pageable);

    Page<Area> findByIsDeletedAndStoreIdInAndIdInOrderById(boolean b, Pageable pageable, List<Integer> storeId, List<Integer> areaId);

    Page<Area> findByIsDeletedAndSubOrganizationIdAndStoreIdInOrderById(boolean b, Integer subOrgId, List<Integer> storeId, Pageable pageable);

    Page<Area> findByIsDeletedAndSubOrganizationIdAndIdInOrderById(boolean b, Integer subOrgId, List<Integer> areaId, Pageable pageable);
}
