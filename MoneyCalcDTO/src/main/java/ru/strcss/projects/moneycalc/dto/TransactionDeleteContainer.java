package ru.strcss.projects.moneycalc.dto;

import lombok.Data;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionDeleteContainer extends AbstractTransactionContainer{
    private String ID;

    public ValidationResult isValid() {
        List reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("Login is empty");
        if (ID.isEmpty()) reasons.add("ID is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

    public TransactionDeleteContainer() {
    }

    public TransactionDeleteContainer(String login, String ID, Transaction transaction) {
        this.login = login;
        this.ID = ID;
    }
}
