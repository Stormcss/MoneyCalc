package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionsSearchContainer extends AbstractTransactionContainer{
    private String rangeFrom;
    private String rangeTo;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
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

