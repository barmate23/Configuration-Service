
package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ContainerHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerHierarchyRepository extends JpaRepository<ContainerHierarchy, Integer> {


    List<ContainerHierarchy> findByAsnLineIdAndIsDeletedFalse(Integer id);

}
