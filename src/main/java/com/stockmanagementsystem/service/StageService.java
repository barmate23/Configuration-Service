package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.Stage;

import java.util.List;

public interface StageService {

//    Stage saveStage(StageRequest stageRequest);
//    List<Stage> getAlStage();
//    Stage getStageById(Integer id);
//    Stage updateStageById(Integer id,StageRequest stageRequest);
//    void deleteStageById(Integer integer);

    List<Stage> getStagesByAssemblyLineId(Integer assemblyLineId);

    List<Stage> getStagesByAssemblyLineId(String assemblyLineId);


}
