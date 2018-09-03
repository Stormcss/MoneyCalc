package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SpendingSectionDeleteContainer extends AbstractContainer {

    private Integer sectionId;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (sectionId == null) reasons.add("idOrName is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}