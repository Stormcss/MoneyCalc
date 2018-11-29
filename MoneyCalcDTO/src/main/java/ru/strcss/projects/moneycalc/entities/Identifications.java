package ru.strcss.projects.moneycalc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Identifications implements Validationable, Serializable {

    private Long id;
    private String name;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (name == null || name.isEmpty()) reasons.add("name is empty");
        return new ValidationResult(reasons.isEmpty(), reasons, "Identifications");
    }
}
