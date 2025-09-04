package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Dock;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


public class DockSpecifications {

   public static Specification<Dock> withFilters(
           List<String> dockIds,
           List<String> attributes,
           List<Integer> createdYears,
           boolean activeOnly,
           Integer subOrgId
   ) {
       return (root, query, criteriaBuilder) -> {
           List<Predicate> predicates = new ArrayList<>();

       /*    if (activeOnly) {
               predicates.add(criteriaBuilder.isTrue(root.get("active")));
           }
*/
           if (activeOnly) {
               predicates.add(criteriaBuilder.isFalse(root.get("isDeleted"))); // Corrected to use isDeleted instead of active
           }
           if (subOrgId!=null) {
               predicates.add(root.get("subOrganizationId").in(subOrgId)); // Corrected to use isDeleted instead of active
           }
           if (dockIds != null && !dockIds.isEmpty()) {
               predicates.add(root.get("dockId").in(dockIds));
           }

           if (createdYears != null && !createdYears.isEmpty()) {
               List<Predicate> yearPredicates = new ArrayList<>();
               for (Integer year : createdYears) {
                   yearPredicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("createdOn")), year));
               }
               predicates.add(criteriaBuilder.or(yearPredicates.toArray(new Predicate[0])));
           }

           if (attributes != null && !attributes.isEmpty()) {
               predicates.add(root.get("attribute").in(attributes));
           }

           return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
       };
   }


}
