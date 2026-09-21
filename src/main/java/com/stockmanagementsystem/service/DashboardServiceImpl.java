package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.response.dashboard.*;
import com.stockmanagementsystem.utils.ServiceConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final LoginUser loginUser;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PurchaseOrderHeadRepository purchaseOrderHeadRepository;
    private final SupplierRepository supplierRepository;
    private final DeviceMasterRepository deviceMasterRepository;
    private final DocksRepository docksRepository;
    private final ProductionShopRepository productionShopRepository;
    private final StoreRepository storeRepository;
    private final BomHeadRepository bomHeadRepository;
    private final HolidayRepository holidayRepository;
    private final ReasonRepository reasonRepository;
    private final EquipmentRepository equipmentRepository;
    private final LocationRepository locationRepository;
    private final SubModuleRepository subModuleRepository;
    private final UserLicenseKeyRepository userLicenseKeyRepository;

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    @Override
    public BaseResponse<DashboardResponseDto> getDashboardData() {
        long startTime = System.currentTimeMillis();
        String logId = loginUser != null ? loginUser.getLogId() : UUID.randomUUID().toString();
        log.info("LogId:{} - DashboardServiceImpl - getDashboardData START", logId);

        BaseResponse<DashboardResponseDto> response = new BaseResponse<>();
        try {
            Integer subOrgId = loginUser != null ? loginUser.getSubOrgId() : null;
            Integer orgId = loginUser != null ? loginUser.getOrgId() : null;

            // 1. Fetch raw entity records per master module (scoped to subOrg / org)
            List<Users> users = getUsers(subOrgId);
            List<Item> items = getItems(subOrgId);
            List<PurchaseOrderHead> purchaseOrders = getPurchaseOrders(subOrgId);
            List<Supplier> suppliers = getSuppliers(subOrgId);
            List<DeviceMaster> devices = getDevices(subOrgId);
            List<Dock> docks = getDocks(subOrgId);
            List<ProductionShop> shops = getProductionShops(subOrgId);
            List<Store> stores = getStores(subOrgId);
            List<BoMHead> boms = getBoms(subOrgId);
            List<Holiday> holidays = getHolidays(subOrgId, orgId);
            List<Reason> reasons = getReasons(subOrgId);
            List<Equipment> equipments = getEquipments(subOrgId);
            List<Location> locations = getLocations(subOrgId);
            List<SubModule> subModules = getSubModules();
            List<ModuleUserLicenceKey> licenseKeys = getLicenseKeys(subOrgId);

            // 2. Build Divided Sub-DTOs for each screen section
            KpiCardsDto kpiCards = KpiCardsDto.builder()
                    .workforceAndAccess(buildWorkforceKpi(users))
                    .itemMasterCatalog(buildItemCatalogKpi(items))
                    .inwardProcurement(buildProcurementKpi(purchaseOrders, suppliers))
                    .hardwareFleet(buildHardwareFleetKpi(devices, docks, shops))
                    .build();

            ConfigurationProgressDto configProgress = buildConfigurationProgress(
                    users, items, suppliers, purchaseOrders, docks, shops, stores,
                    devices, reasons, holidays, boms, equipments, locations
            );

            WorkforceDistributionDto workforceDist = buildWorkforceDistribution(users, subModules, licenseKeys);
            RecentPurchaseOrdersDto recentPOs = buildRecentPurchaseOrders(purchaseOrders, 5);
            ConnectedHardwareFleetDto connectedFleet = buildConnectedHardwareFleet(devices, 5);
            BottomQuickStatsDto bottomStats = buildBottomQuickStats(stores, boms, shops, docks, holidays, reasons);

            // 3. Build Header
            DashboardHeaderDto header = DashboardHeaderDto.builder()
                    .organizationId(orgId != null ? orgId : (subOrgId != null ? subOrgId : 112))
                    .organizationName(loginUser != null && loginUser.getOrgName() != null ? loginUser.getOrgName() : "Sarvosmi Organization")
                    .userName(loginUser != null && loginUser.getUserName() != null ? loginUser.getUserName() : "Admin")
                    .totalRecordsConfigured(configProgress.getTotalRecords())
                    .activeMasterServicesCount(configProgress.getConfiguredModules())
                    .lastSyncedTime(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")))
                    .build();

            // 4. Assemble Consolidated Dashboard Response
            DashboardResponseDto dashboardDto = DashboardResponseDto.builder()
                    .header(header)
                    .kpiCards(kpiCards)
                    .configurationProgress(configProgress)
                    .departmentWorkforceDistribution(workforceDist)
                    .recentPurchaseOrders(recentPOs)
                    .connectedHardwareFleet(connectedFleet)
                    .bottomQuickStats(bottomStats)
                    .build();

            response.setCode(ServiceConstants.SUCCESS_CODE);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Configuration dashboard data fetched successfully");
            response.setLogId(logId);
            response.setData(Collections.singletonList(dashboardDto));
            response.setTotalRecordCount(1L);

        } catch (Exception ex) {
            log.error("LogId:{} - DashboardServiceImpl - getDashboardData error: {}", logId, ex.getMessage(), ex);
            response.setCode(ServiceConstants.ERROR_CODE);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Failed to fetch dashboard data: " + ex.getMessage());
            response.setLogId(logId);
            response.setData(Collections.emptyList());
        }

        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - DashboardServiceImpl - getDashboardData completed in {} ms", logId, (endTime - startTime));
        return response;
    }

    // ==========================================
    // BUILDER / CALCULATION HELPER METHODS
    // ==========================================

    private WorkforceKpiDto buildWorkforceKpi(List<Users> users) {
        long totalUsers = users.size();
        long activeUsers = users.stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                .count();

        double activePercentage = totalUsers > 0
                ? Math.round(((double) activeUsers / totalUsers * 100.0) * 10.0) / 10.0
                : 0.0;

        long totalDepartments = users.stream()
                .map(Users::getDepartment)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(d -> !d.isEmpty())
                .distinct()
                .count();

        return WorkforceKpiDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .activePercentage(activePercentage)
                .totalDepartments(totalDepartments)
                .build();
    }

    private ItemCatalogKpiDto buildItemCatalogKpi(List<Item> items) {
        long totalItems = items.size();

        long classACount = items.stream()
                .filter(i -> i.getClassABC() != null &&
                        (i.getClassABC().equalsIgnoreCase("A") || i.getClassABC().equalsIgnoreCase("Class A")))
                .count();

        long directCount = items.stream()
                .filter(i -> i.getTypeDirectIndirect() != null &&
                        i.getTypeDirectIndirect().equalsIgnoreCase("Direct"))
                .count();

        long indirectCount = items.stream()
                .filter(i -> i.getTypeDirectIndirect() != null &&
                        i.getTypeDirectIndirect().equalsIgnoreCase("Indirect"))
                .count();

        if (directCount == 0 && indirectCount == 0 && totalItems > 0) {
            directCount = totalItems;
        }

        return ItemCatalogKpiDto.builder()
                .totalItems(totalItems)
                .classACount(classACount)
                .directCount(directCount)
                .indirectCount(indirectCount)
                .build();
    }

    private InwardProcurementKpiDto buildProcurementKpi(List<PurchaseOrderHead> purchaseOrders, List<Supplier> suppliers) {
        long totalPOs = purchaseOrders.size();
        long totalSuppliers = suppliers.size();
        long approvedVendors = suppliers.size();

        return InwardProcurementKpiDto.builder()
                .totalPurchaseOrders(totalPOs)
                .totalSuppliers(totalSuppliers)
                .approvedVendors(approvedVendors)
                .build();
    }

    private HardwareFleetKpiDto buildHardwareFleetKpi(List<DeviceMaster> devices, List<Dock> docks, List<ProductionShop> shops) {
        long totalDevices = devices.size();
        long onlineDevices = devices.stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .count();

        double onlinePercentage = totalDevices > 0
                ? Math.round(((double) onlineDevices / totalDevices * 100.0) * 10.0) / 10.0
                : 0.0;

        return HardwareFleetKpiDto.builder()
                .onlineDevices(onlineDevices)
                .totalDevices(totalDevices)
                .onlinePercentage(onlinePercentage)
                .totalDocks((long) docks.size())
                .totalShops((long) shops.size())
                .build();
    }

    private ConfigurationProgressDto buildConfigurationProgress(
            List<Users> users, List<Item> items, List<Supplier> suppliers,
            List<PurchaseOrderHead> pos, List<Dock> docks, List<ProductionShop> shops,
            List<Store> stores, List<DeviceMaster> devices, List<Reason> reasons,
            List<Holiday> holidays, List<BoMHead> boms, List<Equipment> equipments,
            List<Location> locations
    ) {
        List<ModuleStatusDto> moduleStatuses = new ArrayList<>();

        addModuleStatus(moduleStatuses, "User Directory", "USER", (long) users.size());
        addModuleStatus(moduleStatuses, "Item Master", "ITEM", (long) items.size());
        addModuleStatus(moduleStatuses, "Supplier Master", "SUPPLIER", (long) suppliers.size());
        addModuleStatus(moduleStatuses, "Purchase Order", "PURCHASE_ORDER", (long) pos.size());
        addModuleStatus(moduleStatuses, "Dock Management", "DOCK", (long) docks.size());
        addModuleStatus(moduleStatuses, "Shop Setup", "SHOP", (long) shops.size());
        addModuleStatus(moduleStatuses, "Store Management", "STORE", (long) stores.size());
        addModuleStatus(moduleStatuses, "Device Master", "DEVICE_MASTER", (long) devices.size());
        addModuleStatus(moduleStatuses, "Reason Master", "REASON", (long) reasons.size());
        addModuleStatus(moduleStatuses, "Calendar & Holiday", "CALENDAR", (long) holidays.size());
        addModuleStatus(moduleStatuses, "BOM Structures", "BOM", (long) boms.size());
        addModuleStatus(moduleStatuses, "Equipment Setup", "EQUIPMENT", (long) equipments.size());
        addModuleStatus(moduleStatuses, "Location & Zones", "LOCATION", (long) locations.size());

        int totalModules = moduleStatuses.size();
        int configuredModules = (int) moduleStatuses.stream().filter(ModuleStatusDto::getIsConfigured).count();
        int pendingModules = totalModules - configuredModules;
        long totalRecords = moduleStatuses.stream().mapToLong(ModuleStatusDto::getRecordCount).sum();

        double progressPercentage = totalModules > 0
                ? Math.round(((double) configuredModules / totalModules * 100.0) * 10.0) / 10.0
                : 0.0;
        double pendingPercentage = totalModules > 0
                ? Math.round(((double) pendingModules / totalModules * 100.0) * 10.0) / 10.0
                : 0.0;

        String pendingAction = moduleStatuses.stream()
                .filter(m -> !m.getIsConfigured())
                .findFirst()
                .map(m -> m.getModuleName() + " (0 records)")
                .orElse("All Master Modules Configured");

        return ConfigurationProgressDto.builder()
                .totalModules(totalModules)
                .configuredModules(configuredModules)
                .pendingModules(pendingModules)
                .progressPercentage(progressPercentage)
                .pendingPercentage(pendingPercentage)
                .totalRecords(totalRecords)
                .pendingAction(pendingAction)
                .modules(moduleStatuses)
                .build();
    }

    private void addModuleStatus(List<ModuleStatusDto> list, String name, String code, Long count) {
        list.add(ModuleStatusDto.builder()
                .moduleName(name)
                .moduleCode(code)
                .recordCount(count)
                .isConfigured(count > 0)
                .percentage(count > 0 ? 100.0 : 0.0)
                .build());
    }

    /**
     * Build Department Workforce Distribution:
     * 1. Fetches module/submodule names dynamically from the DB (no hardcoded module names).
     * 2. Checks all modules/submodules and determines whether users have been created against them or not.
     * 3. Calculates the percentage dynamically as:
     *    (userCount / totalLicensesForModule) * 100 (number of users created against the number of licenses for that module).
     */
    private WorkforceDistributionDto buildWorkforceDistribution(
            List<Users> users, List<SubModule> subModules, List<ModuleUserLicenceKey> licenseKeys
    ) {
        long totalUsers = users.size();

        // 1. Employment type counts
        long permanent = users.stream()
                .filter(u -> u.getUserType() != null && u.getUserType().equalsIgnoreCase("PERMANENT"))
                .count();
        long contract = users.stream()
                .filter(u -> u.getUserType() != null && u.getUserType().equalsIgnoreCase("CONTRACT"))
                .count();
        long material = users.stream()
                .filter(u -> u.getUserType() != null && u.getUserType().equalsIgnoreCase("MATERIAL"))
                .count();
        long service = users.stream()
                .filter(u -> u.getUserType() != null && u.getUserType().equalsIgnoreCase("SERVICE"))
                .count();

        // 2. Count total available licenses dynamically per module / submodule name & code
        Map<String, Long> totalLicensesByModuleName = new HashMap<>();
        Map<String, Long> totalLicensesByModuleCode = new HashMap<>();

        for (ModuleUserLicenceKey key : licenseKeys) {
            if (key.getLicenceLine() != null) {
                LicenseLine line = key.getLicenceLine();
                if (line.getSubModule() != null) {
                    String subName = line.getSubModule().getSubModuleName();
                    String subCode = line.getSubModule().getSubModuleCode();
                    if (subName != null) {
                        totalLicensesByModuleName.put(subName.trim().toUpperCase(),
                                totalLicensesByModuleName.getOrDefault(subName.trim().toUpperCase(), 0L) + 1L);
                    }
                    if (subCode != null) {
                        totalLicensesByModuleCode.put(subCode.trim().toUpperCase(),
                                totalLicensesByModuleCode.getOrDefault(subCode.trim().toUpperCase(), 0L) + 1L);
                    }
                } else if (line.getModule() != null) {
                    String modName = line.getModule().getModuleName();
                    String modCode = line.getModule().getModuleCode();
                    if (modName != null) {
                        totalLicensesByModuleName.put(modName.trim().toUpperCase(),
                                totalLicensesByModuleName.getOrDefault(modName.trim().toUpperCase(), 0L) + 1L);
                    }
                    if (modCode != null) {
                        totalLicensesByModuleCode.put(modCode.trim().toUpperCase(),
                                totalLicensesByModuleCode.getOrDefault(modCode.trim().toUpperCase(), 0L) + 1L);
                    }
                }
            }
        }

        // 3. Map dynamic SubModules from DB
        Map<String, String> dynamicModuleCodeByName = new LinkedHashMap<>();
        for (SubModule sm : subModules) {
            if (sm.getSubModuleName() != null && !sm.getSubModuleName().trim().isEmpty()) {
                String name = sm.getSubModuleName().trim();
                String code = sm.getSubModuleCode() != null ? sm.getSubModuleCode().trim() : "";
                dynamicModuleCodeByName.put(name, code);
            }
        }

        // 4. Map and count users created against each dynamic module name
        Map<String, Long> userCountByModuleName = new LinkedHashMap<>();
        for (String modName : dynamicModuleCodeByName.keySet()) {
            userCountByModuleName.put(modName, 0L);
        }

        for (Users u : users) {
            String resolvedModuleName = null;

            // Check if user is linked directly to a module license key
            if (u.getModuleUserLicenceKey() != null && u.getModuleUserLicenceKey().getLicenceLine() != null) {
                LicenseLine line = u.getModuleUserLicenceKey().getLicenceLine();
                if (line.getSubModule() != null && line.getSubModule().getSubModuleName() != null) {
                    resolvedModuleName = line.getSubModule().getSubModuleName().trim();
                } else if (line.getModule() != null && line.getModule().getModuleName() != null) {
                    resolvedModuleName = line.getModule().getModuleName().trim();
                }
            }

            // If not found in license key, match against user's department field
            if (resolvedModuleName == null && u.getDepartment() != null && !u.getDepartment().trim().isEmpty()) {
                String dept = u.getDepartment().trim();
                for (String modName : dynamicModuleCodeByName.keySet()) {
                    if (modName.equalsIgnoreCase(dept)) {
                        resolvedModuleName = modName;
                        break;
                    }
                }
                if (resolvedModuleName == null) {
                    resolvedModuleName = dept; // Dynamic department
                }
            }

            if (resolvedModuleName == null || resolvedModuleName.trim().isEmpty()) {
                resolvedModuleName = "Unassigned";
            }

            userCountByModuleName.put(resolvedModuleName,
                    userCountByModuleName.getOrDefault(resolvedModuleName, 0L) + 1L);
        }

        // 5. Build DTO list calculating percentage: (userCount / totalLicensesForModule) * 100
        List<DepartmentDistributionDto> deptList = userCountByModuleName.entrySet().stream()
                .map(entry -> {
                    String moduleName = entry.getKey();
                    Long userCount = entry.getValue();
                    String moduleCode = dynamicModuleCodeByName.getOrDefault(moduleName, "");

                    // Get total licenses for this specific module dynamically
                    Long totalLicenses = totalLicensesByModuleName.getOrDefault(moduleName.toUpperCase(), 0L);
                    if (totalLicenses == 0L && !moduleCode.isEmpty()) {
                        totalLicenses = totalLicensesByModuleCode.getOrDefault(moduleCode.toUpperCase(), 0L);
                    }

                    // If no explicit licenses found in DB for this module, fallback to userCount as baseline
                    long effectiveLicenses = totalLicenses > 0 ? totalLicenses : userCount;

                    // Calculate percentage using the number of licenses for that module against no of users created
                    double licensePct = effectiveLicenses > 0
                            ? Math.round(((double) userCount / effectiveLicenses * 100.0) * 10.0) / 10.0
                            : 0.0;

                    // Workforce overall percentage
                    double wfPct = totalUsers > 0
                            ? Math.round(((double) userCount / totalUsers * 100.0) * 10.0) / 10.0
                            : 0.0;

                    long available = Math.max(0, effectiveLicenses - userCount);
                    boolean isCreated = userCount > 0;

                    return DepartmentDistributionDto.builder()
                            .departmentName(moduleName)
                            .moduleCode(moduleCode)
                            .userCount(userCount)
                            .totalLicenses(effectiveLicenses)
                            .availableLicenses(available)
                            .percentage(licensePct) // (userCount / totalLicenses) * 100
                            .workforcePercentage(wfPct)
                            .isUserCreated(isCreated)
                            .build();
                })
                .sorted(Comparator.comparing(DepartmentDistributionDto::getIsUserCreated).reversed()
                        .thenComparing(DepartmentDistributionDto::getUserCount, Comparator.reverseOrder())
                        .thenComparing(DepartmentDistributionDto::getDepartmentName))
                .collect(Collectors.toList());

        long configuredDepts = deptList.stream().filter(DepartmentDistributionDto::getIsUserCreated).count();

        return WorkforceDistributionDto.builder()
                .totalUsers(totalUsers)
                .permanentCount(permanent)
                .contractCount(contract)
                .materialCount(material)
                .serviceCount(service)
                .totalConfiguredDepartments(configuredDepts)
                .totalModulesChecked(deptList.size())
                .departments(deptList)
                .build();
    }

    private RecentPurchaseOrdersDto buildRecentPurchaseOrders(List<PurchaseOrderHead> purchaseOrders, int limit) {
        long totalPOs = purchaseOrders.size();

        List<RecentPurchaseOrderItemDto> list = purchaseOrders.stream()
                .sorted((a, b) -> {
                    if (a.getId() != null && b.getId() != null) {
                        return b.getId().compareTo(a.getId());
                    }
                    return 0;
                })
                .limit(limit)
                .map(po -> {
                    String supplierName = (po.getSupplier() != null && po.getSupplier().getSupplierName() != null)
                            ? po.getSupplier().getSupplierName() : "N/A";
                    String orderDateStr = po.getPurchaseOrderDate() != null
                            ? DATE_FORMATTER.format(po.getPurchaseOrderDate()) : "-";
                    String deliverByStr = po.getDeliverByDate() != null
                            ? DATE_FORMATTER.format(po.getDeliverByDate()) : "-";
                    Double amount = po.getTotalAmount() != null ? po.getTotalAmount() : 0.0;
                    String formattedAmount = formatCurrency(amount);
                    String status = (po.getStatus() != null && po.getStatus().getStatusName() != null)
                            ? po.getStatus().getStatusName() : "Active";

                    return RecentPurchaseOrderItemDto.builder()
                            .id(po.getId())
                            .poNumber(po.getPurchaseOrderNumber())
                            .supplierName(supplierName)
                            .orderDate(orderDateStr)
                            .deliverByDate(deliverByStr)
                            .amount(amount)
                            .formattedAmount(formattedAmount)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());

        return RecentPurchaseOrdersDto.builder()
                .totalPOs(totalPOs)
                .purchaseOrders(list)
                .build();
    }

    private ConnectedHardwareFleetDto buildConnectedHardwareFleet(List<DeviceMaster> devices, int limit) {
        long totalCount = devices.size();
        long onlineCount = devices.stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .count();

        double onlinePercentage = totalCount > 0
                ? Math.round(((double) onlineCount / totalCount * 100.0) * 10.0) / 10.0
                : 0.0;

        List<ConnectedDeviceItemDto> list = devices.stream()
                .sorted((a, b) -> {
                    if (a.getId() != null && b.getId() != null) {
                        return b.getId().compareTo(a.getId());
                    }
                    return 0;
                })
                .limit(limit)
                .map(device -> {
                    String roleName = (device.getRole() != null && device.getRole().getSubModuleName() != null)
                            ? device.getRole().getSubModuleName() : "Device";
                    boolean isActive = Boolean.TRUE.equals(device.getIsActive());

                    return ConnectedDeviceItemDto.builder()
                            .id(device.getId())
                            .terminal(device.getDeviceName() != null ? device.getDeviceName() : "Terminal")
                            .ipAddress(device.getDeviceIp() != null ? device.getDeviceIp() : "-")
                            .role(roleName)
                            .status(isActive ? "Online" : "Offline")
                            .isActive(isActive)
                            .build();
                })
                .collect(Collectors.toList());

        return ConnectedHardwareFleetDto.builder()
                .onlineCount(onlineCount)
                .totalCount(totalCount)
                .onlinePercentage(onlinePercentage)
                .devices(list)
                .build();
    }

    private BottomQuickStatsDto buildBottomQuickStats(
            List<Store> stores, List<BoMHead> boms, List<ProductionShop> shops,
            List<Dock> docks, List<Holiday> holidays, List<Reason> reasons
    ) {
        return BottomQuickStatsDto.builder()
                .storesCount((long) stores.size())
                .bomActiveAssemblies((long) boms.size())
                .shopsCount((long) shops.size())
                .docksCount((long) docks.size())
                .calendarEventsCount((long) holidays.size())
                .reasonCodesCount((long) reasons.size())
                .build();
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "₹ 0";
        DecimalFormat formatter = new DecimalFormat("##,##,##,##0");
        return "₹ " + formatter.format(amount);
    }

    // ==========================================
    // DATA FETCHING HELPERS WITH SCOPE FALLBACK
    // ==========================================

    private List<Users> getUsers(Integer subOrgId) {
        if (subOrgId != null) {
            List<Users> list = userRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Users> fallback = userRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Item> getItems(Integer subOrgId) {
        if (subOrgId != null) {
            List<Item> list = itemRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Item> fallback = itemRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<PurchaseOrderHead> getPurchaseOrders(Integer subOrgId) {
        if (subOrgId != null) {
            List<PurchaseOrderHead> list = purchaseOrderHeadRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<PurchaseOrderHead> fallback = purchaseOrderHeadRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Supplier> getSuppliers(Integer subOrgId) {
        if (subOrgId != null) {
            List<Supplier> list = supplierRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Supplier> fallback = supplierRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<DeviceMaster> getDevices(Integer subOrgId) {
        if (subOrgId != null) {
            List<DeviceMaster> list = deviceMasterRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<DeviceMaster> fallback = deviceMasterRepository.findAll().stream()
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .collect(Collectors.toList());
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Dock> getDocks(Integer subOrgId) {
        if (subOrgId != null) {
            List<Dock> list = docksRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Dock> fallback = docksRepository.findAll().stream()
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .collect(Collectors.toList());
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<ProductionShop> getProductionShops(Integer subOrgId) {
        if (subOrgId != null) {
            List<ProductionShop> list = productionShopRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<ProductionShop> fallback = productionShopRepository.findByIsDeletedFalse();
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Store> getStores(Integer subOrgId) {
        if (subOrgId != null) {
            List<Store> list = storeRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Store> fallback = storeRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<BoMHead> getBoms(Integer subOrgId) {
        if (subOrgId != null) {
            List<BoMHead> list = bomHeadRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<BoMHead> fallback = bomHeadRepository.findAll().stream()
                .filter(b -> !Boolean.TRUE.equals(b.getIsDeleted()))
                .collect(Collectors.toList());
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Holiday> getHolidays(Integer subOrgId, Integer orgId) {
        if (orgId != null && subOrgId != null) {
            List<Holiday> list = holidayRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(orgId, subOrgId, false);
            if (list != null && !list.isEmpty()) return list;
        }
        if (subOrgId != null) {
            List<Holiday> list = holidayRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Holiday> fallback = holidayRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Reason> getReasons(Integer subOrgId) {
        if (subOrgId != null) {
            List<Reason> list = reasonRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Reason> fallback = reasonRepository.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .collect(Collectors.toList());
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Equipment> getEquipments(Integer subOrgId) {
        if (subOrgId != null) {
            List<Equipment> list = equipmentRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Equipment> fallback = equipmentRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                .collect(Collectors.toList());
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<Location> getLocations(Integer subOrgId) {
        if (subOrgId != null) {
            List<Location> list = locationRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<Location> fallback = locationRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }

    private List<SubModule> getSubModules() {
        List<SubModule> list = subModuleRepository.findByIsDeleted(false);
        return list != null ? list : Collections.emptyList();
    }

    private List<ModuleUserLicenceKey> getLicenseKeys(Integer subOrgId) {
        if (subOrgId != null) {
            List<ModuleUserLicenceKey> list = userLicenseKeyRepository.findByIsDeletedAndSubOrganizationId(false, subOrgId);
            if (list != null && !list.isEmpty()) return list;
        }
        List<ModuleUserLicenceKey> fallback = userLicenseKeyRepository.findByIsDeleted(false);
        return fallback != null ? fallback : Collections.emptyList();
    }
}
