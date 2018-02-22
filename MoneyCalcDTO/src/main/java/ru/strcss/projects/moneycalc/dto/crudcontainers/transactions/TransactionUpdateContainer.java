package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.List;

@Data
public class TransactionUpdateContainer extends AbstractContainer {
    private String id;
    private Transaction transaction;

    public TransactionUpdateContainer() {
    }

    public TransactionUpdateContainer(String login, String ID, Transaction transaction) {
        this.login = login;
        this.id = ID;
        this.transaction = transaction;
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login), new FieldPairs("id", id));
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
