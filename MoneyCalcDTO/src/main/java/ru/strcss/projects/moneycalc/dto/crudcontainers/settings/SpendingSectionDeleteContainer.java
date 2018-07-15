package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SpendingSectionDeleteContainer extends AbstractContainer {

    private String idOrName;
    private SpendingSectionSearchType searchType;

    @Override
    public ValidationResult isValid() {
//        List<String> reasons = validateStringFields(new FieldPairs("idOrName", idOrName));
        List<String> reasons = new ArrayList<>();
        if (idOrName == null) reasons.add("idOrName is empty");
        if (searchType == null) reasons.add("searchType is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}