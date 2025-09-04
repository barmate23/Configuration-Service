package com.stockmanagementsystem.repository;
import com.stockmanagementsystem.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Integer> {
  Optional<Users> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndIsActiveAndId(Integer orgId, Integer subOrgId,boolean b1,boolean b, Integer id);
    Optional<Users> findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndIsActiveAndUserId(Integer orgId, Integer subOrgId,boolean b1,boolean b,String userId);
  Users findByOrganizationIdAndSubOrganizationIdAndIsActiveAndIsDeletedAndUsername(Integer orgId, Integer subOrgId, boolean b, boolean b1, String dockSupervisor);

  Users findByIsDeletedAndIsActiveAndId( boolean b, boolean b1, Integer dockSupervisor);

    List<Users> findByIsDeleted(boolean b);

    Optional<Users> findByIsDeletedAndUsername(boolean b, String storeManagerName);

    Users findByIsDeletedAndIsActiveAndUserId(boolean b, boolean b1, String userId);

    Optional<Users> findByIsDeletedAndUsernameAndSubOrganizationId(boolean b, String storeManagerName, Integer subOrgId);

    List<Users> findByIsDeletedAndSubOrganizationId(boolean b, Integer subOrgId);

    Users findByIsDeletedAndSubOrganizationIdAndIsActiveAndId(boolean b, Integer subOrgId, boolean b1, Integer dockSupervisor);

    List<Users> findByIsDeletedAndSubOrganizationIdAndModuleUserLicenceKeyLicenceLinePartNumberSubModuleMapperSubModuleSubModuleCode(boolean b, Integer subOrgId, String stor);

    List<Users> findByIsDeletedAndIsActiveAndSubOrganizationIdAndModuleUserLicenceKeyLicenceLinePartNumberSubModuleMapperSubModuleSubModuleCode(boolean b, boolean b1, Integer subOrgId, String dosu);

    Users findByIsDeletedAndIsActiveAndSubOrganizationIdAndId(boolean b, boolean b1, Integer subOrgId, Integer dockSupervisor);



  Users findBySubOrganizationIdAndIsDeletedAndUsername(Integer subOrgId, boolean b, String dockSupervisor);

  @Query("SELECT u FROM Users u " +
          "WHERE u.subOrganization.id = :subOrgId AND u.isDeleted = :isDeleted AND u.id NOT IN " +
          "(SELECT us.user.id FROM UserShiftMapper us WHERE us.shift.id = :shiftId AND us.subOrganizationId = :subOrgId AND us.isDeleted = :isDeleted)")
  List<Users> findUsersNotAssignedToShift(@Param("subOrgId") Integer subOrgId, @Param("isDeleted") boolean isDeleted, @Param("shiftId") Integer shiftId);



  Users findBySubOrganizationIdAndIsDeletedAndIsActiveAndUserId(Integer subOrgId, boolean b, boolean b1, String userId);

  Users findBySubOrganizationIdAndIsDeletedAndIsActiveAndId(Integer subOrgId, boolean b, boolean b1, Integer id);

    Users findBySubOrganizationIdAndIsDeletedAndUsernameAndModuleUserLicenceKeyLicenceLineSubModuleSubModuleCode(Integer subOrgId, boolean b, String dockSupervisor, String dosu);
}
