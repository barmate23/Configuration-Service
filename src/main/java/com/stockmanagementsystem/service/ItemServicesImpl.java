package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.AlternativeItemMapperRepository;
import com.stockmanagementsystem.request.ContainerRequest;
import com.stockmanagementsystem.request.ItemRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.validation.Validations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Slf4j
@Service
public class ItemServicesImpl implements ItemService {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    DocksRepository dockRepository;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    LoginUser loginUser;
    @Autowired
    Validations validations;

    @Autowired
    AlternativeItemMapperRepository alternativeItemMapperRepository;
    @Autowired
    BuyerItemMapperRepository buyerItemMapperRepository;
    @Autowired
    SupplierItemMapperRepository supplierItemMapperRepository;

    @Autowired
    StockBalanceRepository stockBalanceRepository;
    @Override
    public BaseResponse<Item> getAllItemWithPagination(Integer pageNo, Integer pageSize){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getAllItemWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEMS LIST FETCHED START");
        BaseResponse<Item> baseResponse = new BaseResponse<>();
        List<Item> itemList=new ArrayList<>();
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            Page<Item> pageResult = this.itemRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId(), pageable);
            baseResponse.setTotalPageCount(pageResult.getTotalPages());
            itemList=pageResult.getContent();
            baseResponse.setTotalRecordCount(pageResult.getTotalElements());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10025S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(itemList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ItemServicesImpl - getAllItemWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10024F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - getAllItemWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getAllItemWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEMS LIST FETCHED TIME" + (endTime - startTime));
        return baseResponse;
    }
    // this method is add item
    @Override
    public BaseResponse<Item>saveItem(ItemRequest itemRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE ITEM METHOD START");
        BaseResponse<Item> baseResponse=new BaseResponse<>();
        List<Item> itemList=new ArrayList<>();
        try{
            Item item = new Item();
            item.setItemId(generateItemId(1));

            item.setName(itemRequest.getItemName());
            item.setDescription(itemRequest.getDescription());
            item.setItemGroup(itemRequest.getItemGroup());
            item.setItemCategory(itemRequest.getItemCategory());
            item.setItemSubcategory(itemRequest.getItemSubcategory());
            item.setTypeDirectIndirect(itemRequest.getTypeDirectIndirect());
            item.setTypeSerialBatchNone(itemRequest.getTypeSerialBatchNone());
            item.setIssueType(itemRequest.getIssueType());
            item.setClassABC(itemRequest.getClassABC());
            item.setAttribute(itemRequest.getAttribute());
            item.setSource(itemRequest.getSource());
            item.setUom(itemRequest.getUom());
            item.setItemUnitWeight(itemRequest.getItemUnitWeight());
            item.setContainerCapacity(itemRequest.getContainerCapacity());
            item.setContainerCapacityUom(itemRequest.getContainerCapacityUom());
            item.setPhysicalForm(itemRequest.getPhysicalForm());
            item.setItemUnitRate(itemRequest.getItemUnitRate());
            item.setCurrency(itemRequest.getCurrency());
            item.setOptimumLevel(itemRequest.getOptimumLevel());
            item.setReorderLevel(itemRequest.getReorderLevel());
            item.setSafetyStockLevel(itemRequest.getSafetyStockLevel());
            item.setCriticalLevel(itemRequest.getCriticalLevel());
            item.setIsActive(true);
            item.setIsDeleted(false);
            item.setSubOrganizationId(loginUser.getSubOrgId());
            item.setOrganizationId(loginUser.getOrgId());
            item.setCreatedBy(loginUser.getUserId()); // Assuming 1 is the default value
            item.setCreatedOn(new Date()); // Assuming current date/time
            item.setModifiedBy(loginUser.getUserId()); // Assuming no modification initially
            item.setModifiedOn(new Date()); // Assuming no modification initially
            item.setItemCode(itemRequest.getItemCode());
            item.setQcRequired(itemRequest.isQcRequired());
            item.setInspection(itemRequest.getInspection());

            // Set Dock
            Optional<Dock> optionalDock = dockRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),itemRequest.getDockId());
            if (optionalDock.isPresent()) {
                item.setDockId(optionalDock.get());
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10014E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
                return baseResponse;
            }
            itemRepository.save(item);
            log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ADD ITEM SUCCESSFULLY ");
            StockBalance stockBalance=new StockBalance();
            stockBalance.setItemId(item);
            stockBalance.setBalanceQuantity(0);
            stockBalance.setCreatedBy(loginUser.getUserId());
            stockBalance.setCreatedOn(new Date());
            stockBalance.setModifiedBy(loginUser.getUserId());
            stockBalance.setModifiedOn(new Date());
            stockBalance.setOrganizationId(loginUser.getOrgId());
            stockBalance.setSubOrganizationId(loginUser.getSubOrgId());
            stockBalanceRepository.save(stockBalance);
            log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ADD STOCK BALANCE ACCORDING TO ITEM SUCCESSFULLY ");
            //this if condition check the alternative and mapped
            if(itemRequest.getAlternativeItemId()!=null){
                item.setAlternativeItem(true);
                Optional<Item> optionalItem=itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),itemRequest.getAlternativeItemId());
                if(optionalItem.isPresent()){
                    AlternateItemMapper alternateItemMapper=new AlternateItemMapper();
                    alternateItemMapper.setAlternateItemId(optionalItem.get());
                    alternateItemMapper.setItem(item);
                    alternateItemMapper.setIsDeleted(false);
                    alternateItemMapper.setOrganizationId(loginUser.getOrgId());
                    alternateItemMapper.setSubOrganizationId(loginUser.getSubOrgId());
                    alternateItemMapper.setCreatedBy(loginUser.getUserId());
                    alternateItemMapper.setCreatedOn(new Date());
                    alternateItemMapper.setModifiedBy(loginUser.getUserId());
                    alternateItemMapper.setModifiedOn(new Date());
                    alternativeItemMapperRepository.save(alternateItemMapper);

                    AlternateItemMapper alternateItemMappers=new AlternateItemMapper();
                    alternateItemMappers.setAlternateItemId(item);
                    alternateItemMappers.setItem(optionalItem.get());
                    alternateItemMappers.setIsDeleted(false);
                    alternateItemMappers.setOrganizationId(loginUser.getOrgId());
                    alternateItemMappers.setSubOrganizationId(loginUser.getSubOrgId());
                    alternateItemMappers.setCreatedBy(loginUser.getUserId());
                    alternateItemMappers.setCreatedOn(new Date());
                    alternateItemMappers.setModifiedBy(loginUser.getUserId());
                    alternateItemMappers.setModifiedOn(new Date());
                    alternativeItemMapperRepository.save(alternateItemMappers);
                    optionalItem.get().setAlternativeItem(true);
                    itemRepository.save(optionalItem.get());
                    log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ADD ALTERNATIVE ITEM SUCCESSFULLY ");
                }else{
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10015E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    long endTime = System.currentTimeMillis();
                    log.error("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime));
                    return baseResponse;
                }
            }
            ContainerRequest containerRequest=itemRequest.getContainerRequest();
            Container container = new Container();
            container.setCode(containerRequest.getCode());
            container.setType(containerRequest.getType());
            container.setDimensionUOM(containerRequest.getDimensionUOM());
            container.setWidth(containerRequest.getWidth());
            container.setHeight(containerRequest.getHeight());
            container.setLength(containerRequest.getLength());
            container.setCircumference(containerRequest.getCircumference());
            container.setWeight(containerRequest.getWeight());
            container.setItemQty(containerRequest.getItemQty());
            container.setMinimumOrderQty(containerRequest.getMinimumOrderQty());
            container.setIsActive(true);
            container.setIsDeleted(false);
            container.setOrganizationId(loginUser.getOrgId());
            container.setSubOrganizationId(loginUser.getSubOrgId());
            container.setCreatedBy(loginUser.getUserId()); // Assuming 1 is the default value
            container.setCreatedOn(new Date()); // Assuming current date/time
            container.setModifiedBy(loginUser.getUserId()); // Assuming no modification initially
            container.setModifiedOn(new Date()); // Assuming no modification initially
            container.setItem(item);
            containerRepository.save(container);

            itemList.add(item);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10026S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(itemList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10025F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE ITEM EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Item>deleteItemById(Integer itemId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - deleteItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  ITEM DELETED START");
        BaseResponse<Item> baseResponse=new BaseResponse<>();
        List<Item> itemList=new ArrayList<>();
        try{
            Optional<Item> item=itemRepository.findByIsDeletedAndId(false,itemId);

            BuyerItemMapper buyerItemMapper = buyerItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndItemId(loginUser.getSubOrgId(), false, itemId);
            SupplierItemMapper supplierItemMapper = supplierItemMapperRepository.findByIsDeletedAndSubOrganizationIdAndItemId(false, loginUser.getSubOrgId(), itemId);
            if(buyerItemMapper != null) {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10141F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(itemList);
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            if(supplierItemMapper != null) {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10142F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(itemList);
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            item.get().setIsDeleted(true);
            itemRepository.save(item.get());
            itemList.add(item.get());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10027S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(itemList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ItemServicesImpl - deleteItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10026F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - deleteItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - deleteItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEMS DELETED TIME" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Item> getItemById(Integer itemId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEMS LIST FETCHED START");
        BaseResponse<Item> baseResponse=new BaseResponse<>();
        try{
            Optional<Item> optionalItem = itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),itemId);

            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                Container container = containerRepository.findByIsDeletedAndSubOrganizationIdAndItemId(false,loginUser.getSubOrgId(), item.getId());
                item.setContainer(container);
                AlternateItemMapper alternateItemMapper=alternativeItemMapperRepository.findByIsDeletedAndSubOrganizationIdAndItemId(false,loginUser.getSubOrgId(),itemId);
                if(alternateItemMapper!=null) {
                    item.setAlternativeItemId(alternateItemMapper.getAlternateItemId().getId());
                    item.setAlternativeItemName(alternateItemMapper.getAlternateItemId().getName());
                }
                List<Item> itemList = new ArrayList<>();
                itemList.add(item);
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10028S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(itemList);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - ItemServicesImpl - getItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            } else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10016E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                log.error("LogId:{} - ItemServicesImpl - getItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
                return baseResponse;
            }

        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10027F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - getItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEMS FETCHED TIME" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Item>updateItem(Integer itemId, ItemRequest itemRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - updateItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE ITEM METHOD START ");
        BaseResponse<Item> baseResponse=new BaseResponse<>();
        List<Item> itemList=new ArrayList<>();
        try{
            Optional<Item> item = itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),itemId);

            item.get().setName(itemRequest.getItemName());
            item.get().setDescription(itemRequest.getDescription());
            item.get().setItemGroup(itemRequest.getItemGroup());
            item.get().setItemCategory(itemRequest.getItemCategory());
            item.get().setItemSubcategory(itemRequest.getItemSubcategory());
            item.get().setTypeDirectIndirect(itemRequest.getTypeDirectIndirect());
            item.get().setTypeSerialBatchNone(itemRequest.getTypeSerialBatchNone());
            item.get().setIssueType(itemRequest.getIssueType());
            item.get().setClassABC(itemRequest.getClassABC());
            item.get().setAttribute(itemRequest.getAttribute());
            item.get().setSource(itemRequest.getSource());
            item.get().setUom(itemRequest.getUom());
            item.get().setItemUnitWeight(itemRequest.getItemUnitWeight());
            item.get().setPhysicalForm(itemRequest.getPhysicalForm());
            item.get().setContainerCapacityUom(itemRequest.getContainerCapacityUom());
            item.get().setContainerCapacity(itemRequest.getContainerCapacity());
            item.get().setItemUnitRate(itemRequest.getItemUnitRate());
            item.get().setCurrency(itemRequest.getCurrency());
            item.get().setOptimumLevel(itemRequest.getOptimumLevel());
            item.get().setReorderLevel(itemRequest.getReorderLevel());
            item.get().setSafetyStockLevel(itemRequest.getSafetyStockLevel());
            item.get().setCriticalLevel(itemRequest.getCriticalLevel());
            item.get().setIsActive(true);
            item.get().setSubOrganizationId(loginUser.getSubOrgId());
            item.get().setOrganizationId(loginUser.getOrgId());
            item.get().setIsDeleted(false);
            item.get().setModifiedBy(null); // Assuming no modification initially
            item.get().setModifiedOn(null); // Assuming no modification initially

//            item.get().setAlternativeItem(itemRequest.getAlternativeI);
            item.get().setItemCode(itemRequest.getItemCode());
            item.get().setQcRequired(itemRequest.isQcRequired());
            item.get().setInspection(itemRequest.getInspection());
            // Set Dock
            Optional<Dock> optionalDock = dockRepository.findById(itemRequest.getDockId());
            if (optionalDock.isPresent()) {
                item.get().setDockId(optionalDock.get());
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10018E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(itemList);
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            itemRepository.save(item.get());
            log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ADD ITEM SUCCESSFULLY ");
//            StockBalance stockBalance=new StockBalance();
//            stockBalance.setItemId(item.get());
//            stockBalance.setBalanceQuantity(0);
//            stockBalance.setCreatedBy(loginUser.getUserId());
//            stockBalance.setCreatedOn(new Date());
//            stockBalance.setModifiedBy(loginUser.getUserId());
//            stockBalance.setModifiedOn(new Date());
//            stockBalance.setOrganizationId(loginUser.getOrgId());
//            stockBalance.setSubOrganizationId(loginUser.getSubOrgId());
//            stockBalanceRepository.save(stockBalance);
            log.info("LogId:{} - ItemServicesImpl - saveItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ADD STOCK BALANCE ACCORDING TO ITEM SUCCESSFULLY ");
            if(itemRequest.getAlternativeItemId()!=null){
                item.get().setAlternativeItem(true);
                Optional<Item> optionalItem=itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),itemRequest.getAlternativeItemId());
                if(optionalItem.isPresent()){
                    AlternateItemMapper alternateItemMapper=new AlternateItemMapper();
                    alternateItemMapper.setAlternateItemId(optionalItem.get());
                    alternateItemMapper.setItem(item.get());
                    alternateItemMapper.setIsDeleted(false);
                    alternateItemMapper.setOrganizationId(loginUser.getOrgId());
                    alternateItemMapper.setSubOrganizationId(loginUser.getSubOrgId());
                    alternateItemMapper.setCreatedBy(loginUser.getUserId());
                    alternateItemMapper.setCreatedOn(new Date());
                    alternateItemMapper.setModifiedBy(loginUser.getUserId());
                    alternateItemMapper.setModifiedOn(new Date());
                    alternativeItemMapperRepository.save(alternateItemMapper);

                    AlternateItemMapper alternateItemMappers=new AlternateItemMapper();
                    alternateItemMappers.setAlternateItemId(item.get());
                    alternateItemMappers.setItem(optionalItem.get());
                    alternateItemMappers.setIsDeleted(false);
                    alternateItemMappers.setOrganizationId(loginUser.getOrgId());
                    alternateItemMappers.setSubOrganizationId(loginUser.getSubOrgId());
                    alternateItemMappers.setCreatedBy(loginUser.getUserId());
                    alternateItemMappers.setCreatedOn(new Date());
                    alternateItemMappers.setModifiedBy(loginUser.getUserId());
                    alternateItemMappers.setModifiedOn(new Date());
                    alternativeItemMapperRepository.save(alternateItemMappers);
                    log.info("LogId:{} - ItemServicesImpl - updateItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ADD ALTERNATIVE ITEM SUCCESSFULLY ");
                }else{
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10019E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    long endTime = System.currentTimeMillis();
                    log.error("LogId:{} - ItemServicesImpl - updateItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
                    return baseResponse;
                }
            }
            ContainerRequest containerRequest=itemRequest.getContainerRequest();
            Container container = containerRepository.findByIsDeletedAndSubOrganizationIdAndItemId(false,loginUser.getSubOrgId(),item.get().getId());
            container.setCode(containerRequest.getCode());
            container.setType(containerRequest.getType());
            container.setDimensionUOM(containerRequest.getDimensionUOM());
            container.setWidth(containerRequest.getWidth());
            container.setHeight(containerRequest.getHeight());
            container.setLength(containerRequest.getLength());
            container.setCircumference(containerRequest.getCircumference());
            container.setWeight(containerRequest.getWeight());
            container.setItemQty(containerRequest.getItemQty());
            container.setMinimumOrderQty(containerRequest.getMinimumOrderQty());
            container.setIsActive(true);
            container.setOrganizationId(loginUser.getOrgId());
            container.setSubOrganizationId(loginUser.getSubOrgId());
            container.setIsDeleted(false);
            container.setCreatedBy(loginUser.getUserId()); // Assuming 1 is the default value
            container.setCreatedOn(new Date()); // Assuming current date/time
            container.setModifiedBy(loginUser.getUserId()); // Assuming no modification initially
            container.setModifiedOn(new Date()); // Assuming no modification initially
            container.setItem(item.get());
            containerRepository.save(container);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10029S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(itemList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ItemServicesImpl - updateItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10028F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - updateItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - updateItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEM UPDATE METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Item> getAllItem(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getAllItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"ITEM LIST FETCHED METHOD START ");
        BaseResponse<Item> baseResponse=new BaseResponse<>();
        try{
            List<Item> itemList=itemRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10030S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(itemList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ItemServicesImpl - getAllItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10029F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - getAllItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getAllItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEM LIST FETCHED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Item> getAllAlternativeItem(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getAllAlternativeItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL ALTERNATIVE ITEM METHOD START ");
        BaseResponse<Item> baseResponse=new BaseResponse<>();
        try{
            List<Item> itemList=itemRepository.findByIsDeletedAndSubOrganizationIdAndAlternativeItem(false,loginUser.getSubOrgId(),false);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10031S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(itemList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ItemServicesImpl - getAllAlternativeItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10030F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - getAllAlternativeItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - getAllAlternativeItem - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ALTERNATIVE ITEM LIST METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse searchItems(Integer pageNumber, Integer pageSize, List<String> name, List<String> itemGroup, List<String> itemCategory, List<String> issueType, List<String> classABC, Date startDate,Date endDate) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - searchItems - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SEARCH ITEM START");
        BaseResponse<Item> response = new BaseResponse<>();

        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Specification<Item> specification = ItemSpecifications.withFilters(loginUser.getSubOrgId(),name, itemGroup, itemCategory, issueType, classABC,true, false);

            Page<Item> itemPage = itemRepository.findAll(specification, pageable);

            List<Item> itemList = itemPage.getContent().stream()
                    .peek(item -> item.setContainer(containerRepository.findByIsDeletedAndItemId(false, item.getId())))
                    .collect(Collectors.toList());
            response.setData(itemList);
            response.setTotalRecordCount(itemPage.getTotalElements());
            response.setTotalPageCount(itemPage.getTotalPages());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10032S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - ItemServicesImpl - searchItems - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10031F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(Collections.emptyList());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ItemServicesImpl - searchItems - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);
            response.setLogId(loginUser.getLogId());
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ItemServicesImpl - searchItems - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"  SEARCH ITEM TIME" + (endTime - startTime));
        return response;
    }

    @Override
    public String generateItemId(Integer count) {
        String itemId = null;
        List<Item> items=itemRepository.findBySubOrganizationIdOrderByIdAsc(loginUser.getSubOrgId());
        if(items!=null){
            itemId = String.format("%s-ITM%06d", loginUser.getSubOrganizationCode(), items.size()+count);
        }else {
            itemId = String.format("%s-ITM%06d", loginUser.getSubOrganizationCode(),count);
        }

        return itemId;
    }

}
