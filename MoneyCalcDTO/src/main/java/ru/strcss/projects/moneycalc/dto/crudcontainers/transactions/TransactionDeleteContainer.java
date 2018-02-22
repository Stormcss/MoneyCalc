package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.Getter;
import lombok.Setter;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.List;


public class TransactionDeleteContainer extends AbstractContainer {
    @Getter
    @Setter
    private String id;

    public TransactionDeleteContainer() {
    }

    public TransactionDeleteContainer(String login, String id) {
        this.login = login;
        this.id = id;
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login), new FieldPairs("id", id));
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
