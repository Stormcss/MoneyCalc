package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SpendingSectionUpdateContainer extends AbstractContainer {

    private String idOrName;
    private SpendingSection spendingSection;
    private SpendingSectionSearchType searchType;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (idOrName == null) reasons.add("idOrName is empty");
        if (searchType == null) reasons.add("searchType is empty");
        if (spendingSection == null) reasons.add("spendingSection is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}