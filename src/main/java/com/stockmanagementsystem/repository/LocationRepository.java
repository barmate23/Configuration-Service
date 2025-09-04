package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer>, JpaSpecificationExecutor<Location> {


    List<Location> findByIsDeletedAndOrganizationId(boolean b, Integer orgId);

    Optional<Location> findByIsDeletedAndLocationId(boolean b, String locationId);
    Page<Location> findByIsDeletedAndLocationId(boolean b, String locationId,Pageable pageable);
    Page<Location> findByIsDeleted(boolean b, Pageable pageable);
    List<Location> findByIsDeleted(boolean b);

    Page<Location> findByIsDeletedAndZoneAreaStoreStoreId(boolean b, String storeId, Pageable pageable);

    @Query(value = "SELECT DISTINCT(*) FROM tbl_location location " +
            "JOIN tbl_item item ON location.item_id=item.id " +
            "JOIN tbl_zone zone ON location.zone_id=zone.id " +
            "JOIN tbl_area area ON zone.area_id=area.id " +
            "JOIN tbl_store store ON area.store_id=area.id " +
            "WHERE COALESCE(location.id, null) = :locationId " +
            "AND COALESCE(item.id, null) = :itemId " +
            "AND COALESCE(zone.id, null) = :zoneId " +
            "AND COALESCE(area.id, null) = :areaId " +
            " AND COALESCE(store.id, null) = :storeId",nativeQuery = true)
    List<Location> getAllLocationWithFilter(@Param("locationId") Integer locationId,@Param("itemId") Integer itemId,@Param("storeId") Integer storeId,@Param("areaId") Integer areaId,@Param("zoneId") Integer zoneId );

    Page<Location> findByIsDeletedAndZoneAreaAreaId(boolean b, String areaId, Pageable pageable);

    Page<Location> findByIsDeletedAndZoneZoneId(boolean b, String zoneId, Pageable pageable);

    Page<Location> findByIsDeletedAndItemItemId(boolean b, String itemId, Pageable pageable);

    Location findByIsDeletedAndId(boolean b, Integer id);
    Page<Location> findByIsDeletedAndId(boolean b, Integer id,Pageable pageable);

    Page<Location> findByIsDeletedAndZoneAreaStoreId(boolean b, Integer storeId, Pageable pageable);

    List<Location> findByIsDeletedAndZoneAreaStoreId(boolean b, Integer storeId);

    Page<Location> findByIsDeletedAndZoneAreaId(boolean b, Integer areaId, Pageable pageable);
    List<Location> findByIsDeletedAndZoneAreaId(boolean b, Integer areaId);

    Page<Location> findByIsDeletedAndZoneId(boolean b, Integer zoneId, Pageable pageable);

    List<Location> findByIsDeletedAndZoneId(boolean b, Integer zoneId);

    Page<Location> findByIsDeletedAndItemId(boolean b, Integer itemId, Pageable pageable);

    Page<Location> findByIsDeletedAndZoneAreaStoreIdIn(boolean b, List<Integer> storeId, Pageable pageable);

    Page<Location> findByIsDeletedAndZoneAreaIdIn(boolean b, List<Integer> areaId, Pageable pageable);

    Page<Location> findByIsDeletedAndZoneIdIn(boolean b, List<Integer> zoneId, Pageable pageable);

    Page<Location> findByIsDeletedAndIdIn(boolean b, List<Integer> locationId, Pageable pageable);

    Page<Location> findByIsDeletedAndItemIdIn(boolean b, List<Integer> itemId, Pageable pageable);



    Page<Location> findByIsDeletedAndIdInOrItemIdIn(boolean b, List<Integer> locationId, List<Integer> itemId, Pageable pageable);

    List<Location> findByIsDeletedAndSubOrganizationIdAndZoneId(boolean b, Integer subOrgId, Integer zoneId);


    List<Location> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<Location> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId, Pageable pageable);

    Optional<Location> findByIsDeletedAndSubOrganizationIdAndLocationIdAndZoneZoneId(boolean b, Integer subOrgId, String locationId, String zoneIds);

    Page<Location> findByIsDeletedAndSubOrganizationIdOrderByIdAsc(boolean b, Integer subOrgId, Pageable pageable);

    Page<Location> findByIsDeletedAndZoneAreaStoreIdInOrderByIdAsc(boolean b, List<Integer> storeId, Pageable pageable);

    Page<Location> findByIsDeletedAndZoneAreaIdInOrderByIdAsc(boolean b, List<Integer> areaId, Pageable pageable);

    Page<Location> findByIsDeletedAndZoneIdInOrderByIdAsc(boolean b, List<Integer> zoneId, Pageable pageable);

    Page<Location> findByIsDeletedAndIdInOrderByIdAsc(boolean b, List<Integer> locationId, Pageable pageable);

    Page<Location> findByIsDeletedAndIdInOrItemIdInOrderByIdAsc(boolean b, List<Integer> locationId, List<Integer> itemId, Pageable pageable);

    Page<Location> findByIsDeletedAndItemIdInOrderByIdAsc(boolean b, List<Integer> itemId, Pageable pageable);

    Location findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer locationId);

    List<Location> findByIsDeletedAndSubOrganizationIdAndZoneAreaStoreId(boolean b, Integer subOrgId, Integer id);
}
