package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.ItemSupplierMapperRequest;
import com.stockmanagementsystem.request.SupplierRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.utils.ServiceConstants;
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
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    LoginUser loginUser;
    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    UserLicenseKeyRepository userLicenseKeyRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    SupplierItemMapperRepository supplierItemMapperRepository;

    @Autowired
    PurchaseOrderHeadRepository purchaseOrderHeadRepository;


    @Override
    public BaseResponse<Supplier> saveSupplier(SupplierRequest supplierRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - saveSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), "START SAVE SUPPLIER METHOD" + (startTime));
        BaseResponse<Supplier> baseResponse = new BaseResponse();
        List<Supplier> suppliers=new ArrayList<>();
        try {

            Supplier existingGSTSupplier = supplierRepository.findBySupplierGSTRegistrationNumberAndIsDeleted(
                    supplierRequest.getSupplierGSTRegistrationNumber(), false);
            List<ModuleUserLicenceKey> moduleUserLicenceKeyList = userLicenseKeyRepository.findByIsDeletedAndLicenceLineSubModuleSubModuleCodeAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationId(false, "ASNS", 1, loginUser.getSubOrgId());
            List<Supplier> supplierList = supplierRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            if (moduleUserLicenceKeyList.size() < supplierList.size()) {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10056E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            if (existingGSTSupplier != null) {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10057E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }

            // Check if a supplier with the same PAN exists
            Supplier existingPANSupplier = supplierRepository.findBySupplierPANNumberAndIsDeleted(
                    supplierRequest.getSupplierPANNumber(), false);

            if (existingPANSupplier != null) {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10058E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }
            Supplier supplier = new Supplier();
            String generatedDockId;
            supplier.setSupplierId(generateSupplierId(1));
            supplier.setAddress1(supplierRequest.getAddress1());
            supplier.setAddress2(supplierRequest.getAddress2());
            supplier.setErpSupplierId(supplierRequest.getErpSupplierId());
            supplier.setAlternateEmail(supplierRequest.getAlternateEmail());
            supplier.setOrganizationId(loginUser.getOrgId());
            supplier.setSubOrganizationId(loginUser.getSubOrgId());
            supplier.setAlternatePhone(supplierRequest.getAlternatePhone());
            supplier.setAreaCode(supplierRequest.getAreaCode());
            supplier.setBuilding(supplierRequest.getBuilding());
            supplier.setCity(supplierRequest.getCity());
            supplier.setContactPersonName(supplierRequest.getContactPersonName());
            supplier.setCountry(supplierRequest.getCountry());
            supplier.setCountryCode(supplierRequest.getCountryCode());
            supplier.setCreditLimitDays(supplierRequest.getCreditLimitDays());
            supplier.setVillage(supplierRequest.getVillage());
            supplier.setTown(supplierRequest.getTown());
            supplier.setTaluka(supplierRequest.getTaluka());
            supplier.setSupplierTANNumber(supplierRequest.getSupplierTANNumber());
            supplier.setSupplierPrimaryBanker(supplierRequest.getSupplierPrimaryBanker());
            supplier.setSupplierPANNumber(supplierRequest.getSupplierPANNumber());
            supplier.setSupplierName(supplierRequest.getSupplierName());
            supplier.setSupplierGSTRegistrationNumber(supplierRequest.getSupplierGSTRegistrationNumber());
            supplier.setSupplierGroup(supplierRequest.getSupplierGroup());
            supplier.setSupplierCategory(supplierRequest.getSupplierCategory());
            supplier.setSubLocality(supplierRequest.getSubLocality());
            supplier.setStreet(supplierRequest.getStreet());
            supplier.setState(supplierRequest.getState());
            supplier.setPrimaryPhone(supplierRequest.getPrimaryPhone());
            supplier.setPrimaryEmail(supplierRequest.getPrimaryEmail());
            supplier.setPostCode(supplierRequest.getPostCode());
            supplier.setBranchCode(supplierRequest.getBranchCode());
            supplier.setPaymentTerms(supplierRequest.getPaymentTerms());
            supplier.setPaymentMethod(supplierRequest.getPaymentMethod());
            supplier.setOtherOrganizationName(supplierRequest.getOtherOrganizationName());
            supplier.setOfficePrimaryPhone(supplierRequest.getOfficePrimaryPhone());
            supplier.setOfficeAlternatePhone(supplierRequest.getOfficeAlternatePhone());
            supplier.setMicrCode(supplierRequest.getMicrCode());
            supplier.setLongitude(supplierRequest.getLongitude());
            supplier.setLandmark(supplierRequest.getLandmark());
            supplier.setLocality(supplierRequest.getLocality());
            supplier.setIfscCode(supplierRequest.getIfscCode());
            supplier.setFullBranchAddress(supplierRequest.getFullBranchAddress());
            supplier.setDistrict(supplierRequest.getDistrict());
            supplier.setDesignation(supplierRequest.getDesignation());
            supplier.setDepartment(supplierRequest.getDepartment());
            supplier.setLatitude(supplierRequest.getLatitude());
            supplier.setDateOfRegistration(supplierRequest.getDateOfRegistration());
            supplier.setCreditLimitRs(supplierRequest.getCreditLimitRs());
            supplier.setIsDeleted(false);
            supplier.setCreatedBy(loginUser.getUserId());
            supplier.setCreatedOn(new Date());
            supplierRepository.save(supplier);
            suppliers.add(supplier);

//            List<SupplierItemMapper> supplierItemMapperList = new ArrayList<>();
//            for (Integer items : supplierRequest.getItemId()) {
//                Optional<Item> itemList = itemRepository.findByIsDeletedAndId(false, items);
//                SupplierItemMapper supplierItemMapper = new SupplierItemMapper();
//                supplierItemMapper.setSupplier(save);
//                supplierItemMapper.setItem(itemList.get());
//                supplierItemMapper.setOrganizationId(loginUser.getOrgId());
//                supplierItemMapper.setSubOrganizationId(loginUser.getSubOrgId());
//                supplierItemMapper.setCreatedBy(loginUser.getUserId());
//                supplierItemMapper.setCreatedOn(new Date());
//                supplierItemMapper.setIsDeleted(false);
//                supplierItemMapperList.add(supplierItemMapper);
//            }
//            List<SupplierItemMapper> savedSupplierItemMapperList = supplierItemMapperRepository.saveAll(supplierItemMapperList);
//            SupplierResponse supplierResponse = supplierEntityToSupplierResponse(save);
//            List<Object> responseData = new ArrayList<>();
//            responseData.add(supplier);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10093S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(suppliers);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - saveSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10092F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - saveSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - saveSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SAVE SUPPLIER TIME :" + (endTime - startTime));
        return baseResponse;
    }

    public String generateSupplierId(Integer count) {
        List<Supplier> suppliers=supplierRepository.findBySubOrganizationIdOrderByIdAsc(loginUser.getSubOrgId());
        if(suppliers!=null){
            return String.format("%s-SPLY%06d", loginUser.getSubOrganizationCode(), suppliers.size()+count);
        }else {
            return String.format("%s-SPLY%06d", loginUser.getSubOrganizationCode(), count);
        }

    }

    @Override
    public BaseResponse deleteBySupplierId(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - deleteBySupplierId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE SUPPLIER START");
        BaseResponse baseResponse = new BaseResponse();
        try {
            Supplier suppliers = supplierRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(), false, id);
            List<PurchaseOrderHead> purchaseOrderHeads = purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationIdAndSupplierId(false, loginUser.getSubOrgId(), suppliers.getSupplierId());
            if(!purchaseOrderHeads.isEmpty()){
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10138F);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setData(new ArrayList<>());
                return baseResponse;
            }
            suppliers.setIsDeleted(true);
            supplierRepository.save(suppliers);

            List<SupplierItemMapper> supplierItemMappers = supplierItemMapperRepository.findAllByIsDeletedAndSupplier_Id(false, suppliers.getId());
            for (SupplierItemMapper mapper : supplierItemMappers) {
                mapper.setIsDeleted(true);
            }
            supplierItemMapperRepository.saveAll(supplierItemMappers);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10094S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(new ArrayList<>());
            log.info("LogId:{} - SupplierServiceImpl - deleteBySupplierId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10093F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(new ArrayList<>());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - deleteBySupplierId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - deleteBySupplierId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," DELETE SUPPLIER TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Supplier> updateSupplier(Integer id, SupplierRequest supplierRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - updateSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE SUPPLIER START");
        BaseResponse<Supplier> baseResponse = new BaseResponse();
        try {
            Optional<Supplier> optionalDocks = supplierRepository.findById(id);
            if (optionalDocks.isPresent()) {
                Supplier supplier = optionalDocks.get();

                Supplier duplicateGSTSupplier = supplierRepository.findBySupplierGSTRegistrationNumberAndIdNotAndIsDeletedAndSubOrganizationId(
                        supplierRequest.getSupplierGSTRegistrationNumber(), id, false,loginUser.getSubOrgId());
                Supplier duplicatePANSupplier = supplierRepository.findBySupplierPANNumberAndIdNotAndIsDeletedAndSubOrganizationId(
                        supplierRequest.getSupplierPANNumber(), id, false,loginUser.getSubOrgId());

                if (duplicateGSTSupplier != null || duplicatePANSupplier != null) {
                    baseResponse.setData(Collections.emptyList());
                    if (duplicateGSTSupplier != null) {
                        ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10059E);
                        baseResponse.setCode(responseMessage.getCode());
                        baseResponse.setStatus(responseMessage.getStatus());
                        baseResponse.setMessage(responseMessage.getMessage());
                    } else {
                        ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10060E);
                        baseResponse.setCode(responseMessage.getCode());
                        baseResponse.setStatus(responseMessage.getStatus());
                        baseResponse.setMessage(responseMessage.getMessage());
                    }
                    baseResponse.setLogId(loginUser.getLogId());
                    return baseResponse;
                }
                supplier.setAddress1(supplierRequest.getAddress1());
                supplier.setAddress2(supplierRequest.getAddress2());
                //  supplier.setSupplierId(supplierRequest.getSupplierId());
                supplier.setErpSupplierId(supplierRequest.getErpSupplierId());
                supplier.setAlternateEmail(supplierRequest.getAlternateEmail());
                supplier.setAlternatePhone(supplierRequest.getAlternatePhone());
                supplier.setAreaCode(supplierRequest.getAreaCode());
                supplier.setBuilding(supplierRequest.getBuilding());
                supplier.setCity(supplierRequest.getCity());
                supplier.setContactPersonName(supplierRequest.getContactPersonName());
                supplier.setCountry(supplierRequest.getCountry());
                supplier.setCountryCode(supplierRequest.getCountryCode());
                supplier.setCreditLimitDays(supplierRequest.getCreditLimitDays());
                supplier.setVillage(supplierRequest.getVillage());
                supplier.setTown(supplierRequest.getTown());
                supplier.setTaluka(supplierRequest.getTaluka());
                supplier.setSupplierTANNumber(supplierRequest.getSupplierTANNumber());
                supplier.setSupplierPrimaryBanker(supplierRequest.getSupplierPrimaryBanker());
                supplier.setSupplierPANNumber(supplierRequest.getSupplierPANNumber());
                supplier.setSupplierName(supplierRequest.getSupplierName());
                supplier.setSupplierGSTRegistrationNumber(supplierRequest.getSupplierGSTRegistrationNumber());
                supplier.setSupplierGroup(supplierRequest.getSupplierGroup());
                supplier.setBranchCode(supplierRequest.getBranchCode());
                supplier.setSupplierCategory(supplierRequest.getSupplierCategory());
                supplier.setSubLocality(supplierRequest.getSubLocality());
                supplier.setStreet(supplierRequest.getStreet());
                supplier.setState(supplierRequest.getState());
                supplier.setPrimaryPhone(supplierRequest.getPrimaryPhone());
                supplier.setPrimaryEmail(supplierRequest.getPrimaryEmail());
                supplier.setPostCode(supplierRequest.getPostCode());
                supplier.setPaymentTerms(supplierRequest.getPaymentTerms());
                supplier.setPaymentMethod(supplierRequest.getPaymentMethod());
                supplier.setOtherOrganizationName(supplierRequest.getOtherOrganizationName());
                supplier.setOfficePrimaryPhone(supplierRequest.getOfficePrimaryPhone());
                supplier.setOfficeAlternatePhone(supplierRequest.getOfficeAlternatePhone());
                supplier.setMicrCode(supplierRequest.getMicrCode());
                supplier.setLongitude(supplierRequest.getLongitude());
                supplier.setLandmark(supplierRequest.getLandmark());
                supplier.setLocality(supplierRequest.getLocality());
                supplier.setIfscCode(supplierRequest.getIfscCode());
                supplier.setFullBranchAddress(supplierRequest.getFullBranchAddress());
                supplier.setDistrict(supplierRequest.getDistrict());
                supplier.setDesignation(supplierRequest.getDesignation());
                supplier.setDepartment(supplierRequest.getDepartment());
                supplier.setLatitude(supplierRequest.getLatitude());
                supplier.setDateOfRegistration(supplierRequest.getDateOfRegistration());
                supplier.setCreditLimitRs(supplierRequest.getCreditLimitRs());
                supplier.setModifiedBy(loginUser.getUserId());
                supplier.setModifiedOn(new Date());
                supplierRepository.save(supplier);
                List<Supplier> suppliers=new ArrayList<>();
                suppliers.add(supplier);

//                List<SupplierItemMapper> supplierItemMapperList = new ArrayList<>();
//                for (Integer items : supplierRequest.getItemId()) {
//                    Optional<Item> itemList = itemRepository.findByIsDeletedAndId(false, items);
//                    SupplierItemMapper supplierItemMapper = new SupplierItemMapper();
//                    supplierItemMapper.setSupplier(updatedSupplier);
//                    supplierItemMapper.setItem(itemList.get());
//                    supplierItemMapper.setOrganizationId(loginUser.getOrgId());
//                    supplierItemMapper.setSubOrganizationId(loginUser.getSubOrgId());
//                    supplierItemMapper.setCreatedBy(loginUser.getUserId());
//                    supplierItemMapper.setCreatedOn(new Date());
//                    supplierItemMapper.setIsDeleted(false);
//                    supplierItemMapperList.add(supplierItemMapper);
//
//                }
//                List<SupplierItemMapper> savedSupplierItemMapperList = supplierItemMapperRepository.saveAll(supplierItemMapperList);

//                SupplierResponse supplierResponse = supplierEntityToSupplierResponse(updatedSupplier);
                //   List<SupplierItemMapperResponse> responseList = mapSupplierItemMapperListToResponse(savedSupplierItemMapperList);

//                List<SupplierResponse> responseData = new ArrayList<>();
//                responseData.add(supplierResponse);
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10095S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(suppliers);
                baseResponse.setLogId(loginUser.getLogId());
                log.info("LogId:{} - SupplierServiceImpl - updateSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

            } else {
                ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10061E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
            }
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10094F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - updateSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - updateSupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," UPDATE SUPPLIER TIME :" + (endTime - startTime));
        return baseResponse;
    }




    @Override
    public BaseResponse<Supplier> searchSuppliers(

            Integer pageNumber, Integer pageSize, List<String> supplierName, List<String> supplierCategory, List<String> supplierGroup, Date startDate,Date endDate
    ) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - searchSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SEARCH SUPPLIER START");
        BaseResponse<Supplier> response = new BaseResponse<>();
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Specification<Supplier> specification = SupplierSpecifications.withFilters(supplierName, supplierCategory, supplierGroup,loginUser.getSubOrgId(), true);
            Page<Supplier> supplierPage = supplierRepository.findAll(specification, pageable);
            response.setData(supplierPage.getContent());
            response.setTotalRecordCount(supplierPage.getTotalElements());
            response.setTotalPageCount(supplierPage.getTotalPages());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10096S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - searchSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10095F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - searchSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
            response.setLogId(loginUser.getLogId());
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - searchSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SEARCH SUPPLIER TIME :" + (endTime - startTime));
        return response;
    }

    @Override
    public BaseResponse<List<SupplierNameResponse>> getSupplierWithIds() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getSupplierWithIds - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED SUPPLIER NAMES WITH IDS START");
        BaseResponse<List<SupplierNameResponse>> baseResponse = new BaseResponse<>();
        try {

            List<SupplierNameResponse> supplierNameResponseList = supplierRepository.findByIsDeletedAndSubOrganizationId(false,loginUser.getSubOrgId()).stream()
                    .map(supplier -> new SupplierNameResponse(supplier.getId(), supplier.getSupplierId(), supplier.getSupplierName(),supplier.getErpSupplierId(), supplier.getSupplierCategory(), supplier.getSupplierGroup()))
                    .collect(Collectors.toList());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10097S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

            baseResponse.setData(Collections.singletonList(supplierNameResponseList));
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - getSupplierWithIds - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());

        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10096F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());

            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - getSupplierWithIds - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getSupplierWithIds - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED SUPPLIER NAMES WITH IDS TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Supplier> getSupplierById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getSupplierById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET SUPPLIER BY ID  START");
        BaseResponse<Supplier> response = new BaseResponse();
        try {
            Supplier supplier = supplierRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(), false, id);

            List<Supplier> responseData = new ArrayList<>();
            responseData.add(supplier);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10098S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());

            response.setLogId(loginUser.getLogId());
            response.setData(responseData);
            log.info("LogId:{} - SupplierServiceImpl - getSupplierById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10097F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());

            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - getSupplierById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getSupplierById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," GET SUPPLIER BY ID TIME :" + (endTime - startTime));
        return response;
    }

    private List<SupplierItemMapperResponse> mapSupplierItemMapperListToResponse(List<SupplierItemMapper> supplierItemMapperList) {
        List<SupplierItemMapperResponse> responseList = new ArrayList<>();
        for (SupplierItemMapper supplierItemMapper : supplierItemMapperList) {
            SupplierItemMapper supplierItemMapper1 = supplierItemMapperRepository.findByIsDeletedAndSupplier_IdAndSubOrganizationId(true, supplierItemMapper.getSupplier().getId(),loginUser.getSubOrgId());
            SupplierItemMapperResponse response = new SupplierItemMapperResponse();
            //   response.setSupplierItemId(supplierItemMapper1.getSupplierItemId());
            response.setItem(supplierItemMapper1.getItem());
            // response.setSupplier(supplierItemMapper.getSupplier());
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public BaseResponse<List<ItemNameResponse>> getItemIdWithName() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED ITEMS WITH NAME START");
        BaseResponse<List<ItemNameResponse>> baseResponse = new BaseResponse<>();
        try {
         /*   List<ItemNameResponse> itemResponseList = itemRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(),false).stream()
                    .map(item -> new ItemNameResponse(item.getId(),item.getItemCode(), item.getItemName()))
                    .collect(Collectors.toList());*/
            List<ItemNameResponse> itemResponseList = itemRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId()).stream()
                    .map(item -> new ItemNameResponse(item.getId(), item.getItemId(), item.getName()))
                    .collect(Collectors.toList());
            baseResponse.setData(Collections.singletonList(itemResponseList));
            baseResponse.setLogId(loginUser.getLogId());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10099S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

            log.info("LogId:{} - SupplierServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10098F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());

            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());

            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getItemIdWithName - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," FETCHED ITEMS WITH NAME TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Supplier> getAllSuppliers() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getAllSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SUPPLIER LIST FETCHED START");
        BaseResponse<Supplier> baseResponse = new BaseResponse<>();
        try {
            List<Supplier> supplierList = supplierRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10100S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(supplierList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - getAllSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10099F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - getAllSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getAllSuppliers - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," SUPPLIER LIST FETCHED TIME :" + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<Supplier> changeItemBySuppliersId(Integer id, List<Integer> itemId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - changeItemBySuppliersId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," CHANGE ITEM BY SUPPLIERS ID START");
        BaseResponse<Supplier> baseResponse = new BaseResponse<>();
        try {
            for (Integer ids : itemId) {
                Optional<SupplierItemMapper> itemSupplierMappers = Optional.ofNullable(supplierItemMapperRepository.findByIsDeletedAndSubOrganizationIdAndItemIdAndSupplierId(false, loginUser.getSubOrgId(), ids, id));
                if (itemSupplierMappers.isEmpty()) {
                    SupplierItemMapper supplierItemMapper = new SupplierItemMapper();
                    Optional<Item> itemList = itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), ids);
                    supplierItemMapper.setSupplier(supplierRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), id));
                    supplierItemMapper.setItem(itemList.get());
                    supplierItemMapper.setOrganizationId(loginUser.getOrgId());
                    supplierItemMapper.setSubOrganizationId(loginUser.getSubOrgId());
                    supplierItemMapper.setCreatedBy(loginUser.getUserId());
                    supplierItemMapper.setCreatedOn(new Date());
                    supplierItemMapper.setIsDeleted(false);
                    supplierItemMapperRepository.save(supplierItemMapper);
                }
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10101S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - changeItemBySuppliersId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10100F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - changeItemBySuppliersId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - changeItemBySuppliersId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," CHANGE ITEM BY SUPPLIERS ID TIME :" + (endTime - startTime));
        return baseResponse;
    }


    @Override
    public BaseResponse<SupplierItemMapper> getItemBySupplier(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEM LIST FETCHED BY SUPPLIER ID START");
        BaseResponse<SupplierItemMapper> baseResponse = new BaseResponse<>();
        try {
            List<SupplierItemMapper> itemSupplierMappers = supplierItemMapperRepository.findByIsDeletedAndSubOrganizationIdAndSupplierId(false, loginUser.getSubOrgId(), id);
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10102S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(itemSupplierMappers);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - getItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10101F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - getItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - getItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," ITEM LIST FETCHED TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Item> removeItemById(Integer supplierId, Integer itemId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - removeItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," REMOVE ITEM BY ID START");
        BaseResponse<Item> baseResponse = new BaseResponse<>();
        try {
            SupplierItemMapper itemSupplierMappers = supplierItemMapperRepository.findByIsDeletedAndSubOrganizationIdAndItemIdAndSupplierId(false, loginUser.getSubOrgId(), itemId, supplierId);
            itemSupplierMappers.setIsDeleted(true);
            supplierItemMapperRepository.save(itemSupplierMappers);
            List<Item> items = new ArrayList<>();
            items.add(itemSupplierMappers.getItem());
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10103S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(items);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - removeItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10102F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - removeItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage() + (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - removeItemById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),"REMOVE ITEM BY ID TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<SupplierItemMapper> mapItemBySupplier(List<ItemSupplierMapperRequest> itemSupplierMapperRequests) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - mapItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," MAP ITEM BY SUPPLIER METHOD START");
        BaseResponse<SupplierItemMapper> baseResponse = new BaseResponse<>();
        List<SupplierItemMapper> supplierItemMappers=new ArrayList<>();
        try {

            for (ItemSupplierMapperRequest itemSupplierMapperRequest:itemSupplierMapperRequests) {
                Supplier supplier=supplierRepository.findByIsDeletedAndSubOrganizationIdAndId(false,loginUser.getSubOrgId(),itemSupplierMapperRequest.getSupplierId());
                if(itemSupplierMapperRequest.getId()==null) {
                    SupplierItemMapper supplierItemMapper = new SupplierItemMapper();
                    supplierItemMapper.setItem(itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), itemSupplierMapperRequest.getItemId()).get());
                    supplierItemMapper.setSupplier(supplier);
                    supplierItemMapper.setIsDay(itemSupplierMapperRequest.getIsDay());
                    supplierItemMapper.setLeadTime(itemSupplierMapperRequest.getLeadTime());
                    supplierItemMapper.setIsDeleted(false);
                    supplierItemMapper.setSubOrganizationId(loginUser.getSubOrgId());
                    supplierItemMapper.setOrganizationId(loginUser.getOrgId());
                    supplierItemMapper.setCreatedBy(loginUser.getUserId());
                    supplierItemMapper.setCreatedOn(new Date());
                    supplierItemMapper.setModifiedBy(loginUser.getUserId());
                    supplierItemMapper.setModifiedOn(new Date());
                    supplierItemMapperRepository.save(supplierItemMapper);
                    supplierItemMappers.add(supplierItemMapper);
                }else {
                    SupplierItemMapper supplierItemMapper = supplierItemMapperRepository.findByIsDeletedAndSubOrganizationIdAndSupplierItemId(false,loginUser.getSubOrgId(),itemSupplierMapperRequest.getId());
                    supplierItemMapper.setItem(itemRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), itemSupplierMapperRequest.getItemId()).get());
                    supplierItemMapper.setSupplier(supplier);
                    supplierItemMapper.setIsDay(itemSupplierMapperRequest.getIsDay());
                    supplierItemMapper.setLeadTime(itemSupplierMapperRequest.getLeadTime());
                    supplierItemMapper.setIsDeleted(false);
                    supplierItemMapper.setSubOrganizationId(loginUser.getSubOrgId());
                    supplierItemMapper.setOrganizationId(loginUser.getOrgId());
                    supplierItemMapper.setCreatedBy(loginUser.getUserId());
                    supplierItemMapper.setCreatedOn(new Date());
                    supplierItemMapper.setModifiedBy(loginUser.getUserId());
                    supplierItemMapper.setModifiedOn(new Date());
                    supplierItemMapperRepository.save(supplierItemMapper);
                    supplierItemMappers.add(supplierItemMapper);
                }
            }
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10102S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(supplierItemMappers);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - SupplierServiceImpl - mapItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage());
            return baseResponse;
        } catch (Exception ex) {
            ResponseMessage responseMessage=getResponseMessages(ResponseKeyConstant.UPLD10101F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - SupplierServiceImpl - mapItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(),ResponseKeyConstant.SPACE+responseMessage.getMessage()+ (endTime - startTime),ex);

        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - SupplierServiceImpl - mapItemBySupplier - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId()," MAP ITEM BY SUPPLIER METHOD EXECUTED TIME :" + (endTime - startTime));
        return baseResponse;
    }
}
