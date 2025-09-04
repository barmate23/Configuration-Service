package com.stockmanagementsystem.repository;
import com.stockmanagementsystem.entity.Shift;
import com.stockmanagementsystem.entity.UserShiftMapper;
import com.stockmanagementsystem.entity.Users;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserShiftRepository extends JpaRepository<UserShiftMapper,Integer> {

    List<UserShiftMapper> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndUser(Integer orgId, Integer subOrgId,boolean b, Users user);

    Page<UserShiftMapper> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShift_Id(Integer orgId, Integer subOrgId,boolean b, Integer shiftId, Pageable pageable);

    List<UserShiftMapper> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShift_Id(Integer orgId, Integer subOrgId,boolean b, Integer shiftId);

    boolean existsByOrganizationIdAndSubOrganizationIdAndIsDeletedAndUser_IdAndShift_Id(Integer orgId, Integer subOrgId,boolean b, Integer id, Integer shiftId);

    Optional<UserShiftMapper> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShift_IdAndUser_Id(Integer orgId, Integer subOrgId,boolean b, Integer shiftId, Integer userId);



    boolean existsByOrganizationIdAndSubOrganizationIdAndIsDeletedAndUserId(Integer orgId, Integer subOrgId, boolean b, Integer id);

    boolean existsByOrganizationIdAndSubOrganizationIdAndIsDeletedAndUserIdAndShiftId(Integer orgId, Integer subOrgId, boolean b, Integer id, Integer id1);

    boolean existsBySubOrganizationIdAndIsDeletedAndUserAndShift(Integer subOrgId, boolean b, Users existingUser, Shift shift);

    boolean existsBySubOrganizationIdAndIsDeletedAndUserAndShift_id(Integer subOrgId, boolean b, Users existingUser, Integer id);
}
