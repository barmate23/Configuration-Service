package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ModuleUserLicenceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleUserLicenceKeyRepository extends JpaRepository<ModuleUserLicenceKey,Integer> {
    ModuleUserLicenceKey findByIsDeletedAndId(boolean b, Integer i);
}
