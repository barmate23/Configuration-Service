package com.stockmanagementsystem.repository;


import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssemblyLineRepository extends JpaRepository<AssemblyLine,Integer> {

//    void deleteStagesForAssemblyLine(AssemblyLine assemblyLine);

    List<Stage> findByAssemblyLineId(Integer assemblyLineId);

    List<Stage> findAllStagesByAssemblyLineId(AssemblyLine assemblyLine);

    Page<AssemblyLine> findByIsDeletedAndId(boolean b, Integer id, Pageable pageable);
    AssemblyLine findByIsDeletedAndId(boolean b, Integer id);

    Page<AssemblyLine> findByIsDeleted(boolean b, Pageable pageable);
    List<AssemblyLine> findByIsDeleted(boolean b);

    Page<AssemblyLine> findByIsDeletedAndIdIn(boolean b, List<Integer> id, Pageable pageable);

    List<AssemblyLine> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<AssemblyLine> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId,Pageable pageable);

    Page<AssemblyLine> findByIsDeletedAndSubOrganizationIdAndIdIn(boolean b, Integer subOrgId, List<Integer> id, Pageable pageable);

    AssemblyLine findByIsDeletedAndSubOrganizationIdAndAssemblyLineId(boolean b, Integer subOrgId, String lineID);

    List<AssemblyLine> findBySubOrganizationId(Integer subOrgId);
}
