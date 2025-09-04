package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.DeviceMaster;
import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.Reason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceMasterRepository extends JpaRepository<DeviceMaster,Integer> {

    Optional<DeviceMaster> findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer deviceId);


    List<DeviceMaster> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<DeviceMaster> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId, Pageable pageable);
    Page<DeviceMaster> findAll(Specification<DeviceMaster> specification, Pageable pageable);

    DeviceMaster findByIsDeletedAndId(boolean b, Integer deviceId);

    DeviceMaster findBySubOrganizationIdAndIsDeletedAndDeviceNameOrSubOrganizationIdAndIsDeletedAndDeviceIp(Integer subOrgId, boolean b, String deviceName, Integer subOrgId1, boolean b1, String deviceIp);

    DeviceMaster findBySubOrganizationIdAndIsDeletedAndDeviceName(Integer subOrgId, boolean b, String deviceName);

    DeviceMaster findBySubOrganizationIdAndIsDeletedAndDeviceIp(Integer subOrgId, boolean b, String deviceIp);
}
