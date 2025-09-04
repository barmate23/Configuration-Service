package com.stockmanagementsystem.request;

import com.stockmanagementsystem.entity.AlternateItemMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlternativeItemMapperRepository extends JpaRepository<AlternateItemMapper,Integer> {
    AlternateItemMapper findByIsDeletedAndSubOrganizationIdAndItemId(boolean b, Integer subOrgId, Integer itemId);
}
