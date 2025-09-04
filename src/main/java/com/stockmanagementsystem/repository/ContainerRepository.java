package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Container;
import com.stockmanagementsystem.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container,Integer> {
    Container findByIsDeletedAndItemId(boolean b, Integer id);


    Container findByIsDeletedAndSubOrganizationIdAndItemId(boolean b, Integer subOrgId, Integer id);
}
