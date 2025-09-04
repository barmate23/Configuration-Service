package com.stockmanagementsystem.repository;

import com.stockmanagementsystem.entity.Item;
import com.stockmanagementsystem.response.ItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item,Integer> {



    Optional<Item> findByIsDeletedAndItemId(boolean b, String itemId);

//    Item findByOrganizationIdAndSubOrganizationIdAndIsActiveAndIsDeletedAndItemCode(Integer orgId, Integer subOrgId, boolean b, boolean b1, String itemCode);

//    Item findByOrganizationIdAndSubOrganizationIdAndIsActiveAndIsDeletedAndItemName(Integer orgId, Integer subOrgId, boolean b, boolean b1, String itemName);

//    Item findByIsActiveAndIsDeletedAndItemCodeOrItemName(boolean b, boolean b1, String itemCode, String itemName);


//    Item findByIsDeletedAndNameAndOrganizationIdAndSubOrganizationId(boolean b, String itemCode, Integer orgId, Integer subOrgId);

    Item findByIsDeletedAndItemIdAndOrganizationIdAndSubOrganizationId(boolean b, String itemCode, Integer orgId, Integer subOrgId);

    Item findByOrganizationIdAndSubOrganizationIdAndIsActiveAndIsDeletedAndItemId(Integer orgId, Integer subOrgId, boolean b, boolean b1, String itemCode);


    Item findByIsActiveAndIsDeletedAndItemIdOrName(boolean b, boolean b1, String itemCode, String itemName);

    Item findByOrganizationIdAndSubOrganizationIdAndIsActiveAndIsDeletedAndName(Integer orgId, Integer subOrgId, boolean b, boolean b1, String itemName);

  Page<Item> findByIsDeleted(boolean b, Pageable pageable);

    Optional<Item> findByIsDeletedAndId(boolean b, Integer item);


    Item findByAndIsActiveAndIsDeletedAndId(boolean b, boolean b1, Integer items);

    List<Item> findByIsActiveAndIsDeleted(boolean b, boolean b1);

    Item findByIsActiveAndIsDeletedAndId(boolean b, boolean b1, Integer items);

    List<Item> findByIsDeleted(boolean b);

    Page<Item> findAll(Specification<Item> specification, Pageable pageable);

    List<Item> findByIsDeletedAndOrganizationId(boolean b, Integer subOrgId);

    Optional<Item> findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer subOrgId, Integer itemId);

    List<Item> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);
    Page<Item> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId,Pageable pageable);

    List<Item> findByIsDeletedAndSubOrganizationIdAndAlternativeItem(boolean b, Integer subOrgId, boolean b1);

    Item findBySubOrganizationIdAndErpItemId(Integer subOrgId, String erpItemID);

    Optional<Item> findByIsDeletedAndSubOrganizationIdAndItemId(boolean b, Integer subOrgId, String itemId);

    Item findBySubOrganizationIdAndIsDeletedAndItemIdOrName(Integer subOrgId, boolean b, String itemCode, String itemName);

    Item findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndItemId(Integer orgId, Integer subOrgId, boolean b, String itemCode);

    Optional<Item> findByIsDeletedAndSubOrganizationIdAndItemCode(boolean b, Integer subOrgId, String itemCode);
    List<Item> findBySubOrganizationId(Integer subOrgId);

    Optional<Item> findByIsDeletedAndItemCode(boolean b, String itemCode);

    boolean existsBySubOrganizationIdAndIsDeletedAndItemId(Integer subOrgId, boolean b, String generatedDockId);

    Item findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndItemCode(Integer orgId, Integer subOrgId, boolean b, String itemCode);

  List<Item> findBySubOrganizationIdOrderByIdAsc(Integer subOrgId);
}
