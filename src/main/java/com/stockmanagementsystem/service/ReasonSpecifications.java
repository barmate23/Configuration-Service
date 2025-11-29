package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.entity.Reason;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ReasonSpecifications {
    public static Specification<Reason> withFilters(
            List<String> reasonId,
            String reasonCategory,
            List<String> itemName,
            Boolean isUserCreated,
            boolean activeOnly,
            Integer subOrgId
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (activeOnly) {
                predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));
            }

            predicates.add((root.get("subOrganizationId").in(subOrgId)));

            if (reasonId != null && !reasonId.isEmpty()) {
                predicates.add(root.get("reasonId").in(reasonId));
            }

            if (isUserCreated != null) {
                if (isUserCreated) {
                    predicates.add(criteriaBuilder.isTrue(root.get("isUserCreated")));
                }
            } else {
                predicates.add(criteriaBuilder.isNull(root.get("isUserCreated")));
            }

            if (reasonCategory != null && !reasonCategory.isEmpty()) {
                predicates.add(root.get("reasonCategoryMaster").get("reasonCategoryName").in(reasonCategory));
            }

            if (itemName != null && !itemName.isEmpty()) {
                Join<Reason, Item> itemJoin = root.join("item");
                predicates.add(itemJoin.get("name").in(itemName));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}