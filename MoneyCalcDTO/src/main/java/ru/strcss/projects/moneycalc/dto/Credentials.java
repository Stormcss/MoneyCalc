package ru.strcss.projects.moneycalc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Credentials implements Validationable {
    private Access access;
    private Identifications identifications;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (access == null) reasons.add("access is empty");
        if (identifications == null) reasons.add("identifications are empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
//        return new ValidationResult(reasons.isEmpty(), reasons, "Credentials");
    }
}