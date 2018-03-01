package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;

import java.util.List;

@Data
@AllArgsConstructor
public class SpendingSectionAddContainer extends AbstractContainer {

    private String login;
    private SpendingSection spendingSection;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login));
        if (spendingSection == null) reasons.add("SpendingSection is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}