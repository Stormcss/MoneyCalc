package ru.strcss.projects.moneycalcserver.controllers.dto;

import lombok.Data;
import ru.strcss.projects.moneycalcserver.enitities.Transaction;

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

