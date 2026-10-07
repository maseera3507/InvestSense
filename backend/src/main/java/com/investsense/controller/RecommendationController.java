package com.investsense.controller;

import com.investsense.dto.RecommendationDtos.*;
import com.investsense.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService service;
    public RecommendationController(RecommendationService service){this.service=service;}

    @PostMapping("/analyze")
    public Result analyze(@RequestBody AnalyzeRequest request){ return service.analyze(request); }

    @PostMapping("/simulate")
    public Result simulate(@RequestBody SimulationRequest request){ return service.simulate(request); }
}
