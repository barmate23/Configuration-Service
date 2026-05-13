package com.stockmanagementsystem.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.exception.ExcelGenerationException;
import com.stockmanagementsystem.exception.NoDataFoundException;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.DockRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.utils.BarcodeGenerator;
import com.stockmanagementsystem.utils.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocksServiceImpl implements DocksService {
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    DocksRepository docksRepository;
    @Autowired
    LoginUser loginUser;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AcceptedRejectedStagingAreaRepository acceptedRejectedStagingAreaRepository;

    @Autowired
    StagingAreaRepository stagingAreaRepository;

    @Autowired
    StoreDockMapperRepository storeDockMapperRepository;

    @Autowired
    UserDockRepository userDockRepository;

    @Override
    public BaseResponse saveDock(DockRequest dockRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - saveDock - UserId:{} - Starting saveDock process",
                loginUser.getLogId(), loginUser.getUserId());

        BaseResponse baseResponse = new BaseResponse();

        try {
            // Step 1: Save dock
            Dock docks = createDock(dockRequest);
            docksRepository.save(docks);

            // Step 2: Map user-dock (only if no error)
            List<UserDockMapper> userDockMapperList = new ArrayList<>();
            for (Integer dockSupervisorId : dockRequest.getDockSupervisors()) {
                Users dockSupervisor = userRepository.findByIsDeletedAndIsActiveAndSubOrganizationIdAndId(
                        false, true, loginUser.getSubOrgId(), dockSupervisorId
                );

                if (dockSupervisor == null) {
                    throw new RuntimeException("Dock supervisor not found for ID " + dockSupervisorId);
                }

                UserDockMapper userDockMapper = new UserDockMapper();
                userDockMapper.setUser(dockSupervisor);
                userDockMapper.setDock(docks);
                userDockMapper.setOrganizationId(loginUser.getOrgId());
                userDockMapper.setSubOrganizationId(loginUser.getSubOrgId());
                userDockMapper.setCreatedBy(loginUser.getUserId());
                userDockMapper.setCreatedOn(new Date());
                userDockMapper.setIsDeleted(false);
                userDockMapperList.add(userDockMapper);
            }

            userDockRepository.saveAll(userDockMapperList);

            // Step 3: Store Dock Mapping
            storeDockMapperRepository.save(createStoreDockMapper(docks, dockRequest));

            // Step 4: Staging Area
            StagingArea stagingArea = createStagingArea(docks);
            stagingAreaRepository.save(stagingArea);

            // Step 5: Accepted & Rejected Staging Areas
            acceptedRejectedStagingAreaRepository.save(createAcceptedRejectedStagingArea(stagingArea, true));
            acceptedRejectedStagingAreaRepository.save(createAcceptedRejectedStagingArea(stagingArea, false));

            // SUCCESS RESPONSE
            baseResponse.setStatus(200);
            baseResponse.setCode(1);
            baseResponse.setMessage("SAVE DOCK SUCCESSFULLY");
            baseResponse.setData(Collections.singletonList(docks));

        } catch (Exception e) {
            log.error("LogId:{} - DocksServiceImpl - saveDock - UserId:{} - Error: {}",
                    loginUser.getLogId(), loginUser.getUserId(), e.getMessage(), e);
            baseResponse.setStatus(500);
            baseResponse.setCode(0);
            baseResponse.setMessage("FAILED TO SAVE DOCK");
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
        }

        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - saveDock - UserId:{} - Total time taken: {} ms",
                loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));

        return baseResponse;
    }


    // Method to create a Dock object from the request
    private Dock createDock(DockRequest dockRequest) {
        Dock docks = new Dock();

        // Generate dock ID and populate fields
        docks.setDockId(generateDockId(1));
        docks.setDockName(dockRequest.getDockName());
        docks.setAttribute(dockRequest.getAttribute());
        docks.setIsDeleted(false);
        docks.setCreatedBy(loginUser.getUserId());
        docks.setCreatedOn(new Date());
        docks.setOrganizationId(loginUser.getOrgId());
        docks.setSubOrganizationId(loginUser.getSubOrgId());

        return docks;
    }

    // Method to create a StoreDockMapper object
    private StoreDockMapper createStoreDockMapper(Dock docks, DockRequest dockRequest) {
        StoreDockMapper storeDockMapper = new StoreDockMapper();

        // Populate fields for StoreDockMapper
        storeDockMapper.setDock(docks);
        storeDockMapper.setStore(storeRepository.findByIsDeletedAndSubOrganizationIdAndIdIn(false, loginUser.getSubOrgId(), dockRequest.getStore()));
        storeDockMapper.setIsDeleted(false);
        storeDockMapper.setCreatedBy(loginUser.getUserId());
        storeDockMapper.setCreatedOn(new Date());
        storeDockMapper.setModifiedBy(loginUser.getUserId());
        storeDockMapper.setModifiedOn(new Date());
        storeDockMapper.setOrganizationId(loginUser.getOrgId());
        storeDockMapper.setSubOrganizationId(loginUser.getSubOrgId());

        return storeDockMapper;
    }

    // Method to create a StagingArea object
    private StagingArea createStagingArea(Dock docks) {
        StagingArea stagingArea = new StagingArea();

        // Populate fields for StagingArea
        stagingArea.setStagingAreaId(docks.getDockId() + "-STG01");
        stagingArea.setStagingArea(docks.getDockName());
        stagingArea.setDock(docks);
        stagingArea.setOrganizationId(loginUser.getOrgId());
        stagingArea.setSubOrganizationId(loginUser.getSubOrgId());
        stagingArea.setIsDeleted(false);
        stagingArea.setCreatedOn(new Date());
        stagingArea.setCreatedBy(loginUser.getUserId());

        return stagingArea;
    }

    // Method to create an AcceptedRejectedStagingArea object
    private AcceptedRejectedStagingArea createAcceptedRejectedStagingArea(StagingArea stagingArea, boolean isAccepted) {
        AcceptedRejectedStagingArea acceptedRejectedStagingArea = new AcceptedRejectedStagingArea();
        // Generate the appropriate code based on acceptance/rejection
        String acceptedRejectedCode = stagingArea.getStagingAreaId() + (isAccepted ? "-ARST-A-01" : "-ARST-R-01");
        acceptedRejectedStagingArea.setAcceptedRejectedCode(acceptedRejectedCode);
        acceptedRejectedStagingArea.setIsDeleted(false);
        acceptedRejectedStagingArea.setOrganizationId(loginUser.getOrgId());
        acceptedRejectedStagingArea.setSubOrganizationId(loginUser.getSubOrgId());
        acceptedRejectedStagingArea.setStagingArea(stagingArea);
        acceptedRejectedStagingArea.setCreatedOn(new Date());
        acceptedRejectedStagingArea.setCreatedBy(loginUser.getUserId());
        acceptedRejectedStagingArea.setIsAccepted(isAccepted);

        return acceptedRejectedStagingArea;
    }


    @Override
    public String generateDockId(Integer count) {
        List<Dock> docks = docksRepository.findBySubOrganizationIdOrderByIdAsc(loginUser.getSubOrgId());
        String dockId = null;
        if (docks != null && !docks.isEmpty()) {
            // Extract the last dockId and parse the numerical part
            int itmNumber = docks.size();
            // Use String.format to ensure the number is always padded to 2 digits
            dockId = String.format("%s-DK%02d", loginUser.getSubOrganizationCode(), itmNumber + count);
        } else {
            dockId = String.format("%s-DK%02d", loginUser.getSubOrganizationCode(), count);
        }
        return dockId;
    }

    @Override
    public BaseResponse deleteByDockId(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - deleteByDockId - UserId:{} - DELETE DOCK START", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse baseResponse = new BaseResponse();
        try {
            Optional<Dock> docksOptional = docksRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, id);
            if (docksOptional.isPresent()) {
                Dock dock = docksOptional.get();
                dock.setIsDeleted(true);
                docksRepository.save(dock);

                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setMessage(" Dock Archived Successfully");
                baseResponse.setStatus(ServiceConstants.STATUS_200);
                baseResponse.setData(new ArrayList<>());
                baseResponse.setCode(ServiceConstants.SUCCESS_CODE);
                log.info("LogId:{} - DocksServiceImpl - deleteByDockId - UserId:{} - SUCCESSFULLY DELETE DOCK", loginUser.getLogId(), loginUser.getUserId());
            } else {
                baseResponse.setStatus(ServiceConstants.STATUS_404);
                baseResponse.setMessage("Dock not found");
            }
        } catch (Exception e) {
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setMessage("Failed To Dock Archived");
            baseResponse.setStatus(ServiceConstants.STATUS_500);
            baseResponse.setData(new ArrayList<>());
            baseResponse.setCode(ServiceConstants.ERROR_CODE);
            log.error("LogId:{} - DocksServiceImpl - deleteByDockId - UserId:{} - FAILLED DOCK DELETE METHOD", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - deleteByDockId - UserId:{} - DOCK DELETE METHOD TIME {}", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse updateDock(Integer id, DockRequest dockRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - updateDock - UserId:{} - UPDATE DOCK START", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse baseResponse = new BaseResponse();
        try {
            Optional<Dock> optionalDocks = docksRepository.findById(id);
            if (optionalDocks.isPresent()) {
                Dock docks = optionalDocks.get();
                docks.setDockName(dockRequest.getDockName());
                docks.setAttribute(dockRequest.getAttribute());
                docksRepository.save(docks);

                // Update User-Dock Mapping
                List<UserDockMapper> existingMappings = userDockRepository.findBySubOrganizationIdAndIsDeletedAndDockId(loginUser.getSubOrgId(), false, id);
                List<Integer> newSupervisorIds = dockRequest.getDockSupervisors();

                // Remove mappings if old user is removed
                for (UserDockMapper mapping : existingMappings) {
                    if (!newSupervisorIds.contains(mapping.getUser().getId())) {
                        mapping.setIsDeleted(true);
                        mapping.setModifiedBy(loginUser.getUserId());
                        mapping.setModifiedOn(new Date());
                        userDockRepository.save(mapping);
                    }
                }

                // Add new mappings
                for (Integer supervisorId : newSupervisorIds) {
                    boolean alreadyExists = existingMappings.stream().anyMatch(m -> m.getUser().getId().equals(supervisorId));
                    if (!alreadyExists) {
                        Users supervisor = userRepository.findByIsDeletedAndIsActiveAndSubOrganizationIdAndId(false, true, loginUser.getSubOrgId(), supervisorId);
                        if (supervisor != null) {
                            UserDockMapper newMapping = new UserDockMapper();
                            newMapping.setDock(docks);
                            newMapping.setUser(supervisor);
                            newMapping.setOrganizationId(loginUser.getOrgId());
                            newMapping.setSubOrganizationId(loginUser.getSubOrgId());
                            newMapping.setCreatedBy(loginUser.getUserId());
                            newMapping.setCreatedOn(new Date());
                            newMapping.setIsDeleted(false);
                            userDockRepository.save(newMapping);
                        }
                    }
                }

                List<Dock> dockList = new ArrayList<>();
                dockList.add(docks);
                baseResponse.setStatus(200);
                baseResponse.setCode(1);
                baseResponse.setMessage(" DOCK UPDATE SUCCESSFULLY ");
                baseResponse.setData(dockList);
                log.info("LogId:{} - DocksServiceImpl - updateDock - UserId:{} - UPDATE DOCK SUCCESSFULLY", loginUser.getLogId(), loginUser.getUserId());
            } else {
                baseResponse.setStatus(ServiceConstants.STATUS_404);
                baseResponse.setData(new ArrayList<>());
                baseResponse.setMessage("DOCK NOT FOUND");
                baseResponse.setCode(ServiceConstants.ERROR_CODE);
                baseResponse.setLogId(loginUser.getLogId());
                log.error("LogId:{} - DocksServiceImpl - updateDock - UserId:{} - DOCK NOT FOUND", loginUser.getLogId(), loginUser.getUserId());
            }
        } catch (Exception e) {
            baseResponse.setStatus(ServiceConstants.STATUS_500);
            baseResponse.setData(new ArrayList<>());
            baseResponse.setMessage("FAILED TO UPDATE DOCK");
            baseResponse.setCode(ServiceConstants.ERROR_CODE);
            baseResponse.setLogId(loginUser.getLogId());
            log.error("LogId:{} - DocksServiceImpl - updateDock - UserId:{} - FAILED TO UPDATE DOCK", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - updateDock - UserId:{} - UPDATE DOCK METHOD TIME {}", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return baseResponse;
    }


    @Override
    public BaseResponse<List<DockNameResponse>> getDockNamesWithIds() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getDockNamesWithIds - UserId:{} - FETCHED DOCK NAMES WITH IDS START", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse<List<DockNameResponse>> baseResponse = new BaseResponse<>();
        try {
            List<DockNameResponse> dockNamesList = docksRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId()).stream()
                    .map(dock -> new DockNameResponse(dock.getId(), dock.getDockName(), dock.getDockId()))
                    .collect(Collectors.toList());
            baseResponse.setData(Collections.singletonList(dockNamesList));
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_200);
            baseResponse.setMessage("SUCCESSFULLY FETCHED DOCK NAMES WITH IDS");
            baseResponse.setCode(ServiceConstants.SUCCESS_CODE);
            log.info("LogId:{} - DocksServiceImpl - getDockNamesWithIds - UserId:{} - SUCCESSFULLY FETCHED DOCK NAMES WITH IDS", loginUser.getLogId(), loginUser.getUserId());
        } catch (Exception e) {
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_500);
            baseResponse.setMessage("FAILED TO FETCH DOCK NAMES WITH IDS");
            baseResponse.setCode(ServiceConstants.ERROR_CODE);
            log.error("LogId:{} - DocksServiceImpl - getDockNamesWithIds - UserId:{} - FAILED TO FETCH DOCK NAMES WITH IDS", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getDockNamesWithIds - UserId:{} - FETCHED DOCK NAMES WITH IDS TIME {}", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<List<AttributeResponse>> getAttributesWithIds() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getAttributesWithIds - UserId:{} - FETCHED ATTRIBUTES WITH IDS START", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse baseResponse = new BaseResponse();
        try {

            List<AttributeResponse> attributeResponseList = docksRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId()).stream()
                    .map(dock -> new AttributeResponse(dock.getId(), dock.getAttribute()))
                    .collect(Collectors.toList());

            baseResponse.setData(attributeResponseList);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_200);
            baseResponse.setMessage("SUCCESSFULLY FETCHED ATTRIBUTES WITH IDS");
            baseResponse.setCode(ServiceConstants.SUCCESS_CODE);
            log.info("LogId:{} - DocksServiceImpl - getAttributesWithIds - UserId:{} - SUCCESSFULLY FETCHED ATTRIBUTES WITH IDS", loginUser.getLogId(), loginUser.getUserId());
        } catch (Exception e) {
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_500);
            baseResponse.setMessage("FAILED TO FETCH ATTRIBUTES WITH IDS");
            baseResponse.setCode(ServiceConstants.ERROR_CODE);
            log.error("LogId:{} - DocksServiceImpl - getAttributesWithIds - UserId:{} - FAILED TO FETCHED ATTRIBUTES WITH IDS", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getAttributesWithIds - UserId:{} - FETCHED ATTRIBUTES WITH IDS TIME {}", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Dock> getAllDocks(int page, int pageSize) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getAllDocks - UserId:{} - FETCHED DOCKS START", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse<Dock> response = new BaseResponse<>();
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Dock> docksPage = docksRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(), false, pageable);
            List<Dock> docks = new ArrayList<>();
            for (Dock dock : docksPage.getContent()) {
                dock.setStore(storeDockMapperRepository.findByIsDeletedAndSubOrganizationIdAndDockId(false, loginUser.getSubOrgId(), dock.getId()).stream().map(StoreDockMapper::getStore).collect(Collectors.toList()));
                dock.setSupervisors(userDockRepository.findBySubOrganizationIdAndIsDeletedAndDockId(loginUser.getSubOrgId(), false, dock.getId()).stream().map(UserDockMapper::getUser).collect(Collectors.toList()));
                docks.add(dock);
            }
            response.setCode(1);
            response.setStatus(200);
            response.setTotalPageCount(docksPage.getTotalPages());
            response.setTotalRecordCount(docksPage.getTotalElements());
            response.setData(docks);
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DocksServiceImpl - getAllDocks - UserId:{} - SUCCESSFULLY FETCHED DOCKS", loginUser.getLogId(), loginUser.getUserId());
        } catch (Exception e) {
            response.setStatus(ServiceConstants.STATUS_500);
            response.setData(new ArrayList<>());
            response.setMessage("FAILED TO FETCH DOCK");
            response.setCode(ServiceConstants.ERROR_CODE);
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DocksServiceImpl - getAllDocks - UserId:{} - FAILED TO DOCK FECTH", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getAllDocks - UserId:{} - FETCHED DOCK TIME {}", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return response;
    }


    @Override
    public BaseResponse<Dock> getDocksById(Integer dockId) {

        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getDocksById - UserId:{} - FETCHED DOCKS START", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse<Dock> response = new BaseResponse<>();
        try {
            Optional<Dock> dock = docksRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), dockId);
            if (dock.isPresent()) {
                List<Dock> docks = new ArrayList<>();
                dock.get().setStore(storeDockMapperRepository.findByIsDeletedAndSubOrganizationIdAndDockId(false, loginUser.getSubOrgId(), dockId).stream().map(StoreDockMapper::getStore).collect(Collectors.toList()));
                dock.get().setSupervisors(userDockRepository.findBySubOrganizationIdAndIsDeletedAndDockId(loginUser.getSubOrgId(), false, dockId).stream().map(UserDockMapper::getUser).collect(Collectors.toList()));
                docks.add(dock.get());
                response.setCode(1);
                response.setStatus(200);
                response.setData(docks);
                response.setLogId(loginUser.getLogId());
                log.info("LogId:{} - DocksServiceImpl - getDocksById - UserId:{} - SUCCESSFULLY FETCHED DOCKS", loginUser.getLogId(), loginUser.getUserId());
            } else {
                response.setStatus(404);
                response.setMessage("Dock not found");
            }
        } catch (Exception e) {
            response.setStatus(ServiceConstants.STATUS_500);
            response.setData(new ArrayList<>());
            response.setMessage("FAILED TO FETCH DOCK");
            response.setCode(ServiceConstants.ERROR_CODE);
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DocksServiceImpl - getDocksById - UserId:{} - FAILED TO DOCK FECTH", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getDocksById - UserId:{} - FETCHED DOCK TIME {}", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return response;
    }

    @Override
    public byte[] generateExcelContentById(Integer id) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - generateExcelContentById - UserId:{} - DOWNLOAD DOCK EXCEL WITH IDS START", loginUser.getLogId(), loginUser.getUserId());
        Optional<Dock> optionalDock = docksRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(), false, id);
        if (!optionalDock.isPresent()) {
            throw new NoDataFoundException("Dock not found with id: " + id);
        }

        Dock dock = optionalDock.get();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Dock Data");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Dock ID");
            headerRow.createCell(1).setCellValue("Dock Name");
            headerRow.createCell(2).setCellValue("attribute");
            headerRow.createCell(3).setCellValue("store");
            headerRow.createCell(4).setCellValue("dockSupervisor");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(dock.getDockId());
            dataRow.createCell(1).setCellValue(dock.getDockName());
            dataRow.createCell(2).setCellValue(dock.getAttribute());
            dataRow.createCell(4).setCellValue(dock.getDockId());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("LogId:{} - DocksServiceImpl - generateExcelContentById - UserId:{} - SUCCESSFULLY DOWNLOAD DOCK EXCEL", loginUser.getLogId(), loginUser.getUserId());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.info("LogId:{} - DocksServiceImpl - generateExcelContentById - UserId:{} - DOWNLOAD DOCK EXCEL FAIL", loginUser.getLogId(), loginUser.getUserId(), e);
            throw new ExcelGenerationException("FAILED TO GENERATE EXCEL FILE", e);
        }
    }


    @Override
    public BaseResponse<Dock> searchDocks(Integer pageNumber, Integer pageSize, List<String> dockIds, List<String> attributes, List<Integer> createdYear) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - searchDocks - UserId:{} - SEARCH DOCK START", loginUser.getLogId(), loginUser.getUserId());

        BaseResponse response = new BaseResponse<>();
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
            Specification<Dock> specification = DockSpecifications.withFilters(dockIds, attributes, createdYear, true, loginUser.getSubOrgId());
            Page<Dock> docksPage = docksRepository.findAll(specification, pageable);
            List<Dock> docks = new ArrayList<>();
            for (Dock dock : docksPage.getContent()) {
                dock.setStore(storeDockMapperRepository.findByIsDeletedAndSubOrganizationIdAndDockId(false, loginUser.getSubOrgId(), dock.getId()).stream().map(StoreDockMapper::getStore).collect(Collectors.toList()));
                dock.setSupervisors(userDockRepository.findBySubOrganizationIdAndIsDeletedAndDockId(loginUser.getSubOrgId(), false, dock.getId()).stream().map(UserDockMapper::getUser).collect(Collectors.toList()));
                docks.add(dock);
            }
            response.setCode(1);
            response.setStatus(200);
            response.setTotalPageCount(docksPage.getTotalPages());
            response.setTotalRecordCount(docksPage.getTotalElements());
            response.setData(docks);
            log.info("LogId:{} - DocksServiceImpl - searchDocks - UserId:{} - SUCCESSFULLY FETCH DOCK", loginUser.getLogId(), loginUser.getUserId());
        } catch (Exception e) {
            response.setStatus(ServiceConstants.STATUS_500);
            response.setData(new ArrayList<>());
            response.setMessage("FAILED TO FETCH DOCK");
            response.setCode(ServiceConstants.ERROR_CODE);
            log.error("LogId:{} - DocksServiceImpl - searchDocks - UserId:{} - FAILED TO FETCH DOCK", loginUser.getLogId(), loginUser.getUserId(), e);
            response.setLogId(loginUser.getLogId());
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - searchDocks - UserId:{} - SEARCH DOCKS TIME {}", loginUser.getLogId(), loginUser.getUserId(), (endTime - startTime));
        return response;
    }

    @Override
    public byte[] generateExcelForAllDocks() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - generateExcelForAllDocks - UserId:{} - DOWNLOAD DOCKS EXCEL START", loginUser.getLogId(), loginUser.getUserId());

        List<Dock> docksList = docksRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(), false);

        return generateExcelForDocks(docksList);
    }

    public static byte[] generateExcelForDocks(List<Dock> docksList) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Dock Data");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Dock ID");
            headerRow.createCell(1).setCellValue("Dock Name");
            headerRow.createCell(2).setCellValue("Attribute");
            headerRow.createCell(3).setCellValue("Store");
            headerRow.createCell(4).setCellValue("Dock Supervisor");

            int rowNum = 1;
            for (Dock dock : docksList) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(dock.getDockId());
                dataRow.createCell(1).setCellValue(dock.getDockName());
                dataRow.createCell(2).setCellValue(dock.getAttribute());
                
                // Fetch supervisors for Excel if not already populated (they should be if called from getAllDocks)
                String supervisorNames = "";
                if (dock.getSupervisors() != null) {
                    supervisorNames = dock.getSupervisors().stream().map(Users::getUsername).collect(Collectors.joining(", "));
                }
                dataRow.createCell(4).setCellValue(supervisorNames);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ExcelGenerationException("Failed to generate Excel file for docks", e);
        }
    }


    @Override
    public List<String> getAllDockIds() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getAllDockIds - UserId:{} - FETCH ALL DOCKS", loginUser.getLogId(), loginUser.getUserId());

        List<String> dockIds = new ArrayList<>();
        List<Dock> docks = docksRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedOrderByIdAsc(loginUser.getOrgId(), loginUser.getSubOrgId(), false);
        for (Dock dock : docks) {
            dockIds.add(dock.getDockId());
        }
        return dockIds;
    }

    @Override
    public byte[] generateDockBarcodePDF() {
        try {
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - DocksServiceImpl - generateDockBarcodePDF - UserId:{} - DOWNLOAD DOCKS BARCODE PDF", loginUser.getLogId(), loginUser.getUserId());

            Document document = new Document();
            List<Dock> docks = docksRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(), false);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            for (Dock dock : docks) {
                byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(dock.getDockId());
                com.itextpdf.text.Image barcodeImage = com.itextpdf.text.Image.getInstance(barcodeImageBytes);
                document.add(barcodeImage);
            }
            document.close();

            log.info("LogId:{} - DocksServiceImpl - generateDockBarcodePDF - UserId:{} - SUCCESSFULLY DOWNLOAD DOCKS BARCODE PDF", loginUser.getLogId(), loginUser.getUserId());
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("LogId:{} - DocksServiceImpl - generateDockBarcodePDF - UserId:{} - FAILED DOWNLOAD DOCK BARCODE PDF", loginUser.getLogId(), loginUser.getUserId(), e);
            return null;
        }
    }

    @Override
    public byte[] generateBarcode(String barcode) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - DocksServiceImpl - generateBarcode - UserId:{} - DOWNLOAD BARCODE PDF", loginUser.getLogId(), loginUser.getUserId());

            Document document = new Document();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(barcode);
            com.itextpdf.text.Image barcodeImage = com.itextpdf.text.Image.getInstance(barcodeImageBytes);
            document.add(barcodeImage);

            document.close();

            log.info("LogId:{} - DocksServiceImpl - generateBarcode - UserId:{} - SUCCESSFULLY DOWNLOAD BARCODE PDF", loginUser.getLogId(), loginUser.getUserId());

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("LogId:{} - DocksServiceImpl - generateBarcode - UserId:{} - FAILED DOWNLOAD BARCODE PDF", loginUser.getLogId(), loginUser.getUserId(), e);
            return null;
        }
    }

    @Override
    public byte[] getStageBarcode(Boolean isAccepted) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("LogId:{} - DocksServiceImpl - getStageBarcode - UserId:{} - DOWNLOAD DOCKS BARCODE PDF", loginUser.getLogId(), loginUser.getUserId());

            Document document = new Document();
            List<AcceptedRejectedStagingArea> acceptedRejectedStagingAreaList = acceptedRejectedStagingAreaRepository.findBySubOrganizationIdAndIsAcceptedAndIsDeleted(loginUser.getSubOrgId(), isAccepted, false);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            for (AcceptedRejectedStagingArea acceptedRejectedStagingArea : acceptedRejectedStagingAreaList) {
                byte[] images = BarcodeGenerator.generateBarcode(acceptedRejectedStagingArea.getAcceptedRejectedCode());
                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(images);
                document.add(image);
            }
            document.close();
            log.info("LogId:{} - DocksServiceImpl - getStageBarcode - UserId:{} - SUCCESSFULLY DOWNLOAD DOCKS BARCODE PDF", loginUser.getLogId(), loginUser.getUserId());

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("LogId:{} - DocksServiceImpl - getStageBarcode - UserId:{} - FAILED DOWNLOAD DOCK BARCODE PDF", loginUser.getLogId(), loginUser.getUserId(), e);
            return null;
        }
    }

    @Override
    public BaseResponse<List<UserResponse>> getUsersWithIds() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getUsersWithIds - UserId:{} - FETCHED USER WITH IDS START", loginUser.getLogId(), loginUser.getUserId());

        BaseResponse baseResponse = new BaseResponse<>();
        try {
            List<UserResponse> userResponseList = userRepository.findByIsDeletedAndIsActiveAndSubOrganizationIdAndModuleUserLicenceKeyLicenceLinePartNumberSubModuleMapperSubModuleSubModuleCode(false, true, loginUser.getSubOrgId(), "DOSU").stream()
                    .map(users -> new UserResponse(users.getId(), users.getUserId(), users.getUsername()))
                    .collect(Collectors.toList());
            baseResponse.setData(userResponseList);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_200);
            baseResponse.setMessage("SUCCESSFULLY FETCHED USER WITH IDS");
            baseResponse.setCode(ServiceConstants.SUCCESS_CODE);
            log.info("LogId:{} - DocksServiceImpl - getUsersWithIds - UserId:{} - SUCCESSFULLY FETCHED USER WITH IDS", loginUser.getLogId(), loginUser.getUserId());

        } catch (Exception e) {
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_500);
            baseResponse.setMessage("FAILED TO FETCH USER WITH IDS");
            baseResponse.setCode(ServiceConstants.ERROR_CODE);
            log.error("LogId:{} - DocksServiceImpl - getUsersWithIds - UserId:{} - FAILED TO FETCHED USER WITH IDS", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<List<StoreWithIdResponse>> getStoresWithIds() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DocksServiceImpl - getStoresWithIds - UserId:{} - FETCHED STORE WITH IDS START", loginUser.getLogId(), loginUser.getUserId());

        BaseResponse baseResponse = new BaseResponse<>();
        try {
            List<StoreWithIdResponse> storeResponseList = storeRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId()).stream()
                    .map(store -> new StoreWithIdResponse(store.getId(), store.getStoreId(), store.getStoreName()))
                    .collect(Collectors.toList());

            baseResponse.setData(storeResponseList);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_200);
            baseResponse.setMessage("SUCCESSFULLY FETCHED STORE WITH IDS");
            baseResponse.setCode(ServiceConstants.SUCCESS_CODE);

            log.info(String.valueOf(loginUser.getLogId() + "SUCCESSFULLY FETCHED STORE WITH IDS"));
        } catch (Exception e) {
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(ServiceConstants.STATUS_500);
            baseResponse.setMessage("FAILED TO FETCH STORE WITH IDS");
            baseResponse.setCode(ServiceConstants.ERROR_CODE);
            log.error("LogId:{} - DocksServiceImpl - getStoresWithIds - UserId:{} - FAILED TO FETCH STORE WITH IDS", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        return baseResponse;
    }


    /**
     * This method fetches the list of accepted and rejected staging areas for a given dock.
     * It retrieves non-deleted staging areas for the logged-in user's sub-organization.
     *
     * @return BaseResponse containing the list of AcceptedRejectedStagingArea objects or an error message if the fetch fails.
     */
    @Override
    public BaseResponse<AcceptedRejectedStagingArea> getAllStagingArea() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - DockingService - getStagingAreaByDock - UserId:{} - GET ALL STAGING AREA METHOD START", loginUser.getLogId(), loginUser.getUserId());

        BaseResponse<AcceptedRejectedStagingArea> baseResponse = new BaseResponse<>();

        try {
            List<AcceptedRejectedStagingArea> acceptedRejectedStagingAreas = acceptedRejectedStagingAreaRepository
                    .findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());

            baseResponse.setCode(1);
            baseResponse.setStatus(200);
            baseResponse.setData(acceptedRejectedStagingAreas);
            baseResponse.setMessage("SUCCESSFULLY FETCHED STAGING LIST");

            log.info("LogId:{} - DockingService - getPickAcceptedAndRejectedContainer - UserId:{} - SUCCESSFULLY FETCHED STAGING LIST", loginUser.getLogId(), loginUser.getUserId());

        } catch (Exception e) {
            baseResponse.setCode(0);
            baseResponse.setStatus(500);
            baseResponse.setMessage("FAILED TO FETCH STAGING LIST");
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            log.error("LogId:{} - DockingService - getPickAcceptedAndRejectedContainer - UserId:{} - FAILED TO FETCH STAGING LIST", loginUser.getLogId(), loginUser.getUserId(), e);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<DockResponse> getAllDocksV2(int page, int pageSize) {
        log.info("LogId:{} - DocksServiceImpl - getAllDocksV2 - UserId:{} - Starting", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse<DockResponse> response = new BaseResponse<>();
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Dock> docksPage = docksRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(), false, pageable);
            
            List<DockResponse> dockResponses = docksPage.getContent().stream().map(dock -> {
                DockResponse dr = new DockResponse();
                dr.setId(dock.getId());
                dr.setDockId(dock.getDockId());
                dr.setDockName(dock.getDockName());
                dr.setAttribute(dock.getAttribute());
                
                List<UserResponse> supervisors = userDockRepository.findBySubOrganizationIdAndIsDeletedAndDockId(loginUser.getSubOrgId(), false, dock.getId())
                        .stream().map(udm -> new UserResponse(udm.getUser().getId(), udm.getUser().getUserId(), udm.getUser().getUsername()))
                        .collect(Collectors.toList());
                dr.setSupervisors(supervisors);
                
                List<Store> stores = storeDockMapperRepository.findByIsDeletedAndSubOrganizationIdAndDockId(false, loginUser.getSubOrgId(), dock.getId())
                        .stream().map(StoreDockMapper::getStore).collect(Collectors.toList());
                
                dr.setStoreResponseList(stores.stream().map(s -> {
                    StoreResponseDto srd = new StoreResponseDto();
                    srd.setId(s.getId());
                    srd.setStoreName(s.getStoreName());
                    return srd;
                }).collect(Collectors.toList()));
                
                return dr;
            }).collect(Collectors.toList());

            response.setCode(1);
            response.setStatus(200);
            response.setTotalPageCount(docksPage.getTotalPages());
            response.setTotalRecordCount(docksPage.getTotalElements());
            response.setData(dockResponses);
            response.setLogId(loginUser.getLogId());
            log.info("LogId:{} - DocksServiceImpl - getAllDocksV2 - Successfully fetched", loginUser.getLogId());
        } catch (Exception e) {
            log.error("LogId:{} - DocksServiceImpl - getAllDocksV2 - Error: {}", loginUser.getLogId(), e.getMessage(), e);
            response.setStatus(500);
            response.setCode(0);
            response.setMessage("FAILED TO FETCH DOCK V2");
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
        }
        return response;
    }

    @Override
    public BaseResponse<DockResponse> getDocksByIdV2(Integer dockId) {
        log.info("LogId:{} - DocksServiceImpl - getDocksByIdV2 - UserId:{} - Starting", loginUser.getLogId(), loginUser.getUserId());
        BaseResponse<DockResponse> response = new BaseResponse<>();
        try {
            Optional<Dock> optionalDock = docksRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), dockId);
            if (optionalDock.isPresent()) {
                Dock dock = optionalDock.get();
                DockResponse dr = new DockResponse();
                dr.setId(dock.getId());
                dr.setDockId(dock.getDockId());
                dr.setDockName(dock.getDockName());
                dr.setAttribute(dock.getAttribute());

                List<UserResponse> supervisors = userDockRepository.findBySubOrganizationIdAndIsDeletedAndDockId(loginUser.getSubOrgId(), false, dock.getId())
                        .stream().map(udm -> new UserResponse(udm.getUser().getId(), udm.getUser().getUserId(), udm.getUser().getUsername()))
                        .collect(Collectors.toList());
                dr.setSupervisors(supervisors);

                List<Store> stores = storeDockMapperRepository.findByIsDeletedAndSubOrganizationIdAndDockId(false, loginUser.getSubOrgId(), dock.getId())
                        .stream().map(StoreDockMapper::getStore).collect(Collectors.toList());

                dr.setStoreResponseList(stores.stream().map(s -> {
                    StoreResponseDto srd = new StoreResponseDto();
                    srd.setId(s.getId());
                    srd.setStoreName(s.getStoreName());
                    return srd;
                }).collect(Collectors.toList()));
                
                response.setData(Collections.singletonList(dr));
                response.setCode(1);
                response.setStatus(200);
                response.setMessage("SUCCESSFULLY FETCHED DOCK V2");
            } else {
                response.setStatus(404);
                response.setCode(0);
                response.setMessage("DOCK NOT FOUND");
                response.setData(new ArrayList<>());
            }
            response.setLogId(loginUser.getLogId());
        } catch (Exception e) {
            log.error("LogId:{} - DocksServiceImpl - getDocksByIdV2 - Error: {}", loginUser.getLogId(), e.getMessage(), e);
            response.setStatus(500);
            response.setCode(0);
            response.setMessage("FAILED TO FETCH DOCK V2");
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
        }
        return response;
    }
}
