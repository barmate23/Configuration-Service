package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.DeviceMaster;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class DeviceMasterSpecifications {

    public static Specification<DeviceMaster> withFilters(
            List<String> deviceIp,
            List<String> deviceName,
            List<String> deviceBrandName,
            boolean isDeleted,
            Integer subOrgId
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (isDeleted) {
                predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));
            }

            predicates.add((root.get("subOrganizationId").in(subOrgId)));

            if (deviceIp != null && !deviceIp.isEmpty()) {
                predicates.add(root.get("deviceIp").in(deviceIp));
            }

            if (deviceName != null && !deviceName.isEmpty()) {
                predicates.add(root.get("deviceName").in(deviceName));
            }

            if (deviceBrandName != null && !deviceBrandName.isEmpty()) {
                predicates.add(root.get("deviceBrandName").in(deviceBrandName));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
