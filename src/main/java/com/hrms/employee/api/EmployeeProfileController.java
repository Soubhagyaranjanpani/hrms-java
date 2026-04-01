package com.hrms.employee.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrms.common.dto.response.ApiResponse;
import com.hrms.common.utils.ResponseUtils;
import com.hrms.employee.application.*;
import com.hrms.employee.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/employee-profile")
@RequiredArgsConstructor
public class EmployeeProfileController {

    private final GetEmployeeFullProfileUseCase getEmployeeFullProfileUseCase;
    private final AddQualificationUseCase addQualificationUseCase;
    private final AddSkillUseCase addSkillUseCase;
    private final AddTrainingUseCase addTrainingUseCase;
    private final GetEmployeeServiceBookUseCase getEmployeeServiceBookUseCase;
    private final PromoteEmployeeUseCase promoteEmployeeUseCase;
    private final TransferEmployeeUseCase transferEmployeeUseCase;

    // 🔥 PROMOTE
    @Operation(summary = "Promote employee")
    @PostMapping("/promote")
    public ApiResponse<String> promote(@RequestBody PromotionRequest req, Principal p) {

        String result = promoteEmployeeUseCase.execute(req, p.getName());

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }

    // 🔥 TRANSFER
    @Operation(summary = "Transfer employee")
    @PostMapping("/transfer")
    public ApiResponse<String> transfer(@RequestBody TransferRequest req, Principal p) {

        String result = transferEmployeeUseCase.execute(req, p.getName());

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }

    // 🔥 ADD QUALIFICATION
    @Operation(summary = "Add qualification")
    @PostMapping("/qualification")
    public ApiResponse<String> addQualification(@RequestBody QualificationRequest req, Principal p) {

        String result = addQualificationUseCase.execute(req, p.getName());

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }

    // 🔥 ADD SKILL
    @Operation(summary = "Add skill")
    @PostMapping("/skill")
    public ApiResponse<String> addSkill(@RequestBody SkillRequest req, Principal p) {

        String result = addSkillUseCase.execute(req, p.getName());

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }

    // 🔥 ADD TRAINING
    @Operation(summary = "Add training")
    @PostMapping("/training")
    public ApiResponse<String> addTraining(@RequestBody TrainingRequest req, Principal p) {

        String result = addTrainingUseCase.execute(req, p.getName());

        return ResponseUtils.createSuccessResponse(
                result,
                new TypeReference<>() {}
        );
    }

    // 🔥 FULL PROFILE
    @Operation(summary = "Get full employee profile")
    @GetMapping("/full/{employeeId}")
    public ApiResponse<EmployeeFullProfileResponse> getFullProfile(
            @PathVariable Long employeeId) {

        EmployeeFullProfileResponse data =
                getEmployeeFullProfileUseCase.execute(employeeId);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<>() {}
        );
    }

    // 🔥 SERVICE BOOK
    @Operation(summary = "Get employee service book")
    @GetMapping("/service-book/{employeeId}")
    public ApiResponse<EmployeeServiceBookResponse> getServiceBook(
            @PathVariable Long employeeId) {

        EmployeeServiceBookResponse data =
                getEmployeeServiceBookUseCase.execute(employeeId);

        return ResponseUtils.createSuccessResponse(
                data,
                new TypeReference<>() {}
        );
    }
}
