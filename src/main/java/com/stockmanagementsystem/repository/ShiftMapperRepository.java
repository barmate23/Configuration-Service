package com.stockmanagementsystem.repository;
import com.stockmanagementsystem.entity.Shift;
import com.stockmanagementsystem.entity.ShiftMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ShiftMapperRepository extends JpaRepository<ShiftMapper,Integer> {

    List<ShiftMapper> findByOrganizationIdAndSubOrganizationIdAndShift(Integer orgId, Integer subOrgId,Shift shift);
    List<ShiftMapper> findByOrganizationIdAndSubOrganizationIdAndShiftAndYear(Integer orgId, Integer subOrgId,Shift shift, Date year);


    List<ShiftMapper> findBySubOrganizationIdAndIsDeletedAndShift( Integer subOrgId,boolean b,Shift shift);

    List<ShiftMapper> findBySubOrganizationIdAndIsDeletedAndShiftAndYear( Integer subOrgId,boolean b,Shift shift, Date year);

    List<ShiftMapper> findBySubOrganizationIdAndIsDeletedAndShift_Id(Integer subOrgId, boolean b, int shiftId);

}
