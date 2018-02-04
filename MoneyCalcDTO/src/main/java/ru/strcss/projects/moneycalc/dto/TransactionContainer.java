package ru.strcss.projects.moneycalc.dto;

import lombok.Data;
import ru.strcss.projects.moneycalc.enitities.Transaction;

@Data
public class TransactionContainer {
    private Transaction transaction;
    private String login;

    public TransactionContainer(Transaction transaction, String login) {
        this.transaction = transaction;
        this.login = login;
    }

    public TransactionContainer() {
    }
}

