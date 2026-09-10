package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.NewAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewAddressRepository extends JpaRepository<NewAddress,Integer> {
    List<NewAddress> findAllByIsDeletedFalseOrderByAddressIdDesc();

    Optional<NewAddress> findByAddressIdAndIsDeletedFalse(Long addressId);

    boolean existsByAddressIdAndIsDeletedFalse(Long addressId);

    List<NewAddress> findByPincodeAndIsDeletedFalse(String pincode);}
