package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.HolidayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayTypeRepository extends JpaRepository<HolidayType,Integer>{

    HolidayType findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId,boolean b,Integer holidayType);

    HolidayType findByIsDeletedAndId( boolean b, Integer holidayType);
}
