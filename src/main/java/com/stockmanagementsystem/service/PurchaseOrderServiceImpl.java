package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.ItemRepository;
import com.stockmanagementsystem.repository.PurchaseOrderHeadRepository;
import com.stockmanagementsystem.repository.PurchaseOrderLineRepository;
import com.stockmanagementsystem.repository.SupplierRepository;
import com.stockmanagementsystem.request.ItemSupplierMapperRequest;
import com.stockmanagementsystem.request.PurchaseOrderHeadRequest;
import com.stockmanagementsystem.request.PurchaseOrderLineRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.ValidationResultResponse;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.utils.ServiceConstants;
import com.stockmanagementsystem.validation.Validations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Slf4j
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    PurchaseOrderHeadRepository purchaseOrderHeadRepository;

    @Autowired
    SupplierService supplierService;

    @Autowired
    PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    LoginUser loginUser;

    @Autowired
    Validations validations;

    @Override
    public BaseResponse<PurchaseOrderHead> getAllPurchaseOrderHeadWithPagination(List<String> orderNumber, Date orderDate, List<Integer> supplier, Date deliveryDate, Integer pageNo, Integer pageSize) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL PURCHASE ORDER HEAD WITH PAGINATION START");
        BaseResponse<PurchaseOrderHead> baseResponse = new BaseResponse<>();
        List<PurchaseOrderHead> purchaseOrderHeads = new ArrayList<>();
        try {
            Page<PurchaseOrderHead> pageResult = null;
            final Pageable pageable = PageRequest.of(pageNo, pageSize);

            if (orderNumber == null && orderDate == null && supplier == null && deliveryDate == null) {
                pageResult = this.purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId(), pageable);
            } else {
                List<PurchaseOrderHead> poh = this.purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());

                if (orderNumber != null) {
                    poh = poh.stream()
                            .filter(k -> orderNumber.contains(k.getPurchaseOrderNumber()))
                            .collect(Collectors.toList());
                }

                if (supplier != null) {
                    poh = poh.stream()
                            .filter(k -> supplier.contains(k.getSupplier().getId()))
                            .collect(Collectors.toList());
                }

                if (orderDate != null) {
                    poh = poh.stream()
                            .filter(k -> k.getPurchaseOrderDate() != null && isSameDay(k.getPurchaseOrderDate(), orderDate))
                            .collect(Collectors.toList());
                }

                if (deliveryDate != null) {
                    poh = poh.stream()
                            .filter(k -> k.getDeliverByDate() != null && isSameDay(k.getDeliverByDate(), deliveryDate))
                            .collect(Collectors.toList());
                }

                purchaseOrderHeads = poh;
                baseResponse.setTotalRecordCount((long) purchaseOrderHeads.size());
            }


            if (pageResult != null) {
                baseResponse.setTotalPageCount(pageResult.getTotalPages());
                purchaseOrderHeads = pageResult.getContent();
                baseResponse.setTotalRecordCount(pageResult.getTotalElements());
            }

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10086S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(purchaseOrderHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10085F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHeadWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL PURCHASE ORDER HEAD WITH PAGINATION TIME " + (endTime - startTime));

        return baseResponse;
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public BaseResponse<PurchaseOrderHead> savePurchaseOrder(PurchaseOrderHeadRequest purchaseOrderHeadRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - savePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVED PURCHASE ORDER START");
        BaseResponse<PurchaseOrderHead> baseResponse = new BaseResponse<>();
        List<PurchaseOrderHead> purchaseOrderHeads = new ArrayList<>();
        try {
            PurchaseOrderHead purchaseOrderHead = new PurchaseOrderHead();
            purchaseOrderHead.setPurchaseOrderDate(purchaseOrderHeadRequest.getPurchaseOrderDate());
            purchaseOrderHead.setPurchaseOrderNumber(purchaseOrderHeadRequest.getPurchaseOrderNumber());
            purchaseOrderHead.setDeliverByDate(purchaseOrderHeadRequest.getDeliverByDate());
            purchaseOrderHead.setDeliveryType(purchaseOrderHeadRequest.getDeliveryType());
            Optional<Supplier> supplierOptional = supplierRepository.findByIsDeletedAndId(false, purchaseOrderHeadRequest.getSupplierId());
            if (supplierOptional.isPresent()) {
                purchaseOrderHead.setSupplier(supplierOptional.get());
            } else {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10053E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(purchaseOrderHeads);
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            purchaseOrderHead.setMobileNumber(purchaseOrderHeadRequest.getMobileNumber());
            purchaseOrderHead.setTotalAmount(purchaseOrderHeadRequest.getPurchaseOrderLineRequests().stream().mapToDouble(PurchaseOrderLineRequest::getTotalAmount).sum());

            purchaseOrderHead.setIsActive(true);
            purchaseOrderHead.setIsDeleted(false);
            purchaseOrderHead.setOrganizationId(loginUser.getOrgId());
            purchaseOrderHead.setSubOrganizationId(loginUser.getSubOrgId());
            purchaseOrderHead.setCreatedOn(new Date());
            purchaseOrderHead.setCreatedBy(loginUser.getUserId());
            purchaseOrderHead.setModifiedOn(new Date());
            purchaseOrderHead.setModifiedBy(loginUser.getUserId());
            purchaseOrderHeadRepository.save(purchaseOrderHead);
            List<ItemSupplierMapperRequest> itemSupplierMapperRequests=new ArrayList<>();
            for (PurchaseOrderLineRequest purchaseOrderLineRequest : purchaseOrderHeadRequest.getPurchaseOrderLineRequests()) {
                PurchaseOrderLine purchaseOrderLine = new PurchaseOrderLine();
                purchaseOrderLine.setPurchaseOrderHead(purchaseOrderHead);
                purchaseOrderLine.setLineNumber(purchaseOrderLineRequest.getLineNumber());
                purchaseOrderLine.setUnitPrice(purchaseOrderLineRequest.getUnitPrice());
                purchaseOrderLine.setPurchaseOrderLineId("");
                purchaseOrderLine.setSubTotalRs(purchaseOrderLineRequest.getSubTotal());
                purchaseOrderLine.setTotalAmountRs(purchaseOrderLineRequest.getTotalAmount());
                purchaseOrderLine.setStateGSTAmount(purchaseOrderLineRequest.getStateGstAmount());
                purchaseOrderLine.setStateGSTPercentage(purchaseOrderLineRequest.getStateGstPercent());

                purchaseOrderLine.setCentralGSTPercentage(purchaseOrderLineRequest.getCentralGstPercent());
                purchaseOrderLine.setLeadTime(purchaseOrderLineRequest.getLeadTime());
                purchaseOrderLine.setIsDay(purchaseOrderLineRequest.getIsDay());
                ItemSupplierMapperRequest itemSupplierMapperRequest = new ItemSupplierMapperRequest();
                if (supplierOptional.isPresent()) {
                    itemSupplierMapperRequest.setSupplierId(supplierOptional.get().getId());
                }
                itemSupplierMapperRequest.setItemId(purchaseOrderLineRequest.getItemId());
                itemSupplierMapperRequest.setIsDay(purchaseOrderLineRequest.getIsDay());
                itemSupplierMapperRequest.setLeadTime(purchaseOrderLineRequest.getLeadTime());

                itemSupplierMapperRequests.add(itemSupplierMapperRequest);

                purchaseOrderLine.setCentralGSTAmount(purchaseOrderLineRequest.getCentralGstAmount());
                purchaseOrderLine.setInterStateGSTPercentage(purchaseOrderLineRequest.getInterStateGstPercent());
                purchaseOrderLine.setInterStateGSTAmount(purchaseOrderLineRequest.getInterStateGstAmount());
                purchaseOrderLine.setPurchaseOrderQuantity(purchaseOrderLineRequest.getPurchaseOrderQuantity());
                purchaseOrderLine.setUom(purchaseOrderLineRequest.getUom());
                Optional<Item> itemOptional = itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), purchaseOrderLineRequest.getItemId());
                if (itemOptional.isPresent()) {
                    purchaseOrderLine.setItem(itemOptional.get());
                } else {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10052E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(purchaseOrderHeads);
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
                purchaseOrderLine.setIsDeleted(false);
                purchaseOrderLine.setOrganizationId(loginUser.getOrgId());
                purchaseOrderLine.setSubOrganizationId(loginUser.getSubOrgId());
                purchaseOrderLine.setCreatedOn(new Date());
                purchaseOrderLine.setCreatedBy(loginUser.getUserId());
                purchaseOrderLine.setModifiedOn(new Date());
                purchaseOrderLine.setModifiedBy(loginUser.getUserId());
                purchaseOrderLineRepository.save(purchaseOrderLine);
            }
            purchaseOrderHeads.add(purchaseOrderHead);
            supplierService.mapItemBySupplier(itemSupplierMapperRequests);

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10087S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(purchaseOrderHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - PurchaseOrderServiceImpl - savePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10086F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - PurchaseOrderServiceImpl - savePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - savePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVE PURCHASE ORDER TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<PurchaseOrderHead> updatePurchaseOrder(Integer poId, PurchaseOrderHeadRequest purchaseOrderHeadRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - updatePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPDATE PURCHASE ORDER START");
        BaseResponse<PurchaseOrderHead> baseResponse = new BaseResponse<>();
        List<PurchaseOrderHead> purchaseOrderHeads = new ArrayList<>();
        try {
            PurchaseOrderHead purchaseOrderHead = purchaseOrderHeadRepository.findByIsDeletedAndId(false, poId);
            purchaseOrderHead.setPurchaseOrderNumber(purchaseOrderHeadRequest.getPurchaseOrderNumber());
            purchaseOrderHead.setPurchaseOrderDate(purchaseOrderHeadRequest.getPurchaseOrderDate());
            purchaseOrderHead.setDeliverByDate(purchaseOrderHeadRequest.getDeliverByDate());
            purchaseOrderHead.setDeliveryType(purchaseOrderHeadRequest.getDeliveryType());
            Optional<Supplier> supplierOptional = supplierRepository.findByIsDeletedAndId(false, purchaseOrderHeadRequest.getSupplierId());
            if (supplierOptional.isPresent()) {
                purchaseOrderHead.setSupplier(supplierOptional.get());
            } else {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10054E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(purchaseOrderHeads);
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            purchaseOrderHead.setMobileNumber(purchaseOrderHeadRequest.getMobileNumber());
            purchaseOrderHead.setTotalAmount(purchaseOrderHeadRequest.getPurchaseOrderLineRequests().stream().mapToDouble(PurchaseOrderLineRequest::getTotalAmount).sum());
            purchaseOrderHead.setIsActive(true);
            purchaseOrderHead.setIsDeleted(false);
            purchaseOrderHead.setOrganizationId(loginUser.getOrgId());
            purchaseOrderHead.setSubOrganizationId(loginUser.getSubOrgId());
            purchaseOrderHead.setCreatedOn(new Date());
            purchaseOrderHead.setCreatedBy(loginUser.getUserId());
            purchaseOrderHead.setModifiedOn(new Date());
            purchaseOrderHead.setModifiedBy(loginUser.getUserId());
            purchaseOrderHeadRepository.save(purchaseOrderHead);
            for (PurchaseOrderLineRequest purchaseOrderLineRequest : purchaseOrderHeadRequest.getPurchaseOrderLineRequests()) {
                PurchaseOrderLine purchaseOrderLine = null;
                if (purchaseOrderLineRequest.getId() != null) {
                    purchaseOrderLine = purchaseOrderLineRepository.findByIsDeletedAndId(false, purchaseOrderLineRequest.getId());
                } else {
                    purchaseOrderLine = new PurchaseOrderLine();
                }
                purchaseOrderLine.setPurchaseOrderHead(purchaseOrderHead);
                purchaseOrderLine.setLineNumber(purchaseOrderLineRequest.getLineNumber());
                purchaseOrderLine.setUnitPrice(purchaseOrderLineRequest.getUnitPrice());
                purchaseOrderLine.setPurchaseOrderLineId("");
                purchaseOrderLine.setSubTotalRs(purchaseOrderLineRequest.getSubTotal());
                purchaseOrderLine.setTotalAmountRs(purchaseOrderLineRequest.getTotalAmount());
                purchaseOrderLine.setStateGSTAmount(purchaseOrderLineRequest.getStateGstAmount());
                purchaseOrderLine.setStateGSTPercentage(purchaseOrderLineRequest.getStateGstPercent());
                purchaseOrderLine.setCentralGSTPercentage(purchaseOrderLineRequest.getCentralGstPercent());
                purchaseOrderLine.setCentralGSTAmount(purchaseOrderLineRequest.getCentralGstAmount());
                purchaseOrderLine.setInterStateGSTPercentage(purchaseOrderLineRequest.getInterStateGstPercent());
                purchaseOrderLine.setInterStateGSTAmount(purchaseOrderLine.getInterStateGSTAmount());
                purchaseOrderLine.setPurchaseOrderQuantity(purchaseOrderLineRequest.getPurchaseOrderQuantity());
                purchaseOrderLine.setUom(purchaseOrderLineRequest.getUom());
                Optional<Item> itemOptional = itemRepository.findByIsDeletedAndId(false, purchaseOrderLineRequest.getItemId());
                if (itemOptional.isPresent()) {
                    purchaseOrderLine.setItem(itemOptional.get());
                } else {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10055E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(purchaseOrderHeads);
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
                purchaseOrderLine.setIsDeleted(false);
                purchaseOrderLine.setOrganizationId(loginUser.getOrgId());
                purchaseOrderLine.setSubOrganizationId(loginUser.getSubOrgId());
                purchaseOrderLine.setCreatedOn(new Date());
                purchaseOrderLine.setCreatedBy(loginUser.getUserId());
                purchaseOrderLine.setModifiedOn(new Date());
                purchaseOrderLine.setModifiedBy(loginUser.getUserId());
                purchaseOrderLineRepository.save(purchaseOrderLine);
            }
            purchaseOrderHeads.add(purchaseOrderHead);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10088S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(purchaseOrderHeads);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - PurchaseOrderServiceImpl - updatePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10087F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - PurchaseOrderServiceImpl - updatePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - updatePurchaseOrder - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPDATE PURCHASE ORDER TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<PurchaseOrderLine> getPurchaseOrderLineByPoId(Integer poId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - getPurchaseOrderLineByPoId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET PURCHASE ORDER LINE BY POID START");

        BaseResponse<PurchaseOrderLine> baseResponse = new BaseResponse<>();
        try {
            List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), poId);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10089S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(purchaseOrderLineList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - PurchaseOrderServiceImpl - getPurchaseOrderLineByPoId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10088F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - PurchaseOrderServiceImpl - getPurchaseOrderLineByPoId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - getPurchaseOrderLineByPoId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET PURCHASE ORDER LINE BY POID TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<PurchaseOrderLine> deletePurchaseOrderLineById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE PURCHASE ORDER LINE BY ID START");

        BaseResponse<PurchaseOrderLine> baseResponse = new BaseResponse<>();
        try {
            List<PurchaseOrderLine> purchaseOrderLineList = new ArrayList<>();
            PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), id);
            purchaseOrderLine.setIsDeleted(true);
            purchaseOrderLineRepository.save(purchaseOrderLine);
            purchaseOrderLineList.add(purchaseOrderLine);

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10090S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(purchaseOrderLineList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10089F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderLineById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE PURCHASE ORDER LINE BY ID TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<PurchaseOrderHead> deletePurchaseOrderHeadById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderHeadById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE PURCHASE ORDER HEAD BY ID START");

        BaseResponse<PurchaseOrderHead> baseResponse = new BaseResponse<>();
        try {
            List<PurchaseOrderHead> purchaseOrderHeadList = new ArrayList<>();
            PurchaseOrderHead purchaseOrderHead = purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), id);
            List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), id);

            boolean allItemsPassAmount = purchaseOrderLineList
                    .stream()
                    .anyMatch(poLine -> poLine.getPurchaseOrderQuantity() > 0);
            Date today = new Date();

            if (allItemsPassAmount) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10140F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            } else {
                if (purchaseOrderHead.getDeliverByDate().after(today)) {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10139F);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
            }
            purchaseOrderHead.setIsDeleted(true);
            purchaseOrderHeadRepository.save(purchaseOrderHead);
            purchaseOrderHeadList.add(purchaseOrderHead);

            purchaseOrderLineList.forEach(pl -> {
                pl.setIsDeleted(true);
            });
            purchaseOrderLineRepository.saveAll(purchaseOrderLineList);

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10091S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(purchaseOrderHeadList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderHeadById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10090F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderHeadById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - deletePurchaseOrderHeadById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE PURCHASE ORDER HEAD BY ID TIME " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<PurchaseOrderHead> getAllPurchaseOrderHead() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL PURCHASE ORDER HEAD START");

        BaseResponse<PurchaseOrderHead> baseResponse = new BaseResponse<>();
        try {
            List<PurchaseOrderHead> purchaseOrderHeadList = purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10092S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(purchaseOrderHeadList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10091F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - PurchaseOrderServiceImpl - getAllPurchaseOrderHead - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL PURCHASE ORDER HEAD TIME " + (endTime - startTime));
        return baseResponse;
    }
}
