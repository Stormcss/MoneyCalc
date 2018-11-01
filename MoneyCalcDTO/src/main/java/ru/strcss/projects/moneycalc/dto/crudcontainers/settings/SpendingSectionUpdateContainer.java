package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.entities.SpendingSection;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SpendingSectionUpdateContainer extends AbstractContainer {

    private Integer sectionId;
    private SpendingSection spendingSection;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (sectionId == null) reasons.add("sectionId is empty");
        if (spendingSection == null) reasons.add("spendingSection is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}