package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday,Integer> {

   Page<Holiday> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId,boolean active, Pageable pageable);

   Optional<Holiday> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId,boolean b, Integer holidayId);
   Holiday findHolidayByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId,boolean active, Integer holidayId);


   Holiday findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndHolidayName(Integer orgId, Integer subOrgId,boolean b, String holidayName);

   Holiday findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndDate(Integer orgId, Integer subOrgId,boolean b, Date date);

   @Query("SELECT h FROM Holiday h WHERE h.organizationId = :orgId AND h.subOrganizationId = :subOrgId AND h.isDeleted = :isDeleted AND h.date >= :startDate AND h.date < :endDate")
   Page<Holiday> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndDateBetween(
           @Param("orgId") Integer orgId,
           @Param("subOrgId") Integer subOrgId,
           @Param("isDeleted") boolean isDeleted,
           @Param("startDate") Date startDate,
           @Param("endDate") Date endDate,
           Pageable pageable
   );

   Holiday findBySubOrganizationIdAndIsDeletedAndId( Integer subOrgId,boolean b, Integer holidayId);

    Holiday findHolidayBySubOrganizationIdAndIsDeletedAndId( Integer subOrgId,boolean b, Integer holidayId);

   Holiday findBySubOrganizationIdAndIsDeletedAndDateOrSubOrganizationIdAndIsDeletedAndHolidayName(Integer subOrgId, boolean b, Date date, Integer subOrgId1, boolean b1, String holidayName);

    Page<Holiday> findByOrganizationIdAndSubOrganizationIdAndIsDeletedOrderByDate(Integer orgId, Integer subOrgId, boolean b, PageRequest pageable);

   Page<Holiday> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndDateBetweenOrderByDate(Integer orgId, Integer subOrgId, boolean b, Date startDate, Date endDate, PageRequest pageable);

   Page<Holiday> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndDateBetweenOrderByDateAsc(Integer orgId, Integer subOrgId, boolean b, Date startDate, Date endDate, PageRequest pageable);

   Page<Holiday> findByOrganizationIdAndSubOrganizationIdAndIsDeletedOrderByDateAsc(Integer orgId, Integer subOrgId, boolean b, PageRequest pageable);
}
