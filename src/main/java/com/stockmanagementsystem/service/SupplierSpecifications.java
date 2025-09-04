package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Dock;
import com.stockmanagementsystem.entity.Supplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class SupplierSpecifications {

    public static Specification<Supplier> withFilters(
            List<String> supplierName,
            List<String> supplierCategory,
            List<String> supplierGroup,
            Integer subOrgId,
            boolean activeOnly
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

        /*    if (activeOnly) {
                predicates.add(criteriaBuilder.isTrue(root.get("active")));
            }*/
            if (activeOnly) {
                predicates.add(criteriaBuilder.isFalse(root.get("isDeleted"))); // Corrected to use isDeleted instead of active
            }

            if (supplierName != null && !supplierName.isEmpty()) {
                predicates.add(root.get("supplierName").in(supplierName));
            }
            predicates.add(root.get("subOrganizationId").in(subOrgId));

            if (supplierCategory != null && !supplierCategory.isEmpty()) {
                predicates.add(root.get("supplierCategory").in(supplierCategory));
            }
            if (supplierGroup != null && !supplierGroup.isEmpty()) {
                predicates.add(root.get("supplierGroup").in(supplierGroup));
            }

//            if (createdYears != null && !createdYears.isEmpty()) {
//                List<Predicate> yearPredicates = new ArrayList<>();
//                for (Integer year : createdYears) {
//                    yearPredicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("createdOn")), year));
//                }
//                predicates.add(criteriaBuilder.or(yearPredicates.toArray(new Predicate[0])));
//            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
