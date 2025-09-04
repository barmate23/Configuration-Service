package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.ItemLocationMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemLocationMapperRepository extends JpaRepository<ItemLocationMapper,Integer> {
}
