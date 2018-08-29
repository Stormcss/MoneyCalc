package ru.strcss.projects.moneycalc.moneycalcserver.dto;

import lombok.*;

/**
 * Utility class for receiving result success.
 * If result is failed then errorMessage could be set
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ResultContainer {
    @NonNull
    private Boolean isSuccess;
    private String errorMessage;
    private String fullErrorMessage;
}
