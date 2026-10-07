package com.investsense.service;

import com.investsense.dto.RecommendationDtos.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RecommendationService {

    public Result analyze(AnalyzeRequest request) {

        Financial f = request.financial();
        Behavior b = request.behavior();

        int score = 0;

        score += riskPoints(b.loss10());
        score += riskPoints(b.loss20());
        score += riskPoints(b.marketCrash());
        score += sipPoints(b.sipDuringCrash());
        score += liquidityPoints(b.liquidity());

        String behavioral =
                score <= 3 ? "LOW" :
                score <= 6 ? "MEDIUM" : "HIGH";

        boolean mismatch =
                !behavioral.equals(f.statedRisk());

        double equity;

        switch (f.statedRisk()) {
            case "LOW" -> equity = 25;
            case "MEDIUM" -> equity = 45;
            default -> equity = 60;
        }

        if (mismatch) {

            if ("LOW".equals(behavioral)) {
                equity -= 20;

            } else if ("MEDIUM".equals(behavioral)
                    && "HIGH".equals(f.statedRisk())) {

                equity -= 10;

            } else if ("HIGH".equals(behavioral)
                    && "LOW".equals(f.statedRisk())) {

                equity += 10;
            }
        }

        if (f.horizonYears() <= 2) {
            equity -= 10;

        } else if (f.horizonYears() >= 8) {
            equity += 5;
        }

        equity = clamp(equity, 10, 70);

        double gold = 10;

        double reserve =
                f.emergencyFund() < f.income() * 3
                        ? 15
                        : 10;

        double debt =
                100 - equity - gold - reserve;

        if (debt < 10) {
            debt = 10;
            equity = 100 - debt - gold - reserve;
        }

        List<String> reasons = new ArrayList<>();

        reasons.add(
                "Your stated risk level is "
                        + f.statedRisk().toLowerCase()
                        + ", while your scenario responses indicate "
                        + behavioral.toLowerCase()
                        + " behavioral risk."
        );

        if (mismatch) {
            reasons.add(
                    "A risk mismatch was detected, so the allocation was adjusted "
                            + "instead of relying only on your stated preference."
            );
        }

        if (f.horizonYears() <= 2) {

            reasons.add(
                    "Your short time horizon reduces the room for recovery from "
                            + "market fluctuations, so the plan uses less equity."
            );

        } else if (f.horizonYears() >= 8) {

            reasons.add(
                    "Your longer time horizon provides more time to remain invested "
                            + "through normal market fluctuations."
            );

        } else {

            reasons.add(
                    "Your medium-term horizon supports a balance between "
                            + "growth-oriented and relatively stable assets."
            );
        }

        if (f.emergencyFund() < f.income() * 3) {

            reasons.add(
                    "Your emergency savings are below three months of income "
                            + "in this demo, so a larger reserve has been included."
            );

        } else {

            reasons.add(
                    "Your emergency savings are relatively stronger, allowing "
                            + "the illustrative allocation to keep a smaller reserve."
            );
        }

        reasons.add(
                "The portfolio is diversified across equity, debt, gold and "
                        + "reserve categories rather than depending on a single asset class."
        );

        return new Result(
                equity,
                debt,
                gold,
                reserve,
                f.statedRisk(),
                behavioral,
                mismatch,
                f.horizonYears(),
                reasons
        );
    }


    public Result simulate(SimulationRequest req) {

        Result current = req.current();

        double equity = current.equity();

        int horizon = req.horizonYears();

        if (horizon <= 2) {
            equity -= 10;

        } else if (horizon >= 8) {
            equity += 5;
        }

        if (req.marketDrop() >= 30) {
            equity -= 5;
        }

        equity = clamp(equity, 10, 70);

        double reserve =
                horizon <= 2
                        ? Math.max(current.reserve(), 15)
                        : current.reserve();

        double gold = current.gold();

        double debt =
                100 - equity - gold - reserve;

        String explanation;

        if (horizon < current.horizonYears()) {

            explanation =
                    "The hypothetical goal horizon became shorter, so the system "
                            + "reduced the equity allocation and shifted weight toward "
                            + "debt/reserve categories. The market-drop setting is a "
                            + "stress-test scenario, not a prediction.";

        } else if (horizon > current.horizonYears()) {

            explanation =
                    "The hypothetical goal horizon became longer, so the system "
                            + "allows a slightly higher equity allocation. The "
                            + "market-drop setting is only a scenario input.";

        } else {

            explanation =
                    "The horizon stayed the same. The market-drop value is treated "
                            + "as a stress-test scenario and does not predict future returns.";
        }

        return new Result(
                equity,
                debt,
                gold,
                reserve,
                current.statedRisk(),
                current.behavioralRisk(),
                current.mismatch(),
                horizon,
                List.of(explanation)
        );
    }


    private int riskPoints(String x) {

        if ("SELL".equals(x)) return 0;
        if ("PARTIAL".equals(x)) return 1;
        if ("WAIT".equals(x)) return 2;
        if ("BUY".equals(x)) return 3;

        return 1;
    }


    private int sipPoints(String x) {

        if ("STOP".equals(x)) return 0;
        if ("CONTINUE".equals(x)) return 2;
        if ("INCREASE".equals(x)) return 3;

        return 1;
    }


    private int liquidityPoints(String x) {

        if ("HIGH".equals(x)) return 0;
        if ("MEDIUM".equals(x)) return 1;

        return 2;
    }


    private double clamp(double x, double min, double max) {

        return Math.max(min, Math.min(max, x));
    }
}