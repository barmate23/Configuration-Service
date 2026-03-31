package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage,Integer> {

    List<Stage> findByAssemblyLineId(Integer assemblyLineId);
    Stage findByIsDeletedAndId(boolean b, Integer id);
    Page<Stage> findByIsDeletedAndAssemblyLineId(boolean b, Integer id, Pageable pageable);
    List<Stage> findByIsDeletedAndAssemblyLineId(boolean b, Integer id);
    List<Stage> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Optional<Stage> findByStageCodeAndAssemblyLineId(String stage, Integer id);
    List<Stage> findBySubOrganizationIdAndAssemblyLineId(Integer subOrgId, Integer asmlId);
}
