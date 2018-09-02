package ru.strcss.projects.moneycalc.moneycalcserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Utility class for receiving result success.
 * If result is failed then errorMessage could be set
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ResultContainer {
    private final boolean isSuccess;
    private String errorMessage;
    private String fullErrorMessage;
}
