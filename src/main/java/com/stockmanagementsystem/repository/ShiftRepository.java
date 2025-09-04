package com.stockmanagementsystem.repository;
import com.stockmanagementsystem.entity.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift,Integer> {

    Page<Shift> findByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId,boolean active, Pageable pageable);

    boolean existsByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShiftName(Integer orgId, Integer subOrgId,boolean b,String shiftName);
    boolean existsByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShiftNameAndIdNot(Integer orgId, Integer subOrgId,boolean b,String shiftName, int shiftId);

    Shift findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId,boolean b, Integer shiftId);

    Shift findByIsDeletedAndId(boolean b, Integer shiftId);

    Page<Shift> findAllBySubOrganizationIdAndIsDeleted( Integer subOrgId,boolean b, Pageable pageable);

    long countByOrganizationIdAndSubOrganizationIdAndIsDeleted(Integer orgId, Integer subOrgId, boolean b);

    Shift findBySubOrganizationIdAndIsDeletedAndId(Integer subOrgId, boolean b, Integer shiftId);

    Optional<Shift> findByIdAndSubOrganizationIdAndIsDeleted(int shiftId, Integer subOrgId, boolean b);

}
