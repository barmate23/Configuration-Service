package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Shift;
import com.stockmanagementsystem.entity.UserDockMapper;
import com.stockmanagementsystem.entity.UserShiftMapper;
import com.stockmanagementsystem.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDockRepository extends JpaRepository<UserDockMapper,Integer> {

    List<UserDockMapper> findBySubOrganizationIdAndIsDeletedAndDockId(Integer subOrgId, boolean b, Integer id);

    UserDockMapper findBySubOrganizationIdAndIsDeletedAndDockIdAndUserId(Integer subOrgId, boolean b, Integer id, Integer dockSupervisorId);
}
