package ru.strcss.projects.moneycalc.dto;

import lombok.Data;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionUpdateContainer extends AbstractTransactionContainer{
    private String ID;
    private Transaction transaction;

    public ValidationResult isValid() {
        List reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("Login is empty");
        if (ID.isEmpty()) reasons.add("id is empty");
        if (!login.equals(transaction.getId())) reasons.add("Logins mismatch");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

    public TransactionUpdateContainer() {
    }

    public TransactionUpdateContainer(String login, String ID, Transaction transaction) {
        this.login = login;
        this.ID = ID;
        this.transaction = transaction;
    }
}
