package com.stockmanagementsystem.repository;
import com.stockmanagementsystem.entity.ModuleUserLicenceKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLicenseKeyRepository extends JpaRepository<ModuleUserLicenceKey,Integer> {
    ModuleUserLicenceKey findByIsDeletedAndIsUsedAndId(boolean b, boolean b1, Integer userLicenseKeyId);

    List<ModuleUserLicenceKey> findByIsDeletedAndIsUsedAndLicenceLineSubModuleIdAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationId(boolean b, boolean b1, Integer subModuleId, int i, Integer subOrgId);

    ModuleUserLicenceKey findByIsDeletedAndId(boolean b, Integer userLicenseKeyId);

    List<ModuleUserLicenceKey> findByIsDeletedAndIdIn(boolean b, List<Integer> additionalDeviceLicense);

    List<ModuleUserLicenceKey> findByIsDeletedAndIsUsedAndLicenceLineSubModuleSubModuleCodeAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationId(boolean b, boolean b1, String stor, int i, Integer subOrgId);

    List<ModuleUserLicenceKey> findByIsDeletedAndIsUsedAndLicenceLineSubModuleSubModuleCodeAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationIdAndLicenceLinePartNumberSubModuleMapperPartNumberDefaultAdditional(boolean b, boolean b1, String area, int i, Integer subOrgId, int i1);

    List<ModuleUserLicenceKey> findByIsDeletedAndLicenceLineSubModuleSubModuleCodeAndLicenceLinePartNumberSubModuleMapperLicenseCategoryAndSubOrganizationId(boolean b, String asns, int i, Integer subOrgId);
}
