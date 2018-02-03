package ru.strcss.projects.moneycalcserver.controllers.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionsSearchContainer {
    private String login;
    private String rangeFrom;
    private String rangeTo;

    public ValidationResult isValid() {
        List reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("login is empty");
        if (rangeFrom.isEmpty()) reasons.add("rangeFrom is empty");
        if (rangeTo.isEmpty()) reasons.add("rangeTo is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

    public TransactionsSearchContainer() {
    }

    public TransactionsSearchContainer(String login, String rangeFrom, String rangeTo) {
        this.login = login;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }
}

