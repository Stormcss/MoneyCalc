package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionDeleteContainer extends AbstractContainer {
    private String id;

    public TransactionDeleteContainer() {
    }

    public TransactionDeleteContainer(String login, String id) {
        this.login = login;
        this.id = id;
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("Login is empty");
        if (id == null || id.isEmpty()) reasons.add("id is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
