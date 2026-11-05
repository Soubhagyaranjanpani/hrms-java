package com.hrms.loan_advance.api;

import com.hrms.common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * Logs the exact error of every failed Loan API call and returns it as JSON.
 * Scoped to LoanAdvanceController only, so no other module is affected.
 *
 * Every log line starts with [LOAN] so you can search the console for it.
 * Unexpected errors (500) are logged with the full stack trace and root cause on the SERVER always;
 * the root cause is only put in the HTTP response when hrms.loan.expose-error-details=true
 * (use that locally, keep it false in production).
 *
 * Note: a 401 never reaches this class. It is produced earlier by Spring Security (see JwtAuthenticationFilter).
 */
@Slf4j
@RestControllerAdvice(assignableTypes = LoanAdvanceController.class)
public class LoanAdvanceExceptionHandler {

    @Value("${hrms.loan.expose-error-details:false}")
    private boolean exposeErrorDetails;

    /** Business rule errors thrown by LoanAdvanceService: 400, 403, 404, 422. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException exception,
                                                                    HttpServletRequest request) {
        int status = exception.getStatusCode().value();
        log.warn("[LOAN] {} {} -> {} {}", request.getMethod(), request.getRequestURI(), status, exception.getReason());
        return buildResponse(status, exception.getReason());
    }

    /** @Valid failures on the request body, e.g. "amount: Loan Amount must be greater than 0". */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException exception,
                                                                HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[LOAN] {} {} -> 400 validation failed: {}", request.getMethod(), request.getRequestURI(), message);
        return buildResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    /** Body is not valid JSON, has a wrong enum value (e.g. loanType) or a wrong date format. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadableBody(HttpMessageNotReadableException exception,
                                                                    HttpServletRequest request) {
        String detail = rootCause(exception).getMessage();
        log.warn("[LOAN] {} {} -> 400 unreadable request body: {}", request.getMethod(), request.getRequestURI(), detail);
        return buildResponse(HttpStatus.BAD_REQUEST.value(), exposeErrorDetails
                ? "Request body is not readable: " + detail
                : "Request body is missing or malformed (check field names, enum values and the date format)");
    }

    /** Bad query parameter or path variable, e.g. ?status=FOO or /abc instead of /12. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException exception,
                                                                  HttpServletRequest request) {
        String message = "Invalid value '" + exception.getValue() + "' for parameter '" + exception.getName() + "'";
        log.warn("[LOAN] {} {} -> 400 {}", request.getMethod(), request.getRequestURI(), message);
        return buildResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    /** A required query parameter is missing. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameter(MissingServletRequestParameterException exception,
                                                                      HttpServletRequest request) {
        String message = "Required parameter '" + exception.getParameterName() + "' is missing";
        log.warn("[LOAN] {} {} -> 400 {}", request.getMethod(), request.getRequestURI(), message);
        return buildResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    /** uploadAttachment called without a form field named "file". */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingPart(MissingServletRequestPartException exception,
                                                                 HttpServletRequest request) {
        String message = "Required file part '" + exception.getRequestPartName() + "' is missing";
        log.warn("[LOAN] {} {} -> 400 {}", request.getMethod(), request.getRequestURI(), message);
        return buildResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    /** Access denied by Spring Security: logged in but not allowed. Handled here so it stays a 403, not a 500. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException exception,
                                                                  HttpServletRequest request) {
        log.warn("[LOAN] {} {} -> 403 {}", request.getMethod(), request.getRequestURI(), exception.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN.value(), "You do not have permission to perform this action");
    }

    /** Anything unexpected (SQL errors, null pointers...): full stack trace goes to the server log. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception exception, HttpServletRequest request) {
        Throwable cause = rootCause(exception);
        // The exception as the LAST argument makes SLF4J print the whole stack trace and the cause chain.
        log.error("[LOAN] {} {} -> 500 {}: {}", request.getMethod(), request.getRequestURI(),
                cause.getClass().getSimpleName(), cause.getMessage(), exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exposeErrorDetails
                ? cause.getClass().getSimpleName() + ": " + cause.getMessage()
                : "Something went wrong while processing the loan request");
    }

    private static ResponseEntity<ApiResponse<Object>> buildResponse(int status, String message) {
        ApiResponse<Object> body = new ApiResponse<>();
        body.setStatus(status);
        body.setMessage(message);
        return ResponseEntity.status(status).body(body);
    }

    private static Throwable rootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}