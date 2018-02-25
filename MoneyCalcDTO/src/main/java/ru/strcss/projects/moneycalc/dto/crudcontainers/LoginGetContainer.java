package ru.strcss.projects.moneycalc.dto.crudcontainers;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.List;

@Data
public class LoginGetContainer extends AbstractContainer {

    public LoginGetContainer(String login) {
        this.login = login;
    }

    public LoginGetContainer() {
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login));
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}