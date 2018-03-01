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
}
