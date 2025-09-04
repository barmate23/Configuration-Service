package com.stockmanagementsystem.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.stockmanagementsystem.entity.*;
import com.itextpdf.text.pdf.PdfWriter;

import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.StoreRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.CreateYearResponse;
import com.stockmanagementsystem.response.StoreResponse;
import com.stockmanagementsystem.utils.BarcodeGenerator;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.validation.Validations;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Service
@Slf4j
public class StoreServiceImpl implements StoreService{

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    LoginUser loginUser;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StoreKeeperMapperRepository storeKeeperMapperRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    Validations validations;

    @Autowired
    UserLicenseKeyRepository userLicenseKeyRepository;
    @Autowired
    AreaServices areaServices;

    @Autowired
    StoreNameRepository storeNameRepository;

    @Autowired
    ModuleUserLicenceKeyRepository moduleUserLicenceKeyRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    PurchaseOrderHeadRepository purchaseOrderHeadRepository;
    @Autowired
    BomHeadRepository bomHeadRepository;
    @Autowired
    DocksRepository docksRepository;

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ZoneRepository zoneRepository;



    @Override
    @Transactional
    public BaseResponse<Store> saveStore(StoreRequest storeRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE STORE METHOD START");
        BaseResponse<Store> baseResponse=new BaseResponse<>();
        try{
            Store store =new Store();
            List<ModuleUserLicenceKey> moduleUserLicenceKeyList =userLicenseKeyRepository.findByIsDeletedAndIsUsedAndLicenceLineSubModuleSubModuleCodeAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationId(false, false, "STOR", 3, loginUser.getSubOrgId());
            List<Store>storeList=storeRepository.findByIsDeletedAndOrganizationId(false,loginUser.getSubOrgId());

            if(moduleUserLicenceKeyList.size()>storeList.size()){
            store.setStoreId(validations.storeIdGeneration(storeRequest.getStoreName()));
            if(validations.isDuplicateStoreName(storeRequest.getStoreName(),null)){
                store.setStoreName(storeRequest.getStoreName().trim().replaceAll(" +", " "));
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10001E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.info("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime));
                return baseResponse;
            }
            if(validations.isDuplicateStoreName(null,storeRequest.getErpStoreId())){
                store.setErpStoreId(storeRequest.getErpStoreId());
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10002E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.info("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime));
                return baseResponse;
            }
            store.setSubOrganizationId(loginUser.getSubOrgId());
            store.setOrganizationId(loginUser.getOrgId());
            store.setCreatedBy(loginUser.getUserId());
            store.setCreatedOn(new Date());
            store.setIsActive(true);
            store.setIsDeleted(false);
            storeRepository.save(store);
            StoreName storeName=storeNameRepository.findByIsDeletedAndIsUsedAndSubOrganizationIdAndStoreName(false,false,loginUser.getSubOrgId(),storeRequest.getStoreName());
            storeName.setIsUsed(true);
            storeNameRepository.save(storeName);
            List<Store> stores=new ArrayList<>();
             stores.add(store);

                List<String> fullName=new ArrayList<>();
                for (String user:storeRequest.getStoreManagerName()) {
                    Optional<Users> users= userRepository.findByIsDeletedAndUsernameAndSubOrganizationId(false,user,loginUser.getSubOrgId());
                    fullName.add(users.get().getFirstName()+" "+users.get().getLastName());
                    StoreKeeperMapper storeKeeperMapper =new StoreKeeperMapper();
                    storeKeeperMapper.setStore(store);
                    storeKeeperMapper.setStoreKeeper(users.get());
                    storeKeeperMapper.setOrganizationId(loginUser.getOrgId());
                    storeKeeperMapper.setSubOrganizationId(loginUser.getSubOrgId());
                    storeKeeperMapper.setIsDeleted(false);
                    storeKeeperMapper.setCreatedBy(loginUser.getUserId());
                    storeKeeperMapper.setCreatedOn(new Date());
                    storeKeeperMapper.setModifiedBy(loginUser.getUserId());
                    storeKeeperMapper.setModifiedOn(new Date());
                    storeKeeperMapperRepository.save(storeKeeperMapper);
                }
                store.setStoreManagerName(String.join(",", fullName));
                storeRepository.save(store);

            log.info("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE KEEPER MAPPED SUCCESSFULLY :");
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10001S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(stores);
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.info("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime));
                Integer additionalArea=0;
                if(storeRequest.getAdditionalAreaLicenceKeyId()!=null && storeRequest.getAdditionalAreaLicenceKeyId().size()!=0){
                   additionalArea=storeRequest.getAdditionalAreaLicenceKeyId().size();
                    for (Integer i:storeRequest.getAdditionalAreaLicenceKeyId()) {
                        ModuleUserLicenceKey moduleUserLicenceKey=moduleUserLicenceKeyRepository.findByIsDeletedAndId(false,i);
                        moduleUserLicenceKey.setIsUsed(true);
                        moduleUserLicenceKeyRepository.save(moduleUserLicenceKey);
                    }
                }
                for (Integer i = 1; i<=(4+additionalArea); i++) {
                   areaServices.createArea(store,i);

                }
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10003E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.error("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime));
            }
        }catch (Exception e){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10001F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
        }
        return baseResponse;
    }
    @Override
    public BaseResponse<Store> updateStore(Integer storeId, StoreRequest storeRequest){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - updateStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE STORE METHOD START");
        BaseResponse<Store> baseResponse=new BaseResponse<>();
        try{
            Optional<Store> store =storeRepository.findByIsDeletedAndIdAndSubOrganizationId(false,storeId,loginUser.getSubOrgId());
            if(storeRequest.getStoreName().equalsIgnoreCase(store.get().getStoreName())){
                store.get().setStoreName(storeRequest.getStoreName());
            }else {
                if(validations.isDuplicateStoreName(storeRequest.getStoreName(),null)){
                    store.get().setStoreName(storeRequest.getStoreName().trim().replaceAll(" +", " "));

                }else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10004E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    long endTime = System.currentTimeMillis();
                    log.info("LogId:{} - StoreServiceImpl - updateStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()  + (endTime - startTime));
                    return baseResponse;
                }
            }
            if(storeRequest.getErpStoreId().equals(store.get().getErpStoreId())){
                store.get().setErpStoreId(storeRequest.getErpStoreId());
            }else {
                if(validations.isDuplicateStoreName(null,storeRequest.getErpStoreId())){
                    store.get().setErpStoreId(storeRequest.getErpStoreId());

                }else {
                    ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10005E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setData(new ArrayList<>());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    long endTime = System.currentTimeMillis();
                    log.info("LogId:{} - StoreServiceImpl - updateStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime));
                    return baseResponse;
                }
            }
            store.get().setStoreManagerName(String.join(",", storeRequest.getStoreManagerName()));
            store.get().setSubOrganizationId(loginUser.getSubOrgId());
            store.get().setOrganizationId(loginUser.getOrgId());
            store.get().setModifiedBy(loginUser.getUserId());
            store.get().setModifiedOn(new Date());
            store.get().setIsActive(true);
            store.get().setIsDeleted(false);
            storeRepository.save(store.get());
            List<Store> stores=new ArrayList<>();
            stores.add(store.get());
            for (String user:storeRequest.getStoreManagerName()) {
                Optional<Users> users= userRepository.findByIsDeletedAndUsernameAndSubOrganizationId(false,user,loginUser.getSubOrgId());
                StoreKeeperMapper storeKeeperMapper =new StoreKeeperMapper();
                storeKeeperMapper.setStore(store.get());
                storeKeeperMapper.setStoreKeeper(users.get());
                storeKeeperMapper.setOrganizationId(loginUser.getOrgId());
                storeKeeperMapper.setSubOrganizationId(loginUser.getSubOrgId());
                storeKeeperMapper.setIsDeleted(false);
                storeKeeperMapper.setCreatedBy(loginUser.getUserId());
                storeKeeperMapper.setCreatedOn(new Date());
                storeKeeperMapper.setModifiedBy(loginUser.getUserId());
                storeKeeperMapper.setModifiedOn(new Date());
                storeKeeperMapperRepository.save(storeKeeperMapper);
            }
            log.info("LogId:{} - StoreServiceImpl - saveStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE KEEPER MAPPED SUCCESSFULLY :");

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10002S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(stores);
            baseResponse.setLogId(loginUser.getLogId());
            Integer additionalArea=0;
            if(storeRequest.getAdditionalAreaLicenceKeyId()!=null && storeRequest.getAdditionalAreaLicenceKeyId().size()!=0){
                additionalArea=storeRequest.getAdditionalAreaLicenceKeyId().size();
                for (Integer i:storeRequest.getAdditionalAreaLicenceKeyId()) {
                    ModuleUserLicenceKey moduleUserLicenceKey=moduleUserLicenceKeyRepository.findByIsDeletedAndId(false,i);
                    moduleUserLicenceKey.setIsUsed(true);
                    moduleUserLicenceKeyRepository.save(moduleUserLicenceKey);
                }
            }
            List<Area> areas=areaRepository.findByIsDeletedAndSubOrganizationIdAndStoreId(false,loginUser.getSubOrgId(),storeId);
            for (Integer i = areas.size()+1; i<=areas.size()+additionalArea; i++) {
                areaServices.createArea(store.get(),i);
            }
        }catch (Exception e){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10002F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - updateStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - updateStore - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE SAVED SUCCESSFULLY :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<StoreResponse> getStoresWithPagination(Integer pageNo, Integer pageSize){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getStoresWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET STORE WITH PAGINATION METHOD START");
        BaseResponse<StoreResponse> baseResponse=new BaseResponse<>();
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            Page<Store> pageResult = this.storeRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId(),pageable);
            final StoreResponse storeResponse = new StoreResponse();
            storeResponse.setPageCount(pageResult.getTotalPages());
            storeResponse.setStores(pageResult.getContent());
            storeResponse.setRecordCount(pageResult.getTotalElements());
            List<StoreResponse> storeResponseList=new ArrayList<>();
            storeResponseList.add(storeResponse);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10003S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(storeResponseList);
            baseResponse.setLogId(loginUser.getLogId());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10003F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - getStoresWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()  + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getStoresWithPagination - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE LIST FETCHED SUCCESSFULLY :" + (endTime - startTime));
       return baseResponse;
    }

    @Override
    public BaseResponse<StoreResponse> getStoreListByERPStoreId(Integer pageNo, Integer pageSize, List<Integer> storeId,List<String> erpStoreId,Date startDate, Date endDate){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getStoreListByERPStoreId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET STORE LIST BY ERP STORE ID METHOD START");
        BaseResponse<StoreResponse> baseResponse=new BaseResponse<>();
        try {
            final Pageable pageable = (Pageable) PageRequest.of((int) pageNo, (int) pageSize);
            Page<Store> pageResult =null;
            if(storeId!=null && erpStoreId!=null){
                pageResult=this.storeRepository.findByIsDeletedAndSubOrganizationIdAndIdInOrErpStoreIdInOrderByIdAsc(false,loginUser.getSubOrgId(),storeId,erpStoreId, pageable);
            }else if (storeId==null && erpStoreId==null){
                pageResult=this.storeRepository.findByIsDeletedAndSubOrganizationIdOrderByIdAsc(false,loginUser.getSubOrgId(), pageable);
            } else if (storeId!=null && erpStoreId==null) {
                pageResult=this.storeRepository.findByIsDeletedAndSubOrganizationIdAndIdInOrderByIdAsc(false,loginUser.getSubOrgId(),storeId, pageable);
            }
            else if (storeId==null && erpStoreId!=null) {
                pageResult=this.storeRepository.findByIsDeletedAndSubOrganizationIdAndErpStoreIdInOrderByIdAsc(false,loginUser.getSubOrgId(),erpStoreId, pageable);
            }
//            else if (createYear!=null && createYear.size()!=0) {
////                pageResult=this.storeRepository.findByIsDeletedAndSubOrganizationIdAnd
//            }
            final StoreResponse storeResponse = new StoreResponse();
            storeResponse.setPageCount(pageResult.getTotalPages());
            storeResponse.setStores(pageResult.getContent());
            storeResponse.setRecordCount(pageResult.getTotalElements());
            List<StoreResponse> storeResponseList=new ArrayList<>();
            storeResponseList.add(storeResponse);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10004S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(storeResponseList);
            baseResponse.setLogId(loginUser.getLogId());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10004F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - getStoreListByERPStoreId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()  + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getStoreListByERPStoreId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE LIST FETCHED SUCCESSFULLY TIME :" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<Store> getAllStores(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAllStores - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STORE METHOD START");
        BaseResponse<Store> baseResponse=new BaseResponse<>();
        try {
            List<Store> stores=storeRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10005S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(stores);
            baseResponse.setLogId(loginUser.getLogId());
        }catch (Exception ex){

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10005F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - getAllStores - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()  + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAllStores - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE LIST FETCHED SUCCESSFULLY TIME :" + (endTime - startTime));
        return baseResponse;
    }

//    @Override
//    public BaseResponse<Store> getAllStoresAcceptItemAssignInLocation(){
//        long startTime = System.currentTimeMillis();
//        log.info("LogId:{} - StoreServiceImpl - getAllStores - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STORE METHOD START");
//        BaseResponse<Store> baseResponse=new BaseResponse<>();
//        try {
//            List<Store> stores=new ArrayList<>();
//            List<Location>locations=locationRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId()).stream().f;
//            List<Location> location=locations.stream().filter(l->l.getItem()!=null).collect(Collectors.toList());
//            for (Location loc:location) {
//                stores.add(loc.getZone().getArea().getStore());
//            }
//            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10005S);
//            baseResponse.setCode(responseMessage.getCode());
//            baseResponse.setStatus(responseMessage.getStatus());
//            baseResponse.setMessage(responseMessage.getMessage());
//            baseResponse.setData(stores);
//            baseResponse.setLogId(loginUser.getLogId());
//        }catch (Exception ex){
//
//            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10005F);
//            baseResponse.setCode(responseMessage.getCode());
//            baseResponse.setStatus(responseMessage.getStatus());
//            baseResponse.setMessage(responseMessage.getMessage());
//            baseResponse.setLogId(loginUser.getLogId());
//            long endTime = System.currentTimeMillis();
//            log.error("LogId:{} - StoreServiceImpl - getAllStores - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()  + (endTime - startTime),ex);
//        }
//        long endTime = System.currentTimeMillis();
//        log.info("LogId:{} - StoreServiceImpl - getAllStores - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE LIST FETCHED SUCCESSFULLY TIME :" + (endTime - startTime));
//        return baseResponse;
//    }

    @Override
    public BaseResponse<StoreName> getAllStoresName(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAllStoresName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STORE METHOD START");
        BaseResponse<StoreName> baseResponse=new BaseResponse<>();
        try {
            List<StoreName> stores=storeNameRepository.findByIsDeletedAndIsUsedAndSubOrganizationId(false,false,loginUser.getSubOrgId());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10006S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(stores);
            baseResponse.setLogId(loginUser.getLogId());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10006F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - getAllStoresName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()  + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAllStoresName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE LIST FETCHED SUCCESSFULLY TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Store> deleteStoreById(Integer storeId){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - deleteStoreById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE STORE BY ID METHOD START");
        BaseResponse<Store> baseResponse=new BaseResponse<>();
        try {
            Optional<Store> store=storeRepository.findByIsDeletedAndIdAndSubOrganizationId(false,storeId,loginUser.getSubOrgId());
            List<Area> areas=areaRepository.findByIsDeletedAndStoreId(false,storeId);
            if(areas!=null && areas.size()==0){
                store.get().setIsDeleted(true);
                List<Store> stores=new ArrayList<>();
                stores.add(store.get());
                storeRepository.save(store.get());

                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10007S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(stores);
                baseResponse.setLogId(loginUser.getLogId());
            }else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10006E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                long endTime = System.currentTimeMillis();
                log.info("LogId:{} - StoreServiceImpl - deleteStoreById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime));
            }
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10007F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - deleteStoreById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - deleteStoreById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE DELETED SUCCESSFULLY :" + (endTime - startTime));
        return baseResponse;
    }


    @Override
    public byte[] generateStoreBarcodePDF() {
        try {
            long startTime = System.currentTimeMillis();
            log.info(loginUser.getLogId() + " DOWNLOAD DOCKS BARCODE PDF");

            List<Store> stores = storeRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(
                    loginUser.getOrgId(), loginUser.getSubOrgId(), false
            );

            // Define the document size as A5
            Document document = new Document(PageSize.A5);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            for (String id : stores.stream().map(s -> s.getStoreId()).collect(Collectors.toList())) {
                // The content to encode in the barcode, e.g., "ORG0000001-S001-RM01"

                // Generate the barcode image
                byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(id);
                com.itextpdf.text.Image barcodeImage = com.itextpdf.text.Image.getInstance(barcodeImageBytes);
                // Add barcode to the document
                document.add(barcodeImage);

                // Add a small space before the text to ensure it's directly below the barcode
                document.add(new Paragraph(" ")); // Adding a space before the text for better separation


                // Move to the next page for the next barcode
                document.newPage();
            }

            log.info(loginUser.getLogId() + " DOWNLOAD Store BARCODE PDF ");
            document.close();

            long endTime = System.currentTimeMillis();
            log.info(loginUser.getLogId() + " SUCCESSFULLY DOWNLOAD DOCKS BARCODE PDF TIME " + (endTime - startTime));
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " FAILED DOWNLOAD DOCK BARCODE PDF", e);
            e.printStackTrace();
            return null;
        }
    }





    @Override
    public BaseResponse<Users> getAllStoreKeeper(){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAllStoreKeeper - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL STORE KEEPER METHOD START");
        BaseResponse<Users> baseResponse=new BaseResponse<>();
        try {
            List<Users> stores=userRepository.findByIsDeletedAndSubOrganizationIdAndModuleUserLicenceKeyLicenceLinePartNumberSubModuleMapperSubModuleSubModuleCode(false,loginUser.getSubOrgId(),"SKPR");
            List<StoreKeeperMapper> storeKeeperMappers=storeKeeperMapperRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId());
//            stores.stream().    reduce(storeKeeperMappers.stream().forEach());

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10008S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(stores);
            baseResponse.setLogId(loginUser.getLogId());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10008F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - getAllStoreKeeper - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAllStoreKeeper - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FAILED TO FETCHED USER LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse getAreaLicense() {
        long startTime = System.currentTimeMillis();
        log.info(loginUser.getLogId() + " GET USERS BY ID METHOD START");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            List<ModuleUserLicenceKey> moduleUserLicenceKeyList =userLicenseKeyRepository.findByIsDeletedAndIsUsedAndLicenceLineSubModuleSubModuleCodeAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationIdAndLicenceLinePartNumberSubModuleMapperPartNumberDefaultAdditional(false, false, "AREA", 3, loginUser.getSubOrgId(),2);

            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10009S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(moduleUserLicenceKeyList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - StoreServiceImpl - getAreaLicense - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10009F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - getAreaLicense - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAreaLicense - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET USER BY ID METHOD EXECUTED TIME:" + (endTime - startTime));
        return baseResponse;
    }
    @Override
    public BaseResponse<CreateYearResponse> getAllYears(String type){
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - CreateYearResponse - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET ALL YEAR METHOD START");
        BaseResponse<CreateYearResponse> baseResponse=new BaseResponse<>();
        List<CreateYearResponse> createYearResponses=new ArrayList<>();
        try {
            if(type.equals("STORE")) {
                List<Store> stores = storeRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = stores.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }else if (type.equals("AREA")){
                List<Area> areas = areaRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = areas.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            else if (type.equals("ZONE")){
                List<Zone> zones = zoneRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = zones.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            else if (type.equals("LOCATION")){
                List<Location> locations = locationRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = locations.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            else if (type.equals("ITEM")){
                List<Item> items = itemRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = items.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            else if (type.equals("DOCK")){
                List<Dock> docks = docksRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = docks.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            else if (type.equals("PURCHASE")){
                List<PurchaseOrderHead> purchaseOrderHeads = purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = purchaseOrderHeads.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            else if (type.equals("BOM")){
                List<BoMHead> boMHeads = bomHeadRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = boMHeads.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            else if (type.equals("SUPLY")){
                List<Supplier> supplierList = supplierRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
                List<Integer> years = supplierList.stream().map(k -> k.getCreatedOn().getYear()).distinct().collect(Collectors.toList());
                for (Integer year : years) {
                    CreateYearResponse createYearResponse = new CreateYearResponse();
                    createYearResponse.setYear((1900 + year - 1) + "-" + (1900 + year));
                    createYearResponse.setStartDate(getStartDate(1900+year - 1));
                    createYearResponse.setEndDate(getEndDate(1900+year));
                    createYearResponses.add(createYearResponse);
                }
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10010S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(createYearResponses);
            baseResponse.setLogId(loginUser.getLogId());
        }catch (Exception ex){
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10010F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - StoreServiceImpl - getAllStoresName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - StoreServiceImpl - getAllStoresName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," STORE LIST FETCHED SUCCESSFULLY TIME :" + (endTime - startTime));
        return baseResponse;
    }

    private static Date getStartDate(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, Calendar.APRIL, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static Date getEndDate(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, Calendar.MARCH, 31, 0, 0, 0);

        return calendar.getTime();
    }
}
