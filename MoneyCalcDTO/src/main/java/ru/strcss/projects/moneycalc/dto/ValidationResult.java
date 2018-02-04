package ru.strcss.projects.moneycalc.dto;

import lombok.Data;

import java.util.List;

@Data
public class ValidationResult {

    /**
     * Resulf of validation
     */
    private boolean isValidated;

    /**
     * List of reasons why validation failed
     */
    private List<String> reasons;

    public ValidationResult(boolean isValidated, List<String> reasons) {
        this.isValidated = isValidated;
        this.reasons = reasons;
    }
}
