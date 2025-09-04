package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone,Integer> {
    Optional<Zone> findByIsDeletedAndZoneId(boolean b, String zoneId);

    List<Zone> findByIsDeleted(boolean b);

    List<Zone> findByIsDeletedAndAreaId(boolean b, Integer areaId);
    Page<Zone> findByIsDeletedAndAreaId(boolean b, Integer areaId,Pageable pageable);

    Page<Zone> findByIsDeleted(boolean b, Pageable pageable);

    Optional<Zone> findByIsDeletedAndId(boolean b, Integer zoneId);
    Page<Zone> findByIsDeletedAndId(boolean b, Integer zoneId,Pageable pageable);


    Page<Zone> findByIsDeletedAndAreaStoreIdAndIdAndAreaId(boolean b, Integer storeId, Integer zoneId, Integer areaId, Pageable pageable);

    Page<Zone> findByIsDeletedAndAreaStoreId(boolean b, Integer storeId, Pageable pageable);

    List<Zone> findByIsDeletedAndAreaIdIn(boolean b, List<Integer> areaId);
    Page<Zone>  findByIsDeletedAndAreaIdIn(boolean b, List<Integer> areaId, Pageable pageable);


    Page<Zone> findByIsDeletedAndAreaStoreIdIn(boolean b, List<Integer> storeId, Pageable pageable);

    Page<Zone> findByIsDeletedAndIdIn(boolean b, List<Integer> zoneId, Pageable pageable);

    Page<Zone> findByIsDeletedAndAreaStoreIdInAndIdInAndAreaIdIn(boolean b, List<Integer> storeId, List<Integer> zoneId, List<Integer> areaId, Pageable pageable);

    List<Zone> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<Zone> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId,Pageable pageable);

    List<Zone> findByIsDeletedAndSubOrganizationIdAndAreaId(boolean b, Integer subOrgId, Integer areaId);

    List<Zone> findByIsDeletedAndSubOrganizationIdAndAreaIdAndOrganizationId(boolean b, Integer subOrgId, Integer areaId, Integer subOrgId1);

    Optional<Zone> findByIsDeletedAndIdAndOrganizationId(boolean b, Integer zoneId, Integer subOrgId);

    Optional<Zone> findByIsDeletedAndIdAndSubOrganizationId(boolean b, Integer zoneId, Integer subOrgId);

    List<Zone> findByIsDeletedAndSubOrganizationIdAndAreaIdIn(boolean b, Integer subOrgId, List<Integer> areaId);

    Page<Zone> findByIsDeletedAndSubOrganizationIdAndAreaStoreIdInAndIdInAndAreaIdIn(boolean b, Integer subOrgId, List<Integer> storeId, List<Integer> zoneId, List<Integer> areaId, Pageable pageable);

    Page<Zone> findByIsDeletedAndSubOrganizationIdAndAreaStoreIdIn(boolean b, Integer subOrgId, List<Integer> storeId, Pageable pageable);

    Optional<Zone> findByIsDeletedAndSubOrganizationIdAndIdAndZoneId(boolean b, Integer subOrgId, Integer zoneId, String zoneIds);

    List<Zone> findByIsDeletedAndSubOrganizationIdAndAreaIdInOrderByIdAsc(boolean b, Integer subOrgId, List<Integer> areaId);

    Page<Zone> findByIsDeletedAndSubOrganizationIdOrderByIdAsc(boolean b, Integer subOrgId, Pageable pageable);

    Page<Zone> findByIsDeletedAndSubOrganizationIdAndAreaStoreIdInAndIdInAndAreaIdInOrderByIdAsc(boolean b, Integer subOrgId, List<Integer> storeId, List<Integer> zoneId, List<Integer> areaId, Pageable pageable);

    Page<Zone> findByIsDeletedAndSubOrganizationIdAndAreaStoreIdInOrderByIdAsc(boolean b, Integer subOrgId, List<Integer> storeId, Pageable pageable);
}
