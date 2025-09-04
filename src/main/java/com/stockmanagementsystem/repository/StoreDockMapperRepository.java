package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.StoreDockMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface StoreDockMapperRepository extends JpaRepository<StoreDockMapper,Integer> {
    StoreDockMapper findByIsDeletedAndSubOrganizationIdAndStoreIdAndDockId(boolean b, Integer subOrgId, Integer id, Integer id1);

    List<StoreDockMapper> findByIsDeletedAndSubOrganizationIdAndDockId(boolean b, Integer subOrgId, Integer id);

    List<StoreDockMapper> findByIsDeletedAndSubOrganizationIdAndDockIdOrderById(boolean b, Integer subOrgId, Integer id);
}
