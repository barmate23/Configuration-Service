package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Location;
import com.stockmanagementsystem.exception.ValidationFailureException;
import com.stockmanagementsystem.response.BaseResponse;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UploadExcelService {

//    public ResponseEntity<BaseResponse> uploadItemDetail(MultipartFile file,String type,String logId) throws IOException;

    ResponseEntity<BaseResponse> uploadItemDetail(MultipartFile file, String type) throws IOException;

    public ResponseEntity<BaseResponse> uploadLocationDetail(MultipartFile file, String type) throws IOException, ValidationFailureException;


//    public ResponseEntity<BaseResponse> uploadSupplierDetail(MultipartFile file, String type,String logId) throws IOException;
//
//    ResponseEntity<BaseResponse> uploadPurchaseOrders(MultipartFile file, String type, String logId) throws IOException;

    ResponseEntity<BaseResponse> uploadSupplierDetail(MultipartFile file, String type) throws IOException;

    ResponseEntity<BaseResponse> uploadStoreDetail(MultipartFile file, String type, String logId) throws IOException;

//    ResponseEntity<BaseResponse> uploadReasonDetails(MultipartFile file, String type, String logId) throws IOException;

  //  ResponseEntity<BaseResponse> uploadDocksDetails(MultipartFile file, String type, String logID);
    ResponseEntity<BaseResponse> uploadDocksDetails(MultipartFile file, String type) throws IOException;

    ResponseEntity<BaseResponse> uploadPurchaseOrders(MultipartFile file, String type) throws IOException;

    ResponseEntity<BaseResponse> uploadReasonDetails(MultipartFile file, String type) throws IOException;

    ResponseEntity<BaseResponse> uploadBomDetail(MultipartFile file, String type, String logId) throws IOException;

    ResponseEntity<BaseResponse> uploadEquipmentDetail(MultipartFile file, String type, String logId) throws IOException;

    //Added BY Kamlesh
    ResponseEntity<BaseResponse> uploadPpeDetails(MultipartFile file, String type) throws IOException, ValidationFailureException;

    ResponseEntity<BaseResponse> uploadDeviceMasterDetails(MultipartFile file, String type) throws IOException;

    ResponseEntity<BaseResponse> uploadUserListDetails(MultipartFile file, String type);

//    ResponseEntity<BaseResponse> uploadPurchaseOrders(MultipartFile file, String type) throw,s IOException;
}
