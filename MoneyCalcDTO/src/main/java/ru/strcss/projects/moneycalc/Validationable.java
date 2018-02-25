package ru.strcss.projects.moneycalc;

import ru.strcss.projects.moneycalc.dto.ValidationResult;

/**
 * Interface for grouping objects with access to validation features
 */
public interface Validationable {
    ValidationResult isValid();
}
