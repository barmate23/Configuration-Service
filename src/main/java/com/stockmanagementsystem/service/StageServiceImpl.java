package com.stockmanagementsystem.service;


import com.stockmanagementsystem.entity.Stage;
import com.stockmanagementsystem.repository.StageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StageServiceImpl implements StageService{

    @Autowired
    private StageRepository stageRepository;

    @Override
    public List<Stage> getStagesByAssemblyLineId(Integer assemblyLineId) {
        return stageRepository.findByAssemblyLineId(assemblyLineId);
    }

    @Override
    public List<Stage> getStagesByAssemblyLineId(String assemblyLineId) {
        return null;
    }
}
