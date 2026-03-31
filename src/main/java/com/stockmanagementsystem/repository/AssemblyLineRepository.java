package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssemblyLineRepository extends JpaRepository<AssemblyLine, Integer> {

    List<AssemblyLine> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<AssemblyLine> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId, Pageable pageable);
    AssemblyLine findByIsDeletedAndId(boolean b, Integer id);
    List<AssemblyLine> findByIsDeletedAndProductionShopId(boolean b, Integer shopId);
    Page<AssemblyLine> findByIsDeletedAndSubOrganizationIdAndIdIn(boolean b, Integer subOrgId, List<Integer> id, Pageable pageable);
    List<AssemblyLine> findBySubOrganizationId(Integer subOrgId);
    AssemblyLine findByIsDeletedAndSubOrganizationIdAndAssemblyLineId(boolean b, Integer subOrgId, String assemblyLineId);
    AssemblyLine findByIsDeletedAndSubOrganizationIdAndLineCode(boolean b, Integer subOrgId, String lineCode);
}
