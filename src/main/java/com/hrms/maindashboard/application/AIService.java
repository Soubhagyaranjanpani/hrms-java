package com.hrms.maindashboard.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.maindashboard.dto.DashboardData;
import com.hrms.maindashboard.dto.DeptData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class AIService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${ai.enabled:true}")
    private boolean aiEnabled;

    @Value("${ai.refresh.seconds:300}")
    private int refreshSeconds;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    private volatile String currentInsight = "";
    private volatile LocalDateTime lastGenerated = LocalDateTime.now();
    private volatile int lastDataHash = 0;

    public int getRefreshSeconds() {
        return refreshSeconds;
    }

    public boolean isEnabled() {
        return aiEnabled;
    }

    public String getLastGeneratedTime() {
        return lastGenerated.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String generateDashboardInsights(DashboardData data) {
        int dataHash = calculateDataHash(data);
        boolean timeExpired = java.time.Duration.between(lastGenerated, LocalDateTime.now()).getSeconds() >= refreshSeconds;
        boolean dataChanged = dataHash != lastDataHash;

        if (timeExpired || dataChanged || currentInsight.isEmpty()) {
            synchronized (this) {
                timeExpired = java.time.Duration.between(lastGenerated, LocalDateTime.now()).getSeconds() >= refreshSeconds;
                dataChanged = dataHash != lastDataHash;
                if (timeExpired || dataChanged || currentInsight.isEmpty()) {
                    currentInsight = generateNewInsight(data);
                    lastGenerated = LocalDateTime.now();
                    lastDataHash = dataHash;
                }
            }
        }
        return currentInsight;
    }

    private String generateNewInsight(DashboardData data) {
        if (!aiEnabled) {
            log.info("AI disabled, using rules");
            return generateDynamicInsight(data);
        }

        // Check if API key is valid
        if (geminiApiKey != null && !geminiApiKey.isEmpty()
                && !geminiApiKey.equals("AIzaSyA-your-gemini-api-key-here")
                && !geminiApiKey.equals("AIzaSyA-your-actual-gemini-key-here")) {

            log.info("Attempting Gemini AI...");
            try {
                String result = generateWithGemini(data);
                log.info("Gemini AI SUCCESS: {}", result);
                return result;
            } catch (Exception e) {
                log.error("Gemini API FAILED: {}", e.getMessage());
            }
        } else {
            log.warn("No valid Gemini API key. Using rules.");
        }

        return generateDynamicInsight(data);
    }

    @SuppressWarnings("unchecked")
    private String generateWithGemini(DashboardData data) {
        String prompt = String.format(
                "You are an HR analytics expert. Based on this data: %d employees (+%d this month), " +
                        "%d tasks (%d in progress, %d in review, %d overdue), payroll ₹%.2fL, performance %.1f/5. " +
                        "Give ONE concise actionable insight with emoji (max 120 chars).",
                data.getTotalEmployees(),
                data.getNewHiresThisMonth(),
                data.getTotalTasks(),
                data.getInProgressTasks(),
                data.getInReviewTasks(),
                data.getOverdueTasks(),
                data.getTotalPayrollThisMonth() / 100000,
                data.getAvgPerformanceRating()
        );

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + geminiModel + ":generateContent?key=" + geminiApiKey;

        try {
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.8);
            generationConfig.put("maxOutputTokens", 150);
            generationConfig.put("topP", 0.95);
            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class
            );

            // Parse response
            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBody(), Map.class
            );

            // Check for error
            if (responseMap.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
                throw new RuntimeException("API Error: " + error.get("message"));
            }

            // Extract text
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) responseMap.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates in response");
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> responseContent =
                    (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) responseContent.get("parts");

            String text = parts.get(0).get("text").toString().trim();
            return text;

        } catch (Exception e) {
            log.error("Gemini error: {}", e.getMessage());
            throw new RuntimeException("Gemini failed: " + e.getMessage(), e);
        }
    }

    private String generateDynamicInsight(DashboardData data) {
        List<String> insights = new ArrayList<>();

        // Employee insights
        if (data.getEmployeeGrowthPct() > 50) {
            insights.add("🚀 " + fmt(data.getEmployeeGrowthPct())
                    + "% workforce growth—onboard " + data.getNewHiresThisMonth()
                    + " new hires");
            insights.add("📈 Team expanded " + fmt(data.getEmployeeGrowthPct())
                    + "% with " + data.getNewHiresThisMonth() + " new members");
        }
        if (data.getNewHiresThisMonth() > 0) {
            insights.add("👋 " + data.getNewHiresThisMonth() + " new joiner"
                    + (data.getNewHiresThisMonth() > 1 ? "s" : "")
                    + "—ensure smooth integration");
        }

        // Task insights
        if (data.getInReviewTasks() > 0) {
            insights.add("🔍 " + data.getInReviewTasks() + " task"
                    + (data.getInReviewTasks() > 1 ? "s" : "")
                    + " awaiting review—approve soon");
        }
        if (data.getCompletedTasks() > 0) {
            insights.add("✅ " + data.getCompletedTasks() + " completed—"
                    + fmt(data.getTaskCompletionRate()) + "% completion rate");
        }
        if (data.getTotalTasks() > 0) {
            insights.add("📋 " + data.getTotalTasks() + " total tasks—"
                    + data.getInProgressTasks() + " active, "
                    + data.getInReviewTasks() + " in review");
        }
        if (data.getOverdueTasks() > 0) {
            insights.add("⚠️ " + data.getOverdueTasks()
                    + " overdue—prioritize immediately");
        }

        // Payroll insights
        if (data.getTotalPayrollThisMonth() > 0) {
            insights.add("💰 ₹" + String.format("%.2f", data.getTotalPayrollThisMonth() / 100000)
                    + "L payroll—" + data.getProcessedPayrollCount() + " employees");
            insights.add("💵 Avg ₹" + fmt(data.getAvgSalary() / 1000)
                    + "K/emp—total ₹" + String.format("%.2f", data.getTotalPayrollThisMonth() / 100000) + "L");
        }
        if (data.getPendingPayrollCount() > 0) {
            insights.add("⏳ " + data.getPendingPayrollCount() + " payroll"
                    + (data.getPendingPayrollCount() > 1 ? "s" : "")
                    + " pending—process today");
        }

        // Performance insights
        if (data.getOutstandingEmployees() > 0) {
            insights.add("⭐ " + data.getOutstandingEmployees()
                    + " outstanding—recognize their work");
        }
        if (data.getAvgPerformanceRating() >= 4.0) {
            insights.add("🌟 Great performance: "
                    + data.getAvgPerformanceRating() + "/5 average");
        }

        // Department insights
        if (data.getDepts() != null && !data.getDepts().isEmpty()) {
            DeptData largest = data.getDepts().get(0);
            insights.add("🏢 " + data.getDepts().size() + " dept"
                    + (data.getDepts().size() > 1 ? "s" : "")
                    + "—" + largest.getDepartment() + " leads at "
                    + fmt(largest.getPct()) + "%");
        }

        if (insights.isEmpty()) {
            return "📊 " + data.getTotalEmployees() + " employees, "
                    + data.getTotalTasks() + " tasks—all metrics stable";
        }

        return insights.get(random.nextInt(insights.size()));
    }

    private int calculateDataHash(DashboardData data) {
        return Objects.hash(
                data.getTotalEmployees(),
                data.getNewHiresThisMonth(),
                data.getTotalTasks(),
                data.getInProgressTasks(),
                data.getPendingPayrollCount(),
                data.getProcessedPayrollCount()
        );
    }

    private String fmt(double value) {
        return String.format("%.0f", value);
    }
}