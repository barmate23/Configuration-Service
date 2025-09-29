package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.BOMLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface BomLineRepository extends JpaRepository<BOMLine,Integer> {

    BOMLine findByIsDeletedAndId(boolean b, Integer id);

    Page<BOMLine> findByIsDeletedAndBomHeadId(boolean b, Integer id, Pageable pageable);

    Page<BOMLine> findByIsDeletedAndSubOrganizationIdAndBomHeadId(boolean b, Integer subOrgId, Integer id, Pageable pageable);
    List<BOMLine> findByIsDeletedAndSubOrganizationIdAndBomHeadId(boolean b, Integer subOrgId, Integer id);

    BOMLine findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer id);

    BOMLine findByIsDeletedAndSubOrganizationIdAndItemItemIdAndBomHeadBomERPCode(boolean b, Integer subOrgId, String itemId, String bomId);

    List<BOMLine> findByIsDeletedAndSubOrganizationIdAndItemItemIdNotInAndBomHeadBomId(boolean b, Integer subOrgId, List<String> ppeHeadLineList, String bomId);
}
