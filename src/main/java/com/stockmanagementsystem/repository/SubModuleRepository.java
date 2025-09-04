package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.SubModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubModuleRepository extends JpaRepository<SubModule,Integer> {


    Optional<SubModule> findByIsDeletedAndId(boolean b, Integer role);

    SubModule findByIsDeletedAndSubModuleCode(boolean b, String zone);

    List<SubModule> findByIsDeleted(boolean b);
}
