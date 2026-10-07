package com.investsense.dto;

import java.util.Map;
import java.util.List;

public class RecommendationDtos {
    public record AnalyzeRequest(Long userId, Financial financial, Behavior behavior) {}
    public record Financial(double income, double monthlySavings, double existingInvestments,
                            double emergencyFund, String goal, double goalAmount,
                            int horizonYears, String statedRisk) {}
    public record Behavior(String loss10, String loss20, String marketCrash,
                           String sipDuringCrash, String liquidity) {}

    public record Result(
        double equity, double debt, double gold, double reserve,
        String statedRisk, String behavioralRisk, boolean mismatch,
        int horizonYears, List<String> reasons
    ) {}

    public record SimulationRequest(Long userId, Result current, int horizonYears, int marketDrop) {}
}
