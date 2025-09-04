package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.BOMLine;
import com.stockmanagementsystem.entity.BoMHead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BomHeadRepository extends JpaRepository<BoMHead,Integer> {
    Page<BoMHead> findByIsDeleted(boolean b, Pageable pageable);

    BoMHead findByIsDeletedAndId(boolean b, Integer bomId);
    Page<BoMHead> findByIsDeletedAndId(boolean b, Integer bomId, Pageable pageable);

    List<BoMHead> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<BoMHead> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId, Pageable pageable);

    Page<BoMHead> findByIsDeletedAndIdIn(boolean b, List<Integer> bomId, Pageable pageable);

    Page<BoMHead> findByIsDeletedAndModelIn(boolean b, List<String> model, Pageable pageable);

    Page<BoMHead> findByIsDeletedAndVariantIn(boolean b, List<String> varient, Pageable pageable);

    Page<BoMHead> findByIsDeletedAndDate(boolean b, Date date, Pageable pageable);

    Page<BoMHead> findByIsDeletedAndSubOrganizationIdAndIdIn(boolean b, Integer subOrgId, List<Integer> bomId, Pageable pageable);

    Page<BoMHead> findByIsDeletedAndSubOrganizationIdAndModelIn(boolean b, Integer subOrgId, List<String> model, Pageable pageable);

    Page<BoMHead> findByIsDeletedAndSubOrganizationIdAndVariantIn(boolean b, Integer subOrgId, List<String> varient, Pageable pageable);

    BoMHead findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer bomId);

    Optional<BoMHead> findByIsDeletedAndBomId(boolean b, String bomId);

    List<BoMHead> findBySubOrganizationId(Integer subOrgId);

    Optional<BoMHead> findByIsDeletedAndSubOrganizationIdAndBomId(boolean b, Integer subOrgId, String bomId);



    boolean existsByBomIdAndSubOrganizationId(String newBomId, Integer subOrgId);

    Optional<BoMHead> findByIsDeletedAndSubOrganizationIdAndBomERPCode(boolean b, Integer subOrgId, String bomErpCode);



    Page<BoMHead> findByIsDeletedAndSubOrganizationIdAndBomERPCodeIn(boolean b, Integer subOrgId, List<String> bomERPCode, Pageable pageable);
}
