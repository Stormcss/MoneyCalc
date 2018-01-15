package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PersonalIdentifications {
    private String name;

    public ValidationResult isValid() {
        List reasons = new ArrayList<>();
        if (name.isEmpty()) reasons.add("Name is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
