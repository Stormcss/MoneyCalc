package ru.strcss.projects.moneycalc.dto;

import lombok.Data;
import ru.strcss.projects.moneycalc.enitities.Transaction;

@Data
public class TransactionContainer extends AbstractTransactionContainer{
    private Transaction transaction;

    public TransactionContainer(Transaction transaction, String login) {
        this.transaction = transaction;
        this.login = login;
    }

    public TransactionContainer() {
    }
}

