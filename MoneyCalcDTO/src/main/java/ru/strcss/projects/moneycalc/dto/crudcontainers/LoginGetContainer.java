package ru.strcss.projects.moneycalc.dto.crudcontainers;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginGetContainer extends AbstractContainer {

    private String login;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login));
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}