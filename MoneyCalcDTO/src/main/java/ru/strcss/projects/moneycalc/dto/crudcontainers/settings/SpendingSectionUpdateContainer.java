package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;

import java.util.List;

@Data
public class SpendingSectionUpdateContainer extends AbstractContainer {
    private String idOrName;
    private SpendingSection spendingSection;
    private SpendingSectionSearchType searchType;

    public SpendingSectionUpdateContainer(String login, String idOrName, SpendingSectionSearchType searchType, SpendingSection spendingSection) {
        this.idOrName = idOrName;
        this.login = login;
        this.searchType = searchType;
        this.spendingSection = spendingSection;
    }

    public SpendingSectionUpdateContainer() {
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login), new FieldPairs("idOrName", idOrName));
        if (idOrName == null) reasons.add("idOrName is empty");
        if (searchType == null) reasons.add("searchType is empty");
        if (spendingSection == null) reasons.add("spendingSection is empty");
        if (spendingSection != null && !spendingSection.isValid().isValidated()) reasons.addAll(spendingSection.isValid().getReasons());
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}