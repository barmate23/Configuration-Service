package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.AssemblyLine;
import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.request.AssemblyLineRequest;
import com.stockmanagementsystem.response.BaseResponse;
import com.stockmanagementsystem.service.AssemblyLineService;
import com.stockmanagementsystem.service.StageService;
import com.stockmanagementsystem.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME+APIConstants.ASSEMBLY_LINE_CONTROLLER})
public class AssemblyLineController {
    @Autowired
    private AssemblyLineService assemblyLineService;
    @Autowired
    private StageService stageService;

    @PostMapping(APIConstants.SAVE_ASSEMBLY_WITH_STAGE)
    public BaseResponse saveAssemblyWithStages(@RequestBody AssemblyLineRequest assemblyLineRequest){
        return assemblyLineService.saveAssemblyLine(assemblyLineRequest);
    }

    @PostMapping(APIConstants.UPDATE_ASSEMBLY_LINE_BY_ID)
    public BaseResponse updateAssemblyLine(@PathVariable Integer id,@RequestBody AssemblyLineRequest assemblyLineRequest){
        return assemblyLineService.updateAssemblyLine(id,assemblyLineRequest);
    }
    @GetMapping(APIConstants.GET_ALL_ASSEMBLY_LINE_WITH_PAGINATION)
    public BaseResponse<AssemblyLine> getAllAssemblyLinesWithStages(
                    @RequestParam(required = false) List<Integer> id,
                    @RequestParam(defaultValue = "0") Integer pageNo,
                    @RequestParam(defaultValue = "10") Integer pageSize){
        return assemblyLineService.getAllAssemblyLineWithPagination(id,pageNo,pageSize);
    }
    @GetMapping(APIConstants.GET_ALL_ASSEMBLY_LINE)
    public BaseResponse<AssemblyLine> getAllAssemblyLinesWithStages(){
        return assemblyLineService.getAllAssemblyLines();
    }
    @GetMapping(APIConstants.GET_ALL_STAGES)
    public BaseResponse<Stage> getAllStage(){
        return assemblyLineService.getAllStage();
    }
    @GetMapping(APIConstants.GET_ALL_STAGES_WITH_PAGINATION)
    public BaseResponse<Stage> getAllStageWithPagination(
                        @RequestParam Integer id,
                        @RequestParam(defaultValue = "0") Integer pageNo,
                        @RequestParam(defaultValue = "10") Integer pageSize){
        return assemblyLineService.getAllStageWithPagination(id,pageNo,pageSize);
    }



    @DeleteMapping(APIConstants.DELETE_ASSEMBLY_LINE_BY_ID)
    public BaseResponse<AssemblyLine> deleteAssemblyLineById(@PathVariable Integer id){
        return assemblyLineService.deleteAssemblyLineById(id);
    }
    @DeleteMapping(APIConstants.DELETE_STAGE_BY_ID)
    public BaseResponse<Stage> deleteStageById(@PathVariable Integer id){
        return assemblyLineService.deleteStageById(id);
    }


}
