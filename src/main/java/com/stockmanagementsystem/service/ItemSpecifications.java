package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Item;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ItemSpecifications {
    public static Specification<Item> withFilters(
            Integer subOrgId,
            List<String> name,
            List<String> itemGroup,
            List<String> itemCategory,
            List<String> issueType,
            List<String> classABC,
            boolean activeOnly,
            boolean deleted
    ) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (deleted) {
                predicates.add(criteriaBuilder.isFalse(root.get("isDeleted"))); // Corrected to use isDeleted instead of active
            }

            if (activeOnly) {
                predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));
            }

            if (subOrgId != null) {
                predicates.add(root.get("subOrganizationId").in(subOrgId));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(root.get("name").in(name));
            }

            if (itemGroup != null && !itemGroup.isEmpty()) {
                predicates.add(root.get("itemGroup").in(itemGroup));
            }
//            if (createdYears != null && !createdYears.isEmpty()) {
//                List<Predicate> yearPredicates = new ArrayList<>();
//                for (Integer year : createdYears) {
//                    yearPredicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("createdOn")), year));
//                }
//                predicates.add(criteriaBuilder.or(yearPredicates.toArray(new Predicate[0])));
//            }

            if (itemCategory != null && !itemCategory.isEmpty()) {
                predicates.add(root.get("itemCategory").in(itemCategory));
            }
            if (issueType != null && !issueType.isEmpty()) {
                predicates.add(root.get("issueType").in(issueType));
            }
            if (classABC != null && !classABC.isEmpty()) {
                predicates.add(root.get("classABC").in(classABC));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
