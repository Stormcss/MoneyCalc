package ru.strcss.projects.moneycalc.dto.crudcontainers.settings;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;

import java.util.List;

@Data
public class SpendingSectionDeleteContainer extends AbstractContainer {
    private String idOrName;
    private SpendingSectionSearchType searchType;

    public SpendingSectionDeleteContainer(String login, String idOrName, SpendingSectionSearchType searchType) {
        this.idOrName = idOrName;
        this.login = login;
        this.searchType = searchType;
    }

    public SpendingSectionDeleteContainer() {
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login), new FieldPairs("idOrName", idOrName));
        if (searchType == null) reasons.add("searchType is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}