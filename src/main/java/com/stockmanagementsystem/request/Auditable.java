package com.stockmanagementsystem.request;

import java.util.Date;

public interface Auditable {
    void setOrganizationId(Integer id);
    void setSubOrganizationId(Integer id);
    void setIsDeleted(Boolean deleted);
    void setCreatedBy(Integer id);
    void setCreatedOn(Date date);
    void setModifiedBy(Integer id);
    void setModifiedOn(Date date);
}
