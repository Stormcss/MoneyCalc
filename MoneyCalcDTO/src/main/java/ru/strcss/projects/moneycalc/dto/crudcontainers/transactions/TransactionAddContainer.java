package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.List;

@Data
public class TransactionAddContainer extends AbstractContainer {
    private Transaction transaction;

    public TransactionAddContainer(String login, Transaction transaction) {
        this.transaction = transaction;
        this.login = login;
    }

    public TransactionAddContainer() {
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login));
        if (transaction == null) reasons.add("transaction is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}

