package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.BoMHead;
import com.stockmanagementsystem.entity.BuyerItemMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerItemMapperRepository extends JpaRepository<BuyerItemMapper,Integer> {

    BuyerItemMapper findBySubOrganizationIdAndIsDeletedAndItemId(Integer subOrgId, boolean b, Integer itemId);
}