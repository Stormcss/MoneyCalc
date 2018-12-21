package ru.strcss.projects.moneycalc.moneycalcdto;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;

/**
 * Interface for grouping objects with access to validation features
 */
public interface Validationable {
    ValidationResult isValid();
}
