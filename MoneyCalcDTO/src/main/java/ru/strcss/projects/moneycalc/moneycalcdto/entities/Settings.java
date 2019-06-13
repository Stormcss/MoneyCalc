package ru.strcss.projects.moneycalc.moneycalcdto.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.Validationable;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settings implements Validationable, Serializable {

    private LocalDate periodFrom;
    private LocalDate periodTo;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (periodFrom == null) reasons.add("periodFrom is empty");
        if (periodTo == null) reasons.add("periodTo is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
