
package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ContainerSerialMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerSerialMapperRepository extends JpaRepository<ContainerSerialMapper, Integer> {


    List<ContainerSerialMapper> findByIsDeletedFalse();
}

