package com.stockmanagementsystem.repository;


import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage,Integer> {

    Optional<Stage> findByStageCodeAndAssemblyLineId(String stage, Integer id);

    List<Stage> findByAssemblyLineId(Integer assemblyLineId);

    List<Stage> findAllStagesByAssemblyLine(AssemblyLine assemblyLine);

    Stage findByIsDeletedAndId(boolean b, Integer id);

    Page<Stage> findByIsDeleted(boolean b, Pageable pageable);
    Page<Stage> findByIsDeletedAndAssemblyLineId(boolean b, Integer id, Pageable pageable);

    List<Stage> findByIsDeletedAndAssemblyLineId(boolean b, Integer id);

    List<Stage> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);

    List<Stage> findBySubOrganizationId(Integer subOrgId);

    List<Stage> findBySubOrganizationIdAndAssemblyLineId(Integer subOrgId, Integer asmlId);
}
