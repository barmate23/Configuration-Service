package com.stockmanagementsystem.repository;


import com.stockmanagementsystem.entity.AcceptedRejectedContainerBarcode;
import com.stockmanagementsystem.entity.WeeklyOffDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyOffDaysRepository extends JpaRepository<WeeklyOffDays,Integer> {

    List<WeeklyOffDays> findByIsChecked(boolean b);
}
