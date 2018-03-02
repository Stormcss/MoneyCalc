package ru.strcss.projects.moneycalc.dto.crudcontainers.identifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;

import java.util.List;

@Data
@AllArgsConstructor
public class IdentificationsUpdateContainer extends AbstractContainer {

    private String login;
    private Identifications identifications;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login));
        if (identifications == null) reasons.add("identifications are empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}

