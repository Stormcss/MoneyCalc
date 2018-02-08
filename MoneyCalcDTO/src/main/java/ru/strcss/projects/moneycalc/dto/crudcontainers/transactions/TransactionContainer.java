package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionContainer extends AbstractTransactionContainer{
    private Transaction transaction;

    public TransactionContainer(Transaction transaction, String login) {
        this.transaction = transaction;
        this.login = login;
    }

    public TransactionContainer() {
    }

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("Login is empty");
        if (transaction == null) reasons.add("transaction is empty");
//        if (transaction == null) {
//            reasons.add("transaction is empty");
//        } else {
//            if (transaction.get_id() == null) {
//                transaction.set_id(UUID.randomUUID().toString().replace("-","").toUpperCase());
//            }
//        }
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

}

