package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.request.AssemblyLineRequest;
import com.stockmanagementsystem.response.BaseResponse;

import java.util.List;

public interface AssemblyLineService {
    BaseResponse<AssemblyLine> saveAssemblyLine(AssemblyLineRequest assemblyLineRequest);

    BaseResponse<AssemblyLine> updateAssemblyLine(Integer id,AssemblyLineRequest assemblyLineRequest);

    BaseResponse<AssemblyLine> getAllAssemblyLineWithPagination(List<Integer> id,Integer pageNo, Integer pageSize);

    BaseResponse<AssemblyLine> getAllAssemblyLines();

    BaseResponse<Stage> getAllStageWithPagination(Integer id, Integer pageNo, Integer pageSize);

//    AssemblyLine getAssemblyLineByIdWithStages(Integer id);
//    BaseResponse updateAssemblyLineById(Integer id,AssemblyLineRequest assemblyLineRequest);
    BaseResponse<AssemblyLine> deleteAssemblyLineById(Integer id);

    BaseResponse<Stage> deleteStageById(Integer id);

    BaseResponse<Stage> getAllStage();
}
