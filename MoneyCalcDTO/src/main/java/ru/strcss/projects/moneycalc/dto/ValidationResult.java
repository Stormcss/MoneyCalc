package ru.strcss.projects.moneycalc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResult {

    /**
     * Result of validation
     */
    private boolean isValidated;

    /**
     * List of reasons why validation has failed
     */
    private List<String> reasons;


    /**
     * Name of validated object
     */
    private String objectName;

    public ValidationResult(boolean isValidated, List<String> reasons) {
        this.isValidated = isValidated;
        this.reasons = reasons;
    }
}
