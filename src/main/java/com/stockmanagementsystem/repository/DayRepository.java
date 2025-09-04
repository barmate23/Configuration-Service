package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DayRepository extends JpaRepository<Day,Integer> {
    Optional<Day> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(Integer orgId, Integer subOrgId, boolean b, Integer dayId);

    Optional<Day> findByIsDeletedAndId(boolean b, Integer dayId);
}
